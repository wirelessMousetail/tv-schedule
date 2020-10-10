package org.wirelessmousetail.tvschedule.core.tvmaze.api;

import java.util.Objects;

public class TvMazeNetwork {
    private long id;
    private String name;

    public TvMazeNetwork() {
    }

    public TvMazeNetwork(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TvMazeNetwork that = (TvMazeNetwork) o;
        return id == that.id &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "TvMazeNetwork{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
