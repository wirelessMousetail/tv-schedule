package org.wirelessmousetail.tvschedule.api;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.OffsetTime;

public class Program {
    private long id;
    private String name;
    private Channel channel;
    private LocalDate date;
    private OffsetTime startTime;
    private OffsetDateTime endTime;

    private Program() { //todo replace with jackson annotations?
    }

    public Program(long id, String name, Channel channel, LocalDate date, OffsetTime startTime, OffsetDateTime endTime) {
        this.id = id;
        this.name = name;
        this.channel = channel;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Channel getChannel() {
        return channel;
    }

    public LocalDate getDate() {
        return date;
    }

    public OffsetTime getStartTime() {
        return startTime;
    }

    public OffsetDateTime getEndTime() {
        return endTime;
    }
}
