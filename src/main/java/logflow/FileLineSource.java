package logflow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/** Reads a file and emits one String per line. */
public class FileLineSource implements Source<String> {
    private final String path;

    public FileLineSource(String path) { this.path = path; }

    @Override
    public void produce(Emitter<String> out) {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                out.emit(line);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read file: " + path, e);
        }
    }
}
