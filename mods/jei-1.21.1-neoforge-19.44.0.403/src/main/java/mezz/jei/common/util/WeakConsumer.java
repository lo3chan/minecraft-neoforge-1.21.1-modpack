/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.common.util;

import java.lang.ref.WeakReference;
import java.util.function.Consumer;

public class WeakConsumer<T>
implements Consumer<T> {
    private final WeakReference<Consumer<T>> weakReference;

    public WeakConsumer(Consumer<T> consumer) {
        this.weakReference = new WeakReference<Consumer<Consumer<T>>>(consumer);
    }

    @Override
    public void accept(T t) {
        Consumer consumer = (Consumer)this.weakReference.get();
        if (consumer != null) {
            consumer.accept(t);
        }
    }
}

