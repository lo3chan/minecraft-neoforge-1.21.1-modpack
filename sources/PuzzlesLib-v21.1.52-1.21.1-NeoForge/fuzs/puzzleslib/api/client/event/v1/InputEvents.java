package fuzs.puzzleslib.api.client.event.v1;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;

public final class InputEvents {
   public static final EventInvoker<InputEvents.MouseClick> MOUSE_CLICK = EventInvoker.lookup(InputEvents.MouseClick.class);
   public static final EventInvoker<InputEvents.MouseScroll> MOUSE_SCROLL = EventInvoker.lookup(InputEvents.MouseScroll.class);
   public static final EventInvoker<InputEvents.KeyPress> KEY_PRESS = EventInvoker.lookup(InputEvents.KeyPress.class);

   private InputEvents() {
   }

   @FunctionalInterface
   public interface KeyPress {
      EventResult onKeyPress(int var1, int var2, int var3, int var4);
   }

   @FunctionalInterface
   public interface MouseClick {
      EventResult onMouseClick(int var1, int var2, int var3);
   }

   @FunctionalInterface
   public interface MouseScroll {
      EventResult onMouseScroll(boolean var1, boolean var2, boolean var3, double var4, double var6);
   }
}
