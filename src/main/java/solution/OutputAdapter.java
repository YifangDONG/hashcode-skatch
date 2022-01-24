package solution;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class OutputAdapter {

    public static List<List<String>> adapt(List<Assign> assigns) {
        List<List<String>> result = new ArrayList<>();
        for (Assign assign : assigns) {
            List<String> line = new ArrayList<>();
            List<Ride> rides = assign.rides();
            line.add(String.valueOf(rides.size()));
            List<String> ids = rides
                    .stream()
                    .map(Ride::id)
                    .map(String::valueOf)
                    .collect(Collectors.toList());
            line.addAll(ids);
            result.add(line);
        }
       return result;
    }
}
