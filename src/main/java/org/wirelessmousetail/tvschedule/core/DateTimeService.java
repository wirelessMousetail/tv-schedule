package org.wirelessmousetail.tvschedule.core;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;

public class DateTimeService {
    private Clock clock;

    public DateTimeService(Clock clock) {
        this.clock = clock;
    }

    public DateTimeService() {
        this.clock = Clock.system(ZoneId.of("Europe/London"));
    }

    public LocalDate getNextWeekStart() {
        return LocalDate.now(clock).plusWeeks(1).with(DayOfWeek.MONDAY);
    }

    public boolean onNextWeek(LocalDate date) {
        LocalDate nextWeekStart = getNextWeekStart();
        return !nextWeekStart.isAfter(date) && nextWeekStart.plusDays(7).isAfter(date);
    }
}
