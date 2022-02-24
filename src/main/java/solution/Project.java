package solution;

import java.util.List;

public record Project(String name, int days, int reward, int before, List<Skill> skills) {
}
