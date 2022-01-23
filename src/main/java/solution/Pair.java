package solution;

public record Pair(int x, int y) {

    Pair(String x, String y) {
        this(Integer.parseInt(x), Integer.parseInt(y));
    }

    public int distance(Pair p) {
        return Math.abs(this.x - p.x) + Math.abs(this.y - p.y);
    }
}
