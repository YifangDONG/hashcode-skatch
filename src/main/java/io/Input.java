package io;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Input {

    private final String baseDirIn;

    public Input(String baseDirIn) {
        this.baseDirIn = baseDirIn;
    }

    public List<List<String>> read(String fileName) {
        List<List<String>> content = new LinkedList<>();
        try(FileReader fileReader = new FileReader(baseDirIn + fileName)) {
            Scanner scanner = new Scanner(fileReader);
            while (scanner.hasNextLine()) {
                String s = scanner.nextLine();
                if(!s.isBlank()) {
                    content.add(Arrays.stream(s.split(" ")).toList());
                }
            }
            return new ArrayList<>(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
