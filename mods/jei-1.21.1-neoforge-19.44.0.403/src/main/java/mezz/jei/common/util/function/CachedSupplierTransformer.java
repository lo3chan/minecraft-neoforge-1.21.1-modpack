/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.common.util.function;

import java.util.function.Function;
import java.util.function.Supplier;
import mezz.jei.common.util.function.CachedFunction;

public class CachedSupplierTransformer<T, R>
implements Supplier<R> {
    private final Supplier<T> supplier;
    private final CachedFunction<T, R> cachedFunction;

    public CachedSupplierTransformer(Supplier<T> supplier, Function<T, R> function) {
        this.supplier = supplier;
        this.cachedFunction = new CachedFunction<T, R>(function);
    }

    @Override
    public R get() {
        T currentValue = this.supplier.get();
        return this.cachedFunction.apply(currentValue);
    }
}

