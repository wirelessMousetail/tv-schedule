package org.wirelessmousetail.tvschedule.core.tvmaze;

import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.setup.Environment;
import org.wirelessmousetail.tvschedule.TvScheduleConfiguration;
import org.wirelessmousetail.tvschedule.client.TvMazeClient;
import org.wirelessmousetail.tvschedule.core.DateTimeService;
import org.wirelessmousetail.tvschedule.dao.ProgramsDao;

import javax.ws.rs.client.Client;
import java.util.Objects;

public class TvMazeLoaderFactory {
    private static final String NAME = "tv-maze-loader";

    private String url;
    private String countryCode;
    private ProgramsDao programsDao;
    private DateTimeService dateTimeService;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public TvMazeLoaderFactory withProgramsStorage(ProgramsDao programsDao) {
        this.programsDao = programsDao;
        return this;
    }

    public TvMazeLoaderFactory withDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
        return this;
    }

    public TvMazeLoader build(TvScheduleConfiguration configuration, Environment environment) {
        Objects.requireNonNull(programsDao, "Programs storage should be set");
        Objects.requireNonNull(dateTimeService, "Date time service should be set");
        final Client client = new JerseyClientBuilder(environment)
                .using(configuration.getJerseyClientConfiguration())
                .using(environment.getObjectMapper())
                .build(NAME);

        return new TvMazeLoader(new TvMazeClient(client, countryCode, url), programsDao, dateTimeService);
    }
}
