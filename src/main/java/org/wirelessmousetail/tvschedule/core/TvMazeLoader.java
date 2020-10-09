package org.wirelessmousetail.tvschedule.core;

import io.dropwizard.lifecycle.Managed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wirelessmousetail.tvschedule.client.TvMazeClient;
import org.wirelessmousetail.tvschedule.core.api.TvMazeProgramEntity;
import org.wirelessmousetail.tvschedule.storage.ProgramsIMS;

import java.time.LocalDate;

public class TvMazeLoader implements Managed {
    private static final Logger LOG = LoggerFactory.getLogger(TvMazeLoader.class);

    private final TvMazeClient client;
    private final ProgramsIMS programsIMS;
    private final DateTimeService dateTimeService;

    public TvMazeLoader(TvMazeClient client, ProgramsIMS programsIMS, DateTimeService dateTimeService) {
        this.client = client;
        this.programsIMS = programsIMS;
        this.dateTimeService = dateTimeService;
    }

    @Override
    public void start() throws Exception {
        LocalDate nextWeekStart = dateTimeService.getNextWeekStart();

        LOG.info("TV schedule import for the next week {} is started", nextWeekStart);

        for (int i = 0; i < 7; i++) {
            TvMazeProgramEntity[] programs = client.loadSchedule(nextWeekStart.plusDays(i));
            for (TvMazeProgramEntity program : programs) {
                if (program.getShow() == null || program.getShow().getNetwork() == null) {
                    LOG.info("Information about show or channel is absent: {}", program);
                    continue;
                }
                programsIMS.add(
                        program.getShow().getName(),
                        program.getShow().getNetwork().getName(), //todo add complex behaviour: choose from config one of the: 1. skip this show (default), 2. set unknoownn channel, 3. get channel name from webChannel entity
                        program.getAirdate(),
                        program.getAirtime(),
                        program.getAirdate().atTime(program.getAirtime()).plusMinutes(program.getRuntime())


                );
            }
        }
        LOG.info("TV schedule for the next week is imported");
    }

    @Override
    public void stop() throws Exception {
        //do nothing
    }
}
