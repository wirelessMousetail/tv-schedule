package org.wirelessmousetail.tvschedule.resources;

import com.codahale.metrics.annotation.Timed;
import io.dropwizard.jersey.jsr310.LocalDateParam;
import org.hibernate.validator.constraints.Length;
import org.wirelessmousetail.tvschedule.api.Program;
import org.wirelessmousetail.tvschedule.core.DateTimeService;
import org.wirelessmousetail.tvschedule.core.ProgramsFilter;
import org.wirelessmousetail.tvschedule.dao.ProgramsDao;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.time.LocalDate;
import java.util.List;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static org.eclipse.jetty.http.HttpStatus.UNPROCESSABLE_ENTITY_422;

@Path("/program")
@Produces(MediaType.APPLICATION_JSON)
public class ProgramResource {
    private final ProgramsDao programsDao;
    private final DateTimeService dateTimeService;

    public ProgramResource(ProgramsDao programsDao, DateTimeService dateTimeService) {
        this.programsDao = programsDao;
        this.dateTimeService = dateTimeService;
    }

    /**
     * Retrieved all programs for the next week, or filtered by date and/or keywords.
     * Date should be on the next week, or error will be returned.
     */
    @GET
    @Timed
    public List<Program> getPrograms(@QueryParam("date") LocalDateParam date,
                                     @QueryParam("keywords") @Length(max = 100) String keywords) {
        assertOnNextWeek(date, "Parameter 'date' should contain date from the next week");
        return programsDao.get(new ProgramsFilter(date, keywords));
    }

    /**
     * Retrieved a program by its id.
     * @param id
     * @return
     */
    @GET
    @Timed
    @Path("{id}")
    public Program getProgram(@NotNull @PathParam("id") long id) {
        Program program = programsDao.get(id);
        if (program == null) {
            throw new WebApplicationException(String.format("There is no program with id %d", id), NOT_FOUND);
        }
        return program;
    }

    /**
     * Adds a program to the schedule. All fields, except id, should be filled. Id must be null.
     * Date should be on the next week, or error will be returned.
     * @param program
     * @return
     */
    @POST
    @Timed
    public Response addProgram(@Valid @NotNull Program program) {
        if (program.getId() != null) {
            throw new WebApplicationException("Field 'id' should be null", UNPROCESSABLE_ENTITY_422);
        }
        assertOnNextWeek(program.getDate(), "Field 'date' should contain date from the next week");
        Program created = programsDao.add(program);
        return Response.created(
                UriBuilder.fromResource(ProgramResource.class)
                        .path("{id}").build(created.getId())).entity(created).build();
    }

    /**
     * Updates a program, passed in request. All fields should be set.
     * Date should be on the next week, or error will be returned.
     * @param program
     * @return
     */
    @PUT
    @Timed
    public Program updateProgram(@Valid @NotNull Program program) {
        if (program.getId() == null) {
            throw new WebApplicationException("Field 'id' should not be null", UNPROCESSABLE_ENTITY_422);
        }
        assertOnNextWeek(program.getDate(), "Field 'date' should contain date from the next week");
        Program updated = programsDao.update(program);
        if (updated == null) {
            throw new WebApplicationException(String.format("There is no program with id %d", program.getId()), NOT_FOUND);
        }
        return updated;
    }

    /**
     * Deletes a program with a given id
     * @param id
     */
    @DELETE
    @Timed
    @Path("{id}")
    public void deleteProgram(@NotNull @PathParam("id") long id) {
        if (!programsDao.delete(id)) {
            throw new WebApplicationException(String.format("There is no program with id %d", id), NOT_FOUND);
        }
    }


    private void assertOnNextWeek(LocalDateParam date, String message) {
        if (date != null) {
            assertOnNextWeek(date.get(), message);
        }
    }

    /**
     * Asserts that date is on the next week, or throws a WebApplicationException exception with 422 response
     * @param date
     * @param message
     */
    private void assertOnNextWeek(LocalDate date, String message) {
        if(date != null && !dateTimeService.onNextWeek(date)) {
            throw new WebApplicationException(message, UNPROCESSABLE_ENTITY_422);
        }
    }
}
