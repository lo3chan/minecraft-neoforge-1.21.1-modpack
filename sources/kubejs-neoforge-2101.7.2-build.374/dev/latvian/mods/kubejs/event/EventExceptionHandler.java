package dev.latvian.mods.kubejs.event;

@FunctionalInterface
public interface EventExceptionHandler {
   Throwable handle(KubeEvent event, EventHandlerContainer container, Throwable ex);
}
