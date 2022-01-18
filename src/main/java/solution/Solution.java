package solution;

import java.util.*;
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
                .map(lib -> new Lib(lib.id(), lib.books().stream()
                        .sorted(Comparator.comparingInt(Book::score).reversed())
                        .mapToInt(Book::id)
                        .boxed()
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }
    default List<Lib> sortByBookValueAndLibInitDay(List<Library> libraries) {
        return libraries.stream()
                .sorted(Comparator.comparingInt(Library::nDay))
                .map(lib -> new Lib(lib.id(), lib.books().stream()
                        .sorted(Comparator.comparingInt(Book::score).reversed())
                        .mapToInt(Book::id)
                        .boxed()
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    default List<Lib> sortByBookValueAndLibBook(List<Library> libraries) {
        return libraries.stream()
                .sorted(Comparator.comparingInt(Library::nBook))
                .map(lib -> new Lib(lib.id(), lib.books().stream()
                        .sorted(Comparator.comparingInt(Book::score).reversed())
                        .mapToInt(Book::id)
                        .boxed()
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }



    default List<Lib> sortByBookValueAndLibBookCount(List<Library> libraries) {
        return libraries.stream()
                .sorted((b1,b2) -> b2.books().size() - b1.books().size())
                .map(lib -> new Lib(lib.id(), lib.books().stream()
                        .sorted(Comparator.comparingInt(Book::score).reversed())
                        .mapToInt(Book::id)
                        .boxed()
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    default List<Lib> sortByBookValueAndLibCapacity(List<Library> libraries) {
        return libraries.stream()
                .sorted(Comparator.comparingLong(Library::capacity))
                .map(lib -> new Lib(lib.id(), lib.books().stream()
                        .sorted(Comparator.comparingInt(Book::score).reversed())
                        .mapToInt(Book::id)
                        .boxed()
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }


    default List<Lib> unit_test_a() {
        return List.of(
                new Lib(1, List.of(5,2,3)),
                new Lib(0, List.of(0,1,2,3,4))
        );
    }

    default int score(int nday, List<Lib> libs, Map<Integer, Library> idToLibs, Map<Integer, Book> idToBooks) {
        Set<Integer> scannedBooks = new HashSet<>();
        int currentDay = 0;
        for(Lib lib : libs) {
            Library library = idToLibs.get(lib.id());
            int initDay = library.nDay();
            if(initDay + currentDay > nday) {
                break;
            }
            Set<Integer> initBooks = library.books().stream().map(Book::id).collect(Collectors.toSet());
            if(!initBooks.containsAll(lib.books())) {
                List<Integer> notExitBook = lib.books()
                        .stream()
                        .filter(id -> !initBooks.contains(id))
                        .collect(Collectors.toList());
                throw new IllegalArgumentException("books is not legal" + lib.id() + notExitBook);
            }

            currentDay += initDay;
            int restDay = nday - currentDay;
            if(restDay < 0) {
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
    };
}
