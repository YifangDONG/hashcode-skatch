import collection.Iteration;
import collection.ListUtils;
import io.Input;
import io.Output;
import io.ReadOutput;
import logging.LogObjectFactory;
import me.tongfei.progressbar.ProgressBar;
import solution.*;
import summary.Case;
import summary.Summary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    private static void getResultSummary() {
        for(int c = 0; c < 6; c++) {
            System.out.println(Summary.getBestScore(CASES.get(c)));
        }
    }

    private static void execute() {
        for (int c = 0; c < 6; c++) {
            executeCase(c);
        }
    }

    private static void executeCase(int c) {
        var aCase = CASES.get(c);
        var lastResult = Summary.getLastResult(aCase);

        // read input
        List<List<String>> content = INPUT.read(aCase.name());

        // adapt input to model
        int nBook = Integer.parseInt(content.get(0).get(0));
        int nLib = Integer.parseInt(content.get(0).get(1));
        int nDay = Integer.parseInt(content.get(0).get(2));

        List<Integer> bookValues = content.get(1).stream()
                .mapToInt(Integer::parseInt)
                .boxed()
                .collect(Collectors.toList());
        Map<Integer, Book> books = ListUtils.indexList(bookValues)
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Book::new
                ));

        List<Library> libs = new ArrayList<>(nLib);
        AtomicInteger libCount = new AtomicInteger(0);
        for (int i = 2; i < 2 + 2 * nLib; i += 2) {
            List<Book> bookInLib = content.get(i + 1)
                    .stream()
                    .mapToInt(Integer::parseInt)
                    .mapToObj(books::get)
                    .collect(Collectors.toList());
            Library lib = new Library(
                    libCount.getAndIncrement(),
                    Integer.parseInt(content.get(i).get(0)),
                    Integer.parseInt(content.get(i).get(1)),
                    Integer.parseInt(content.get(i).get(2)),
                    bookInLib
            );
            libs.add(lib);
        }
        Map<Integer, Library> idToLibs = libs.stream()
                .collect(Collectors.toMap(
                        Library::id,
                        Function.identity()
                ));


        // calculate result
        Solution solution = LogObjectFactory.create(new SolutionImpl(), Solution.class);

//        List<Lib> example = readFromOutput("b_18.out");
//        List<Lib> example = solution.unit_test_a();
//        List<Lib> example = solution.dummy(libs);
//        List<Lib> example = solution.sortByBookValue(libs);
        List<Lib> example = solution.sortByBookValueAndLibInitDay(libs);
//            List<Lib> example = solution.sortByBookValueAndLibBook(libs);
//            List<Lib> example = solution.sortByBookValueAndLibBookCount(libs);
//            List<Lib> example = solution.sortByBookValueAndLibCapacity(libs);

        int score = solution.score(nDay, example, idToLibs, books);
        System.out.println(score);

        // adapt result to output
        List<List<String>> result = new ArrayList<>();
        result.add(List.of(String.valueOf(example.size())));
        for (int i = 0; i < example.size(); i++) {
            result.add(List.of(String.valueOf(example.get(i).id()), String.valueOf(example.get(i).books().size())));
            result.add(example.get(i).books().stream().map(String::valueOf).collect(Collectors.toList()));
        }

        // write output
        int count = lastResult.count() + 1;
        OUTPUT.write(String.format("%s\\%d", aCase.name(), count), result);
        Summary.addResult(aCase, count, score);
    }

    private static void testExampleA() {
        var aCase = CASES.get(0);

        // read input
        List<List<String>> content = INPUT.read(aCase.name());

        // adapt input to model
        int nBook = Integer.parseInt(content.get(0).get(0));
        int nLib = Integer.parseInt(content.get(0).get(1));
        int nDay = Integer.parseInt(content.get(0).get(2));

        List<Integer> bookValues = content.get(1).stream()
                .mapToInt(Integer::parseInt)
                .boxed()
                .collect(Collectors.toList());
        Map<Integer, Book> books = ListUtils.indexList(bookValues)
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Book::new
                ));

        List<Library> libs = new ArrayList<>(nLib);
        AtomicInteger libCount = new AtomicInteger(0);
        for (int i = 2; i < 2 + 2 * nLib; i += 2) {
            List<Book> bookInLib = content.get(i + 1)
                    .stream()
                    .mapToInt(Integer::parseInt)
                    .mapToObj(books::get)
                    .collect(Collectors.toList());
            Library lib = new Library(
                    libCount.getAndIncrement(),
                    Integer.parseInt(content.get(i).get(0)),
                    Integer.parseInt(content.get(i).get(1)),
                    Integer.parseInt(content.get(i).get(2)),
                    bookInLib
            );
            libs.add(lib);
        }
        Map<Integer, Library> idToLibs = libs.stream()
                .collect(Collectors.toMap(
                        Library::id,
                        Function.identity()
                ));

        List<List<String>> resultContent = READ_OUTPUT.read("a\\0");
        // adapt to output model
        // calculate the score to test the score function is correct
        List<Lib> example = new ArrayList<>();
        int nlib = Integer.parseInt(resultContent.get(0).get(0));
        for (int i = 1; i < 1 + 2 * nlib; i += 2) {
            List<Integer> libBooks = resultContent.get(i + 1).stream()
                    .mapToInt(Integer::parseInt)
                    .boxed()
                    .collect(Collectors.toList());
            example.add(new Lib(Integer.parseInt(resultContent.get(i).get(0)), libBooks));
        }

        SolutionImpl solution = new SolutionImpl();
        int score = solution.score(nDay, example, idToLibs, books);
        assert score == 16;
    }

}
