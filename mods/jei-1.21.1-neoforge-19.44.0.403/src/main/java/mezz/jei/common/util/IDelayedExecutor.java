/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.common.util;

import java.time.Duration;
import java.util.concurrent.Future;

public interface IDelayedExecutor {
    public Future<?> schedule(Runnable var1, Duration var2);
}

