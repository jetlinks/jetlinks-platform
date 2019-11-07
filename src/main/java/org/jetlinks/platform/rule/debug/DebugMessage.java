package org.jetlinks.platform.rule.debug;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttEndpoint;
import io.vertx.mqtt.MqttServer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import reactor.core.publisher.Flux;

import java.io.Serializable;
import java.time.Duration;
import java.util.Date;


@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class DebugMessage implements Serializable {

    private String type;

    private String contextId;

    private Object message;

    private Date timestamp;

    public static DebugMessage of(String type, String contextId, Object message) {
        return of(type, contextId, message, new Date());
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        MqttServer.create(vertx)
                .endpointHandler(endpoint -> {
                    MqttEndpoint mqttEndpoint = endpoint.accept(false);

                    Flux.interval(Duration.ofSeconds(5))
                            .subscribe(i->{
                                mqttEndpoint.publish("/chiefdata/push/fire_alarm", Buffer.buffer(
                                        ( "{\n" +
                                               "\t\"devid\": \"test\",\n" +
                                               "\t\"pname\": \"TBS-110\",\n" +
                                               "\t\"aid\": 1,\n" +
                                               "\t\"a_name\": \"未来科技城\",\n" +
                                               "\t\"b_name\": \"C2 栋\",\n" +
                                               "\t\"lid\": 5,\n" +
                                               "\t\"l_name\": \"4-5-201\",\n" +
                                               "\t\"time\": \"2018-01-04 16:28:50\",\n" +
                                               "\t\"alarm_type\": 1,\n" +
                                               "\t\"alarm_type_name\": \"火灾报警\",\n" +
                                               "\t\"event_id\": 32,\n" +
                                               "\t\"event_count\": 1,\n" +
                                               "\t\"device_type\": 1,\n" +
                                               "\t\"comm_type\": 2,\n" +
                                               "\t\"first_alarm_time\": \"2019-11-04 16:28:50\",\n" +
                                               "\t\"last_alarm_time\": \"2019-11-04 16:28:50\",\n" +
                                               "\t\"lng\": 22.22,\n" +
                                               "\t\"lat\": 23.23\n" +
                                               "}").getBytes()
                                ), MqttQoS.AT_MOST_ONCE,false,false);
                            });

                })
                .listen(1888);
    }
}
