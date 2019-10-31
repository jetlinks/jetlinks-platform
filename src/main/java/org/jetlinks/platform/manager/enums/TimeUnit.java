package org.jetlinks.platform.manager.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetlinks.platform.manager.statistics.TimePeriod;

import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * @author bsetfeng
 * @since 1.0
 **/
@AllArgsConstructor
@Getter
public enum TimeUnit {

    day {
        @Override
        public TimePeriod getTimePeriodPoint(int interval, Date date, TimeAbout timeAbout, String format) {
            return timeAbout.getTimePeriodPoint(interval, date, ChronoUnit.DAYS, format);
        }
    }, week {
        @Override
        public TimePeriod getTimePeriodPoint(int interval, Date date, TimeAbout timeAbout, String format) {
            return timeAbout.getTimePeriodPoint(interval, date, ChronoUnit.WEEKS, format);
        }
    }, month {
        @Override
        public TimePeriod getTimePeriodPoint(int interval, Date date, TimeAbout timeAbout, String format) {
            return timeAbout.getTimePeriodPoint(interval, date, ChronoUnit.MONTHS, format);
        }
    }, all {
        @Override
        public TimePeriod getTimePeriodPoint(int interval, Date date, TimeAbout timeAbout, String format) {
            return TimePeriod.builder()
                    .afterTimeMilli(System.currentTimeMillis())
                    .beforeTimeMilli(0)
                    .build();
        }
    };

    abstract public TimePeriod getTimePeriodPoint(int interval, Date date, TimeAbout timeAbout, String format);
}
