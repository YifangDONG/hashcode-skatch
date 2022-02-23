package solution;

import java.util.Map;

// data center has id = nCache + 1
public record Endpoint(int id, Map<Integer, Integer> latency) {
}
