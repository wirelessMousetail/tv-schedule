package org.wirelessmousetail.tvschedule.core;

import com.google.common.base.Strings;
import io.dropwizard.jersey.jsr310.LocalDateParam;
import org.wirelessmousetail.tvschedule.api.Program;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class ProgramsFilter {
    private LocalDate date;
    private List<String> keywords;

    public ProgramsFilter(LocalDateParam date, String keywords) {
        if(date != null) {
            this.date = date.get();
        }
        if (!Strings.isNullOrEmpty(keywords)) {
            this.keywords = Arrays.asList(keywords.toLowerCase().split("\\s+"));
        }
    }



    public LocalDate getDate() {
        return date;
    }

    public boolean filter(Program program) {
        if (keywords == null) {
            return true;
        }
        return Arrays.asList(program.getName().toLowerCase().split("\\s+")).containsAll(keywords);
    }
}
