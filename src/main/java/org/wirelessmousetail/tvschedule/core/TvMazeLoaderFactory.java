package org.wirelessmousetail.tvschedule.core;

import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.setup.Environment;
import org.wirelessmousetail.tvschedule.TvScheduleConfiguration;
import org.wirelessmousetail.tvschedule.client.TvMazeClient;
import org.wirelessmousetail.tvschedule.storage.ProgramsIMS;

import javax.ws.rs.client.Client;
import java.util.Objects;

public class TvMazeLoaderFactory {
    private static final String NAME = "tv-maze-loader";

    private String url;
    private String countryCode;
    private ProgramsIMS programsIMS;
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

    public TvMazeLoaderFactory withInMemoryStorage(ProgramsIMS programsIMS) {
        this.programsIMS = programsIMS;
        return this;
    }

    public TvMazeLoaderFactory withDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
        return this;
    }

    public TvMazeLoader build(TvScheduleConfiguration configuration, Environment environment) {
        Objects.requireNonNull(programsIMS, "In memory storage should be set");
        Objects.requireNonNull(dateTimeService, "Date time service should be set");
        final Client client = new JerseyClientBuilder(environment)
                .using(configuration.getJerseyClientConfiguration())
                .using(environment.getObjectMapper())
                .build(NAME);

        return new TvMazeLoader(new TvMazeClient(client, countryCode, url), programsIMS, dateTimeService);
    }
}
