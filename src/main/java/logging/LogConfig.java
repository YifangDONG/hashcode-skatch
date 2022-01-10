package logging;

public record LogConfig(Level logLevel, boolean logInput, boolean logOutput, boolean logTime) {
}
