package org.jetlinks.platform.manager.statistics.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;

/**
 * @author bsetfeng
 * @since 1.0
 **/
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DateAggregationParam {

    //聚合结果显示的时间格式
    private String format;

    //时间聚合区间
    private DateHistogramInterval interval;

    //聚合名称
    private String name;

    //聚合字段名称
    private String field;
    /**
     * 子聚合
     *
     * @see AggregationBuilders
     */
    private AggregationBuilder subAggregation;

    //子聚合名称
    private String subAggregationName;

}
