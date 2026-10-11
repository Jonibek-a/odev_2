package logflow;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ParserStageTest {

    private static final String VALID =
        "192.168.1.183 - - [28/Sep/2026:08:01:03 +0300] \"DELETE /index.html HTTP/1.1\" 200 36770 "
        + "\"https://www.google.com/\" \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/128.0\"";

    private ParserStage parser;
    private CollectingEmitter<LogRecord> out;

    @BeforeEach
    void setUp() { parser = new ParserStage(); out = new CollectingEmitter<>(); }

    @Test
    void validLine() {
        parser.process(VALID, out);
        assertEquals(1, out.items.size());
        LogRecord r = out.items.get(0);
        assertEquals(Instant.parse("2026-09-28T05:01:03Z"), r.timestamp());
        assertEquals("192.168.1.183", r.clientIp());
        assertEquals("DELETE", r.method());
        assertEquals("/index.html", r.path());
        assertEquals(200, r.status());
        assertEquals(36770L, r.bytes());
        assertEquals(VALID, r.raw());
        assertTrue(r.attributes().isEmpty());
        assertEquals(0, parser.malformedCount());
    }

    @Test
    void missingFields() {
        parser.process("10.0.0.1 - - [28/Sep/2026:08:01:03 +0300] \"GET / HTTP/1.1\"", out);
        assertTrue(out.items.isEmpty());
        assertEquals(1, parser.malformedCount());
    }

    @Test
    void badTimestamp() {
        parser.process("10.0.0.1 - - [99/Foo/2026:08:01:03 +0300] \"GET / HTTP/1.1\" 200 10 \"-\" \"ua\"", out);
        assertTrue(out.items.isEmpty());
        assertEquals(1, parser.malformedCount());
    }

    @Test
    void badStatusCode() {
        parser.process("10.0.0.1 - - [28/Sep/2026:08:01:03 +0300] \"GET / HTTP/1.1\" abc 10 \"-\" \"ua\"", out);
        assertTrue(out.items.isEmpty());
        assertEquals(1, parser.malformedCount());
    }

    @Test
    void emptyLine() {
        parser.process("", out);
        parser.process("   ", out);
        assertTrue(out.items.isEmpty());
        assertEquals(2, parser.malformedCount());
    }

    @Test
    void extraWhitespace() {
        parser.process("   10.0.0.1  -  -  [28/Sep/2026:08:01:03 +0300]  \"GET /a HTTP/1.1\"  404  12  \"-\"  \"curl/8.5.0\"   ", out);
        assertEquals(1, out.items.size());
        assertEquals(404, out.items.get(0).status());
        assertEquals("curl/8.5.0", out.items.get(0).userAgent());
    }

    @Test
    void quotedUserAgentWithSpaces() {
        parser.process(VALID, out);
        assertEquals("Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/128.0", out.items.get(0).userAgent());
    }

    @Test
    void queryStringIsKeptInPath() {
        parser.process("10.0.0.1 - - [28/Sep/2026:08:01:03 +0300] \"GET /search?q=java HTTP/1.1\" 200 5 \"-\" \"ua\"", out);
        assertEquals("/search?q=java", out.items.get(0).path());
    }

    @Test
    void dashBytesBecomesZero() {
        parser.process("10.0.0.1 - - [28/Sep/2026:08:01:03 +0300] \"GET / HTTP/1.1\" 304 - \"-\" \"ua\"", out);
        assertEquals(0L, out.items.get(0).bytes());
    }

    @Test
    void gluedStatusAndBytesIsMalformed() {
        parser.process("192.168.1.200 - - [28/Sep/2026:08:09:02 +0300] \"DELETE /a HTTP/1.1\" 40410091 \"-\" \"ua\"", out);
        assertTrue(out.items.isEmpty());
        assertEquals(1, parser.malformedCount());
    }

    @Test
    void missingSpaceBetweenQuotedFieldsIsMalformed() {
        parser.process("10.0.6.159 - - [28/Sep/2026:08:22:20 +0300] \"GET /a HTTP/1.1\" 404 40545\"-\" \"curl/8.5.0\"", out);
        assertTrue(out.items.isEmpty());
        assertEquals(1, parser.malformedCount());
    }

    @Test
    void attributesAreImmutable() {
        parser.process(VALID, out);
        assertThrows(UnsupportedOperationException.class,
                () -> out.items.get(0).attributes().put("x", "y"));
    }
}
