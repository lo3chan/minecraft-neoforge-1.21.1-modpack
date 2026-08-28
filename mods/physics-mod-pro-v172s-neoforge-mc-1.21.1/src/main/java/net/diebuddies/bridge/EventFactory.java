/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.bridge;

import java.util.function.Function;
import net.diebuddies.bridge.Event;
import net.diebuddies.bridge.EventFactoryImpl;

public final class EventFactory {
    private static boolean profilingEnabled = true;

    private EventFactory() {
    }

    public static boolean isProfilingEnabled() {
        return profilingEnabled;
    }

    public static void invalidate() {
        EventFactoryImpl.invalidate();
    }

    public static <T> Event<T> createArrayBacked(Class<? super T> type, Function<T[], T> invokerFactory) {
        return EventFactoryImpl.createArrayBacked(type, invokerFactory);
    }

    public static <T> Event<T> createArrayBacked(Class<T> type, T emptyInvoker, Function<T[], T> invokerFactory) {
        return EventFactory.createArrayBacked(type, listeners -> {
            if (((Object[])listeners).length == 0) {
                return emptyInvoker;
            }
            if (((Object[])listeners).length == 1) {
                return listeners[0];
            }
            return invokerFactory.apply((T[])listeners);
        });
    }

    public static String getHandlerName(Object handler) {
        return handler.getClass().getName();
    }
}

