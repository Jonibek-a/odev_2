package logflow;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java logflow.Main <log-file>");
            System.exit(1);
        }
        ParserStage parser = new ParserStage();
        new Pipeline(new FileLineSource(args[0]), new ConsoleSink())
                .addStage(parser)
                .run();
        System.out.println("Hatalı (atlanan) satır sayısı: " + parser.malformedCount());
    }
}
