package org.wirelessmousetail.tvschedule.dao;

import org.wirelessmousetail.tvschedule.api.Program;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

import static org.wirelessmousetail.tvschedule.core.DateTimeService.DAYS_IN_WEEK;

/**
 * Implements an index for the ProgramsDao, which separates shows by days and sorts them by air time.
 * Contain data for the next week only and will fail with exception, if other date is passed as parameter or in
 * {@link Program} entity. New index should be built during week change.
 */
class DateIndex {
    /**
     * Array with date indexes, where each element represents one day on the next week.
     */
    private final DateIndexEntry[] dateIndex;
    private final LocalDate weekStart;

    DateIndex(LocalDate weekStart) {
        this.weekStart = weekStart;
        this.dateIndex = Stream.generate(DateIndexEntry::new).limit(DAYS_IN_WEEK).toArray(DateIndexEntry[]::new);
    }

    /**
     * Inserts new program into the index.
     * @param program program to insert
     * @exception IllegalArgumentException if air date is not on the next week
     */
    void insert(Program program) {
        int dayOfTheWeek = calculateDayIndex(program.getDate());
        dateIndex[dayOfTheWeek].insert(program);
    }

    /**
     * Removes a program from the index.
     * @param program program to remove
     * @exception IllegalArgumentException if air date is not on the next week
     */
    void remove(Program program) {
        int dayOfTheWeek = calculateDayIndex(program.getDate());
        dateIndex[dayOfTheWeek].remove(program);
    }

    /**
     * Returns ordered stream of a programs for the given date, or for the whole next week if date is null.
     * @param date date for which programs should be selected. Could be null.
     * @return stream of {@link Program} entities
     * @exception IllegalArgumentException if date is not on the next week
     */
    Stream<Program> stream(@Nullable LocalDate date) {
        if (date == null) {
            return Arrays.stream(dateIndex).flatMap(DateIndexEntry::stream);
        } else {
            int dayOfTheWeek = calculateDayIndex(date);
            return dateIndex[dayOfTheWeek].stream();
        }
    }

    /**
     * Checks if this index is built for the week, started with the given date.<br/>
     * <b>Monday is considered as a week start for all locales</b>
     * @param weekStart date of the week start
     * @return true, if this index is belong to the week started with the given date, and false - if not
     */
    boolean belongsToWeek(LocalDate weekStart) {
        return weekStart.equals(this.weekStart);
    }

    /**
     * Updates a program in index. Removes old version from the index and inserts the new one.
     * <b>This method is not thread safe and requires external synchronization!</b>
     * @param oldValue old version of a program
     * @param newValue new version of a program
     * @exception IllegalArgumentException if ids of the arguments are not the same
     * @exception IllegalArgumentException if air date of any argument is not on the next week
     */
    void update(Program oldValue, Program newValue) {
        if (!Objects.equals(oldValue.getId(), newValue.getId())) {
            throw new IllegalArgumentException(
                    String.format("Both values should have the same id, but they're differ: oldId=%d, newId=%d",
                            oldValue.getId(), newValue.getId()));
        }

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
