package org.jetlinks.platform.manager.elasticsearch;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.hswebframework.easyorm.elasticsearch.enums.LinkTypeEnum;
import org.hswebframework.ezorm.core.param.QueryParam;
import org.jetlinks.platform.manager.enums.EsDataType;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * @author bsetfeng
 * @since 1.0
 **/
@Slf4j
public class AggregationBuilder {

    public static class Builder {

    }

    public AggregationBuilder.Builder addDateHistogram(DateHistogramAggregationBuilder aggregationBuilder) {
        return null;
    }
}
