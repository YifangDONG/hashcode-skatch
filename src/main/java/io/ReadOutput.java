package io;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ReadOutput {

    private static final String BASE_DIR_IN = "src\\main\\resources\\out\\";

    public static List<List<String>> read(String fileName) {
        List<List<String>> content = new LinkedList<>();
        try(FileReader fileReader = new FileReader(BASE_DIR_IN + fileName)) {
            Scanner scanner = new Scanner(fileReader);
            while (scanner.hasNextLine()) {
                String s = scanner.nextLine();
                content.add(Arrays.stream(s.split(" ")).toList());
            }
            return new ArrayList<>(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
