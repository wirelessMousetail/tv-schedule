package org.wirelessmousetail.tvschedule.core.tvmaze.api;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Class for <i>program</i> entity of <a href="http://www.tvmaze.com/api">TV Maze API</a>.
 */
public class TvMazeProgramEntity {
    private long id;
    private LocalDate airdate;
    private LocalTime airtime;
    /**
     * Duration of a program in minutes
     */
    private int runtime;
    private TvMazeShow show;

    public TvMazeProgramEntity() {
        //for deserialization
    }

    public TvMazeProgramEntity(long id, LocalDate airdate, LocalTime airtime, int runtime, TvMazeShow show) {
        this.id = id;
        this.airdate = airdate;
        this.airtime = airtime;
        this.runtime = runtime;
        this.show = show;
    }

    public long getId() {
        return id;
    }

    public LocalDate getAirdate() {
        return airdate;
    }

    public LocalTime getAirtime() {
        return airtime;
    }

    public int getRuntime() {
        return runtime;
    }

    public TvMazeShow getShow() {
        return show;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TvMazeProgramEntity that = (TvMazeProgramEntity) o;
        return id == that.id &&
                runtime == that.runtime &&
                Objects.equals(airdate, that.airdate) &&
                Objects.equals(airtime, that.airtime) &&
                Objects.equals(show, that.show);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, airdate, airtime, runtime, show);
    }

    @Override
    public String toString() {
        return "TvMazeProgramEntity{" +
                "id=" + id +
                ", airdate=" + airdate +
                ", airtime=" + airtime +
                ", runtime=" + runtime +
                ", show=" + show +
                '}';
    }
}
