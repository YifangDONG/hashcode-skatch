package solution;

import java.util.List;

public record Request(int videoId, int endpointId, int count) {

    public Request(List<String> content) {
        this(Integer.parseInt(content.get(0)), Integer.parseInt(content.get(1)), Integer.parseInt(content.get(2)));
    }
}
