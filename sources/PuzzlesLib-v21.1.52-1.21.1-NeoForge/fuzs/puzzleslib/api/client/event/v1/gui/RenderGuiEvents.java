package fuzs.puzzleslib.api.client.event.v1.gui;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;

public final class RenderGuiEvents {
   public static final EventInvoker<RenderGuiEvents.Before> BEFORE = EventInvoker.lookup(RenderGuiEvents.Before.class);
   public static final EventInvoker<RenderGuiEvents.After> AFTER = EventInvoker.lookup(RenderGuiEvents.After.class);

   private RenderGuiEvents() {
   }

   @FunctionalInterface
   public interface After {
      void onAfterRenderGui(Gui var1, GuiGraphics var2, DeltaTracker var3);
   }

   @FunctionalInterface
   public interface Before {
      void onBeforeRenderGui(Gui var1, GuiGraphics var2, DeltaTracker var3);
   }
}
