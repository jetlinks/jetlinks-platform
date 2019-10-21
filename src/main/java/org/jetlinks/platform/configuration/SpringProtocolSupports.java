package org.jetlinks.platform.configuration;

import org.jetlinks.core.ProtocolSupport;
import org.jetlinks.supports.DefaultProtocolSupports;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;


@Component
public class SpringProtocolSupports extends DefaultProtocolSupports implements BeanPostProcessor {


    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        if (o instanceof ProtocolSupport) {
           register(((ProtocolSupport) o));
        }
        return o;
    }
}
