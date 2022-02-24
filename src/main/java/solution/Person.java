package solution;

import java.util.List;
import java.util.Map;

public record Person(String name, Map<String, Skill> skills) {
}
