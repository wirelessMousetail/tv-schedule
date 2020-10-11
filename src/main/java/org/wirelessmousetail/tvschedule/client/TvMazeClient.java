package org.wirelessmousetail.tvschedule.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wirelessmousetail.tvschedule.core.tvmaze.api.TvMazeProgramEntity;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Client to connect to TV maze API. Will upload TV schedule for the given date for the configured country.<br/>
 * TV maze api url could be configured in property <i>tvMazeLoader.url</i>.<br/>
 * Country could be configured in property <i>tvMazeLoader.countryCode</i>(ISO 3166-1).
 */
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

    public synchronized TvMazeProgramEntity[] loadSchedule(LocalDate date) {
        LOG.info("Downloading schedule for {}", date);
        TvMazeProgramEntity[] response = client.target(url)
                .queryParam("country", countryCode)
                .queryParam("date", date.format(DateTimeFormatter.ISO_DATE))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get(TvMazeProgramEntity[].class);
        LOG.debug("TV maze response: {}", (Object) response);
        return response;
    }
}
