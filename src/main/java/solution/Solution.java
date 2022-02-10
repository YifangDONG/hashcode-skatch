package solution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface Solution {
    // default impl is done in the interface to be able to use the logging

    default List<Server> greedy(List<Request> requests, int capacity, int cacheCount,
        Map<Integer, Integer> vidToSize, Map<Integer, Endpoint> endpoints ) {

        Map<Integer, Server> result = new HashMap<>();

        HashMap<Integer, Integer> cacheStatus = createCache(capacity, cacheCount);

        for(Request request : requests) {
            var videoId = request.videoId();
            Integer videoSize = vidToSize.get(videoId);
            var endpointId = request.endpointId();
            var connectedCache = endpoints.get(endpointId).latency().entrySet()
                .stream()
                .filter(e -> e.getKey() != -1)
                .map(e -> new Cache(e.getKey(), e.getValue()))
                .sorted(Comparator.comparingInt(Cache::latency))
                .collect(Collectors.toList());
            for(Cache cache : connectedCache) {
                var remaindSize = cacheStatus.get(cache.id()) - videoSize;
                if(remaindSize >= 0) {
                    cacheStatus.put(cache.id(), remaindSize);
                    if(result.containsKey(cache.id())) {
                        result.get(cache.id()).videos().add(videoId);
                    }else {
                        var videos = new HashSet<Integer>();
                        videos.add(videoId);
                        result.put(cache.id(), new Server(cache.id(), videos));
                    }
                    break;
                }
            }
        }
        return new ArrayList<>(result.values());

    }

    private HashMap<Integer, Integer> createCache(int capacity, int cacheCount) {
        var cacheStatus = new HashMap<Integer, Integer>();
        for (int i = 0; i < cacheCount; i++) {
            cacheStatus.put(i, capacity);
        }
        return cacheStatus;
    }

    default Map<Integer, Set<Integer>> videoToCache(List<Server> servers) {
        Map<Integer, Set<Integer>> result = new HashMap<>();
        for (Server server : servers) {
            Map<Integer, Integer> videoToCache = server.videos().stream()
                .collect(Collectors.toMap(Function.identity(), ignore -> server.id()));
            for (Map.Entry<Integer, Integer> e : videoToCache.entrySet()) {
                if (!result.containsKey(e.getKey())) {
                    result.put(e.getKey(), new HashSet<>());
                }
                result.get(e.getKey()).add(e.getValue());
            }
        }
        return result;
    }

    default double score(Map<Integer, Set<Integer>> videoToCache, Map<Integer, Video> videos,
        Map<Integer, Endpoint> endpoints,
        List<Request> requests) {

        long score = 0;
        long totalRequests = 0;
        for (Request request : requests) {
            totalRequests += request.count();

            int videoId = request.videoId();
            Set<Integer> cacheIds = videoToCache.get(videoId);
            if (cacheIds != null && !cacheIds.isEmpty()) {
                int endpointId = request.endpointId();
                Endpoint endpoint = endpoints.get(endpointId);
                Integer centerL = endpoint.latency().get(-1);
                int minLatency = centerL;
                for (Integer cacheId : cacheIds) {
                    Integer latency = endpoint.latency().get(cacheId);
                    if(latency != null) { // endpoint doesn't connect to this cache
                        if (latency < minLatency) {
                            minLatency = latency;
                        }
                    }
                }
                score += request.count() * (centerL - minLatency);
            }
        }
        return 1.0 * score / totalRequests * 1000;
    }
}
