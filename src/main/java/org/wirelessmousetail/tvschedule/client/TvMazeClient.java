package org.wirelessmousetail.tvschedule.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wirelessmousetail.tvschedule.core.tvmaze.api.TvMazeProgramEntity;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TvMazeClient {
    private static final Logger LOG = LoggerFactory.getLogger(TvMazeClient.class);
    private final Client client;
    private final String countryCode;
    private final String url;

    public TvMazeClient(Client client, String countryCode, String url) {
        this.client = client;
        this.countryCode = countryCode;
        this.url = url;
    }
    //todo should be:
    // - thread safe
    // - should exist in only one instance
    // - add rate limiting
    public synchronized TvMazeProgramEntity[] loadSchedule(LocalDate date) {
        LOG.debug("Downloading schedule for {}", date);
        TvMazeProgramEntity[] response = client.target(url)
                .queryParam("country", countryCode)
                .queryParam("date", date.format(DateTimeFormatter.ISO_DATE))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get(TvMazeProgramEntity[].class);
        LOG.debug("TV maze response: {}", (Object) response);
        return response;
    }
}
