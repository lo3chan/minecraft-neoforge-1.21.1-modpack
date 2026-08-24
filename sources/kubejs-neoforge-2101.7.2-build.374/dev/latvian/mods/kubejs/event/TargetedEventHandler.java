package dev.latvian.mods.kubejs.event;

import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.ScriptTypeHolder;
import dev.latvian.mods.kubejs.script.ScriptTypePredicate;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.HideFromJS;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

public class TargetedEventHandler<E> extends EventHandler {
   protected Map<Object, EventHandlerContainer[]> extraEventContainers;

   TargetedEventHandler(EventGroup g, String n, ScriptTypePredicate st, EventTargetType<E> target, Supplier<Class<? extends KubeEvent>> e) {
      super(g, n, st, e);
      this.target = target;
      this.extraEventContainers = null;
   }

   @HideFromJS
   public TargetedEventHandler<E> hasResult(TypeInfo result) {
      super.hasResult(result);
      return this;
   }

   public TargetedEventHandler<E> hasResult() {
      super.hasResult();
      return this;
   }

   @HideFromJS
   public TargetedEventHandler<E> exceptionHandler(EventExceptionHandler handler) {
      this.exceptionHandler = handler;
      return this;
   }

   @Override
   public boolean hasListeners() {
      return this.eventContainers != null || this.extraEventContainers != null;
   }

   public boolean hasListeners(@Nullable E extraId) {
      return this.eventContainers != null || extraId != null && this.extraEventContainers != null && this.extraEventContainers.containsKey(extraId);
   }

   public EventResult post(KubeEvent event, @Nullable E extraId) {
      if (this.scriptTypePredicate instanceof ScriptTypeHolder type) {
         return this.postInternal(type, extraId, event);
      } else {
         throw new IllegalStateException("You must specify which script type to post event to");
      }
   }

   public EventResult post(ScriptTypeHolder type, @Nullable E extraId, KubeEvent event) {
      return this.postInternal(type, extraId, event);
   }

   @HideFromJS
   @Override
   public void clear(ScriptType type) {
      super.clear(type);
      if (this.extraEventContainers != null) {
         Iterator<Entry<Object, EventHandlerContainer[]>> entries = this.extraEventContainers.entrySet().iterator();

         while (entries.hasNext()) {
            Entry<Object, EventHandlerContainer[]> entry = entries.next();
            entry.getValue()[type.ordinal()] = null;
            if (EventHandlerContainer.isEmpty(entry.getValue())) {
               entries.remove();
            }
         }

         if (this.extraEventContainers.isEmpty()) {
            this.extraEventContainers = null;
         }
      }
   }

   @Override
   protected EventHandlerContainer[] createMap(@Nullable Object extraId) {
      if (extraId == null) {
         return super.createMap(extraId);
      } else {
         if (this.extraEventContainers == null) {
            this.extraEventContainers = (Map<Object, EventHandlerContainer[]>)(this.target.identity ? new Reference2ObjectOpenHashMap() : new HashMap<>());
         }

         EventHandlerContainer[] map = this.extraEventContainers.get(extraId);
         if (map == null) {
            map = new EventHandlerContainer[ScriptType.VALUES.length];
            this.extraEventContainers.put(extraId, map);
         }

         return map;
      }
   }

   @Override
   public void forEachListener(ScriptType type, Consumer<EventHandlerContainer> callback) {
      super.forEachListener(type, callback);
      if (this.extraEventContainers != null) {
         for (Entry<Object, EventHandlerContainer[]> entry : this.extraEventContainers.entrySet()) {
            for (EventHandlerContainer c = entry.getValue()[type.ordinal()]; c != null; c = c.child) {
               callback.accept(c);
            }
         }
      }
   }

   public Set<E> findUniqueExtraIds(ScriptType type) {
      if (!this.hasListeners()) {
         return Set.of();
      } else {
         HashSet<E> set = new HashSet<>();
         this.forEachListener(type, c -> {
            if (c.target != null) {
               set.add((E)c.target);
            }
         });
         return set;
      }
   }
}
