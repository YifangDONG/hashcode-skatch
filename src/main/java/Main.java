import collection.Iteration;
import io.Input;
import io.Output;
import logging.LogObjectFactory;
import me.tongfei.progressbar.ProgressBar;
import solution.InputAdapter;
import solution.OutputAdapter;
import solution.Solution;
import solution.SolutionImpl;
import summary.Case;
import summary.Summary;

import java.util.List;

public class Main {

    private static final int LOOP = 1;
    private static final List<Case> CASES = List.of(Case.a, Case.b, Case.c, Case.d, Case.e, Case.f);
    private static final Input INPUT = new Input("src\\main\\resources\\in\\");
    private static final Output OUTPUT = new Output("src\\main\\resources\\out\\");
    private static final Input READ_OUTPUT = new Input("src\\main\\resources\\out\\");

    public static void main(String[] args) {

        testExampleA();
//        execute();
//        getResultSummary();
    }

    private static void testExampleA() {
        // adapt to output model
        InputAdapter inputAdapter = new InputAdapter(INPUT.read(Case.a.name()));
        InputAdapter resultAdapter = new InputAdapter(READ_OUTPUT.read("a\\0"));
        // calculate the score to test the score function is correct
    }

    private static void getResultSummary() {
        for (Case aCase : CASES) {
            System.out.println(Summary.getBestScore(aCase));
        }
    }

    private static void execute() {
        for (Case aCase : CASES) {
            executeCase(aCase);
        }
    }

    private static void executeCase(Case aCase) {

        var lastResult = Summary.getLastResult(aCase);

        // read input
        List<List<String>> content = INPUT.read(aCase.name());

        // adapt input to model
        InputAdapter inputAdapter = new InputAdapter(content);

        // calculate score
        Solution solution = LogObjectFactory.create(new SolutionImpl(), Solution.class);
        for (Integer i : ProgressBar.wrap(Iteration.range(0, LOOP), "generate solution")) {
            // do something to evaluate the solution
        }

        long score = solution.score();

        // adapt score to output
        var outputAdapter = new OutputAdapter();
        List<List<String>> outputContent = outputAdapter.adapt();

        // write output
        int count = lastResult.count() + 1;
        OUTPUT.write(String.format("%s\\%d", aCase.name(), count), outputContent);
        Summary.addResult(aCase, count, score);
    }

}
