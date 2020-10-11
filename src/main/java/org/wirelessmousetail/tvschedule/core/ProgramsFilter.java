package org.wirelessmousetail.tvschedule.core;

import com.google.common.base.Strings;
import io.dropwizard.jersey.jsr310.LocalDateParam;
import org.wirelessmousetail.tvschedule.api.Program;
import org.wirelessmousetail.tvschedule.dao.ProgramsDao;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Filter for the {@link ProgramsDao#get} request.
 * Could contain date, for which programs should be selected and (or) list of keywords, which program name should contain.
 * If nothing is set all next week schedule would be returned.
 */
public class ProgramsFilter {
    private LocalDate date;
    private List<String> keywords;

    public ProgramsFilter(@Nullable LocalDateParam date, @Nullable String keywords) {
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

    @Override
    public String toString() {
        return "ProgramsFilter{" +
                "date=" + date +
                ", keywords=" + keywords +
                '}';
    }
}
