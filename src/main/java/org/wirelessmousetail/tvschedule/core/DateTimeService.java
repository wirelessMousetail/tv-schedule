package org.wirelessmousetail.tvschedule.core;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class DateTimeService {
    private Clock clock;

    private static final DayOfWeek FIRST_DAY_OF_WEEK = WeekFields.of(Locale.UK).getFirstDayOfWeek(); //todo locale to properties

    public DateTimeService(Clock clock) {
        this.clock = clock;
    }

    public DateTimeService() {
        this.clock = Clock.systemDefaultZone(); //todo set timezone through config?
    }

    public LocalDate getNextWeekStart() {
        return LocalDate.now(clock).plusWeeks(1).with(FIRST_DAY_OF_WEEK);
    }
}
