package io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Output {

    private static final String BASE_DIR_OUT = "src\\main\\resources\\out\\";

    public static void write(String fileName, List<List<String>> content) {

        try (BufferedWriter out = new BufferedWriter(new FileWriter(BASE_DIR_OUT + fileName))) {
            for(List<String> line : content) {
                out.write(String.join(" ", line));
                out.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
