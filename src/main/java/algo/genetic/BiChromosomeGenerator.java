package algo.genetic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BiChromosomeGenerator {

    public static List<Integer> generate(int length, double probability) {
        List<Integer> result = new ArrayList<>();
        Random random = new Random();
        double compare = length * probability;
        for (int i = 0; i < length; i++) {
            double v = random.nextDouble(length);
            if (v < compare) {
                result.add(1);
            } else {
                result.add(0);
            }
        }
        return result;
    }
}
