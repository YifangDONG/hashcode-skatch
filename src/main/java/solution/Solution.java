package solution;

import collection.Iteration;
import me.tongfei.progressbar.ProgressBar;
import java.util.*;
import java.util.stream.Collectors;

public interface Solution {
    // default impl is done in the interface to be able to use the logging
    default List<Assign> greedyC(List<Ride> rides, int steps, int nVehicle, int bonus) {
        // C: All ride start and end the same time, bonus = 1
        // => max the number of ride
        // => target is to have 10000/81 = 123 ride per car
        // current greedy on min wast time give 96 ride per car
        // after loop 10000 times, find max assigned ride is 8220,
        // next step is to max the distance of assigned ride

        int maxRide = 0;
        List<Assign> bestResult = new ArrayList<>();
        for(int rep : ProgressBar.wrap(Iteration.range(0, 10000), "loop")) {
            if(rep % 1000 == 0) {
                System.err.println(maxRide);
            }

            int currNRides = 0;
            List<Ride> candidate = new ArrayList<>(rides);
//            ToIntFunction<Ride> rideToIntFunction = ride -> ride.start().distance(ride.end());
            Collections.shuffle(candidate);
            List<Assign> result = new ArrayList<>();
            List<Integer> posTimes = new ArrayList<>();

            for (int i = 0; i < nVehicle; i++) {
                result.add(new Assign(new ArrayList<>()));
                posTimes.add(0);
            }

            int finishedCar = 0;
            while (!candidate.isEmpty()) {
                Ride toAssign = candidate.get(0);
                int wastTime = Integer.MAX_VALUE;
                int vehicle = -1;
                for (int j = 0; j < nVehicle; j++) {
                    Integer posTime = posTimes.get(j);
                    if (posTime >= steps) {
                        finishedCar++;
                    } else {
                        Pair pos;
                        if (result.get(j).rides().isEmpty()) {
                            pos = new Pair(0, 0);
                        } else {
                            pos = result.get(j).rides().get(result.get(j).rides().size() - 1).end();
                        }
                        int currWast = Math.max(toAssign.startT() - posTime, pos.distance(toAssign.start()));
                        if (currWast < wastTime && canFinish(toAssign, pos, posTime)) {
                            wastTime = currWast;
                            vehicle = j;
                        }
                    }
                }
                if (finishedCar == nVehicle) {
                    // break if every car doesn't have time
                    break;
                }else if (vehicle == -1) {
                    candidate.remove(0);
                }else {
                    result.get(vehicle).rides().add(toAssign);
                    int posTime = posTimes.get(vehicle) + wastTime + toAssign.start().distance(toAssign.end());
                    posTimes.set(vehicle, posTime);
                    candidate.remove(0);
                    currNRides++;
                }
            }
            if(currNRides > maxRide) {
                maxRide = currNRides;
                bestResult = result;
            }
        }

        return bestResult;
    }

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

        int finishedCar = 0;
        while (!candidate.isEmpty()) {
            if (candidate.size() % 100 == 0) {
                System.err.println(candidate.size());
            }
            Ride toAssign = candidate.get(0);
            int wastTime = Integer.MAX_VALUE;
            int vehicle = -1;
            for (int j = 0; j < nVehicle; j++) {
                Integer posTime = posTimes.get(j);
                if (posTime >= steps) {
                    finishedCar++;
                } else {
                    Pair pos;
                    if (result.get(j).rides().isEmpty()) {
                        pos = new Pair(0, 0);
                    } else {
                        pos = result.get(j).rides().get(result.get(j).rides().size() - 1).end();
                    }
                    int currWast = Math.max(toAssign.startT() - posTime, pos.distance(toAssign.start()));
                    if (currWast < wastTime && canFinish(toAssign, pos, posTime)) {
                        wastTime = currWast;
                        vehicle = j;
                    }
                }
            }
            if (finishedCar == nVehicle) {
                // break if every car doesn't have time
                break;
            }else if (vehicle == -1) {
                candidate.remove(0);
            }else {
                result.get(vehicle).rides().add(toAssign);
                int posTime = posTimes.get(vehicle) + wastTime + toAssign.start().distance(toAssign.end());
                posTimes.set(vehicle, posTime);
                candidate.remove(0);
            }
        }
        return result;
    }

    default List<Assign> greedyE(List<Ride> rides, final int steps, final int vehicle, final int bonus) {
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

    default List<Integer> noScoreRide(Assign assign, int steps) {
        // simulate by time
        List<Integer> noScoreRide = new ArrayList<>();
        List<Ride> rides = assign.rides();
        int t = 0;
        int r = 0;
        Pair pos = new Pair(0, 0);
        boolean start = false;
        while (r < rides.size()) {
            Ride ride = rides.get(r);
            if (t > steps) {
                return noScoreRide;
            }
            if (!start) {
                int distance = ride.start().distance(pos);
                t += distance;
                if (t <= ride.startT()) {
                    t = ride.startT();
                }
                start = true;

            } else {
                int distance = ride.start().distance(ride.end());
                if (t + distance > ride.endT()) {
                    noScoreRide.add(ride.id());
                }
                t += distance;
                pos = ride.end();
                r++;
                start = false;
            }

        }
        return noScoreRide;
    }

    default int finishTime(Assign assign, int steps) {
        // simulate by time

        List<Ride> rides = assign.rides();
        int t = 0;
        int r = 0;
        Pair pos = new Pair(0, 0);
        boolean start = false;
        while (r < rides.size()) {
            Ride ride = rides.get(r);
            if (t > steps) {
                return steps;
            }
            if (!start) {
                int distance = ride.start().distance(pos);
                t += distance;
                if (t <= ride.startT()) {
                    t = ride.startT();
                }
                start = true;

            } else {
                int distance = ride.start().distance(ride.end());
                t += distance;
                pos = ride.end();
                r++;
                start = false;
            }

        }
        return t;
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
                t += distance;
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
