package org.wirelessmousetail.tvschedule;

import org.wirelessmousetail.tvschedule.api.Channel;
import org.wirelessmousetail.tvschedule.api.Program;

import java.time.LocalDate;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Deprecated //todo delete
public class DummyProgramProvider {
    public List<Program> getPrograms() {
        return getDummyList();
    }

    public List<Program> getPrograms(LocalDate date, String keywords) {
        List<Program> dummyList = getPrograms();
        if (date != null) {
            dummyList = dummyList.stream().filter(program -> program.getDate().equals(date)).collect(Collectors.toList());
        }
        if (keywords != null) {
            List<String> words = Arrays.asList(keywords.toLowerCase().split("\\s+"));
            dummyList = dummyList.stream()
                    .filter(program ->
                            Arrays.asList(program.getName().toLowerCase().split("\\s+")).containsAll(words))
                    .collect(Collectors.toList());

        }
        return dummyList;
    }

    private List<Program> getDummyList() {
        OffsetTime time = OffsetTime.of(20, 0, 0, 0, ZoneOffset.UTC);
        LocalDate date = LocalDate.now();
        Channel bbs = new Channel(1, "BBS");
        Channel other = new Channel(1, "Motley");
        return Arrays.asList(
                new Program(1, "Dr Who", bbs, date,
                        time,
                        time.atDate(date).plusMinutes(40)),
                new Program(2, "Something uninteresting", bbs, date,
                        time.plusMinutes(40),
                        time.atDate(date).plusMinutes(100)),
                new Program(3, "Something completely different", other,
                        date.plusDays(1),
                        time,
                        time.atDate(date.plusDays(1)).plusMinutes(20))
        );
    }
}
