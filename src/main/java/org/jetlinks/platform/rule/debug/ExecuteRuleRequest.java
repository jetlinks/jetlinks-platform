package org.jetlinks.platform.rule.debug;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExecuteRuleRequest {
    private String sessionId;

    private String contextId;

    private String startWith;

    private String endWith;

    private Object data;
}
