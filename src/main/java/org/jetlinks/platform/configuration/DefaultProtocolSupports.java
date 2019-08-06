package org.jetlinks.platform.configuration;

import org.jetlinks.core.ProtocolSupport;
import org.jetlinks.core.ProtocolSupports;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DefaultProtocolSupports implements ProtocolSupports, BeanPostProcessor {

    private Map<String, ProtocolSupport> supports = new ConcurrentHashMap<>();

    @Override
    public ProtocolSupport getProtocol(String protocol) {
        ProtocolSupport support = supports.get(protocol);
        if (support == null) {
            throw new UnsupportedOperationException("不支持的协议:" + protocol);
        }
        return support;
    }

    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
        return o;
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        if (o instanceof ProtocolSupport) {
            supports.put(((ProtocolSupport) o).getId(), ((ProtocolSupport) o));
        }
        return o;
    }
}
