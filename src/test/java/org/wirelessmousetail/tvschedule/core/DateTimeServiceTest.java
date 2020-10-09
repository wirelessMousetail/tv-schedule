package org.wirelessmousetail.tvschedule.core;

import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.Test;

import java.time.*;

public class DateTimeServiceTest {

    public static final ZoneOffset DEFAULT_ZONE = ZoneOffset.UTC;
    public static final LocalDate EXPECTED_DATE = LocalDate.of(2020, 10, 12);

    @Test
    public void getNextWeekStartNowMonday() {
        Instant monday = ZonedDateTime.of(2020, 10, 5, 0, 0, 0, 0, DEFAULT_ZONE).toInstant();
        DateTimeService service = new DateTimeService(Clock.fixed(monday, DEFAULT_ZONE));

        LocalDate result = service.getNextWeekStart();

        MatcherAssert.assertThat(result, IsEqual.equalTo(EXPECTED_DATE));
    }

    @Test
    public void getNextWeekStartNowSunday() {
        Instant monday = ZonedDateTime.of(2020, 10, 11, 0, 0, 0, 0, DEFAULT_ZONE).toInstant();
        DateTimeService service = new DateTimeService(Clock.fixed(monday, DEFAULT_ZONE));

        LocalDate result = service.getNextWeekStart();

        MatcherAssert.assertThat(result, IsEqual.equalTo(EXPECTED_DATE));
    }
}