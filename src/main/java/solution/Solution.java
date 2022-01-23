package solution;

import java.util.*;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

public interface Solution {
    // default impl is done in the interface to be able to use the logging


    default List<Lib> solutionD(Map<Integer, Library> libraries, int nDay, int nBook, int nLibs) {
        // all books has score 65
        // signup = 2, capacity = 1

        // count lib group by the nBooks (there's 4 libs only have 1 book):
        // {1=4, 2=60, 3=334, 4=1234, 5=2911, 6=4976, 7=6437, 8=6122, 9=4430, 10=2291, 11=939, 12=225, 13=35, 14=2}

        // 15000 books exist in 2 lib, 63600 books exist in 3 libs {2=15000, 3=63600}

        // 30001 days can signup 30001 / 2 = 15000 libs
        // => chose 15000 libs to have max diff number of books scanned

        // greedy with getLibScoreD -> 77530 * 65
        // target 78571

        int best = 0;
        List<Boolean> bestSigned = null;
        List<Integer> bestScanned = null;

        int scannedCount = 0;
        int signedCount = 0;
        int signedLimit = nDay / 2;
        List<Boolean> signed = new ArrayList<>(Collections.nCopies(nLibs, false));
        List<Integer> scanned = new ArrayList<>(Collections.nCopies(nBook, 0));

        int loop = 5_000_000_00;
        for (int rep = 0; rep < loop; rep++) {
            // random select a lib and flip it
            Random random = new Random();
            int li = random.nextInt(nLibs);
            if (signed.get(li)) {
                // lib is selected, calculate the lost
                int lost = 0;
                for (Book book : libraries.get(li).books()) {
                    if (scanned.get(book.id()) == 1) {
                        lost++;
                    }
                }
//                if (lost * (Math.exp(0.1 * rep / loop) - 1) <= 2) { // do flip, remove this lib from the signedUp libs
//                if (lost <= 1 && 0.1 * rep / loop < 0.55 || lost == 0) { // do flip, remove this lib from the signedUp libs
                if (lost <= 1) { // do flip, remove this lib from the signedUp libs
                    // TODO improve the accept probability
                    signedCount--;
                    signed.set(li, false);
                    for (Book book : libraries.get(li).books()) {
                        if (scanned.get(book.id()) == 1) {
                            scannedCount--;
                        }
                        scanned.set(book.id(), scanned.get(book.id()) - 1);
                    }
                }
            } else {
                // lib is not selected, calculate the gain
                int gain = 0;
                for (Book book : libraries.get(li).books()) {
                    if (scanned.get(book.id()) == 0) {
                        gain++;
                    }
                }
                if (gain > 0 && signedCount < signedLimit) {
                    signedCount++;
                    signed.set(li, true);
                    for (Book book : libraries.get(li).books()) {
                        if (scanned.get(book.id()) == 0) {
                            scannedCount++;
                        }
                        scanned.set(book.id(), scanned.get(book.id()) + 1);
                    }
                }
            }

            if (scannedCount > best) {
                best = scannedCount;
                bestSigned = new ArrayList<>(signed);
                bestScanned = new ArrayList<>(scanned);
            }
            if (rep % 1000_000 == 0) {
                System.out.println(best);
            }
        }

        List<Lib> result = new ArrayList<>();
        for (int i = 0; i < nLibs; i++) {
            if (bestSigned.get(i)) {
                Library library = libraries.get(i);
                List<Integer> books = new ArrayList<>();
                for (Book book : library.books()) {
                    if (bestScanned.get(book.id()) > 0) {
                        books.add(book.id());
                        bestScanned.set(book.id(), bestScanned.get(book.id()) - 1);
                    }
                }
                result.add(new Lib(i, books));
            }
        }
        return result;
    }

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

    default List<Lib> greedy(List<Library> libraries, int nDay, Map<Integer, Integer> bookFreq) {
        List<Library> toChose = new ArrayList<>(libraries);
        int currentDay = 0;
        List<Lib> result = new ArrayList<>();
        Set<Integer> scannedBooks = new HashSet<>();
        while (currentDay < nDay) {
            Library lib = getBestLibrary(toChose, nDay - currentDay, scannedBooks, bookFreq);
            if (lib == null) {
                break;
            }
            toChose.remove(lib);
            currentDay += lib.nDay();

            long totalCap = (nDay - currentDay) * lib.capacity();
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

    private Library getBestLibrary(List<Library> libraries, int remainDay, Set<Integer> scannedBooks, Map<Integer, Integer> bookFreq) {
        double bestScore = 0;
        Library best = null;
        for (Library library : libraries) {
            double score = getLibScoreD(library, remainDay, scannedBooks, bookFreq);
            if (score > bestScore) {
                bestScore = score;
                best = library;
            }
        }

        return best;
    }

    private long getLibScore(Library library, int remainDay, Set<Integer> scannedBooks) {
        int days = remainDay - library.nDay();
        if (days <= 0) {
            return 0;
        }
        long score = library.books().stream()
                .filter(book -> !scannedBooks.contains(book.id()))
                .sorted(Comparator.comparingInt(Book::score).reversed())
                .limit(days * library.capacity())
                .map(Book::score)
                .map(Long::valueOf)
                .reduce(0L, Long::sum);
        return score / library.nDay();
    }

    private double getLibScoreD(Library library, int remainDay, Set<Integer> scannedBooks, Map<Integer, Integer> bookFreq) {
        int days = remainDay - library.nDay();
        if (days <= 0) {
            return 0;
        }
        double score = library.books().stream()
                .filter(book -> !scannedBooks.contains(book.id()))
                .sorted(Comparator.comparingInt(Book::score).reversed())
                .limit(days * library.capacity())
                .map(book -> book.score() / bookFreq.get(book.id()))
                .map(Double::valueOf)
                .reduce(0d, Double::sum);
        return score / library.nDay();
    }


    default List<Lib> generateUniqueBook(List<Library> libraries, int nday) {
        List<Library> libs = libraries.stream()
                .sorted(Comparator.comparingInt(compareInitDayByN())
                        .thenComparing(Comparator.comparingLong(compareBookValueF()).reversed())
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
                .sorted(Comparator.comparingLong(compareBookValue()).reversed())
                .map(bookSortedByValue())
                .collect(Collectors.toList());
    }

    default List<Lib> solutionC(List<Library> libraries) {
        return libraries.stream()
                .sorted(Comparator.comparingInt(Library::nDay)
                        .thenComparing(Comparator.comparingLong(compareBookValue()).reversed()))
                .map(bookSortedByValue())
                .collect(Collectors.toList());
    }

    private ToLongFunction<Library> compareBookValue() {
        return lib -> lib.books()
                .stream()
                .map(Book::score)
                .map(Long::valueOf)
                .reduce(0L, Long::sum);
    }

    private ToIntFunction<Library> compareInitDayByN() {
        return lib -> Math.floorDiv(lib.nDay(), 10);
    }

    private ToLongFunction<Library> compareBookValueF() {
        return lib -> Math.floorDiv(lib.books()
                .stream()
                .sorted(Comparator.comparingInt(Book::score))
                .limit(Math.floorDiv(lib.nBook(), 2))
                .map(Book::score)
                .map(Long::valueOf)
                .reduce(0L, Long::sum), 10000);
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
