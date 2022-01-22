import collection.ListUtils;
import io.Input;
import io.Output;
import logging.LogObjectFactory;
import solution.*;
import summary.Case;
import summary.Summary;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {

    private static final int LOOP = 100;
    private static final List<Case> CASES = List.of(Case.a, Case.b, Case.c, Case.d, Case.e, Case.f);
    private static final Input INPUT = new Input("src\\main\\resources\\in\\");
    private static final Output OUTPUT = new Output("src\\main\\resources\\out\\");
    private static final Input READ_OUTPUT = new Input("src\\main\\resources\\out\\");

    public static void main(String[] args) {
//        showStatics();
        executeCase(Case.e);
//        testExampleA();
//        execute();
        getResultSummary();
    }

    private static void showStatics() {
        // each book has same value, only exist in one lib, each lib scan 1000 books, capacity = 1
        // => should sort on init days
        List<List<String>> content = INPUT.read(Case.f.name());
        int nLib = Integer.parseInt(content.get(0).get(1));
        Map<Integer, Book> idToBook = getIdToBook(content);
        List<Library> libs = getLibraries(content, nLib, idToBook);
//        Integer totalBooks = libs.stream()
//                .filter(lib -> List.of(30).contains(lib.nDay()) )
//                .map(library -> library.nBook())
//                .reduce(0, Integer::sum);
//        System.out.println(totalBooks);
//        Set<Book> unitBooks = libs.stream()
//                .filter(lib -> List.of(30).contains(lib.nDay()))
//                .flatMap(library -> library.books().stream())
//                .collect(Collectors.toSet());
//        System.out.println(unitBooks.size());

//        libs.stream()
//                .sorted(Comparator.comparingInt(Library::nDay))
//                .forEach(lib -> System.out.println(lib.nDay() + " " + lib.nBook() + " " + lib.capacity() + " " + lib.books().get(0)));
        libs.stream()
                .sorted(Comparator.comparingInt(Library::nDay))
//                .sorted(Comparator.comparingInt(lib -> totalValue(lib)))
                .forEach(lib -> System.out.println(lib.nDay() + " " + lib.nBook() + " " + lib.capacity() + " " + totalValue(lib)));

    }

    private static Integer totalValue(Library lib) {
//        return lib.books().stream().map(Book::score).reduce(0, Integer::sum);
        return lib.books()
                .stream()
                .sorted(Comparator.comparingInt(Book::score))
                .limit(Math.floorDiv(lib.nBook(), 2))
                .map(Book::score)
                .reduce(0, Integer::sum);
    }

    private static void getResultSummary() {
        for (int c = 0; c < 6; c++) {
            System.out.println(Summary.getBestScore(CASES.get(c)));
        }
    }

    private static void execute() {
        for (int c = 0; c < 6; c++) {
            executeCase(CASES.get(c));
        }
    }

    private static void executeCase(Case aCase) {
        var lastResult = Summary.getLastResult(aCase);

        // read input
        List<List<String>> content = INPUT.read(aCase.name());

        // adapt input to model
        int nBook = Integer.parseInt(content.get(0).get(0));
        int nLib = Integer.parseInt(content.get(0).get(1));
        int nDay = Integer.parseInt(content.get(0).get(2));

        Map<Integer, Book> idToBook = getIdToBook(content);
        List<Library> libs = getLibraries(content, nLib, idToBook);
        Map<Integer, Library> idToLibs = libs.stream()
                .collect(Collectors.toMap(
                        Library::id,
                        Function.identity()
                ));


        // calculate result
        Solution solution = LogObjectFactory.create(new SolutionImpl(), Solution.class);


        List<Lib> bestSolution = solution.greedy(libs, nDay);
        int bestScore = solution.score(nDay, bestSolution, idToLibs, idToBook);
        System.out.println(bestScore);

        // adapt result to output
        List<List<String>> result = new ArrayList<>();
        result.add(List.of(String.valueOf(bestSolution.size())));
        for (int i = 0; i < bestSolution.size(); i++) {
            result.add(List.of(String.valueOf(bestSolution.get(i).id()), String.valueOf(bestSolution.get(i).books().size())));
            result.add(bestSolution.get(i).books().stream().map(String::valueOf).collect(Collectors.toList()));
        }

        // write output
        int count = lastResult.count() + 1;
        OUTPUT.write(String.format("%s\\%d", aCase.name(), count), result);
        Summary.addResult(aCase, count, bestScore);
    }

    private static List<Library> getLibraries(List<List<String>> content, int nLib, Map<Integer, Book> idToBook) {
        List<Library> libs = new ArrayList<>(nLib);
        AtomicInteger libCount = new AtomicInteger(0);
        for (int i = 2; i < 2 + 2 * nLib; i += 2) {
            List<Book> bookInLib = content.get(i + 1)
                    .stream()
                    .mapToInt(Integer::parseInt)
                    .mapToObj(idToBook::get)
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
        return libs;
    }

    private static Map<Integer, Book> getIdToBook(List<List<String>> content) {
        Map<Integer, Book> books = ListUtils.indexList(
                        content.get(1).stream()
                                .mapToInt(Integer::parseInt)
                                .boxed()
                                .collect(Collectors.toList())
                )
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Book::new
                ));
        return books;
    }

    private static void testExampleA() {
        var aCase = CASES.get(0);

        // read input
        List<List<String>> content = INPUT.read(aCase.name());

        // adapt input to model
        int nBook = Integer.parseInt(content.get(0).get(0));
        int nLib = Integer.parseInt(content.get(0).get(1));
        int nDay = Integer.parseInt(content.get(0).get(2));

        Map<Integer, Book> books = getIdToBook(content);
        Map<Integer, Library> idToLibs = getLibraries(content, nLib, books)
                .stream()
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
