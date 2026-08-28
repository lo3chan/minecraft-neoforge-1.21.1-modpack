/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.common.util.function;

import java.util.function.Function;
import org.jetbrains.annotations.Nullable;

public class CachedFunction<T, R>
implements Function<T, R> {
    private final Function<T, R> function;
    @Nullable
    private T previousValue;
    @Nullable
    private R cachedResult;

    public CachedFunction(Function<T, R> function) {
        this.function = function;
    }

    @Override
    public R apply(T currentValue) {
        if (currentValue.equals(this.previousValue)) {
            assert (this.cachedResult != null);
            return this.cachedResult;
        }
        this.cachedResult = this.function.apply(currentValue);
        this.previousValue = currentValue;
        return this.cachedResult;
    }
}

