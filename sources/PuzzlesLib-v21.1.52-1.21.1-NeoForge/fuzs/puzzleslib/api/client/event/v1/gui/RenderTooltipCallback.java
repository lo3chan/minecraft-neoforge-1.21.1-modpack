package fuzs.puzzleslib.api.client.event.v1.gui;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;

@FunctionalInterface
public interface RenderTooltipCallback {
   EventInvoker<RenderTooltipCallback> EVENT = EventInvoker.lookup(RenderTooltipCallback.class);

   EventResult onRenderTooltip(GuiGraphics var1, Font var2, int var3, int var4, List<ClientTooltipComponent> var5, ClientTooltipPositioner var6);
}
