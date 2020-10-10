package org.wirelessmousetail.tvschedule.dao;

import org.wirelessmousetail.tvschedule.api.Program;
import org.wirelessmousetail.tvschedule.core.DateTimeService;
import org.wirelessmousetail.tvschedule.utils.ProgramCreatorUtil;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class ProgramsDao {
    private final DateTimeService dateTimeService;

    private final AtomicLong idGenerator = new AtomicLong();
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock concurrentOpsLock = readWriteLock.readLock();
    private final Lock exclusiveOpsLock = readWriteLock.writeLock();
    private final ConcurrentHashMap<Long, Program> programs = new ConcurrentHashMap<>();
    private final DateIndex dateIndex;

    public ProgramsDao(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
        this.dateIndex = new DateIndex(dateTimeService.getNextWeekStart()); //todo this should be reworked to add week change support
    }

    //todo bulk insert could be implemented to increase initial setup perfomance
    public long add(Program program) {
        concurrentOpsLock.lock();
        try {
            program = ProgramCreatorUtil.withId(program, idGenerator.incrementAndGet());
            programs.put(program.getId(), program);
            dateIndex.insert(program);
            return program.getId();
        } finally {
            concurrentOpsLock.unlock();
        }
    }

    public int size() {
        return programs.size();
    }

    public void delete(long id) {
        concurrentOpsLock.lock();
        try {
            Program program = programs.get(id);
            dateIndex.remove(program);
            programs.remove(id);
        } finally {
            concurrentOpsLock.unlock();
        }
    }

    public void update(Program newValue) {
        exclusiveOpsLock.lock();
        try {
            Program oldValue = programs.get(newValue.getId());
            dateIndex.update(oldValue, newValue);
            programs.replace(newValue.getId(), newValue);
        } finally {
            exclusiveOpsLock.unlock();
        }
    }

    public List<Program> get(@Nullable LocalDate date, @Nullable String keywords) { //todo create class Filter?
        concurrentOpsLock.lock();
        try {
            return dateIndex.stream(date)
                    .filter(program -> filter(program, keywords))
                    .collect(Collectors.toList());
        } finally {
            concurrentOpsLock.unlock();
        }
    }

    private boolean filter(Program program, String keywords) {
        if (keywords == null) {
            return true;
        }
        List<String> words = Arrays.asList(keywords.toLowerCase().split("\\s+")); //todo improve this
        return Arrays.asList(program.getName().toLowerCase().split("\\s+")).containsAll(words);
    }

}
