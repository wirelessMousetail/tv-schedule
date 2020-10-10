package org.wirelessmousetail.tvschedule.dao;

import io.dropwizard.jersey.jsr310.LocalDateParam;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.wirelessmousetail.tvschedule.api.Program;
import org.wirelessmousetail.tvschedule.core.DateTimeService;
import org.wirelessmousetail.tvschedule.core.ProgramsFilter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProgramsDaoTest {

    @Mock
    private DateTimeService timeService;

    private static final int DUMMY_ENTRIES_COUNT = 7;
    private static final ProgramsFilter EMPTY_FILTER = new ProgramsFilter(null, null);
    private static final LocalDate NEXT_WEEK_START = LocalDate.of(2020, 10, 5);
    private static final LocalDate NEXT_WEEK_END = LocalDate.of(2020, 10, 11);
    private static final String TEST_NAME = "testName";
    private static final String TEST_CHANNEL = "testChannel";
    private static final LocalTime START_TIME = LocalTime.of(13, 0);
    private static final LocalTime END_TIME = LocalTime.of(14, 0);
    private static final String FILTER_MATCH_NAME_1 = "Should be found 1";
    private static final String FILTER_MATCH_NAME_2 = "Should be found 2";

    @Before
    public void setUp() throws Exception {
        when(timeService.getNextWeekStart()).thenReturn(NEXT_WEEK_START);
    }

    @Test
    public void addToWeekStart() {
        ProgramsDao programsDao = new ProgramsDao(timeService);
        Program expected = createProgramEntity(1L, NEXT_WEEK_START);

        Program added = programsDao.add(createProgramEntity(null, NEXT_WEEK_START));

        Assert.assertThat(added, equalTo(expected));
        Assert.assertThat(programsDao.get(1), equalTo(expected));
    }

    @Test
    public void addToWeekEnd() {
        ProgramsDao programsDao = new ProgramsDao(timeService);
        Program expected = createProgramEntity(1L, NEXT_WEEK_END);

        Program added = programsDao.add(createProgramEntity(null, NEXT_WEEK_END));

        Assert.assertThat(added, equalTo(expected));
        Assert.assertThat(programsDao.get(1), equalTo(expected));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addProgramWithId() {
        ProgramsDao programsDao = new ProgramsDao(timeService);

        programsDao.add(createProgramEntity(1L, NEXT_WEEK_START));

        Assert.fail("Should not be reached");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addBeforeNextWeek() {
        ProgramsDao programsDao = new ProgramsDao(timeService);

        programsDao.add(createProgramEntity(null, NEXT_WEEK_START.minusDays(1)));

        Assert.fail("Should not be reached");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addAfterNextWeek() {
        ProgramsDao programsDao = new ProgramsDao(timeService);

        programsDao.add(createProgramEntity(null, NEXT_WEEK_END.plusDays(1)));

        Assert.fail("Should not be reached");
    }

    @Test
    public void addWeekChanged() {
        ProgramsDao programsDao = new ProgramsDao(timeService);
        fillWithDummyData(programsDao);

        LocalDate newWeekStart = NEXT_WEEK_START.plusDays(7);
        when(timeService.getNextWeekStart()).thenReturn(newWeekStart);
        long expectedId = 8L;
        Program expected = createProgramEntity(expectedId, newWeekStart);

        Program added = programsDao.add(createProgramEntity(null, newWeekStart));

        Assert.assertThat(added, equalTo(expected));
        Assert.assertThat(programsDao.get(expectedId), equalTo(expected));
        Assert.assertThat(programsDao.get(EMPTY_FILTER), contains(expected));
    }

    @Test
    public void deleteExisted() {
        ProgramsDao programsDao = new ProgramsDao(timeService);
        fillWithDummyData(programsDao);

        boolean result = programsDao.delete(1L);

        Assert.assertTrue(result);
        Assert.assertNull(programsDao.get(1L));
        Assert.assertThat(programsDao.get(EMPTY_FILTER).size(), equalTo(DUMMY_ENTRIES_COUNT-1));
    }

    @Test
    public void deleteAbsent() {
        ProgramsDao programsDao = new ProgramsDao(timeService);
        fillWithDummyData(programsDao);

        boolean result = programsDao.delete(DUMMY_ENTRIES_COUNT+1);

        Assert.assertFalse(result);
        Assert.assertNull(programsDao.get(8L));
        Assert.assertThat(programsDao.get(EMPTY_FILTER).size(), equalTo(DUMMY_ENTRIES_COUNT));
    }

    @Test
    public void deleteExistedWeekChanged() {
        ProgramsDao programsDao = new ProgramsDao(timeService);
        fillWithDummyData(programsDao);

        LocalDate newWeekStart = NEXT_WEEK_START.plusDays(7);
        when(timeService.getNextWeekStart()).thenReturn(newWeekStart);

        boolean result = programsDao.delete(1L);

        Assert.assertFalse(result);
        Assert.assertTrue(programsDao.get(EMPTY_FILTER).isEmpty());
    }

    @Test
    public void updateExisted() {
        ProgramsDao programsDao = new ProgramsDao(timeService);
        fillWithDummyData(programsDao);
        Program expected = createProgramEntity(1L, NEXT_WEEK_START);

        Program result = programsDao.update(createProgramEntity(1L, NEXT_WEEK_START));

        Assert.assertThat(result, equalTo(expected));
        Assert.assertThat(programsDao.get(1L), equalTo(expected));
    }

    @Test
    public void updateAbsent() {
        ProgramsDao programsDao = new ProgramsDao(timeService);
        fillWithDummyData(programsDao);

        Program result = programsDao.update(createProgramEntity(8L, NEXT_WEEK_START));

        Assert.assertNull(result);
        Assert.assertNull(programsDao.get(8L));
    }

    @Test
    public void updateExistedWeekChanged() {
        ProgramsDao programsDao = new ProgramsDao(timeService);
        fillWithDummyData(programsDao);

        LocalDate newWeekStart = NEXT_WEEK_START.plusDays(7);
        when(timeService.getNextWeekStart()).thenReturn(newWeekStart);

        Program result = programsDao.update(createProgramEntity(1L, NEXT_WEEK_START));

        Assert.assertNull(result);
        Assert.assertTrue(programsDao.get(EMPTY_FILTER).isEmpty());
    }

    @Test
    public void getNoFilter() {
        ProgramsDao programsDao = new ProgramsDao(timeService);
        fillWithDummyData(programsDao);

        List<Program> programs = programsDao.get(EMPTY_FILTER);

        Assert.assertThat(programs, equalTo(generateDummyData(true)));
    }

    @Test
    public void getDateFilter() {
        ProgramsDao programsDao = new ProgramsDao(timeService);
        fillWithDummyData(programsDao);
        Program expected = generateDummyData(true).get(1);

        List<Program> programs = programsDao.get(createFilter(NEXT_WEEK_START.plusDays(1), null));

        Assert.assertThat(programs, contains(expected));
    }

    @Test
    public void getKeywordsFilter() {
        ProgramsDao programsDao = new ProgramsDao(timeService);
        fillWithData(programsDao,
                createProgramEntity(null, FILTER_MATCH_NAME_1, NEXT_WEEK_START),
                createProgramEntity(null, FILTER_MATCH_NAME_2, NEXT_WEEK_START),
                createProgramEntity(null, "Shouldn't be found", NEXT_WEEK_START));
        Program expected1 = createProgramEntity(1L, FILTER_MATCH_NAME_1, NEXT_WEEK_START);
        Program expected2 = createProgramEntity(2L, FILTER_MATCH_NAME_2, NEXT_WEEK_START);

        List<Program> result = programsDao.get(new ProgramsFilter(null, "should found"));

        Assert.assertThat(result, contains(expected1, expected2));
    }

    @Test
    public void getDateKeywordsFilter() {
        ProgramsDao programsDao = new ProgramsDao(timeService);
        fillWithData(programsDao,
                createProgramEntity(null, FILTER_MATCH_NAME_1, NEXT_WEEK_START),
                createProgramEntity(null, FILTER_MATCH_NAME_2, NEXT_WEEK_START.plusDays(1)),
                createProgramEntity(null, "Shouldn't be found", NEXT_WEEK_START));
        Program expected1 = createProgramEntity(1L, FILTER_MATCH_NAME_1, NEXT_WEEK_START);

        List<Program> result = programsDao.get(createFilter(NEXT_WEEK_START, "should found"));

        Assert.assertThat(result, contains(expected1));
    }

    @Test
    public void getWeekChanged() {
        ProgramsDao programsDao = new ProgramsDao(timeService);
        fillWithDummyData(programsDao);

        LocalDate newWeekStart = NEXT_WEEK_START.plusDays(7);
        when(timeService.getNextWeekStart()).thenReturn(newWeekStart);

        List<Program> result = programsDao.get(EMPTY_FILTER);

        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void getExactProgramAbsent() {
        ProgramsDao programsDao = new ProgramsDao(timeService);
        fillWithDummyData(programsDao);

        Program result = programsDao.get(8L);

        Assert.assertNull(result);
    }

    @Test
    public void getExactProgramWeekChanged() {
        ProgramsDao programsDao = new ProgramsDao(timeService);
        fillWithDummyData(programsDao);

        LocalDate newWeekStart = NEXT_WEEK_START.plusDays(7);
        when(timeService.getNextWeekStart()).thenReturn(newWeekStart);

        Program result = programsDao.get(1L);

        Assert.assertNull(result);
    }

    private Program createProgramEntity(Long id, LocalDate airDate) {
        return createProgramEntity(id, TEST_NAME, airDate);
    }

    private Program createProgramEntity(Long id, String name, LocalDate airDate) {
        return new Program(id, name, TEST_CHANNEL, airDate, START_TIME, LocalDateTime.of(airDate, END_TIME));
    }

    private void fillWithDummyData(ProgramsDao programsDao) {
        generateDummyData(false).forEach(programsDao::add);
    }

    private void fillWithData(ProgramsDao programsDao, Program... programs){
        for (Program program : programs) {
            programsDao.add(program);
        }
    }

    private List<Program> generateDummyData(boolean withIds) {
        return Stream.iterate(0, i -> i+1)
                .limit(DUMMY_ENTRIES_COUNT)
                .map(i ->
                        new Program(
                                withIds? (long) (i + 1) :null,
                                format("program name %d", i),
                                format("channel %d", i),
                                NEXT_WEEK_START.plusDays(i),
                                START_TIME,
                                LocalDateTime.of(NEXT_WEEK_START.plusDays(i), END_TIME)
                        )
                ).collect(Collectors.toList());
    }

    private ProgramsFilter createFilter(LocalDate date, String keywords) {
        return new ProgramsFilter(new LocalDateParam(date.toString()), keywords);
    }
}