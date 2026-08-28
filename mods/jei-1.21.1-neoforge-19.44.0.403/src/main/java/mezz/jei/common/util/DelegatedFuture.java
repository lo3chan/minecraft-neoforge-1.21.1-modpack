/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  java.util.concurrent.Future$State
 */
package mezz.jei.common.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public interface DelegatedFuture<T>
extends Future<T> {
    public Future<T> getDelegate();

    @Override
    default public boolean cancel(boolean mayInterruptIfRunning) {
        return this.getDelegate().cancel(mayInterruptIfRunning);
    }

    @Override
    default public boolean isCancelled() {
        return this.getDelegate().isCancelled();
    }

    @Override
    default public boolean isDone() {
        return this.getDelegate().isDone();
    }

    @Override
    default public T get() throws InterruptedException, ExecutionException {
        return this.getDelegate().get();
    }

    @Override
    default public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.getDelegate().get(timeout, unit);
    }

    default public T resultNow() {
        return (T)this.getDelegate().resultNow();
    }

    default public Throwable exceptionNow() {
        return this.getDelegate().exceptionNow();
    }

    default public Future.State state() {
        return this.getDelegate().state();
    }
}

