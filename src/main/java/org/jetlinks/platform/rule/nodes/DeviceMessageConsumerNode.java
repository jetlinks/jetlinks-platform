package org.jetlinks.platform.rule.nodes;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetlinks.core.device.DeviceConfigKey;
import org.jetlinks.core.device.DeviceRegistry;
import org.jetlinks.core.message.*;
import org.jetlinks.core.message.event.EventMessage;
import org.jetlinks.core.message.function.FunctionInvokeMessageReply;
import org.jetlinks.core.message.property.ReadPropertyMessageReply;
import org.jetlinks.platform.events.DeviceConnectedEvent;
import org.jetlinks.platform.events.DeviceDisconnectedEvent;
import org.jetlinks.platform.events.DeviceMessageEvent;
import org.jetlinks.rule.engine.api.RuleData;
import org.jetlinks.rule.engine.api.executor.ExecutionContext;
import org.jetlinks.rule.engine.api.model.NodeType;
import org.jetlinks.rule.engine.executor.CommonExecutableRuleNodeFactoryStrategy;
import org.jetlinks.rule.engine.executor.node.RuleNodeConfig;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.Disposable;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
@SuppressWarnings("all")
public class DeviceMessageConsumerNode extends CommonExecutableRuleNodeFactoryStrategy<DeviceMessageConsumerNode.Config> {

    private EmitterProcessor<Map<String, Object>> processor;

    @Autowired
    private DeviceRegistry registry;

    public DeviceMessageConsumerNode() {
        processor = EmitterProcessor.create(false);
    }

    @EventListener
    public void handleDeviceMessage(DeviceDisconnectedEvent event) {
        if (processor.hasDownstreams()) {
            Map<String, Object> msg = new HashMap<>();
            msg.put("type", "offline");
            msg.put("deviceId", event.getSession().getDeviceId());
            msg.put("timestamp",System.currentTimeMillis());

            event.getSession()
                    .getOperator()
                    .getConfig(DeviceConfigKey.productId)
                    .map(r -> {
                        msg.put("productId", r);
                        return msg;
                    }).switchIfEmpty(Mono.just(msg))
                    .subscribe(processor::onNext);
        }

    }

    @EventListener
    public void handleDeviceMessage(DeviceConnectedEvent event) {
        if (processor.hasDownstreams()) {
            Map<String, Object> msg = new HashMap<>();
            msg.put("type", "online");
            msg.put("deviceId", event.getSession().getDeviceId());
            msg.put("timestamp",System.currentTimeMillis());
            event.getSession()
                    .getOperator()
                    .getConfig(DeviceConfigKey.productId)
                    .map(r -> {
                        msg.put("productId", r);
                        return msg;
                    }).switchIfEmpty(Mono.just(msg))
                    .subscribe(processor::onNext);
        }

    }

    @EventListener
    public void handleDeviceMessage(DeviceMessageEvent event) {
        if (processor.hasDownstreams()) {
            convertMessage(event)
                    .onErrorContinue((err, data) -> log.error(err.getMessage(), err))
                    .subscribe(processor::onNext);
        }
    }

    @Override
    public String getSupportType() {
        return "device-message-consumer";
    }

    @Override
    public Function<RuleData, Publisher<Object>> createExecutor(ExecutionContext executionContext, Config config) {
        return Mono::just;
    }

    @Override
    protected void onStarted(ExecutionContext context, Config config) {
        super.onStarted(context, config);

        Disposable disposable = processor
                .filter(map -> StringUtils.isEmpty(config.getProductId()) || config.getProductId().equals(map.get("productId")))
                .map(RuleData::create)
                .flatMap(data -> context.getOutput().write(Mono.just(data)))
                .subscribe();

        context.onStop(disposable::dispose);
    }

    public Mono<Map<String, Object>> convertMessage(DeviceMessageEvent event) {
        Map<String, Object> msg = new HashMap<>();
        Message message = event.getMessage();
        if (message instanceof ChildDeviceMessageReply) {
            message = ((ChildDeviceMessageReply) message).getChildDeviceMessage();
        }
        if (message instanceof ChildDeviceMessage) {
            message = ((ChildDeviceMessage) message).getChildDeviceMessage();
        }

        if (message instanceof EventMessage) {
            msg.put("type", "event");
        } else if (message instanceof ReadPropertyMessageReply) {
            msg.put("type", "read-property");
        } else if (message instanceof FunctionInvokeMessageReply) {
            msg.put("type", "invoke-function");
        } else if (message instanceof DeviceOnlineMessage) {
            msg.put("type", "online");
        } else if (message instanceof DeviceOfflineMessage) {
            msg.put("type", "offline");
        }
        msg.put("message", message);
        if (message instanceof DeviceMessage) {
            String device = ((DeviceMessage) message).getDeviceId();
            return registry
                    .getDevice(device)
                    .flatMap(operator -> operator.getConfig(DeviceConfigKey.productId))
                    .doOnNext(productId -> {
                        msg.put("productId", productId);
                    }).thenReturn(msg);

        }

        return Mono.just(msg);
    }

    @Getter
    @Setter
    public static class Config implements RuleNodeConfig {


        private String productId;

        @Override
        public NodeType getNodeType() {
            return NodeType.PEEK;
        }

        @Override
        public void setNodeType(NodeType nodeType) {

        }
    }
}
