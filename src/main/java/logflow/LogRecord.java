package logflow;

import java.time.Instant;
import java.util.Map;

/**
 * Domain model carried by the pipeline. Immutable.
 *
 * attributes: EXTENSION POINT. Later stages (weeks 5-7) add data the current
 * parser knows nothing about (geo-location, session id, tags) to this map, so
 * new information does not require new record fields or changes in existing stages.
 */
public record LogRecord(
        Instant timestamp,
        String clientIp,
        String method,
        String path,
        int status,
        long bytes,
        String userAgent,
        Map<String, String> attributes,
        String raw) {

    public LogRecord {
        attributes = (attributes == null) ? Map.of() : Map.copyOf(attributes);
    }
}
