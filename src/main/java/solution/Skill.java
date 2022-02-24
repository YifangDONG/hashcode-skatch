package solution;

public record Skill(String type, int level) {

    Skill addLevel() {
        return new Skill(this.type, this.level + 1);
    }
}
