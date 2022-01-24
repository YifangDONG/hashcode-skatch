package solution;

import collection.Iteration;
import me.tongfei.progressbar.ProgressBar;

import java.util.*;
import java.util.stream.Collectors;

public interface Solution {
    // default impl is done in the interface to be able to use the logging

    default List<Assign> minWastDistance(List<Ride> rides, int steps, int vehicle) {
        // assign rides to vehicle one by one, min wast distance
        List<Ride> candidate = new ArrayList<>(rides);
        List<Assign> result = new ArrayList<>();
        Pair pos = new Pair(0, 0);

        for (int i : ProgressBar.wrap(Iteration.range(0, vehicle), "Assign rides")) {
            if (candidate.isEmpty()) {
                result.add(new Assign(Collections.emptyList()));
            } else {
                var assignedRides = new ArrayList<Ride>();
                int t = 0;
                while (t < steps) {
                    if(candidate.isEmpty()) {
                        break;
                    }
                    var next = findCloest(candidate, pos, t);
                    candidate.remove(next);
                    assignedRides.add(next);
                    t += pos.distance(next.start());
                    t += next.start().distance(next.end());
                    pos = next.end();
                }
                result.add(new Assign(assignedRides));
            }
        }
        return result;
    }

    private Ride findCloest(List<Ride> rides, Pair pos, int t) {
        Ride ride = rides.get(0);
        int dist = pos.distance(ride.start());
        for (Ride curr : rides) {
            int distance = pos.distance(curr.start());
            //skip if cannot finish on time
            if (distance < dist && canFinishAndHasBonus(curr, pos, t)) {
                dist = distance;
                ride = curr;
            }
        }
        return ride;
    }

    private boolean canFinish(Ride curr, Pair pos, int posTime) {
        int t = posTime + pos.distance(curr.start());
        if (t < curr.startT()) {
            t = curr.startT();
        }
        t += curr.start().distance(curr.end());
        return t <= curr.endT();
    }

    private boolean canFinishAndHasBonus(Ride curr, Pair pos, int posTime) {
        int t = posTime + pos.distance(curr.start());
        boolean hasbonus = false;
        if (t < curr.startT()) {
            t = curr.startT();
            hasbonus = true;
        }
        t += curr.start().distance(curr.end());
        return t <= curr.endT() && hasbonus;
    }

    default List<Assign> dummy(List<Ride> rides, int vehicle) {
        Deque<Ride> candidate = new ArrayDeque<>(rides);
        Map<Integer, Assign> result = new HashMap<>();
        int i = 0;
        while (!candidate.isEmpty()) {
            int key = i % vehicle;
            if (result.get(key) == null) {
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
        while (r < rides.size()) {
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
                if (t + distance <= ride.endT()) {
                    score += distance;
                    if (hasBonus) {
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
