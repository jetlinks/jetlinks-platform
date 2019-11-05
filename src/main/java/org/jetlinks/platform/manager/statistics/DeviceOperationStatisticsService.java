package org.jetlinks.platform.manager.statistics;

import org.hswebframework.ezorm.core.param.QueryParam;
import org.jetlinks.platform.manager.enums.DeviceState;
import org.jetlinks.platform.manager.enums.TimeUnit;
import org.jetlinks.platform.manager.statistics.request.DateAggregationParam;

import java.util.Date;

/**
 * @version 1.0
 **/
public interface DeviceOperationStatisticsService {


    /**
     * 单个设备属性值变化统计
     * @param deviceId 设备Id
     * @param property 属性
     * @return 周期内属性值变化的对象
     */
    Object devicePropertyChange(
            DateAggregationParam aggregationParam,
            String deviceId,
            String property,
            int interval,
            TimeUnit timeUnit,
            Date endWith
    );

    /**
     * 计算单个设备功能调用次数
     *
     * @return
     */
    int countDeviceFunctionCall(String deviceId, String function);

    /**
     * 统计时间周期类单个设备功能调用次数
     *
     * @return
     */
    Object countDeviceFunctionCallByPeriod(
            String deviceId,
            String function,
            int interval,
            TimeUnit timeUnit,
            Date endWith
    );



    /**
     * 系统设备不同状态的数量
     *
     * @return
     * @see DeviceState
     */
    /**
     * 获取设备不同状态的数量
     *
     * @param state 设备状态
     * @return 设备数量
     */
    int getDeviceCountByState(DeviceState state);

    /**
     * 获取已注册设备总量
     *
     * @return 设备总量
     */
    int getDeviceTotal();

    /**
     * 根据时间周期统计所有不同状态的设备数量
     *
     * @return echars 结构对象
     */
    Object countDeviceState(
            int period,
            int interval,
            TimeUnit timeUnit,
            Date endWith
    );

    /**
     * 计算属性值异常数量
     *
     * @param productId      产品ID
     * @param property       属性
     * @param abnormalBorder 属性异常值边界
     * @param compareRule    异常比对规则
     * @return 异常数量
     */
    int countProperyValueAbnormal(String productId, String property, Object abnormalBorder, Object compareRule);

    /**
     * * 设备属性范围统计
     * ps：七个一天内所有网关cpu使用率超过80%的设备数量
     * ps：六个一个月内所有网关温度在35℃-45℃内的数量
     *
     * @param productId 产品ID
     * @param property  属性
     * @param period    周期
     * @param interval  时间跨度个数
     * @param timeUnit  时间单位
     * @param endWith   统计的时间终点
     * @param range     属性值范围对象
     * @return 符合属性范围对象
     */
    Object countDevicePropertyValueRange(
            String productId,
            String property,
            int period,
            int interval,
            TimeUnit timeUnit,
            Date endWith,
            Object range
    );
}
