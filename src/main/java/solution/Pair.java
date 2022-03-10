package solution;

public record Pair(int a, int b) {

    Pair plus(Pair pair) {
        return new Pair(pair.a() + a, pair.b() + b);
    }

    double average() {
        return 1.0 * a / b;
    }
}
