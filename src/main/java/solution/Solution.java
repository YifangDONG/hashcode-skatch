package solution;

import org.checkerframework.checker.units.qual.A;

import java.util.*;
import java.util.stream.Collectors;

public interface Solution {
    // default impl is done in the interface to be able to use the logging

    default List<Assign> dummy(List<Ride> rides, int vehicle) {
        Deque<Ride> candidate = new ArrayDeque<>(rides);
        Map<Integer, Assign> result = new HashMap<>();
        int i = 0;
        while (!candidate.isEmpty()) {
            int key = i % vehicle;
            if(result.get(key) == null) {
                result.put(key, new Assign(new ArrayList<>()));
            }
            result.get(key).rides().add(candidate.pop());
            i++;
        }
        return new ArrayList<>(result.values());
    }

    default int score(List<Assign> assigns, int steps, int vehicle, int bonus) {
        check(assigns, vehicle);
        int score = 0;
        for (Assign assign : assigns) {
            score += score(assign, steps, bonus);
        }
        return score;
    }

    default int score(Assign assign, int steps, int bonus) {
        // simulate by time
        int score = 0;
        List<Ride> rides = assign.rides();
        int t = 0;
        int r = 0;
        Pair pos = new Pair(0, 0);
        boolean start = false;
        boolean hasBonus = false;
        while(r < rides.size()) {
            Ride ride = rides.get(r);
            if (t > steps) {
                return score;
            }
            if (!start) {
                int distance = ride.start().distance(pos);
                t += distance;
                if (t <= ride.startT()) {
                    hasBonus = true;
                    t = ride.startT();
                }
                start = true;

            } else {
                int distance = ride.start().distance(ride.end());
                if(t + distance <= ride.endT()) {
                    score += distance;
                    if(hasBonus) {
                        score += bonus;
                    }
                }
                pos = ride.end();
                r++;
                start = false;
                hasBonus = false;
            }

        }
        return score;
    }

    private void check(List<Assign> assigns, int vehicle) {
        if (assigns.size() != vehicle) {
            throw new IllegalArgumentException("must have F lines");
        }
        List<Ride> allRides = assigns.stream()
                .map(Assign::rides)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        Set<Ride> uniqueRides = assigns.stream()
                .map(Assign::rides)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        if (allRides.size() != uniqueRides.size()) {
            throw new IllegalArgumentException("one ride is assigned more than once");
        }
    }
}
