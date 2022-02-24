package solution;

import java.util.Map;

public record Person(String name, Map<String, Skill> skills) {

    Person addSkillLevel(Skill skill) {
        var newLevel = this.skills.getOrDefault(skill.type(), new Skill(skill.type(), 0)).addLevel();
        this.skills.put(skill.type(), newLevel);
        return new Person(this.name, this.skills);
    }
}
