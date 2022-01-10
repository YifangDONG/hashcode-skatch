package collection;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class Iteration {

    public static List<Integer> range(int start, int end) {
        return IntStream.range(start, end).boxed().collect(Collectors.toList());
    }

    public static List<Long> range(long start, long end) {
        return LongStream.range(start, end).boxed().collect(Collectors.toList());
    }
}
