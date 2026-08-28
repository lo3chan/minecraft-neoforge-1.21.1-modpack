/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.bridge;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import net.diebuddies.bridge.Event;

class ArrayBackedEvent<T>
extends Event<T> {
    private final Function<T[], T> invokerFactory;
    private final Lock lock = new ReentrantLock();
    private T[] handlers;

    ArrayBackedEvent(Class<? super T> type, Function<T[], T> invokerFactory) {
        this.invokerFactory = invokerFactory;
        this.handlers = (Object[])Array.newInstance(type, 0);
        this.update();
    }

    void update() {
        this.invoker = this.invokerFactory.apply((T[][])this.handlers);
    }

    @Override
    public void register(T listener) {
        Objects.requireNonNull(listener, "Tried to register a null listener!");
        this.lock.lock();
        try {
            this.handlers = Arrays.copyOf(this.handlers, this.handlers.length + 1);
            this.handlers[this.handlers.length - 1] = listener;
            this.update();
        }
        finally {
            this.lock.unlock();
        }
    }
}

