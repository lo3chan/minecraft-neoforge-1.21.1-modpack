/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.neoforged.bus.api.Event
 *  net.neoforged.bus.api.EventPriority
 *  net.neoforged.bus.api.IEventBus
 */
package mezz.jei.neoforge.events;

import java.util.function.Consumer;
import mezz.jei.common.util.WeakConsumer;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;

public class EventSubscription<T extends Event> {
    private final IEventBus eventBus;
    private final Consumer<T> listener;
    private final WeakConsumer<T> registeredListener;

    public static <T extends Event> EventSubscription<T> register(IEventBus eventBus, Class<T> eventType, Consumer<T> listener) {
        return EventSubscription.register(eventBus, EventPriority.NORMAL, eventType, listener);
    }

    public static <T extends Event> EventSubscription<T> register(IEventBus eventBus, EventPriority priority, Class<T> eventType, Consumer<T> listener) {
        return new EventSubscription<T>(eventBus, priority, eventType, listener);
    }

    private EventSubscription(IEventBus eventBus, EventPriority priority, Class<T> eventType, Consumer<T> listener) {
        this.eventBus = eventBus;
        this.listener = listener;
        WeakConsumer<T> weakListener = new WeakConsumer<T>(listener);
        eventBus.addListener(priority, false, eventType, weakListener);
        this.registeredListener = weakListener;
    }

    public void unregister() {
        this.eventBus.unregister(this.registeredListener);
    }
}

