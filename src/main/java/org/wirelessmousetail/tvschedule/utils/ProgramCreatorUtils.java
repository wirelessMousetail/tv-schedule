package org.wirelessmousetail.tvschedule.utils;

import org.wirelessmousetail.tvschedule.api.Program;
import org.wirelessmousetail.tvschedule.core.tvmaze.api.TvMazeProgramEntity;

/**
 * Helper class to simplify {@link Program} creation.
 */
public class ProgramCreatorUtils {
    /**
     * Creates a new program entity from existed entity without id, and set the given id to it
     * @param program
     * @param id
     * @return
     */
    public static Program withId(Program program, long id) {
        if (program.getId() != null) {
            throw new IllegalArgumentException(String.format("Program %s already have an id", program));
        }
        return new Program(id, program.getName(), program.getChannel(), program.getDate(), program.getStartTime(),
                program.getEndTime());
    }

    /**
     * Creates a new program entity from {@link TvMazeProgramEntity}. TvMazeProgramEntity should contain all the fields,
     * including network.
     * @param tvMazeProgram
     * @return
     */
    public static Program create(TvMazeProgramEntity tvMazeProgram) {
        return new Program(
                null,
                tvMazeProgram.getShow().getName(),
                tvMazeProgram.getShow().getNetwork().getName(),
                tvMazeProgram.getAirdate(),
                tvMazeProgram.getAirtime(),
                tvMazeProgram.getAirdate().atTime(tvMazeProgram.getAirtime()).plusMinutes(tvMazeProgram.getRuntime())
        );
    }
}
