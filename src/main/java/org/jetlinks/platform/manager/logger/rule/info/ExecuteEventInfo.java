package org.jetlinks.platform.manager.logger.rule.info;

import lombok.Getter;
import lombok.Setter;

/**
 * @author bsetfeng
 * @since 1.0
 **/
@Getter
@Setter
public class ExecuteEventInfo {

    private String event;

    private long createTime = System.currentTimeMillis();

    private String instanceId;

    private String nodeId;

    private String ruleData;
}
