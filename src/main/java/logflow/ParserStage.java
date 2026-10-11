package logflow;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses an Apache/Nginx "combined" log line into a LogRecord.
 *
 * MALFORMED LINES: this week they are only SKIPPED and COUNTED. Proper handling
 * comes in week 5. Architecture is built layer by layer.
 */
public class ParserStage implements Stage<String, LogRecord> {

    private static final Pattern LINE = Pattern.compile(
            "^(\\S+)\\s+\\S+\\s+\\S+\\s+\\[([^\\]]+)\\]"
          + "\\s+\"(\\S+)\\s+(\\S+)(?:\\s+[^\"]*)?\""
          + "\\s+(\\d{3})\\s+(\\d+|-)"
          + "(?:\\s+\"[^\"]*\"\\s+\"([^\"]*)\")?$");

    private static final DateTimeFormatter TIME =
            DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);

    private long malformed = 0;

    public long malformedCount() { return malformed; }

    @Override
    public void process(String line, Emitter<LogRecord> out) {
        String trimmed = line == null ? "" : line.trim();
        Matcher m = LINE.matcher(trimmed);
        if (trimmed.isEmpty() || !m.matches()) { malformed++; return; }

        try {
            int status = Integer.parseInt(m.group(5));
            if (status < 100 || status > 599) { malformed++; return; }

            var timestamp = OffsetDateTime.parse(m.group(2), TIME).toInstant();
            long bytes = m.group(6).equals("-") ? 0 : Long.parseLong(m.group(6));
            String ua = m.group(7) == null ? "-" : m.group(7);

            out.emit(new LogRecord(timestamp, m.group(1), m.group(3), m.group(4),
                    status, bytes, ua, Map.of(), trimmed));
        } catch (DateTimeParseException | NumberFormatException e) {
            malformed++;
        }
    }
}
