package solution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import summary.Result;

// This class is used to adapt the raw content to the adapted data model
public class InputAdapter {

    private final List<List<String>> content;

    public InputAdapter(List<List<String>> content) {
        this.content = content;
    }

    public int nVideo() {
        return Integer.parseInt(content.get(0).get(0));
    }

    public int nEndpoints() {
        return Integer.parseInt(content.get(0).get(1));
    }

    public int nRequest() {
        return Integer.parseInt(content.get(0).get(2));
    }

    public int nCache() {
        return Integer.parseInt(content.get(0).get(3));
    }

    public int cacheSize() {
        return Integer.parseInt(content.get(0).get(4));
    }

    public List<Video> videos() {
        List<Video> videos = new ArrayList<>();
        List<Integer> sizes = content.get(1).stream()
            .map(Integer::parseInt)
            .collect(Collectors.toList());
        for (int i = 0; i < sizes.size(); i++) {
            videos.add(new Video(i, sizes.get(i)));
        }
        return videos;
    }

    public int[] videoSizes() {
        return content.get(1).stream()
            .map(Integer::parseInt)
            .mapToInt(Integer::intValue)
            .toArray();
    }

    public List<Endpoint> endpoints() {
        int n = nEndpoints();
        List<Endpoint> endpoints = new ArrayList<>();
        int i = 2;
        while (endpoints.size() < n) {
            int centerLatency = Integer.parseInt(content.get(i).get(0));
            int nCacheConnected = Integer.parseInt(content.get(i).get(1));
            Map<Integer, Integer> latency = new HashMap<>();
            latency.put(nCache(), centerLatency);
            i++;
            for (int j = 0; j < nCacheConnected; j++) {
                latency.put(Integer.parseInt(content.get(i).get(0)), Integer.parseInt(content.get(i).get(1)));
                i++;
            }
            endpoints.add(new Endpoint(endpoints.size(), latency));
        }
        return endpoints;
    }

    public Table<Integer, Integer, Integer> endpointToCacheLatency() {
        Table<Integer, Integer, Integer> table = HashBasedTable.create();
        var endpoints = endpoints();
        for (Endpoint endpoint : endpoints) {
            endpoint.latency()
                .forEach((cache, latency) -> table.put(endpoint.id(), cache, latency));
        }
        return table;
    }

    public int totalCounts() {
        return requests().stream().map(Request::count).mapToInt(Integer::intValue).sum();
    }

    public List<Request> requests() {
        List<Request> requests = new ArrayList<>();
        for (int i = content.size() - nRequest(); i < content.size(); i++) {
            requests.add(new Request(content.get(i)));
        }
        return requests;
    }

    public List<Server> servers() {
        List<Server> servers = new ArrayList<>();
        int n = Integer.parseInt(content.get(0).get(0));
        for (int i = 1; i < n + 1; i++) {
            List<String> serverDsc = content.get(i);
            int id = Integer.parseInt(serverDsc.get(0));
            var videos = serverDsc.subList(1, serverDsc.size())
                .stream()
                .map(Integer::parseInt)
                .collect(Collectors.toSet());
            servers.add(new Server(id, videos));
        }
        return servers;
    }

    public Map<Integer, Endpoint> endpointMap() {
        return endpoints().stream().collect(Collectors.toMap(
            Endpoint::id,
            Function.identity()
        ));
    }

    public Map<Integer, Integer> videoToSize() {
        return videos().stream().collect(Collectors.toMap(
            Video::id,
            Video::size
        ));
    }
}
