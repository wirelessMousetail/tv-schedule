package org.wirelessmousetail.tvschedule.core.tvmaze;

import io.dropwizard.lifecycle.Managed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wirelessmousetail.tvschedule.client.TvMazeClient;
import org.wirelessmousetail.tvschedule.core.DateTimeService;
import org.wirelessmousetail.tvschedule.core.tvmaze.api.TvMazeProgramEntity;
import org.wirelessmousetail.tvschedule.dao.ProgramsDao;
import org.wirelessmousetail.tvschedule.utils.ProgramCreatorUtils;

import java.time.LocalDate;

import static org.wirelessmousetail.tvschedule.core.DateTimeService.DAYS_IN_WEEK;

/**
 * Class which performs upload of next week schedule during startup.
 * Designed to be extended to perform schedule update during week changes.<br/>
 * <i>Important: </i>all shows without network(channel) entity are currently skipped, since this application was
 * designed to provide the TV schedule. Though this behaviour could be changed in future.
 */
public class TvMazeLoader implements Managed {
    private static final Logger LOG = LoggerFactory.getLogger(TvMazeLoader.class);


    private final TvMazeClient client;
    private final ProgramsDao programsDao;
    private final DateTimeService dateTimeService;

    public TvMazeLoader(TvMazeClient client, ProgramsDao programsDao, DateTimeService dateTimeService) {
        this.client = client;
        this.programsDao = programsDao;
        this.dateTimeService = dateTimeService;
    }

    @Override
    public void start() throws Exception {
        LocalDate nextWeekStart = dateTimeService.getNextWeekStart();

        LOG.info("TV schedule import for the next week {} is started", nextWeekStart);

        int importedProgramsCount = 0;

        for (int i = 0; i < DAYS_IN_WEEK; i++) {
            TvMazeProgramEntity[] programs = client.loadSchedule(nextWeekStart.plusDays(i));
            for (TvMazeProgramEntity program : programs) {
                if (notTvProgram(program)) {
                    LOG.debug("Information about show or channel is absent: {}", program);
                    continue;
                }
                programsDao.add(ProgramCreatorUtils.create(program));
                importedProgramsCount++;
            }
        }
        LOG.info("{} programs for the next week were imported", importedProgramsCount);
    }

    /**
     * Network entity could be absent: it is not properly described in TV Maze API, but looks like
     * it is absent for shows, broadcasting through the internet only.
     *
     * @param program
     * @return
     */
    private boolean notTvProgram(TvMazeProgramEntity program) {
        return program.getShow() == null || program.getShow().getNetwork() == null;
    }

    @Override
    public void stop() throws Exception {
        //do nothing
    }
}
