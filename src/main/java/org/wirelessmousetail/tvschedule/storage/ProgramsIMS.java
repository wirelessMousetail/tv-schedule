package org.wirelessmousetail.tvschedule.storage;

import org.wirelessmousetail.tvschedule.api.Program;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * In memory storage for the programs
 */
public class ProgramsIMS { //todo rename?
    private final ConcurrentHashMap<Long, Program> programs = new ConcurrentHashMap<>();
    private final AtomicLong counter = new AtomicLong();

    //todo rework it:
    // - add builder from TvMazeClient to Program
    // - it should not be in this class or in TvMazeLoader
    // - id should be added here
    // - all strings should be normalized
    public void add(String name, String channel, LocalDate airDate, LocalTime startTime, LocalDateTime endTime) {
        Program program = new Program(counter.incrementAndGet(), name, channel, airDate, startTime, endTime);
        programs.put(program.getId(), program);
    }

    //todo should be sorted in air date/time order
    //todo REWORK SEACH ALGORITHM!!!!
    public List<Program> getPrograms(LocalDate date, String keywords) {
        List<Program> result = new ArrayList<>(programs.values());
        if (date != null) {
            result = result.stream().filter(program -> program.getDate().equals(date)).collect(Collectors.toList());
        }
        if (keywords != null) {
            List<String> words = Arrays.asList(keywords.toLowerCase().split("\\s+"));
            result = result.stream()
                    .filter(program ->
                            Arrays.asList(program.getName().toLowerCase().split("\\s+")).containsAll(words))
                    .collect(Collectors.toList());

        }
        return result;
    }
}
