package org.wirelessmousetail.tvschedule.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.dropwizard.validation.ValidationMethod;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

public class Program {
    private Long id;
    @NotEmpty
    @Length(max = 100)
    private String name;
    @NotEmpty
    @Length(max = 100)
    private String channel;
    @NotNull
    private LocalDate date;
    @NotNull
    private LocalTime startTime;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endTime;

    private Program() {
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

    @Override
    public String toString() {
        return "Program{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", channel='" + channel + '\'' +
                ", date=" + date +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

    @ValidationMethod(message = "Start time should be before end time")
    @JsonIgnore
    public boolean isEndTimeValid() {
        return startTime.atDate(date).isBefore(endTime);
    }


}
