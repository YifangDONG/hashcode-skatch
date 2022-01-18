package solution;

import java.util.Map;

public record Book(int id, int score) {

    public Book(Map.Entry<Integer, Integer> entry) {
        this(entry.getKey(), entry.getValue());
    }
}
