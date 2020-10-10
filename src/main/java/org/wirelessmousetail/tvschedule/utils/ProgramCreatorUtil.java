package org.wirelessmousetail.tvschedule.utils;

import org.wirelessmousetail.tvschedule.api.Program;
import org.wirelessmousetail.tvschedule.core.tvmaze.api.TvMazeProgramEntity;

public class ProgramCreatorUtil {
    public static Program withId(Program program, long id) {
        if (program.getId() != null) {
            throw new IllegalArgumentException(String.format("Program %s already have an id", program));
        }
        return new Program(id, program.getName(), program.getChannel(), program.getDate(), program.getStartTime(),
                program.getEndTime());
    }

    public static Program create(TvMazeProgramEntity tvMazeProgram) { //todo all strings should be normalized
        return new Program(
                null,
                tvMazeProgram.getShow().getName(),
                tvMazeProgram.getShow().getNetwork().getName(), //todo add complex behaviour: choose from config one of the: 1. skip this show (default), 2. set unknoownn channel, 3. get channel name from webChannel entity
                tvMazeProgram.getAirdate(),
                tvMazeProgram.getAirtime(),
                tvMazeProgram.getAirdate().atTime(tvMazeProgram.getAirtime()).plusMinutes(tvMazeProgram.getRuntime())
        );
    }
}
