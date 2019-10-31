package org.jetlinks.platform.manager.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetlinks.platform.manager.statistics.TimePeriod;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Getter
@AllArgsConstructor
public enum TimeAbout {
    plus {
        @Override
        public TimePeriod getTimePeriodPoint(int interval, Date date, ChronoUnit chronoUnit, String format) {
            if (date == null) {
                date = new Date();
            }
            TimePeriod TimeCycle = new TimePeriod();
            LocalDateTime dateTime = transDayMax(date);
            TimeCycle.setAfterTimeMilli(dateTime.plus(interval, chronoUnit)
                    .toInstant(ZoneOffset.of("+8"))
                    .toEpochMilli());
            TimeCycle.setBeforeTimeMilli(dateTime.toInstant(ZoneOffset.of("+8"))
                    .toEpochMilli());
            return TimeCycle;
        }
    }, minus {
        @Override
        public TimePeriod getTimePeriodPoint(int interval, Date date, ChronoUnit chronoUnit, String format) {
            if (date == null) {
                date = new Date();
            }
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss");
            LocalDateTime dateTime = transDayMax(date);
            TimePeriod TimeCycle = new TimePeriod();
            TimeCycle.setBeforeTimeMilli(dateTime.minus(interval, chronoUnit)
                    .toInstant(ZoneOffset.of("+8"))
                    .toEpochMilli());
            TimeCycle.setAfterTimeStr(dateTime.format(dtf));
            TimeCycle.setAfterTimeMilli(dateTime.toInstant(ZoneOffset.of("+8"))
                    .toEpochMilli());
            return TimeCycle;
        }
    };

    abstract public TimePeriod getTimePeriodPoint(int interval, Date date, ChronoUnit chronoUnit, String format);

    public LocalDateTime transDayMax(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        return LocalDateTime.of(instant.atZone(zoneId).toLocalDate(), LocalTime.MAX);//
    }
}
