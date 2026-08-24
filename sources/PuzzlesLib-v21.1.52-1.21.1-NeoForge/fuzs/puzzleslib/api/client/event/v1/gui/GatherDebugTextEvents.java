package fuzs.puzzleslib.api.client.event.v1.gui;

import com.mojang.blaze3d.platform.Window;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import java.util.List;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

public final class GatherDebugTextEvents {
   public static final EventInvoker<GatherDebugTextEvents.Left> LEFT = EventInvoker.lookup(GatherDebugTextEvents.Left.class);
   public static final EventInvoker<GatherDebugTextEvents.Right> RIGHT = EventInvoker.lookup(GatherDebugTextEvents.Right.class);

   private GatherDebugTextEvents() {
   }

   @FunctionalInterface
   public interface Left {
      void onGatherLeftDebugText(Window var1, GuiGraphics var2, DeltaTracker var3, List<String> var4);
   }

   @FunctionalInterface
   public interface Right {
      void onGatherRightDebugText(Window var1, GuiGraphics var2, DeltaTracker var3, List<String> var4);
   }
}
