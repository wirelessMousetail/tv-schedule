package org.wirelessmousetail.tvschedule.core.tvmaze;

import io.dropwizard.lifecycle.Managed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wirelessmousetail.tvschedule.client.TvMazeClient;
import org.wirelessmousetail.tvschedule.core.DateTimeService;
import org.wirelessmousetail.tvschedule.core.tvmaze.api.TvMazeProgramEntity;
import org.wirelessmousetail.tvschedule.dao.ProgramsDao;
import org.wirelessmousetail.tvschedule.utils.ProgramCreatorUtil;

import java.time.LocalDate;

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
        for (int i = 0; i < 7; i++) {
            TvMazeProgramEntity[] programs = client.loadSchedule(nextWeekStart.plusDays(i));
            for (TvMazeProgramEntity program : programs) {
                if (program.getShow() == null || program.getShow().getNetwork() == null) {
                    LOG.info("Information about show or channel is absent: {}", program);
                    continue;
                }
                programsDao.add(ProgramCreatorUtil.create(program));
                importedProgramsCount++;
            }
        }
        LOG.info("{} programs for the next week were imported", importedProgramsCount);
    }

    @Override
    public void stop() throws Exception {
        //do nothing
    }
}
