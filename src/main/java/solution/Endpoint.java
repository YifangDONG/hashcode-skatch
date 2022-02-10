package solution;

import java.util.Map;

// data center has id = -1
public record Endpoint(int id, Map<Integer, Integer> latency) {
}
