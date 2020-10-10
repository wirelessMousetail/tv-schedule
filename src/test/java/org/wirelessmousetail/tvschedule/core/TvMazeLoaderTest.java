package org.wirelessmousetail.tvschedule.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.wirelessmousetail.tvschedule.api.Program;
import org.wirelessmousetail.tvschedule.client.TvMazeClient;
import org.wirelessmousetail.tvschedule.core.tvmaze.TvMazeLoader;
import org.wirelessmousetail.tvschedule.core.tvmaze.api.TvMazeNetwork;
import org.wirelessmousetail.tvschedule.core.tvmaze.api.TvMazeProgramEntity;
import org.wirelessmousetail.tvschedule.core.tvmaze.api.TvMazeShow;
import org.wirelessmousetail.tvschedule.dao.ProgramsDao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TvMazeLoaderTest {

    @Mock
    private TvMazeClient client;

    @Mock
    private DateTimeService timeService;


    //TEST DATA:
    private static final LocalDate MONDAY = LocalDate.of(2020, 10, 5);
    private static final LocalTime AIRTIME_PROGRAM_1 = LocalTime.of(10, 0, 0);
    private static final int RUNTIME_PROGRAM_1 = 40;
    private static final LocalDateTime END_TIME_PROGRAM_1 = AIRTIME_PROGRAM_1.atDate(MONDAY).plusMinutes(RUNTIME_PROGRAM_1);
    private static final String NAME_PROGRAM_1 = "Program 1";

    private static final LocalTime AIRTIME_PROGRAM_2 = LocalTime.of(10, 0, 0);
    private static final int RUNTIME_PROGRAM_2 = 120;
    private static final LocalDateTime END_TIME_PROGRAM_2 = AIRTIME_PROGRAM_2.atDate(MONDAY).plusMinutes(RUNTIME_PROGRAM_2);
    private static final String NAME_PROGRAM_2 = "Program 2";

    private static final LocalTime AIRTIME_PROGRAM_3 = LocalTime.of(23, 0, 0);
    private static final int RUNTIME_PROGRAM_3 = 90;
    private static final LocalDateTime END_TIME_PROGRAM_3 = LocalDateTime.of(2020, 10, 6, 0, 30, 0);
    private static final String NAME_PROGRAM_3 = "Program 3";

    private static final LocalTime AIRTIME_PROGRAM_4 = LocalTime.of(16, 10, 0);
    private static final int RUNTIME_PROGRAM_4 = 20;
    private static final String NAME_PROGRAM_4 = "Program 4";

    private static final TvMazeNetwork CHANNEL_1 = new TvMazeNetwork(1, "Channel 1");
    private static final TvMazeNetwork CHANNEL_2 = new TvMazeNetwork(1, "Channel 2");


    @Test
    public void testWholeWeekLoaded() throws Exception {
        when(timeService.getNextWeekStart()).thenReturn(MONDAY);
        ProgramsDao programsDao = new ProgramsDao(timeService);
        TvMazeLoader loader = new TvMazeLoader(client, programsDao, timeService);
        when(client.loadSchedule(any(LocalDate.class))).thenReturn(createTestData(), new TvMazeProgramEntity[]{});

        loader.start();

        ArgumentCaptor<LocalDate> date = ArgumentCaptor.forClass(LocalDate.class);
        verify(client, times(7)).loadSchedule(date.capture());
        assertThat(date.getAllValues(), equalTo(sevenDays(MONDAY)));

        assertThat(programsDao.get(null, null), contains(
                new Program(1L, NAME_PROGRAM_1, CHANNEL_1.getName(), MONDAY, AIRTIME_PROGRAM_1, END_TIME_PROGRAM_1),
                new Program(2L, NAME_PROGRAM_2, CHANNEL_2.getName(), MONDAY, AIRTIME_PROGRAM_2, END_TIME_PROGRAM_2),
                new Program(3L, NAME_PROGRAM_3, CHANNEL_1.getName(), MONDAY, AIRTIME_PROGRAM_3, END_TIME_PROGRAM_3)
        ));

    }

    public TvMazeProgramEntity[] createTestData() {
        return new TvMazeProgramEntity[]{
                        new TvMazeProgramEntity(1, MONDAY, AIRTIME_PROGRAM_1, RUNTIME_PROGRAM_1, new TvMazeShow(1, NAME_PROGRAM_1, CHANNEL_1)),
                        new TvMazeProgramEntity(2, MONDAY, AIRTIME_PROGRAM_2, RUNTIME_PROGRAM_2, new TvMazeShow(1, NAME_PROGRAM_2, CHANNEL_2)),
                        new TvMazeProgramEntity(3, MONDAY, AIRTIME_PROGRAM_3, RUNTIME_PROGRAM_3, new TvMazeShow(1, NAME_PROGRAM_3, CHANNEL_1)),
                        new TvMazeProgramEntity(4, MONDAY, AIRTIME_PROGRAM_4, RUNTIME_PROGRAM_4, new TvMazeShow(1, NAME_PROGRAM_4, null))
        };
    }

    private List<LocalDate> sevenDays(LocalDate from) {
        return Stream.iterate(0, i -> i + 1).limit(7).map(from::plusDays).collect(Collectors.toList());
    }
}