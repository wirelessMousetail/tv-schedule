package org.wirelessmousetail.tvschedule.dao;

import org.wirelessmousetail.tvschedule.api.Program;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.stream.Stream;

class DateIndex {
    private final DateIndexEntry[] dateIndex;
    private final LocalDate weekStart;

    DateIndex(LocalDate weekStart) {
        this.weekStart = weekStart;
        this.dateIndex = Stream.generate(DateIndexEntry::new).limit(7).toArray(DateIndexEntry[]::new);
    }


    void insert(Program program) {
        int dayOfTheWeek = calculateDayIndex(program.getDate());
        dateIndex[dayOfTheWeek].insert(program);
    }

    void remove(Program program) {
        int dayOfTheWeek = calculateDayIndex(program.getDate());
        dateIndex[dayOfTheWeek].remove(program);
    }

    Stream<Program> stream(@Nullable LocalDate date) {
        if (date == null) {
            return Arrays.stream(dateIndex).flatMap(DateIndexEntry::stream);
        } else {
            int dayOfTheWeek = calculateDayIndex(date);
            return dateIndex[dayOfTheWeek].stream();
        }
    }

    /**
     * <b>This method is not thread safe and requires external synchronization!</b>
     * @param oldValue old value
     * @param newValue new value
     */
    void update(Program oldValue, Program newValue) {
        int oldValueDay = calculateDayIndex(oldValue.getDate());
        int newValueDay = calculateDayIndex(newValue.getDate());
        dateIndex[oldValueDay].remove(oldValue);
        dateIndex[newValueDay].insert(newValue);
    }

    private int calculateDayIndex(LocalDate date) {
        int dayOfTheWeek = Math.toIntExact(ChronoUnit.DAYS.between(weekStart, date));
        if (dayOfTheWeek < 0 || dayOfTheWeek > 6) {
            throw new IllegalArgumentException(String.format("Program air date '%s' is beyond next week", date));
        }
        return dayOfTheWeek;
    }
}
