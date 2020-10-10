package org.wirelessmousetail.tvschedule.resources;

import io.dropwizard.jersey.jsr310.LocalDateParam;
import org.wirelessmousetail.tvschedule.api.Program;
import org.wirelessmousetail.tvschedule.dao.ProgramsDao;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.text.Normalizer;
import java.util.List;
import java.util.Optional;

@Path("/program")
@Produces(MediaType.APPLICATION_JSON)
public class ProgramResource {
    private final ProgramsDao programsDao;

    public ProgramResource(ProgramsDao programsDao) {
        this.programsDao = programsDao;
    }

    @GET
    public List<Program> getPrograms(@QueryParam("date") LocalDateParam date, @QueryParam("keywords") String keywords) {
        if (keywords != null) {
            keywords = Normalizer.normalize(keywords, Normalizer.Form.NFKC); //todo this is bad, change it
        }
        return programsDao.get(
                Optional.ofNullable(date).map(LocalDateParam::get).orElse(null), //todo this is bad, change it
                keywords);
    }
}
