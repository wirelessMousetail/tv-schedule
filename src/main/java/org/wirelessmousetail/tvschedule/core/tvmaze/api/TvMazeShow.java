package org.wirelessmousetail.tvschedule.core.tvmaze.api;

import java.util.Objects;

public class TvMazeShow {
    private long id;
    private String name;
    private TvMazeNetwork network;

    public TvMazeShow() {

    }

    public TvMazeShow(long id, String name, TvMazeNetwork network) {
        this.id = id;
        this.name = name;
        this.network = network;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public TvMazeNetwork getNetwork() {
        return network;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TvMazeShow that = (TvMazeShow) o;
        return id == that.id &&
                Objects.equals(name, that.name) &&
                Objects.equals(network, that.network);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, network);
    }

    @Override
    public String toString() {
        return "TvMazeShow{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", network=" + network +
                '}';
    }
}
