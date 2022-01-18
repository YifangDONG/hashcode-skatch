package collection;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
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

    public static <T> List<Integer> getPositions(List<T> list, Predicate<T> predicate) {
        List<Integer> positions = new ArrayList<>();
        for(int i = 0; i < list.size(); i++) {
            if(predicate.test(list.get(i))) {
                positions.add(i);
            }
        }
        return positions;
    }

    /**
     * @param list Should not use LinkedList !!
     * @param <T>
     * @return
     */
    public static <T> BiMap<Integer, T> index(List<T> list) {
        BiMap<Integer, T> index = HashBiMap.create();
        for(int i = 0 ; i < list.size(); i++) {
            index.put(i, list.get(i));
        }
        return index;
    }

    public static <T> Map<Integer, T> indexList(List<T> list) {
        AtomicInteger accumulator = new AtomicInteger(0);
        return list.stream().collect(Collectors.toMap(
                i -> accumulator.getAndIncrement(),
                Function.identity()
        ));
    }
}
