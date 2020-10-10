package org.wirelessmousetail.tvschedule.core;

import org.hamcrest.core.IsEqual;
import org.junit.Test;

import java.time.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class DateTimeServiceTest {

    public static final ZoneOffset DEFAULT_ZONE = ZoneOffset.UTC;
    public static final LocalDate EXPECTED_DATE = LocalDate.of(2020, 10, 12);

    @Test
    public void getNextWeekStartNowMonday() {
        Instant monday = ZonedDateTime.of(2020, 10, 5, 0, 0, 0, 0, DEFAULT_ZONE).toInstant();
        DateTimeService service = new DateTimeService(Clock.fixed(monday, DEFAULT_ZONE));

        LocalDate result = service.getNextWeekStart();

        assertThat(result, IsEqual.equalTo(EXPECTED_DATE));
    }

    @Test
    public void getNextWeekStartNowSunday() {
        Instant sunday = ZonedDateTime.of(2020, 10, 11, 0, 0, 0, 0, DEFAULT_ZONE).toInstant();
        DateTimeService service = new DateTimeService(Clock.fixed(sunday, DEFAULT_ZONE));

        LocalDate result = service.getNextWeekStart();

        assertThat(result, IsEqual.equalTo(EXPECTED_DATE));
    }

    @Test
    public void onNextWeekBefore() {
        Instant monday = ZonedDateTime.of(2020, 10, 5, 0, 0, 0, 0, DEFAULT_ZONE).toInstant();
        DateTimeService service = new DateTimeService(Clock.fixed(monday, DEFAULT_ZONE));

        boolean result = service.onNextWeek(LocalDate.of(2020, 10, 11));

        assertFalse(result);
    }

    @Test
    public void onNextWeekAfter() {
        Instant monday = ZonedDateTime.of(2020, 10, 5, 0, 0, 0, 0, DEFAULT_ZONE).toInstant();
        DateTimeService service = new DateTimeService(Clock.fixed(monday, DEFAULT_ZONE));

        boolean result = service.onNextWeek(LocalDate.of(2020, 10, 19));

        assertFalse(result);
    }

    @Test
    public void onNextWeekMonday() {
        Instant monday = ZonedDateTime.of(2020, 10, 5, 0, 0, 0, 0, DEFAULT_ZONE).toInstant();
        DateTimeService service = new DateTimeService(Clock.fixed(monday, DEFAULT_ZONE));

        boolean result = service.onNextWeek(LocalDate.of(2020, 10, 12));

        assertTrue(result);
    }

    @Test
    public void onNextWeekSunday() {
        Instant monday = ZonedDateTime.of(2020, 10, 5, 0, 0, 0, 0, DEFAULT_ZONE).toInstant();
        DateTimeService service = new DateTimeService(Clock.fixed(monday, DEFAULT_ZONE));

        boolean result = service.onNextWeek(LocalDate.of(2020, 10, 18));

        assertTrue(result);
    }
}