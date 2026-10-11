package logflow;

public class ConsoleSink implements Sink<LogRecord> {
    @Override
    public void consume(LogRecord r) {
        System.out.printf("%s %-15s %-6s %s -> %d (%d B) [%s]%n",
                r.timestamp(), r.clientIp(), r.method(), r.path(),
                r.status(), r.bytes(), r.userAgent());
    }
}
