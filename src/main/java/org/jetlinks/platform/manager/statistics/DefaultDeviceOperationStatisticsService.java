package org.jetlinks.platform.manager.statistics;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.hswebframework.ezorm.core.param.QueryParam;
import org.hswebframework.ezorm.core.param.Term;
import org.hswebframework.web.api.crud.entity.QueryParamEntity;
import org.jetlinks.platform.manager.elasticsearch.ElasticRestClient;
import org.jetlinks.platform.manager.elasticsearch.QueryParamTranslator;
import org.jetlinks.platform.manager.enums.DeviceState;
import org.jetlinks.platform.manager.enums.EsDataType;
import org.jetlinks.platform.manager.enums.TimeAbout;
import org.jetlinks.platform.manager.enums.TimeUnit;
import org.jetlinks.platform.manager.statistics.request.DateAggregationParam;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;

/**
 * @author bsetfeng
 * @since 1.0
 **/
@Service
@Slf4j
public class DefaultDeviceOperationStatisticsService implements DeviceOperationStatisticsService {

    @Autowired
    private ElasticRestClient restClient;

    @Override
    public Object devicePropertyChange(
            DateAggregationParam aggregationParam,
            String deviceId,
            String property,
            int interval,
            TimeUnit timeUnit,
            Date endWith
    ) {
        TimePeriod timePeriod = timeUnit.getTimePeriodPoint(interval, endWith, TimeAbout.minus,"");

        QueryParam queryParam = QueryParamEntity.newQuery()
                .where("deviceId", deviceId)
                .and("property.keyword", property).getParam();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        DateHistogramAggregationBuilder aggregation = AggregationBuilders.dateHistogram(aggregationParam.getName())
                .field(aggregationParam.getField())
                .dateHistogramInterval(aggregationParam.getInterval())
                .timeZone(DateTimeZone.getDefault())
                .format(aggregationParam.getFormat());
        aggregation.subAggregation(aggregationParam.getSubAggregation());
        searchSourceBuilder.aggregation(aggregation);
        searchSourceBuilder.query(QueryParamTranslator.translate(queryParam));
        SearchRequest request = new SearchRequest("device_info-2019-10-30")
                .types("doc")
                .source(searchSourceBuilder);
        log.debug("查询参数：{}", searchSourceBuilder.toString());
        try {
            SearchResponse response = restClient.getClient().search(request, RequestOptions.DEFAULT);
            System.out.println(response.getHits().getTotalHits());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int countDeviceFunctionCall(String deviceId, String function) {
        return 0;
    }

    @Override
    public Object countDeviceFunctionCallByPeriod(String deviceId, String function, int interval, TimeUnit timeUnit, Date endWith) {
        return null;
    }

    @Override
    public int getDeviceCountByState(DeviceState state) {
        return 0;
    }

    @Override
    public int getDeviceTotal() {
        return 0;
    }

    @Override
    public Object countDeviceState(int period, int interval, TimeUnit timeUnit, Date endWith) {
        return null;
    }

    @Override
    public int countProperyValueAbnormal(String productId, String property, Object abnormalBorder, Object compareRule) {
        return 0;
    }

    @Override
    public Object countDevicePropertyValueRange(String productId, String property, int period, int interval, TimeUnit timeUnit, Date endWith, Object range) {
        return null;
    }
}
