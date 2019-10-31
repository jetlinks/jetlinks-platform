package org.jetlinks.platform.manager.statistics;

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
public class TimePeriod {

    private long beforeTimeMilli;

    private String beforeTimeStr;

    private long afterTimeMilli;

    private String afterTimeStr;

}
