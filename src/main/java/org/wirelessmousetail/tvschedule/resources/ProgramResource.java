package org.wirelessmousetail.tvschedule.resources;

import io.dropwizard.jersey.jsr310.LocalDateParam;
import org.wirelessmousetail.tvschedule.DummyProgramProvider;
import org.wirelessmousetail.tvschedule.api.Program;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.text.Normalizer;
import java.util.List;
import java.util.Optional;

@Path("/program")
@Produces(MediaType.APPLICATION_JSON)
public class ProgramResource {
    private final DummyProgramProvider programProvider;

    public ProgramResource(DummyProgramProvider programProvider) {
        this.programProvider = programProvider;
    }

    @GET
    public List<Program> getPrograms(@QueryParam("date") LocalDateParam date, @QueryParam("keywords") String keywords) {
        if (keywords != null) {
            keywords = Normalizer.normalize(keywords, Normalizer.Form.NFKC); //todo this is bad, change it
        }
        return programProvider.getPrograms(
                Optional.ofNullable(date).map(LocalDateParam::get).orElse(null), //todo this is bad, change it
                keywords);
    }
}
