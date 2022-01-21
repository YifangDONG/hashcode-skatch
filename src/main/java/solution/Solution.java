package solution;

import java.util.*;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

public interface Solution {
    // default impl is done in the interface to be able to use the logging


    default List<Lib> dummy(List<Library> libraries) {
        return libraries.stream()
                .map(lib -> new Lib(lib.id(), lib.books().stream().mapToInt(Book::id).boxed().collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    default List<Lib> sortByBookValue(List<Library> libraries) {
        return libraries.stream()
                .map(bookSortedByValue())
                .collect(Collectors.toList());
    }

    default List<Lib> generateUniqueBook(List<Library> libraries, int nday) {
        List<Library> libs = libraries.stream()
                .sorted(Comparator.comparingInt(compareInitDayByN())
                        .thenComparing(Comparator.comparingInt(compareBookValueF()).reversed())
                        .thenComparing(Comparator.comparingLong(Library::capacity).reversed()))
//                        .thenComparingInt(Library::nDay)
                .collect(Collectors.toList());

        Set<Integer> scannedBooks = new HashSet<>();
        int currentDay = 0;
        List<Lib> result = new ArrayList<>();
        for (Library lib : libs) {
            int initDay = lib.nDay();
            if (initDay + currentDay > nday) {
                break;
            }
            currentDay += initDay;
            int restDay = nday - currentDay;
            if (restDay < 0) {
                break;
            }
            long totalCap = restDay * lib.capacity();
            List<Integer> scanned = lib.books()
                    .stream()
                    .filter(book -> !scannedBooks.contains(book.id()))
                    .sorted(Comparator.comparingInt(Book::score).reversed())
                    .limit(totalCap)
                    .map(Book::id)
                    .collect(Collectors.toList());
            scannedBooks.addAll(scanned);
            result.add(new Lib(lib.id(), scanned));
        }
        return result;
    }

    default List<Lib> sortByTotalBookValue(List<Library> libraries) {
        return libraries.stream()
                .sorted(Comparator.comparingInt(compareBookValue()).reversed())
                .map(bookSortedByValue())
                .collect(Collectors.toList());
    }

    default List<Lib> solutionC(List<Library> libraries) {
        return libraries.stream()
                .sorted(Comparator.comparingInt(Library::nDay)
                        .thenComparing(Comparator.comparingInt(compareBookValue())))
                .sorted(Comparator.comparingInt(compareBookValue()))
                .map(bookSortedByValue())
                .collect(Collectors.toList());
    }

    private ToIntFunction<Library> compareBookValue() {
        return lib -> lib.books().stream().map(Book::score).reduce(0, Integer::sum);
    }

   private ToIntFunction<Library> compareInitDayByN() {
        return lib -> Math.floorDiv(lib.nDay(), 10);
    }

    private ToIntFunction<Library> compareBookValueF() {
        return lib -> Math.floorDiv(lib.books()
                .stream()
                .sorted(Comparator.comparingInt(Book::score))
                .limit(Math.floorDiv(lib.nBook(), 2))
                .map(Book::score)
                .reduce(0, Integer::sum), 10000);
    }

    default List<Lib> sortByBookValueAndLibInitDay(List<Library> libraries) {
        return libraries.stream()
                .sorted(Comparator.comparingInt(Library::nDay))
                .map(bookSortedByValue())
                .collect(Collectors.toList());
    }

    private Function<Library, Lib> bookSortedByValue() {
        return lib -> new Lib(lib.id(), lib.books().stream()
                .sorted(Comparator.comparingInt(Book::score).reversed())
                .mapToInt(Book::id)
                .boxed()
                .collect(Collectors.toList()));
    }

    default List<Lib> sortByBookValueAndLibBook(List<Library> libraries) {
        return libraries.stream()
                .sorted(Comparator.comparingInt(Library::nBook))
                .map(bookSortedByValue())
                .collect(Collectors.toList());
    }


    default List<Lib> sortByBookValueAndLibBookCount(List<Library> libraries) {
        return libraries.stream()
                .sorted((b1, b2) -> b2.books().size() - b1.books().size())
                .map(bookSortedByValue())
                .collect(Collectors.toList());
    }

    default List<Lib> sortByBookValueAndLibCapacity(List<Library> libraries) {
        return libraries.stream()
                .sorted(Comparator.comparingLong(Library::capacity))
                .map(bookSortedByValue())
                .collect(Collectors.toList());
    }


    default List<Lib> unit_test_a() {
        return List.of(
                new Lib(1, List.of(5, 2, 3)),
                new Lib(0, List.of(0, 1, 2, 3, 4))
        );
    }

    default int score(int nday, List<Lib> libs, Map<Integer, Library> idToLibs, Map<Integer, Book> idToBooks) {
        Set<Integer> scannedBooks = new HashSet<>();
        int currentDay = 0;
        for (Lib lib : libs) {
            Library library = idToLibs.get(lib.id());
            int initDay = library.nDay();
            if (initDay + currentDay > nday) {
                break;
            }
            Set<Integer> initBooks = library.books().stream().map(Book::id).collect(Collectors.toSet());
            if (!initBooks.containsAll(lib.books())) {
                List<Integer> notExitBook = lib.books()
                        .stream()
                        .filter(id -> !initBooks.contains(id))
                        .collect(Collectors.toList());
                throw new IllegalArgumentException("books is not legal" + lib.id() + notExitBook);
            }

            currentDay += initDay;
            int restDay = nday - currentDay;
            if (restDay < 0) {
                break;
            }
            long totalCap = restDay * library.capacity();
            Set<Integer> scanned = lib.books().stream().limit(totalCap).collect(Collectors.toSet());
            scannedBooks.addAll(scanned);
        }
        return scannedBooks.stream()
                .map(idToBooks::get)
                .map(Book::score)
                .reduce(0, Integer::sum);
    }

    ;
}
