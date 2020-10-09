package org.wirelessmousetail.tvschedule.resources;

import io.dropwizard.jersey.jsr310.LocalDateParam;
import org.wirelessmousetail.tvschedule.api.Program;
import org.wirelessmousetail.tvschedule.storage.ProgramsIMS;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.text.Normalizer;
import java.util.List;
import java.util.Optional;

@Path("/program")
@Produces(MediaType.APPLICATION_JSON)
public class ProgramResource {
    private final ProgramsIMS programsIMS;

    public ProgramResource(ProgramsIMS programsIMS) {
        this.programsIMS = programsIMS;
    }

    @GET
    public List<Program> getPrograms(@QueryParam("date") LocalDateParam date, @QueryParam("keywords") String keywords) {
        if (keywords != null) {
            keywords = Normalizer.normalize(keywords, Normalizer.Form.NFKC); //todo this is bad, change it
        }
        return programsIMS.getPrograms(
                Optional.ofNullable(date).map(LocalDateParam::get).orElse(null), //todo this is bad, change it
                keywords);
    }
}
