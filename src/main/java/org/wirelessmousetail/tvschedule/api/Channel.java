package org.wirelessmousetail.tvschedule.api;

@Deprecated //todo delete?
public class Channel {
    private long id;
    private String name;

    private Channel() {//todo replace with jackson annotations?
    }

    public Channel(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
