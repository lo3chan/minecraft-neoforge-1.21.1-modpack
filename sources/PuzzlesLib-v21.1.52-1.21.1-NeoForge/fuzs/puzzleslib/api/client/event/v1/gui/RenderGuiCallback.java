package fuzs.puzzleslib.api.client.event.v1.gui;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

@Deprecated
@FunctionalInterface
public interface RenderGuiCallback {
   EventInvoker<RenderGuiCallback> EVENT = EventInvoker.lookup(RenderGuiCallback.class);

   void onRenderGui(Minecraft var1, GuiGraphics var2, DeltaTracker var3);
}
