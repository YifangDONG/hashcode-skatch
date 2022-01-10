import collection.Iteration;
import io.Input;
import io.Output;
import logging.LogObjectFactory;
import me.tongfei.progressbar.ProgressBar;
import solution.Solution;
import solution.SolutionImpl;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static final int LOOP = 1;

    public static void main(String[] args) {

        List<String> files = List.of("example");
        String file = files.get(0);

        // read input
        List<List<String>> content = Input.read(file);

        // adapt input to model

        // calculate result
        Solution solution = LogObjectFactory.create(new SolutionImpl(), Solution.class);
        for (Integer i : ProgressBar.wrap(Iteration.range(0, LOOP), "generate solution")) {
            // do something to evaluate the solution
        }
        // adapt result to output

        // write output
        List<List<String>> result = new ArrayList<>();
        Output.write(file, result);
    }

}
