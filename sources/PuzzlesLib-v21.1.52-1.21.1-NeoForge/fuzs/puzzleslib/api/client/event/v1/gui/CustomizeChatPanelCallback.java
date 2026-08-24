package fuzs.puzzleslib.api.client.event.v1.gui;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

@FunctionalInterface
public interface CustomizeChatPanelCallback {
   EventInvoker<CustomizeChatPanelCallback> EVENT = EventInvoker.lookup(CustomizeChatPanelCallback.class);

   void onRenderChatPanel(GuiGraphics var1, DeltaTracker var2, MutableInt var3, MutableInt var4);
}
