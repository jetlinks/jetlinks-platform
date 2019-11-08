package org.jetlinks.platform.manager.web.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hswebframework.web.bean.FastBeanCopier;
import org.jetlinks.core.message.codec.MqttMessage;
import org.jetlinks.core.message.codec.SimpleMqttMessage;
import org.jetlinks.gateway.vertx.mqtt.VertxMqttMessage;
import org.jetlinks.rule.engine.executor.node.mqtt.PayloadType;

/**
 * @author bsetfeng
 * @since 1.0
 **/
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class MqttMessageRequest {

    private String topic;

    private String deviceId;

    private int qosLevel;

    private Object data;

    private int messageId;

    private boolean will;

    private boolean dup;

    private boolean retain;

    public static MqttMessage of(MqttMessageRequest request, PayloadType type) {
        SimpleMqttMessage message = FastBeanCopier.copy(request, new SimpleMqttMessage());
        message.setPayload(type.write(request.getData()));
        return message;
    }
}
