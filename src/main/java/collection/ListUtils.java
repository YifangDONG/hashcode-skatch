package collection;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

public class ListUtils {

    public static <T> Map<T, Long> valueCountLong(List<T> list) {
        return list
                .parallelStream()
                .collect(groupingBy(Function.identity(), counting()));
    }

    public static <T> Map<T, Integer> valueCountInt(List<T> list) {

        return list
                .parallelStream()
                .collect(groupingBy(Function.identity(), summingInt(e -> 1)));
    }

    public static <T> List<T> flatten(List<List<T>> lists) {
        return lists.stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
