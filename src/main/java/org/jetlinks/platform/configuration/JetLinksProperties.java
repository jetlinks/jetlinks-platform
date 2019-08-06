package org.jetlinks.platform.configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetlinks.core.message.codec.Transport;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.util.Map;

@ConfigurationProperties(prefix = "jetlinks")
@Getter
@Setter
public class JetLinksProperties {

    private String serverId;

    private Map<Transport, Long> transportLimit;

    @PostConstruct
    @SneakyThrows
    public void init() {
        if (serverId == null) {
            serverId = InetAddress.getLocalHost().getHostName();
        }
    }
}
