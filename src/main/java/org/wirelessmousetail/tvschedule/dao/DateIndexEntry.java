package org.wirelessmousetail.tvschedule.dao;

import org.wirelessmousetail.tvschedule.api.Program;

import java.util.Comparator;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Stream;

/**
 * Entry for the date index.
 * Be aware: entry didn't actually check if inserted entry is belong to appropriate date. Moreover, it didn't even
 * know to which date it belongs. This is under {@link DateIndex} class responsibility
 */
class DateIndexEntry {
    private final ConcurrentSkipListSet<Program> thisDayPrograms = new ConcurrentSkipListSet<>(
            Comparator.comparing(Program::getDate).thenComparing(Program::getStartTime).thenComparing(Program::getId)
    );


    void insert(Program program) {
        thisDayPrograms.add(program);
    }

    void remove(Program program) {
        thisDayPrograms.remove(program);
    }

    Stream<Program> stream() {
        return thisDayPrograms.stream();
    }
}
