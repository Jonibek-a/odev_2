## v2
Yeni: LogRecord, ParserStage, CollectingEmitter (test), ParserStageTest, pom.xml
Değişen: Pipeline (String'e sabitli → herhangi bir tipi taşıyabilir), ConsoleSink (Sink<String> → Sink<LogRecord>), Main (parser eklendi, sayaç yazdırıldı)
Kaldırılan: Record arayüzü (LogRecord ile yer değiştirdi)
Değişmeyen: Stage, Emitter, Source, Sink, StageException, FileLineSource (tek dosyadan ayrı dosyalara taşındı, içerik aynı)

## v1
Tek dosyalı iskelet pipeline (LogFlow.java)
