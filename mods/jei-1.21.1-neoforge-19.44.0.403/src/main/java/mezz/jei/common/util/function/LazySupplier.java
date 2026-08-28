/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.common.util.function;

import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

public class LazySupplier<T>
implements Supplier<T> {
    private final Supplier<T> supplier;
    @Nullable
    private T cachedResult;

    public LazySupplier(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T get() {
        if (this.cachedResult == null) {
            this.cachedResult = this.supplier.get();
        }
        return this.cachedResult;
    }
}

