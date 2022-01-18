import collection.Iteration;
import collection.ListUtils;
import io.Input;
import io.Output;
import io.ReadOutput;
import logging.LogObjectFactory;
import me.tongfei.progressbar.ProgressBar;
import solution.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {

    public static final int LOOP = 1;

    public static void main(String[] args) {

        List<String> files = List.of(
                "a_example.txt",
                "b_read_on.txt",
                "c_incunabula.txt",
                "d_tough_choices.txt",
                "e_so_many_books.txt",
                "f_libraries_of_the_world.txt");

        for(int k = 0 ; k < 6; k++) {
        String file = files.get(k);

        // read input
        List<List<String>> content = Input.read(file);

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
        for (Integer i : ProgressBar.wrap(Iteration.range(0, LOOP), "generate solution")) {
            // do something to evaluate the solution
        }
        // adapt result to output
        List<List<String>> result = new ArrayList<>();
        result.add(List.of(String.valueOf(example.size())));
        for (int i = 0; i < example.size(); i++) {
            result.add(List.of(String.valueOf(example.get(i).id()), String.valueOf(example.get(i).books().size())));
            result.add(example.get(i).books().stream().map(String::valueOf).collect(Collectors.toList()));
        }


//         write output
            Output.write(file, result);
    }
    }

    private static List<Lib> readFromOutput(String file) {
        List<List<String>> outputContent = ReadOutput.read(file);
        List<Lib> example = new ArrayList<>();
        int nlib = Integer.parseInt(outputContent.get(0).get(0));
        for (int i = 1; i < 1 + 2 * nlib; i += 2) {
            List<Integer> libBooks = outputContent.get(i + 1).stream()
                    .mapToInt(Integer::parseInt)
                    .boxed()
                    .collect(Collectors.toList());
            example.add(new Lib(Integer.parseInt(outputContent.get(i).get(0)), libBooks));
        }
        return example;
    }

}
