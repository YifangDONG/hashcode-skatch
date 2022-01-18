package solution;

import java.util.List;

public record Library(int id, int nBook, int nDay, long capacity, List<Book> books) {
}
