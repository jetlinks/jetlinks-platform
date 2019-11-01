package org.jetlinks.platform.manager.utils;

import org.jetlinks.platform.manager.entity.ElasticSearchIndexEntity;

/**
 * 创建设备事件index
 *
 * @author bsetfeng
 * @since 1.0
 **/
public class GenerateDeviceEventIndex {

    public static ElasticSearchIndexEntity generateIndex(String productId, String eventId) {
        return ElasticSearchIndexEntity.builder()
                .index("event_".concat(productId).concat("_").concat(eventId))
                .type("doc")
                .build();
    }
}
