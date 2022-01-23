package solution;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// This class is used to adapt the raw content to the adapted data model
public class InputAdapter {

    private final List<List<String>> content;

    public InputAdapter(List<List<String>> content) {
        this.content = content;
    }

    public List<Ride> getRides() {
        int nRides = getNRides();
        List<Ride> rides = new ArrayList<>();
        for (int i = 0; i < nRides; i++) {
            List<String> line = content.get(i + 1);
            Pair start = new Pair(line.get(0), line.get(1));
            Pair end = new Pair(line.get(2), line.get(3));
            int startT = Integer.parseInt(line.get(4));
            int endT = Integer.parseInt(line.get(5));
            Ride ride = new Ride(i, start, end, startT, endT);
            rides.add(ride);
        }
        return rides;
    }

    public Map<Integer, Ride> getIdToRide() {
        return getRides().stream()
                .collect(Collectors.toMap(
                        Ride::id,
                        Function.identity()
                ));

    }


    public int getNRow() {
        return Integer.parseInt(content.get(0).get(0));
    }

    public int getNCol() {
        return Integer.parseInt(content.get(0).get(1));
    }

    public int getNVehicles() {
        return Integer.parseInt(content.get(0).get(2));
    }

    public int getNRides() {
        return Integer.parseInt(content.get(0).get(3));
    }

    public int getBonus() {
        return Integer.parseInt(content.get(0).get(4));
    }

    public int getNSteps() {
        return Integer.parseInt(content.get(0).get(5));
    }

    public List<Assign> readOutput(Map<Integer, Ride> idToRide) {
        List<Assign> assigns = new ArrayList<>();
        for (int i = 0; i < content.size(); i++) {
            List<String> line = content.get(i);
            List<Ride> rides = line.subList(1, line.size())
                    .stream()
                    .map(Integer::parseInt)
                    .map(idToRide::get)
                    .collect(Collectors.toList());
            assigns.add(new Assign(rides));
        }
        return assigns;
    }
}
