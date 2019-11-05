package org.jetlinks.platform.rule.debug;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.lettuce.core.api.StatefulRedisConnection;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hswebframework.utils.StringUtils;
import org.hswebframework.web.exception.NotFoundException;
import org.hswebframework.web.id.IDGenerator;
import org.jetlinks.core.cluster.ClusterManager;
import org.jetlinks.core.cluster.ClusterQueue;
import org.jetlinks.core.cluster.ClusterTopic;
import org.jetlinks.lettuce.RedisTopic;
import org.jetlinks.rule.engine.api.*;
import org.jetlinks.rule.engine.api.executor.*;
import org.jetlinks.rule.engine.api.model.Condition;
import org.jetlinks.rule.engine.api.model.NodeType;
import org.jetlinks.rule.engine.api.model.RuleEngineModelParser;
import org.jetlinks.rule.engine.api.model.RuleModel;
import org.jetlinks.rule.engine.cluster.logger.ClusterLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@Service
public class RuleEngineDebugService {

    @Autowired
    private ClusterManager clusterManager;

    @Autowired
    private ExecutableRuleNodeFactory executableRuleNodeFactory;

    @Autowired
    private RuleEngineModelParser modelParser;

    @Autowired
    private ConditionEvaluator conditionEvaluator;

    private Map<String, Session> sessionStore = new ConcurrentHashMap<>();


    private ClusterTopic<ExecuteRuleRequest> executeRuleTopic;

    @PostConstruct
    public void init() {
        executeRuleTopic = clusterManager.getTopic("rule.engine.debug.instance.execute");

        clusterManager.<String>getTopic("rule.engine.debug.session.created")
                .subscribe()
                .subscribe((id) -> {
                    if (sessionStore.containsKey(id)) {
                        return;
                    }
                    sessionStore.put(id, new Session(id));
                });

        //context 停止
        clusterManager.<String>getTopic("rule.engine.debug.session.context.stopped")
                .subscribe()
                .subscribe((context) -> sessionStore
                        .values()
                        .stream()
                        .filter(session -> session.contexts.containsKey(context))
                        .forEach(session -> session.stopContext(context)));
        //会话关闭
        clusterManager.<String>getTopic("rule.engine.debug.session.close")
                .subscribe()
                .subscribe((id) -> Optional.ofNullable(id)
                        .map(sessionStore::remove)
                        .ifPresent(Session::close));

        //有新的context创建了
        clusterManager.<String>getTopic("rule.engine.debug.session.context.created")
                .subscribe()
                .subscribe((contextId) -> sessionStore
                        .values()
                        .stream()
                        .map(session -> session.contexts.get(contextId))
                        .filter(Objects::nonNull)
                        .filter(ctx -> !ctx.isLocal())
                        .forEach(DebugExecutionContext::stop));

        executeRuleTopic.subscribe().subscribe((request) ->
                Optional.ofNullable(sessionStore.get(request.getSessionId()))
                        .filter(session -> session.contexts.containsKey(request.getContextId()))
                        .ifPresent(session -> session.execute(request)));

    }

    public Flux<DebugMessage> getDebugMessages(String sessionId) {
        return getSession(sessionId)
                .consumeOutPut();
    }

    private Session getSession(String id) {
        return Optional.ofNullable(id)
                .map(sessionStore::get)
                .orElseThrow(() -> new NotFoundException("session不存在"));
    }

    public Mono<String> startSession() {
        String sessionId = IDGenerator.UUID.generate();
        Session session = new Session(sessionId);
        session.local = true;
        sessionStore.put(sessionId, session);

        return clusterManager.getTopic("rule.engine.debug.session.created")
                .publish(Mono.just(sessionId))
                .thenReturn(sessionId);
    }


    @SneakyThrows
    public String startNode(String sessionId, RuleNodeConfiguration configuration) {
        configuration.setNodeType(NodeType.MAP);
        Session session = getSession(sessionId);

        DebugExecutionContext context = session.createContext(configuration);

        clusterManager.<String>getTopic("rule.engine.debug.session.context.created")
                .publish(Mono.just(context.id))
                .subscribe();

        ExecutableRuleNode ruleNode = executableRuleNodeFactory.create(configuration);

        ruleNode.start(context);

        return context.id;
    }

    public void sendData(String sessionId, String contextId, RuleData ruleData) {
        getSession(sessionId)
                .getContext(contextId)
                .execute(ruleData);
    }


    public Mono<Boolean> stopContext(String sessionId, String contextId) {
        getSession(sessionId).stopContext(contextId);

        return clusterManager.<String>getTopic("rule.engine.debug.session.context.stopped")
                .publish(Mono.just(contextId))
                .thenReturn(true);
    }

    public Set<String> getAllContext(String sessionId) {
        return getSession(sessionId)
                .contexts
                .keySet();
    }

    public Mono<Boolean> closeSession(String sessionId) {
        return clusterManager.getTopic("rule.engine.debug.session.close")
                .publish(Mono.just(sessionId))
                .then(Mono.just(true));
    }

    public Mono<Boolean> testCondition(String sessionId, Condition condition, Object data) {

        Session session = getSession(sessionId);

        try {
            boolean success = conditionEvaluator.evaluate(condition, RuleData.create(data));
            return session.writeMessage(DebugMessage.of("output", null, "测试条件:".concat(success ? "通过" : "未通过")));
        } catch (Exception e) {
            return session.writeMessage(DebugMessage.of("error", null, StringUtils.throwable2String(e)));
        }
    }

    private class Session {
        private String id;

        private long lastOperationTime;

        private Map<String, DebugExecutionContext> contexts = new ConcurrentHashMap<>();

        private Map<String, RuleInstanceContext> instanceContext = new ConcurrentHashMap<>();

        private Map<String, String> instanceContextMapping = new ConcurrentHashMap<>();

        private ClusterQueue<DebugMessage> messageQueue;

        @Getter
        private boolean local = false;

        private Session(String id) {
            this.lastOperationTime = System.currentTimeMillis();
            this.id = id;
            this.messageQueue = clusterManager.getQueue("rule.engine.debug.message:".concat(id));
        }

        private boolean isTimeout() {
            return System.currentTimeMillis() - lastOperationTime > TimeUnit.MINUTES.toMillis(15);
        }

        private void checkContextTimeout() {
            contexts.entrySet()
                    .stream()
                    .filter(e -> e.getValue().isTimeout())
                    .map(Map.Entry::getKey)
                    .map(contexts::remove)
                    .forEach(DebugExecutionContext::stop);
        }

        private void stopContext(String contextId) {
            Optional.ofNullable(contexts.remove(contextId))
                    .ifPresent(ExecutionContext::stop);
        }

        private Logger createLogger(String contextId, String nodeId) {
            ClusterLogger logger = new ClusterLogger();
            logger.setParent(new Slf4jLogger("rule.engine.debug.".concat(nodeId)));
            logger.setNodeId(nodeId);
            logger.setInstanceId(contextId);
            logger.setLogInfoConsumer(logInfo -> {

                Map<String, Object> data = new HashMap<>();
                data.put("level", logInfo.getLevel());
                data.put("message", logInfo.getMessage());

                writeMessage(DebugMessage.of("log", contextId, data))
                        .subscribe();

            });
            return logger;
        }

        private DebugExecutionContext createContext(RuleNodeConfiguration configuration) {
            lastOperationTime = System.currentTimeMillis();
            String id = Optional.ofNullable(configuration.getId()).orElseGet(IDGenerator.MD5::generate);
            DebugExecutionContext context = contexts.get(id);
            if (context != null) {
                context.stop();
                contexts.remove(id);
            }

            context = new DebugExecutionContext(id, createLogger(id, configuration.getNodeId()), this);
            context.local = true;
            contexts.put(id, context);
            return context;
        }

        private DebugExecutionContext getContext(String id) {
            lastOperationTime = System.currentTimeMillis();

            return contexts.computeIfAbsent(id, _id -> new DebugExecutionContext(id, new Slf4jLogger("rule.engine.debug.none"), this));
        }

//        private String startRule(String format, String data) {
//            RuleModel model = modelParser.parse(format, data);
//            Rule rule = new Rule();
//            rule.setId(IDGenerator.UUID.generate());
//            rule.setVersion(1);
//            rule.setModel(model);
//
//            Optional.of(model.getId())
//                    .map(instanceContextMapping::get)
//                    .map(instanceContext::get)
//                    .ifPresent(RuleInstanceContext::stop);
//
//            RuleInstanceContext context = engine.startRule(rule);
//            instanceContext.put(context.getId(), context);
//            instanceContextMapping.put(model.getId(), context.getId());
//
//            return context.getId();
//
//        }

        private void execute(RuleData ruleData) {
            String instanceId = ruleData.getAttribute("instanceId").map(String::valueOf).orElse(null);

            RuleInstanceContext context = instanceContext.get(instanceId);
            if (context != null) {
                doExecute(context, ruleData);
            }

        }

        private void doExecute(RuleInstanceContext context, RuleData ruleData) {

            context.execute(Mono.just(ruleData))
                    .doOnError((throwable) -> {
                        writeMessage(DebugMessage.of("error", context.getId(), "执行规则失败:" + StringUtils.throwable2String(throwable)));
                    })
                    .subscribe(resp -> {
                        writeMessage(DebugMessage.of("output", context.getId(), resp.getData()));
                    });

        }

        private void execute(ExecuteRuleRequest request) {

            RuleInstanceContext context = instanceContext.get(request.getContextId());
            if (context == null) {
                executeRuleTopic.publish(Mono.just(request)).subscribe();
                return;
            }
            RuleData ruleData = RuleData.create(request.getData());
            RuleDataHelper.markStartWith(ruleData, request.getStartWith());
            RuleDataHelper.markSyncReturn(ruleData, request.getEndWith());
            ruleData.setAttribute("debugSessionId", id);
            ruleData.setAttribute("instanceId", request.getContextId());

            doExecute(context, ruleData);
        }

        private Mono<Boolean> writeMessage(DebugMessage message) {
            lastOperationTime = System.currentTimeMillis();
            return messageQueue.add(Mono.just(message));
        }


        @SneakyThrows
        public Flux<DebugMessage> consumeOutPut() {

            return messageQueue
                    .subscribe()
                    .map(Function.identity());
        }

        public void close() {

            contexts.forEach((s, context) -> context.stop());
            instanceContext.values().forEach(RuleInstanceContext::stop);
            instanceContext.clear();
            instanceContextMapping.clear();
        }

    }

    private class DebugExecutionContext implements ExecutionContext {

        private Session session;

        private String id;

        private ClusterQueue<RuleData> inputQueue;

        private Logger logger;

        private List<Runnable> stopListener = new CopyOnWriteArrayList<>();

        private long lastOperationTime = System.currentTimeMillis();

        @Getter
        private boolean local = false;

        public DebugExecutionContext(String id, Logger logger, Session session) {
            this.session = session;
            this.logger = logger;
            this.id = id;
            this.inputQueue = clusterManager.getQueue("rule-engine-debug:input:".concat(session.id).concat(":").concat(id));
            inputQueue.setLocalConsumerPercent(1F);
        }

        public boolean isTimeout() {
            return System.currentTimeMillis() - lastOperationTime > TimeUnit.MINUTES.toMillis(15);
        }

        @Override
        public String getInstanceId() {
            return id;
        }

        @Override
        public String getNodeId() {
            return id;
        }

        @Override
        public Logger logger() {
            return logger;
        }

        public void execute(RuleData ruleData) {
            lastOperationTime = System.currentTimeMillis();

            ruleData.setAttribute("debug", true);
            inputQueue.add(Mono.just(ruleData))
                    .doOnError(((throwable) -> logger.error("执行规则失败", throwable)))
                    .subscribe();
        }

        @Override
        public Input getInput() {

            return new Input() {
                @Override
                public Flux<RuleData> subscribe() {
                    return inputQueue.subscribe()
                            .doOnNext(data -> {
                                log.debug("handle input :{}", data);
                            })
                            .doFinally(s -> {
                                log.debug("unsubscribe input:{}", id);
                            });
                }

                @Override
                public void close() {
                    inputQueue.stop();
                }
            };
        }

        @Override
        public Output getOutput() {
            return (data) -> Flux.from(data)
                    .flatMap(d -> session.writeMessage(DebugMessage.of("output", id, JSON.toJSONString(d.getData(), SerializerFeature.PrettyFormat))))
                    .then(Mono.just(true));
        }

        @Override
        public Mono<Void> fireEvent(String event, RuleData data) {
            //data = data.copy();
            //logger.info("event:{} data: {}",event,data.getData());
            return Mono.empty();
        }

        @Override
        public Mono<Void> onError(RuleData data, Throwable e) {
            //  data = data.copy();

            // RuleDataHelper.putError(data, e);
            return session
                    .writeMessage(DebugMessage.of("error", id, StringUtils.throwable2String(e)))
                    .then();
        }

        @Override
        public void stop() {
            stopListener.forEach(Runnable::run);
           // this.inputQueue.stop();
        }

        @Override
        public void onStop(Runnable runnable) {
            stopListener.add(runnable);
        }
    }


}
