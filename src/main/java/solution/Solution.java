package solution;

import collection.Iteration;
import me.tongfei.progressbar.ProgressBar;

import java.util.*;
import java.util.stream.Collectors;

public interface Solution {
    // default impl is done in the interface to be able to use the logging

    default List<Assign> minWastTime(List<Ride> rides, int steps, int nVehicle, int bonus) {
        // the wastTime = time spent by car without getting reward

        List<Ride> candidate = new ArrayList<>(rides);
        candidate.sort(Comparator.comparingInt(Ride::endT));
        List<Assign> result = new ArrayList<>();
        List<Integer> posTimes = new ArrayList<>();

        for (int i = 0; i < nVehicle; i++) {
            result.add(new Assign(new ArrayList<>()));
            posTimes.add(0);
        }

        while (!candidate.isEmpty()) {
            if (candidate.size() % 100 == 0) {
                System.err.println(candidate.size());
            }
            Ride toAssign = candidate.get(0);
            int wastTime = Integer.MAX_VALUE;
            int vehicle = -1;
            for (int j = 0; j < nVehicle; j++) {
                Integer posTime = posTimes.get(j);
                if (posTime < steps) {

                    Pair pos;
                    if (result.get(j).rides().isEmpty()) {
                        pos = new Pair(0, 0);
                    } else {
                        pos = result.get(j).rides().get(result.get(j).rides().size() - 1).end();
                    }
                    int currWast = Math.max(toAssign.startT() - posTime, pos.distance(toAssign.start()));
                    if (currWast < wastTime) {
                        wastTime = currWast;
                        vehicle = j;
                    }
                }
            }
            if (vehicle == -1) {
                // break if every car doesn't have time
                break;
            }
            result.get(vehicle).rides().add(toAssign);
            int posTime = posTimes.get(vehicle) + wastTime;
            posTimes.set(vehicle, posTime);
            candidate.remove(0);
        }
        return result;
    }

    default List<Assign> greedyE(List<Ride> rides, int steps, int vehicle, int bonus) {
        // E has high bonus, the aime is each assigned ride should have bonus

        Deque<Ride> candidate = new ArrayDeque<>(rides);
        List<Ride> rest = new ArrayList<>();
        List<Assign> result = new ArrayList<>();
        List<Integer> posTimes = new ArrayList<>();

        for (int i = 0; i < vehicle; i++) {
            result.add(new Assign(new ArrayList<>()));
            posTimes.add(0);
        }

        while (!candidate.isEmpty()) {
            if (candidate.size() % 100 == 0) {
                System.err.println(candidate.size());
            }
            for (int j = 0; j < vehicle; j++) {
                Pair pos;
                if (result.get(j).rides().isEmpty()) {
                    pos = new Pair(0, 0);
                } else {
                    pos = result.get(j).rides().get(result.get(j).rides().size() - 1).end();
                }
                Ride ride = candidate.peek();
                Integer postTime = posTimes.get(j);
                if (postTime < steps && canFinishAndHasBonus(ride, pos, postTime)) {
                    result.get(j).rides().add(ride);
                    postTime = ride.startT();
                    postTime += ride.start().distance(ride.end());
                    posTimes.set(j, postTime);
                    candidate.pop();
                    break;
                }
            }
            rest.add(candidate.pop());
        }

        for (int j = 0; j < vehicle; j++) {
            if (rest.isEmpty()) {
                break;
            }
            if (posTimes.get(j) < steps) {
                Pair pos;
                if (result.get(j).rides().isEmpty()) {
                    pos = new Pair(0, 0);
                } else {
                    pos = result.get(j).rides().get(result.get(j).rides().size() - 1).end();
                }

                Ride ride = maxReward(rest, pos, posTimes.get(j), bonus);
                rest.remove(ride);
                result.get(j).rides().add(ride);
                int t = posTimes.get(j) + pos.distance(ride.start()) + ride.start().distance(ride.end());
                posTimes.set(j, t);
            }
        }
        return result;
    }

    default List<Assign> greedy(List<Ride> rides, int steps, int vehicle, int bonus) {
        // assign rides to vehicle one by one, min wast distance
        List<Ride> candidate = new ArrayList<>(rides);
        candidate.sort(Comparator.comparingInt(Ride::endT));
        List<Assign> result = new ArrayList<>();
        Pair pos = new Pair(0, 0);

        for (int i : ProgressBar.wrap(Iteration.range(0, vehicle), "Assign rides")) {
            if (candidate.isEmpty()) {
                result.add(new Assign(Collections.emptyList()));
            } else {
                var assignedRides = new ArrayList<Ride>();
                int t = 0;
                while (t < steps) {
                    if (candidate.isEmpty()) {
                        break;
                    }
                    var next = maxReward(candidate, pos, t, bonus);
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

    private Ride maxReward(List<Ride> rides, Pair pos, int t, int bonus) {
        Ride ride = rides.get(0);
        int maxReward = 0;
        for (Ride curr : rides) {
            int currentReward = getReward(curr, pos, t, bonus);
            if (currentReward > maxReward) {
                maxReward = currentReward;
                ride = curr;
            }
        }
        return ride;
    }

    private int getReward(Ride curr, Pair pos, int posTime, int bonus) {
        int t = posTime + pos.distance(curr.start());
        boolean hasbonus = false;
        if (t < curr.startT()) {
            t = curr.startT();
            hasbonus = true;
        }
        t += curr.start().distance(curr.end());
        int reward = 0;
        if (t <= curr.endT()) {
            reward += curr.start().distance(curr.end());
            if (hasbonus) {
                reward += bonus;
            }
        }
        return reward;
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
