package org.wirelessmousetail.tvschedule.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wirelessmousetail.tvschedule.api.Program;
import org.wirelessmousetail.tvschedule.core.DateTimeService;
import org.wirelessmousetail.tvschedule.core.ProgramsFilter;
import org.wirelessmousetail.tvschedule.utils.ProgramCreatorUtils;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * In memory storage for the {@ling Program} entities.
 * Supports thread safe execution for all operations, and concurrent execution
 * for all operations except {@link ProgramsDao#update(Program)}.
 * <br/>
 * Id to the programs are assigned by this service.
 */
public class ProgramsDao {
    private static final Logger LOG = LoggerFactory.getLogger(ProgramsDao.class);

    private final DateTimeService dateTimeService;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock concurrentOpsLock = readWriteLock.readLock();
    private final Lock exclusiveOpsLock = readWriteLock.writeLock();

    private final AtomicLong idGenerator = new AtomicLong();
    private final ConcurrentHashMap<Long, Program> programs = new ConcurrentHashMap<>();
    /**
     * Date index. Updated during week changes
     */
    private volatile DateIndex dateIndex;

    public ProgramsDao(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
        this.dateIndex = new DateIndex(dateTimeService.getNextWeekStart());
    }

    /**
     * Adds a program to the storage.
     * @param program new program. Id should be null.
     * @return added program with assigned id
     * @exception IllegalArgumentException if air date of argument is not on the next week
     * @exception IllegalArgumentException if argument has non null id
     */
    public Program add(Program program) {
        LOG.debug("Adding new program: {}", program);
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

    /**
     * Deletes a program from the storage
     * @param id of a program to be deleted
     * @return true, if program was deleted successfully and false - if there is no entity with the given id
     */
    public boolean delete(long id) {
        LOG.debug("Deleting a program with id '{}'", id);
        checkForWeekChange();
        concurrentOpsLock.lock();
        try {
            Program program = programs.get(id);
            if (program == null) {
                LOG.warn("There is no program with id '{}'. Nothing will be deleted", id);
                return false;
            }
            dateIndex.remove(program);
            return programs.remove(id) != null;
        } finally {
            concurrentOpsLock.unlock();
        }
    }

    /**
     * Updates given program. This operation is performed under write, unlike all other operations, which could
     * run concurrently. <br/>
     * Write lock is required, since storage becomes in inconsistent state during update.
     * @param newValue program to update
     * @return updated program or null, if such entity was not found.
     * @exception IllegalArgumentException if air date of argument is not on the next week
     */
    public Program update(Program newValue) {
        LOG.debug("Updating a program: {}", newValue);
        checkForWeekChange();
        exclusiveOpsLock.lock();
        try {
            Program oldValue = programs.get(newValue.getId());
            if (oldValue == null) {
                LOG.warn("There is no program with id '{}'. Nothing will be updated", newValue.getId());
                return null;
            }
            dateIndex.update(oldValue, newValue);
            programs.replace(newValue.getId(), newValue);
            return newValue;
        } finally {
            exclusiveOpsLock.unlock();
        }
    }

    /**
     * Retrieves list of programs in air date-time order:
     * <ul>
     *     <li>for the whole next week if filter is empty</li>
     *     <li>filtered by date</li>
     *     <li>filtered by keywords in name. All keywords should be in program name, in any order (case insensitive)</li>
     *     <li>filtered by both</li>
     * </ul>
     * @param filter
     * @return list of programs in air date-time order
     */
    public List<Program> get(@NotNull ProgramsFilter filter) {
        LOG.debug("Retrieving programs for the next week, with filter {}", filter);
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

    /**
     * Retrieves exact program by id.
     * @param id id of a program
     * @return program, or null, if there is no such program.
     */
    public Program get(long id) {
        LOG.debug("Retrieving program with id '{}'", id);
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
     * <ol>
     *     <li>take exclusive write lock</li>
     *     <li>create date index for the next week</li>
     *     <li>clear main storage, since old entries will no longer be available</li>
     *     <li>release exclusive write lock</li>
     * </ol>
     * This is relatively heavy operation, but its slowest part will be executed only once a week.
     */
    private void checkForWeekChange() {
        //This check is firstly performed without lock, because in most cases it will fail, so no further actions are required
        if (!dateIndex.belongsToWeek(dateTimeService.getNextWeekStart())) {
            exclusiveOpsLock.lock();
            try {
                //additional check under write lock is required, because date index could have already been concurrently updated
                if (!dateIndex.belongsToWeek(dateTimeService.getNextWeekStart())) {
                    LOG.info("Week change was detected. All old info will be deleted, index for the next week will be initialized");
                    dateIndex = new DateIndex(dateTimeService.getNextWeekStart());
                    programs.clear();
                }
            } finally {
                exclusiveOpsLock.unlock();
            }

        }
    }
}
