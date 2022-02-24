package or_tools_example;
// copy from or-tools java example
import java.util.ArrayList;
import java.util.List;

import com.google.ortools.Loader;
import com.google.ortools.algorithms.KnapsackSolver;

public class Knapsack {
    private Knapsack() {}

    private static void solve() {
        // [START solver]
        KnapsackSolver solver = new KnapsackSolver(
            KnapsackSolver.SolverType.KNAPSACK_MULTIDIMENSION_BRANCH_AND_BOUND_SOLVER, "test");
        // [END solver]

        // [START data]
        final long[] values = {360, 83, 59, 130, 431, 67, 230, 52, 93, 125, 670, 892, 600, 38, 48, 147,
            78, 256, 63, 17, 120, 164, 432, 35, 92, 110, 22, 42, 50, 323, 514, 28, 87, 73, 78, 15, 26,
            78, 210, 36, 85, 189, 274, 43, 33, 10, 19, 389, 276, 312};

        final long[][] weights = {{7, 0, 30, 22, 80, 94, 11, 81, 70, 64, 59, 18, 0, 36, 3, 8, 15, 42, 9,
            0, 42, 47, 52, 32, 26, 48, 55, 6, 29, 84, 2, 4, 18, 56, 7, 29, 93, 44, 71, 3, 86, 66, 31,
            65, 0, 79, 20, 65, 52, 13}};

        final long[] capacities = {850};
        // [END data]

        // [START solve]
        solver.init(values, weights, capacities);
        final long computedValue = solver.solve();
        // [END solve]

        // [START print_solution]
        List<Integer> packedItems = new ArrayList<>();
        List<Long> packedWeights = new ArrayList<>();
        int totalWeight = 0;
        System.out.println("Total value = " + computedValue);
        for (int i = 0; i < values.length; i++) {
            if (solver.bestSolutionContains(i)) {
                packedItems.add(i);
                packedWeights.add(weights[0][i]);
                totalWeight = (int) (totalWeight + weights[0][i]);
            }
        }
        System.out.println("Total weight: " + totalWeight);
        System.out.println("Packed items: " + packedItems);
        System.out.println("Packed weights: " + packedWeights);
        // [END print_solution]
    }

    public static void main(String[] args) throws Exception {
        Loader.loadNativeLibraries();
        Knapsack.solve();
    }
}
