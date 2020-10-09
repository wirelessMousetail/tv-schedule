package org.wirelessmousetail.tvschedule;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;
import org.wirelessmousetail.tvschedule.core.TvMazeLoaderFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class TvScheduleConfiguration extends Configuration {
    @Valid
    @NotNull
    private JerseyClientConfiguration jerseyClient = new JerseyClientConfiguration();

    @Valid
    @NotNull
    private TvMazeLoaderFactory tvMazeLoaderFactory;

    @JsonProperty("jerseyClient")
    public JerseyClientConfiguration getJerseyClientConfiguration() {
        return jerseyClient;
    }

    @JsonProperty("jerseyClient")
    public void setJerseyClientConfiguration(JerseyClientConfiguration jerseyClient) {
        this.jerseyClient = jerseyClient;
    }

    @JsonProperty("tvMazeLoader")
    public TvMazeLoaderFactory getTvMazeLoaderFactory() {
        return tvMazeLoaderFactory;
    }

    @JsonProperty("tvMazeLoader")
    public void setTvMazeLoaderFactory(TvMazeLoaderFactory tvMazeLoaderFactory) {
        this.tvMazeLoaderFactory = tvMazeLoaderFactory;
    }
}
