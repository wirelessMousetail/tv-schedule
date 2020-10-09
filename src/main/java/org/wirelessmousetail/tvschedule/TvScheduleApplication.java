package org.wirelessmousetail.tvschedule;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
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
        //todo replace with jackson annotation?
        bootstrap.getObjectMapper().registerModule(new JavaTimeModule());
        bootstrap.getObjectMapper().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public void run(TvScheduleConfiguration tvScheduleConfiguration, Environment environment) throws Exception {
        final DummyProgramProvider programProvider = new DummyProgramProvider();

        environment.jersey().register(new ProgramResource(programProvider));
        //todo should be configurable (through the command line arguments?)
        environment.getObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    }
}
