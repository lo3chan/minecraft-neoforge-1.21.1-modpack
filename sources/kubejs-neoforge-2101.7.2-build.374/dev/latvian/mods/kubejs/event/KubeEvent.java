package dev.latvian.mods.kubejs.event;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.HideFromJS;
import org.jetbrains.annotations.Nullable;

public interface KubeEvent {
   @HideFromJS
   @Nullable
   default Object defaultExitValue(Context cx) {
      return null;
   }

   @HideFromJS
   @Nullable
   default Object mapExitValue(Context cx, @Nullable Object value) {
      TypeInfo t = this.getExitValueType();
      return t == null ? value : cx.jsToJava(value, t);
   }

   @HideFromJS
   @Nullable
   default TypeInfo getExitValueType() {
      return null;
   }

   @Info("Cancels the event with default exit value. Execution will be stopped **immediately**.\n\n`cancel` denotes a `false` outcome.\n")
   default Object cancel(Context cx) throws EventExit {
      return this.cancel(cx, this.defaultExitValue(cx));
   }

   @Info("Stops the event with default exit value. Execution will be stopped **immediately**.\n\n`success` denotes a `true` outcome.\n")
   default Object success(Context cx) throws EventExit {
      return this.success(cx, this.defaultExitValue(cx));
   }

   @Info("Stops the event with default exit value. Execution will be stopped **immediately**.\n\n`exit` denotes a `default` outcome.\n")
   default Object exit(Context cx) throws EventExit {
      return this.exit(cx, this.defaultExitValue(cx));
   }

   @Info("Cancels the event with the given exit value. Execution will be stopped **immediately**.\n\n`cancel` denotes a `false` outcome.\n")
   default Object cancel(Context cx, @Nullable Object value) throws EventExit {
      throw EventResult.Type.INTERRUPT_FALSE.exit(cx, this.mapExitValue(cx, value));
   }

   @Info("Stops the event with the given exit value. Execution will be stopped **immediately**.\n\n`success` denotes a `true` outcome.\n")
   default Object success(Context cx, @Nullable Object value) throws EventExit {
      throw EventResult.Type.INTERRUPT_TRUE.exit(cx, this.mapExitValue(cx, value));
   }

   @Info("Stops the event with the given exit value. Execution will be stopped **immediately**.\n\n`exit` denotes a `default` outcome.\n")
   default Object exit(Context cx, @Nullable Object value) throws EventExit {
      throw EventResult.Type.INTERRUPT_DEFAULT.exit(cx, this.mapExitValue(cx, value));
   }

   @HideFromJS
   default void afterPosted(EventResult result) {
   }
}
