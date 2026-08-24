package dev.latvian.mods.kubejs.event;

import dev.latvian.mods.rhino.Context;
import java.util.function.Consumer;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.common.util.TriState;
import org.jetbrains.annotations.Nullable;

public class EventResult {
   public static final EventResult PASS = EventResult.Type.PASS.defaultResult;
   private final Context cx;
   private final EventResult.Type type;
   private final Object value;

   private EventResult(@Nullable Context cx, EventResult.Type type, @Nullable Object value) {
      this.cx = cx;
      this.type = type;
      this.value = value;
   }

   @Nullable
   public Context cx() {
      return this.cx;
   }

   public EventResult.Type type() {
      return this.type;
   }

   @Nullable
   public Object value() {
      return this.value;
   }

   public boolean override() {
      return this.type != EventResult.Type.PASS;
   }

   public boolean pass() {
      return this.type == EventResult.Type.PASS;
   }

   public boolean interruptDefault() {
      return this.type == EventResult.Type.INTERRUPT_DEFAULT;
   }

   public boolean interruptFalse() {
      return this.type == EventResult.Type.INTERRUPT_FALSE;
   }

   public boolean interruptTrue() {
      return this.type == EventResult.Type.INTERRUPT_TRUE;
   }

   public boolean applyCancel(ICancellableEvent event) {
      if (this.interruptFalse()) {
         event.setCanceled(true);
         return true;
      } else {
         return false;
      }
   }

   public void applyTristate(Consumer<TriState> consumer) {
      if (this.interruptFalse()) {
         consumer.accept(TriState.FALSE);
      } else if (this.interruptTrue()) {
         consumer.accept(TriState.TRUE);
      }
   }

   public static enum Type {
      ERROR,
      PASS,
      INTERRUPT_DEFAULT,
      INTERRUPT_FALSE,
      INTERRUPT_TRUE;

      private final EventResult defaultResult = new EventResult(null, this, null);
      private final EventExit defaultExit = new EventExit(this.defaultResult);

      public EventExit exit(@Nullable Context cx, @Nullable Object value) {
         return value == null ? this.defaultExit : new EventExit(new EventResult(cx, this, value));
      }
   }
}
