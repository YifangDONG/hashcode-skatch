import collection.Iteration;
import collection.ListUtils;
import io.Input;
import io.Output;
import logging.LogObjectFactory;
import me.tongfei.progressbar.ProgressBar;
import solution.Endpoint;
import solution.InputAdapter;
import solution.OutputAdapter;
import solution.Request;
import solution.Server;
import solution.Solution;
import solution.SolutionImpl;
import solution.Video;
import summary.Case;
import summary.Summary;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {

    private static final int LOOP = 1;
    private static final List<Case> CASES = List.of(Case.a, Case.b, Case.c, Case.d, Case.e);
    private static final Input INPUT = new Input("src\\main\\resources\\in\\");
    private static final Output OUTPUT = new Output("src\\main\\resources\\out\\");
    private static final Input READ_OUTPUT = new Input("src\\main\\resources\\out\\");

    public static void main(String[] args) {

//        testExampleA();
                execute();
                getResultSummary();
    }

    private static void testExampleA() {
        // adapt to output model
        InputAdapter inputAdapter = new InputAdapter(INPUT.read(Case.a.name()));
        InputAdapter resultAdapter = new InputAdapter(READ_OUTPUT.read("a\\0"));
        Solution solution = new SolutionImpl();

        Map<Integer, Endpoint> endpointMap = inputAdapter.endpoints().stream().collect(Collectors.toMap(
            Endpoint::id,
            Function.identity()
        ));

        List<Server> servers = resultAdapter.servers();

        Map<Integer, Set<Integer>> videoToCache = solution.videoToCache(servers);
        double score = solution.score(videoToCache, null, endpointMap, inputAdapter.requests());
        System.out.println(score);

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
        var requests = inputAdapter.requests();
        Map<Integer, Endpoint> endpointMap = inputAdapter.endpoints().stream().collect(Collectors.toMap(
            Endpoint::id,
            Function.identity()
        ));
        var videoToSize = inputAdapter.videos().stream().collect(Collectors.toMap(
            Video::id,
            Video::size
        ));
        var result = solution.greedy(requests, inputAdapter.cacheSize(), inputAdapter.nCache(), videoToSize, endpointMap);

        Map<Integer, Set<Integer>> videoToCache = solution.videoToCache(result);
        double score = solution.score(videoToCache, null, endpointMap, inputAdapter.requests());
        System.out.printf("%s\\%d%n",  aCase.name(), (long)score);
        int count = lastResult.count() + 1;
        // adapt score to output

        // write output
        List<List<String>> outputContent = OutputAdapter.adapt(result);
        OUTPUT.write(String.format("%s\\%d", aCase.name(), count), outputContent);
        Summary.addResult(aCase, count, (long) score);
    }

}
