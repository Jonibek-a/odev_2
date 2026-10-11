package logflow;

public interface Stage<I, O> {
    void process(I input, Emitter<O> out) throws StageException;
    default void open() {}   // lifecycle hook, unused so far
    default void close() {}
}
