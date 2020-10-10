package org.wirelessmousetail.tvschedule.core;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.wirelessmousetail.tvschedule.api.Program;

import java.util.Arrays;
import java.util.List;

@RunWith(Parameterized.class)
public class ProgramsFilterTest {

    private static final String MATCH_1 = "MATCH ME";
    private static final String MATCH_2 = "match me";
    private static final String MATCH_3 = "also Match me";
    private static final String MATCH_4 = "but me will match";
    private static final List<String> TEST_COLLECTION = Arrays.asList(MATCH_1, MATCH_2, MATCH_3, "filter me out",
            "do not match", MATCH_4);

    @Parameterized.Parameters(name = "{index}: {0}.filter({1})={2}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                        {"MATCH ME", "Me will match to mask", true},
                        {"match me", "Me will match to mask", true},
                        {"Match Me", "also Match me", true},
                        {"match Me", "do not match", false},
                        {"match Me", "filter me out", false}
                }
        );
    }

    private String keywords;
    private String programName;
    private boolean result;

    public ProgramsFilterTest(String keywords, String programName, boolean result) {
        this.keywords = keywords;
        this.programName = programName;
        this.result = result;
    }

    @Test
    public void testUpperCaseMatch() {
        ProgramsFilter filter = new ProgramsFilter(null, keywords);

        Assert.assertThat(filter.filter(createProgram(programName)), Matchers.equalTo(result));
    }


    private Program createProgram(String name) {
        return new Program(null, name, null, null, null, null);
    }
}