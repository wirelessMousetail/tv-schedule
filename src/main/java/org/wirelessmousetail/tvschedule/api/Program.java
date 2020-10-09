package org.wirelessmousetail.tvschedule.api;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

public class Program {
    private Long id;
    private String name;
    private String channel; //todo return separate entity?
    private LocalDate date; //todo think about time zones (maybe there're some in tv maze api?)
    private LocalTime startTime;
    private LocalDateTime endTime;

    private Program() { //todo replace with jackson annotations and final fields?
    }

    public Program(Long id, String name, String channel, LocalDate date, LocalTime startTime, LocalDateTime endTime) {
        this.id = id;
        this.name = name;
        this.channel = channel;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getChannel() {
        return channel;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Program program = (Program) o;
        return Objects.equals(id, program.id) &&
                Objects.equals(name, program.name) &&
                Objects.equals(channel, program.channel) &&
                Objects.equals(date, program.date) &&
                Objects.equals(startTime, program.startTime) &&
                Objects.equals(endTime, program.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, channel, date, startTime, endTime);
    }

}
