/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.neoforged.bus.api.Event
 *  net.neoforged.bus.api.EventPriority
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.fml.event.IModBusEvent
 */
package mezz.jei.neoforge.events;

import java.util.function.Consumer;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.IModBusEvent;

public class PermanentEventSubscriptions {
    private final IEventBus eventBus;
    private final IEventBus modEventBus;

    public PermanentEventSubscriptions(IEventBus eventBus, IEventBus modEventBus) {
        this.eventBus = eventBus;
        this.modEventBus = modEventBus;
    }

    public <T extends Event> void register(Class<T> eventType, Consumer<T> listener) {
        this.register(EventPriority.NORMAL, eventType, listener);
    }

    public <T extends Event> void register(EventPriority priority, Class<T> eventType, Consumer<T> listener) {
        if (IModBusEvent.class.isAssignableFrom(eventType)) {
            this.modEventBus.addListener(priority, false, eventType, listener);
        } else {
            this.eventBus.addListener(priority, false, eventType, listener);
        }
    }

    public IEventBus getModEventBus() {
        return this.modEventBus;
    }
}

