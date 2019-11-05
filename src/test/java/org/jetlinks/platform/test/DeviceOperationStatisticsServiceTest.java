package org.jetlinks.platform.test;

import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.jetlinks.platform.manager.enums.TimeUnit;
import org.jetlinks.platform.manager.statistics.DeviceOperationStatisticsService;
import org.jetlinks.platform.manager.statistics.request.DateAggregationParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/**
 * @author bsetfeng
 * @since 1.0
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class DeviceOperationStatisticsServiceTest {

    @Autowired
    private DeviceOperationStatisticsService statisticsService;

    @Test
    public void devicePropertyChangeTest(){
        DateAggregationParam aggregationParam = DateAggregationParam.builder()
                .subAggregation(AggregationBuilders.avg("avgResult").field("value"))
                .field("@timestamp")
                .format("HH:mm")
                .interval(DateHistogramInterval.hours(1))
                .name("results")
                .subAggregationName("avgResult")
                .build();
        String deviceId = "test0";
        String property = "cpuUsage";
        statisticsService.devicePropertyChange(aggregationParam,deviceId,property,0, TimeUnit.day,new Date());
    }
}
