package dev.latvian.mods.kubejs.event;

import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.ScriptTypePredicate;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class EventGroup {
   public final String name;
   private final Map<String, EventHandler> handlers;

   public static EventGroup of(String name) {
      return new EventGroup(name);
   }

   private EventGroup(String n) {
      this.name = n;
      this.handlers = new HashMap<>();
   }

   @Override
   public String toString() {
      return this.name;
   }

   @Override
   public int hashCode() {
      return this.name.hashCode();
   }

   @Override
   public boolean equals(Object obj) {
      return obj == this || obj instanceof EventGroup g && this.name.equals(g.name);
   }

   public EventHandler add(String name, ScriptTypePredicate scriptType, Supplier<Class<? extends KubeEvent>> eventType) {
      EventHandler handler = new EventHandler(this, name, scriptType, eventType);
      this.handlers.put(name, handler);
      return handler;
   }

   public EventHandler startup(String name, Supplier<Class<? extends KubeEvent>> eventType) {
      return this.add(name, ScriptType.STARTUP, eventType);
   }

   public EventHandler server(String name, Supplier<Class<? extends KubeEvent>> eventType) {
      return this.add(name, ScriptType.SERVER, eventType);
   }

   public EventHandler client(String name, Supplier<Class<? extends KubeEvent>> eventType) {
      return this.add(name, ScriptType.CLIENT, eventType);
   }

   public EventHandler common(String name, Supplier<Class<? extends KubeEvent>> eventType) {
      return this.add(name, ScriptTypePredicate.COMMON, eventType);
   }

   public Map<String, EventHandler> getHandlers() {
      return this.handlers;
   }
}
