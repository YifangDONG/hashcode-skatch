package solution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public List<Endpoint> endpoints() {
        int n = nEndpoints();
        List<Endpoint> endpoints = new ArrayList<>();
        int i = 2;
        while (endpoints.size() < n) {
            int center = Integer.parseInt(content.get(i).get(0));
            int nCache = Integer.parseInt(content.get(i).get(1));
            Map<Integer, Integer> latency = new HashMap<>();
            latency.put(-1, center);
            i++;
            for (int j = 0; j < nCache; j++) {
                latency.put(Integer.parseInt(content.get(i).get(0)), Integer.parseInt(content.get(i).get(1)));
                i++;
            }
            endpoints.add(new Endpoint(endpoints.size(), latency));
        }
        return endpoints;
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
}
