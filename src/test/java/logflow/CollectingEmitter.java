package logflow;

import java.util.ArrayList;
import java.util.List;

/** Test double: collects every emitted item. Reused every week. */
public class CollectingEmitter<T> implements Emitter<T> {
    public final List<T> items = new ArrayList<>();
    @Override public void emit(T item) { items.add(item); }
}
