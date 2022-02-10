package solution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class OutputAdapter {

    public static List<List<String>> adapt(List<Server> servers) {
        List<List<String>> result = new ArrayList<>();
        result.add(List.of(String.valueOf(servers.size())));
        for(Server server : servers) {
            var line = new ArrayList<String>();
            line.add(String.valueOf(server.id()));
            var videos = server.videos().stream().map(String::valueOf).collect(Collectors.toList());
            line.addAll(videos);
            result.add(line);
        }
        return result;
    }
}
