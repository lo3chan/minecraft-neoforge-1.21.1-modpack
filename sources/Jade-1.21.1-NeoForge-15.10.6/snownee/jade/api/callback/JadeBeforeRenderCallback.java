package snownee.jade.api.callback;

import net.minecraft.client.gui.GuiGraphics;
import snownee.jade.api.Accessor;
import snownee.jade.api.ui.IBoxElement;
import snownee.jade.api.ui.TooltipRect;

@FunctionalInterface
public interface JadeBeforeRenderCallback {
   boolean beforeRender(IBoxElement var1, TooltipRect var2, GuiGraphics var3, Accessor<?> var4);
}
