package summary;

import io.Input;
import io.Output;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Summary {

    private static final String DIR = "src\\main\\resources\\";
    private static final String FILE = "summary.txt";
    private static final Input INPUT = new Input(DIR);
    private static final Output OUTPUT = new Output(DIR);

    public static List<Result> getResults() {
        return INPUT.read(FILE)
                .stream()
                .map(line -> new Result(Case.valueOf(line.get(0)), Integer.parseInt(line.get(1)), Long.parseLong(line.get(2))))
                .collect(Collectors.toList());
    }

    public static Result getLastResult(Case aCase) {
        return getResults().stream()
                .filter(r -> r.aCase() == aCase)
                .max(Comparator.comparingInt(Result::count))
                .get();
    }

    public static Result getBestScore(Case aCase) {
        return getResults().stream()
                .filter(r -> r.aCase() == aCase)
                .max(Comparator.comparingLong(Result::score))
                .get();
    }


    public static void addResult(Case aCase, int count, long score) {
        List<String> result = List.of(aCase.name(), String.valueOf(count), String.valueOf(score));
        OUTPUT.append(FILE, result);
    }
}
