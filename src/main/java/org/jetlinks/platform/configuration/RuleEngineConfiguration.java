package org.jetlinks.platform.configuration;

import org.jetlinks.core.cluster.ClusterManager;
import org.jetlinks.core.device.DeviceRegistry;
import org.jetlinks.core.server.MessageHandler;
import org.jetlinks.rule.engine.api.ConditionEvaluator;
import org.jetlinks.rule.engine.api.RuleEngine;
import org.jetlinks.rule.engine.api.executor.ExecutableRuleNodeFactory;
import org.jetlinks.rule.engine.cluster.DefaultWorkerNodeSelector;
import org.jetlinks.rule.engine.cluster.WorkerNodeSelectorStrategy;
import org.jetlinks.rule.engine.condition.ConditionEvaluatorStrategy;
import org.jetlinks.rule.engine.condition.DefaultConditionEvaluator;
import org.jetlinks.rule.engine.condition.supports.DefaultScriptEvaluator;
import org.jetlinks.rule.engine.condition.supports.ScriptConditionEvaluatorStrategy;
import org.jetlinks.rule.engine.condition.supports.ScriptEvaluator;
import org.jetlinks.rule.engine.executor.DefaultExecutableRuleNodeFactory;
import org.jetlinks.rule.engine.executor.ExecutableRuleNodeFactoryStrategy;
import org.jetlinks.rule.engine.executor.node.device.DeviceOperationNode;
import org.jetlinks.rule.engine.executor.node.mqtt.MqttClientManager;
import org.jetlinks.rule.engine.executor.node.mqtt.MqttConsumerNode;
import org.jetlinks.rule.engine.executor.node.mqtt.MqttProducerNode;
import org.jetlinks.rule.engine.executor.node.timer.TimerNode;
import org.jetlinks.rule.engine.model.DefaultRuleModelParser;
import org.jetlinks.rule.engine.model.RuleModelParserStrategy;
import org.jetlinks.rule.engine.model.antv.AntVG6RuleModelParserStrategy;
import org.jetlinks.rule.engine.standalone.StandaloneRuleEngine;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;

@Configuration
public class RuleEngineConfiguration {


    @Bean
    public DefaultRuleModelParser defaultRuleModelParser() {
        return new DefaultRuleModelParser();
    }

    @Bean
    public DefaultConditionEvaluator defaultConditionEvaluator() {
        return new DefaultConditionEvaluator();
    }

    @Bean
    public DefaultExecutableRuleNodeFactory defaultExecutableRuleNodeFactory() {
        return new DefaultExecutableRuleNodeFactory();
    }

    @Bean
    public DefaultWorkerNodeSelector defaultWorkerNodeSelector() {
        return new DefaultWorkerNodeSelector();
    }

    @Bean
    public TimerNode timerNode(ClusterManager clusterManager) {
        return new TimerNode(clusterManager);
    }

    @Bean
    public AntVG6RuleModelParserStrategy antVG6RuleModelParserStrategy() {
        return new AntVG6RuleModelParserStrategy();
    }

    @Bean
    public BeanPostProcessor autoRegisterStrategy(DefaultRuleModelParser defaultRuleModelParser,
                                                  DefaultConditionEvaluator defaultConditionEvaluator,
                                                  DefaultExecutableRuleNodeFactory ruleNodeFactory,
                                                  DefaultWorkerNodeSelector workerNodeSelector) {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

                return bean;
            }

            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof RuleModelParserStrategy) {
                    defaultRuleModelParser.register(((RuleModelParserStrategy) bean));
                }
                if (bean instanceof ConditionEvaluatorStrategy) {
                    defaultConditionEvaluator.register(((ConditionEvaluatorStrategy) bean));
                }
                if (bean instanceof ExecutableRuleNodeFactoryStrategy) {
                    ruleNodeFactory.registerStrategy(((ExecutableRuleNodeFactoryStrategy) bean));
                }
                if (bean instanceof WorkerNodeSelectorStrategy) {
                    workerNodeSelector.register(((WorkerNodeSelectorStrategy) bean));
                }
                return bean;
            }
        };
    }

    @Bean
    public ScriptEvaluator ruleEngineScriptEvaluator() {
        return new DefaultScriptEvaluator();
    }

    @Bean
    public ScriptConditionEvaluatorStrategy scriptConditionEvaluatorStrategy(ScriptEvaluator scriptEvaluator) {
        return new ScriptConditionEvaluatorStrategy(scriptEvaluator);
    }

    @Bean
    public RuleEngine ruleEngine(ExecutableRuleNodeFactory ruleNodeFactory,
                                 ConditionEvaluator conditionEvaluator,
                                 ExecutorService executorService) {
        StandaloneRuleEngine ruleEngine = new StandaloneRuleEngine();
        ruleEngine.setNodeFactory(ruleNodeFactory);
        ruleEngine.setExecutor(executorService);
        ruleEngine.setEvaluator(conditionEvaluator);
        return ruleEngine;
    }

    @Bean
    public MqttConsumerNode mqttConsumerNode(MqttClientManager clientManager) {
        return new MqttConsumerNode(clientManager);
    }

    @Bean
    public MqttProducerNode mqttProducerNode(MqttClientManager clientManager) {
        return new MqttProducerNode(clientManager);
    }

    @Bean
    public DeviceOperationNode deviceOperationNode(MessageHandler messageHandler, ClusterManager clusterManager, DeviceRegistry registry) {
        return new DeviceOperationNode(messageHandler, clusterManager, registry);
    }


}
