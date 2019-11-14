package org.jetlinks.platform.manager.elasticsearch.index;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * @version 1.0
 **/
@AllArgsConstructor
@Getter
public class  DeviceEventIndexProvider implements ElasticIndexProvider {

    private String productId;

    private String eventId;

    @Override
    public String getIndex() {
        return "event_".concat(getProductId()).concat("_").concat(getEventId());
    }

    @Override
    public String getType() {
        return "doc";
    }
}
