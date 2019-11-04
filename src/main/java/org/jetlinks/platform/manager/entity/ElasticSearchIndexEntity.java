package org.jetlinks.platform.manager.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author bsetfeng
 * @since 1.0
 **/
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ElasticSearchIndexEntity {

    private String index;

    private String type;
}
