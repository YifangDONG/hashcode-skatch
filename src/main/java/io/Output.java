package io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Output {

    private final String baseDirOut;

    public Output(String baseDirOut) {
        this.baseDirOut = baseDirOut;
    }

    public void write(String fileName, List<List<String>> content) {

        try (BufferedWriter out = new BufferedWriter(new FileWriter(baseDirOut + fileName))) {
            for(List<String> line : content) {
                out.write(String.join(" ", line));
                out.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void append(String fileName, List<String> content) {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(baseDirOut + fileName, true))) {
            out.newLine();
            out.write(String.join(" ", content));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
