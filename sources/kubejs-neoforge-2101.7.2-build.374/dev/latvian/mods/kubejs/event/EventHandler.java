package dev.latvian.mods.kubejs.event;

import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.ScriptTypeHolder;
import dev.latvian.mods.kubejs.script.ScriptTypePredicate;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.HideFromJS;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

public class EventHandler extends BaseFunction {
   private static final TypeInfo EVENT_HANDLER_TYPE_INFO = TypeInfo.of(IEventHandler.class);
   public final EventGroup group;
   public final String name;
   public final ScriptTypePredicate scriptTypePredicate;
   public final Supplier<Class<? extends KubeEvent>> eventType;
   protected TypeInfo result;
   public transient EventTargetType<?> target;
   public transient boolean targetRequired;
   protected EventHandlerContainer[] eventContainers;
   public transient EventExceptionHandler exceptionHandler;

   EventHandler(EventGroup g, String n, ScriptTypePredicate st, Supplier<Class<? extends KubeEvent>> e) {
      this.group = g;
      this.name = n;
      this.scriptTypePredicate = st;
      this.eventType = e;
      this.result = null;
      this.target = null;
      this.targetRequired = false;
      this.eventContainers = null;
      this.exceptionHandler = null;
   }

   @HideFromJS
   public EventHandler hasResult(TypeInfo result) {
      this.result = result;
      return this;
   }

   public EventHandler hasResult() {
      return this.hasResult(TypeInfo.NONE);
   }

   @HideFromJS
   @Nullable
   public TypeInfo getResult() {
      return this.result;
   }

   @HideFromJS
   public EventHandler exceptionHandler(EventExceptionHandler handler) {
      this.exceptionHandler = handler;
      return this;
   }

   private <E> TargetedEventHandler<E> requiredTarget(EventTargetType<E> type, boolean required) {
      TargetedEventHandler<E> handler = new TargetedEventHandler<>(this.group, this.name, this.scriptTypePredicate, type, this.eventType);
      handler.result = this.result;
      handler.targetRequired = required;
      handler.exceptionHandler = this.exceptionHandler;
      handler.group.getHandlers().put(this.name, handler);
      return handler;
   }

   public <E> TargetedEventHandler<E> requiredTarget(EventTargetType<E> type) {
      return this.requiredTarget(type, true);
   }

   public <E> TargetedEventHandler<E> supportsTarget(EventTargetType<E> type) {
      return this.requiredTarget(type, false);
   }

   @HideFromJS
   public void clear(ScriptType type) {
      if (this.eventContainers != null) {
         this.eventContainers[type.ordinal()] = null;
         if (EventHandlerContainer.isEmpty(this.eventContainers)) {
            this.eventContainers = null;
         }
      }
   }

   public boolean hasListeners() {
      return this.eventContainers != null;
   }

   public void listen(@Nullable Context cx, ScriptType type, @Nullable Object extraId, IEventHandler handler) {
      if (cx != null && !((KubeJSContext)cx).kjsFactory.manager.canListenEvents) {
         throw new IllegalStateException("Event handler '" + this + "' can only be registered during script loading!");
      } else if (!this.scriptTypePredicate.test(type)) {
         throw new UnsupportedOperationException(
            "Tried to register event handler '"
               + this
               + "' for invalid script type "
               + type
               + "! Valid script types: "
               + this.scriptTypePredicate.getValidTypes()
         );
      } else {
         if (extraId != null && this.target != null) {
            extraId = Wrapper.unwrapped(extraId);
            extraId = this.target.transformer.transform(extraId);
         }

         if (this.target != null && this.targetRequired && extraId == null) {
            throw new IllegalArgumentException("Event handler '" + this + "' requires extra id!");
         } else if (this.target == null && extraId != null) {
            throw new IllegalArgumentException("Event handler '" + this + "' doesn't support extra id!");
         } else if (this.target != null && extraId != null && !this.target.validator.test(extraId)) {
            throw new IllegalArgumentException("Event handler '" + this + "' doesn't accept id '" + this.target.toString.transform(extraId) + "'!");
         } else {
            int[] line = new int[1];
            String source = cx == null ? "java" : Context.getSourcePositionFromStack(cx, line);
            EventHandlerContainer[] map = this.createMap(extraId);
            int index = type.ordinal();
            if (map[index] == null) {
               map[index] = new EventHandlerContainer(extraId, handler, source, line[0]);
            } else {
               map[index].add(extraId, handler, source, line[0]);
            }
         }
      }
   }

   protected EventHandlerContainer[] createMap(@Nullable Object extraId) {
      if (this.eventContainers == null) {
         this.eventContainers = new EventHandlerContainer[ScriptType.VALUES.length];
      }

      return this.eventContainers;
   }

   @HideFromJS
   public void listenJava(ScriptType type, @Nullable Object extraId, IEventHandler handler) {
      this.listen(null, type, extraId, handler);
   }

   public EventResult post(KubeEvent event) {
      if (this.scriptTypePredicate instanceof ScriptTypeHolder type) {
         return this.postInternal(type, null, event);
      } else {
         throw new IllegalStateException("You must specify which script type to post event to");
      }
   }

   public EventResult post(ScriptTypeHolder scriptType, KubeEvent event) {
      return this.postInternal(scriptType, null, event);
   }

   protected EventResult postInternal(ScriptTypeHolder type, @Nullable Object extraId, KubeEvent event) {
      if (!this.hasListeners()) {
         return EventResult.PASS;
      } else {
         ScriptType scriptType = type.kjs$getScriptType();
         if (this.target != null && this.targetRequired && extraId == null) {
            throw new IllegalArgumentException("Event handler '" + this + "' requires extra id!");
         } else if (this.target == null && extraId != null) {
            throw new IllegalArgumentException("Event handler '" + this + "' doesn't support extra id " + extraId + "!");
         } else {
            EventResult eventResult = EventResult.PASS;

            try {
               EventHandlerContainer[] extraContainers = this instanceof TargetedEventHandler<?> h
                  ? (h.extraEventContainers == null ? null : h.extraEventContainers.get(extraId))
                  : null;
               if (extraContainers != null) {
                  EventHandlerContainer handler = extraContainers[scriptType.ordinal()];
                  if (handler != null) {
                     handler.handle(scriptType.console, this, event);
                  }

                  if (!scriptType.isStartup()) {
                     handler = extraContainers[ScriptType.STARTUP.ordinal()];
                     if (handler != null) {
                        handler.handle(scriptType.console, this, event);
                     }
                  }
               }

               if (this.eventContainers != null) {
                  EventHandlerContainer handlerx = this.eventContainers[scriptType.ordinal()];
                  if (handlerx != null) {
                     handlerx.handle(scriptType.console, this, event);
                  }

                  if (!scriptType.isStartup()) {
                     handlerx = this.eventContainers[ScriptType.STARTUP.ordinal()];
                     if (handlerx != null) {
                        handlerx.handle(scriptType.console, this, event);
                     }
                  }
               }
            } catch (EventExit var9) {
               eventResult = var9.result;
            } catch (Throwable var10) {
               scriptType.console.error("Internal Error in '" + this + "'", var10);
               eventResult = EventResult.Type.ERROR.exit(null, var10).result;
            }

            event.afterPosted(eventResult);
            return eventResult;
         }
      }
   }

   public String toString() {
      return this.group + "." + this.name;
   }

   public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      ScriptType type = ((KubeJSContext)cx).getType();

      try {
         if (args.length == 1) {
            this.listen(cx, type, null, (IEventHandler)cx.jsToJava(args[0], EVENT_HANDLER_TYPE_INFO));
         } else if (args.length == 2) {
            IEventHandler handler = (IEventHandler)cx.jsToJava(args[1], EVENT_HANDLER_TYPE_INFO);

            for (Object o : ListJS.orSelf(args[0])) {
               this.listen(cx, type, o, handler);
            }
         }
      } catch (Exception var9) {
         type.console.error(var9.getLocalizedMessage());
      }

      return null;
   }

   public void forEachListener(ScriptType type, Consumer<EventHandlerContainer> callback) {
      if (this.eventContainers != null) {
         for (EventHandlerContainer c = this.eventContainers[type.ordinal()]; c != null; c = c.child) {
            callback.accept(c);
         }
      }
   }
}
