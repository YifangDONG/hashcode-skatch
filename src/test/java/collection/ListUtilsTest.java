package collection;

import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ListUtilsTest {

    @Test
    public void flattenTest() {
        List<List<Integer>> lists = List.of(List.of(1, 2, 3), List.of(4), Collections.emptyList());
        List<Integer> flattenList = ListUtils.flatten(lists);
        List<Integer> expected = List.of(1, 2, 3, 4);
        assertEquals(expected, flattenList);
    }

    @Test
    public void valueCountIntTest() {
        List<Integer> list = List.of(1, 2, 2, 2, 2, 3);
        Map<Integer, Integer> valueCountInt = ListUtils.valueCountInt(list);
        Map<Integer, Integer> expected = Map.of(1, 1, 2, 4, 3, 1);
        assertEquals(expected, valueCountInt);
    }

    @Test
    public void valueCountLongTest() {
        List<Integer> list = List.of(1, 2, 2, 2, 2, 3);
        Map<Integer, Long> valueCountInt = ListUtils.valueCountLong(list);
        Map<Integer, Long> expected = Map.of(1, 1L, 2, 4L, 3, 1L);
        assertEquals(expected, valueCountInt);
    }

    @Test
    public void getPositionsTest() {
        List<Integer> list = List.of(1, 0, 1, 0, 1);
        List<Integer> positions = ListUtils.getPositions(list, i -> i == 1);
        List<Integer> expected = List.of(0, 2, 4);
        assertEquals(expected, positions);
    }
}