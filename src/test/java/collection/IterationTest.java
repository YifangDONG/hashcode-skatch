package collection;

import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class IterationTest {

    @Test
    public void rangeIntTest() {
        List<Integer> range = Iteration.range(1, 2);
        List<Integer> expected = List.of(1);
        assertEquals(expected, range);
    }

    @Test
    public void rangeIntTest_startBiggerThanEnd() {
        List<Integer> range = Iteration.range(2, 1);
        List<Integer> expected = Collections.emptyList();
        assertEquals(expected, range);
    }

    @Test
    public void rangeLongTest() {
        List<Long> range = Iteration.range(1L, 2L);
        List<Long> expected = List.of(1L);
        assertEquals(expected, range);
    }

    @Test
    public void rangeLongTest_startBiggerThanEnd() {
        List<Long> range = Iteration.range(2L, 1L);
        List<Long> expected = Collections.emptyList();
        assertEquals(expected, range);
    }
}