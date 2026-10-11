package logflow;

import java.util.ArrayList;
import java.util.List;

/** Ordered list of stages; run() pushes records from source through the stages to the sink. */
public class Pipeline {
    private final Source<?> source;
    private final List<Stage<?, ?>> stages = new ArrayList<>();
    private final Sink<?> sink;

    public Pipeline(Source<?> source, Sink<?> sink) {
        this.source = source;
        this.sink = sink;
    }

    public Pipeline addStage(Stage<?, ?> stage) {
        stages.add(stage);
        return this;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void run() {
        for (Stage<?, ?> s : stages) s.open();
        try {
            ((Source) source).produce(chain(0));
        } finally {
            for (int i = stages.size() - 1; i >= 0; i--) stages.get(i).close();
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Emitter chain(final int i) {
        if (i == stages.size()) {
            return item -> ((Sink) sink).consume(item);
        }
        final Stage stage = stages.get(i);
        final Emitter next = chain(i + 1);
        return item -> {
            try {
                stage.process(item, next);
            } catch (StageException e) {
                throw new RuntimeException("Stage " + i + " failed", e);
            }
        };
    }
}
