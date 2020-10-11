package org.wirelessmousetail.tvschedule.core;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * This service provides functionality to calculate next week start and check if date is fit to next week.
 */
public class DateTimeService {
    private Clock clock;
    public static final int DAYS_IN_WEEK = 7;

    public DateTimeService(Clock clock) {
        this.clock = clock;
    }

    public DateTimeService() {
        this.clock = Clock.system(ZoneId.of("Europe/London"));
    }

    /**
     * Will return the date of the next monday
     * @return
     */
    public LocalDate getNextWeekStart() {
        return LocalDate.now(clock).plusWeeks(1).with(DayOfWeek.MONDAY);
    }

    /**
     * Checks if given date is on the next week
     * @param date
     * @return true, if date is on the next week, and false - if not
     */
    public boolean onNextWeek(LocalDate date) {
        LocalDate nextWeekStart = getNextWeekStart();
        return !nextWeekStart.isAfter(date) && nextWeekStart.plusDays(DAYS_IN_WEEK).isAfter(date);
    }
}
