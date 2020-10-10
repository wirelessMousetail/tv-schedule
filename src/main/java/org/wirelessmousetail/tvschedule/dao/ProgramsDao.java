package org.wirelessmousetail.tvschedule.dao;

import org.wirelessmousetail.tvschedule.api.Program;
import org.wirelessmousetail.tvschedule.core.DateTimeService;
import org.wirelessmousetail.tvschedule.core.ProgramsFilter;
import org.wirelessmousetail.tvschedule.utils.ProgramCreatorUtils;

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
    private volatile DateIndex dateIndex;

    public ProgramsDao(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
        this.dateIndex = new DateIndex(dateTimeService.getNextWeekStart());
    }

    public Program add(Program program) {
        checkForWeekChange();
        concurrentOpsLock.lock();
        try {
            program = ProgramCreatorUtils.withId(program, idGenerator.incrementAndGet());
            programs.put(program.getId(), program);
            dateIndex.insert(program);
            return program;
        } finally {
            concurrentOpsLock.unlock();
        }
    }

    public boolean delete(long id) {
        checkForWeekChange();
        concurrentOpsLock.lock();
        try {
            Program program = programs.get(id);
            if (program == null) {
                return false;
            }
            dateIndex.remove(program);
            return programs.remove(id) != null;
        } finally {
            concurrentOpsLock.unlock();
        }
    }

    public Program update(Program newValue) {
        checkForWeekChange();
        exclusiveOpsLock.lock();
        try {
            Program oldValue = programs.get(newValue.getId());
            if (oldValue == null) {
                return null;
            }
            dateIndex.update(oldValue, newValue);
            programs.replace(newValue.getId(), newValue);
            return newValue;
        } finally {
            exclusiveOpsLock.unlock();
        }
    }

    public List<Program> get(ProgramsFilter filter) {
        checkForWeekChange();
        concurrentOpsLock.lock();
        try {
            return dateIndex.stream(filter.getDate())
                    .filter(filter::filter)
                    .collect(Collectors.toList());
        } finally {
            concurrentOpsLock.unlock();
        }
    }

    public Program get(long id) {
        checkForWeekChange();
        concurrentOpsLock.lock();
        try {
            return programs.get(id);
        } finally {
            concurrentOpsLock.unlock();
        }
    }

    /**
     * This method is intended to detect week change. In such case it will:
     * * take exclusive write lock
     * * create date index for the next week
     * * clear main storage, since old entries will no longer be available
     * * release exclusive write lock
     * This is relatively heavy operation, but its heavy part will be executed only once a week.
     */
    private void checkForWeekChange() {
        //This check is firstly performed without lock, because in most cases it will fail, so no further actions are required
        if (!dateIndex.belongsToWeek(dateTimeService.getNextWeekStart())) {
            exclusiveOpsLock.lock();
            try {
                //additional check under write lock is required, because date index could have already been concurrently updated
                if (!dateIndex.belongsToWeek(dateTimeService.getNextWeekStart())) {
                    dateIndex = new DateIndex(dateTimeService.getNextWeekStart());
                    programs.clear();
                }
            } finally {
                exclusiveOpsLock.unlock();
            }

        }
    }
}
