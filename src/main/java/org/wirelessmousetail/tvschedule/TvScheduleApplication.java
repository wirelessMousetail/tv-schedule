package org.wirelessmousetail.tvschedule;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.wirelessmousetail.tvschedule.core.DateTimeService;
import org.wirelessmousetail.tvschedule.core.tvmaze.TvMazeLoader;
import org.wirelessmousetail.tvschedule.dao.ProgramsDao;
import org.wirelessmousetail.tvschedule.resources.ProgramResource;

public class TvScheduleApplication extends Application<TvScheduleConfiguration> {

    public static void main(String[] args) throws Exception {
        new TvScheduleApplication().run(args);
    }

    @Override
    public String getName() {
        return "tv-schedule";
    }

    @Override
    public void initialize(Bootstrap<TvScheduleConfiguration> bootstrap) {
        bootstrap.getObjectMapper().registerModule(new JavaTimeModule());
        bootstrap.getObjectMapper().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        bootstrap.getObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    }

    @Override
    public void run(TvScheduleConfiguration config, Environment environment) throws Exception {
        DateTimeService dateTimeService = new DateTimeService();
        ProgramsDao programsDao = new ProgramsDao(dateTimeService);

        TvMazeLoader tvMazeLoader = config.getTvMazeLoaderFactory()
                .withProgramsStorage(programsDao)
                .withDateTimeService(dateTimeService)
                .build(config, environment);
        environment.lifecycle().manage(tvMazeLoader);

        environment.jersey().register(new ProgramResource(programsDao, dateTimeService));
        if(config.isPrettyPrintedOutput()) {
            environment.getObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        }
    }
}
