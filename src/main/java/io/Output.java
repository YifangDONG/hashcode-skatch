package io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Output {

    private static final String BASE_DIR_OUT = "src\\main\\resources\\out\\";
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH-mm-ss-ns");

    public static void write(String fileName, List<List<String>> content) {

        try (BufferedWriter out = new BufferedWriter(new FileWriter(BASE_DIR_OUT + fileName + "-" + LocalTime.now().format(TIME_FORMATTER)))) {
            for(List<String> line : content) {
                out.write(String.join(" ", line));
                out.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
