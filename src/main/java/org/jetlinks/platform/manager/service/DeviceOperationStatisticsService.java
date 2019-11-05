package org.jetlinks.platform.manager.service;

import lombok.extern.slf4j.Slf4j;
import org.jetlinks.platform.manager.elasticsearch.ElasticRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author bsetfeng
 * @since 1.0
 **/
@Service
@Slf4j
public class DeviceOperationStatisticsService {


    @Autowired
    private ElasticRestClient restClient;


    /**
     * 系统当前设备在线量
     *
     * @return
     */
    public int deviceOnlineCount() {
        return 0;
    }

    /**
     * 系统已注册设备总量
     *
     * @return
     */
    public int deviceTotal() {
        return 0;
    }

    /**
     * 系统当前用户在线量
     *
     * @return
     */
    public int userOnlineCount() {
        return 0;
    }

    /**
     * 系统用户总数
     *
     * @return
     */
    public int userTotal() {
        return 0;
    }


    /**
     * 时间维度上面的设备上下线量统计
     *
     * @return echars 结构对象
     */
    public Object deviceOnOffline() {
        return null;
    }

    /**
     * 属性值异常数量
     *
     * @return
     */
    public int properyValueAbnormalCount() {
        return 0;
    }

    /**
     * 时间维度上面的设备属性范围统计
     * ps：七个一天内所有网关cpu使用率超过80%的设备数量
     * ps：六个一个月内所有网关温度在35℃-45℃内的数量
     *
     * @return
     */
    public Object devicePropertyRange() {
        return null;
    }

    /**
     * 单个设备属性值变化统计
     *
     * @return
     */
    public Object deviceProertyChange() {
        return null;
    }

    /**
     * 设备功能调用次数
     *
     * @return
     */
    public int deviceFuncCallCount() {
        return 0;
    }

    /**
     * 时间周期内所有设备功能调用次数统计
     * @return
     */
    public Object devieFuncCall(){
        return null;
    }

}
