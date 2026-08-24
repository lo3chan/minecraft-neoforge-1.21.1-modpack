package fuzs.puzzleslib.api.client.event.v1.gui;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import java.util.List;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;

@FunctionalInterface
public interface GatherEffectScreenTooltipCallback {
   EventInvoker<GatherEffectScreenTooltipCallback> EVENT = EventInvoker.lookup(GatherEffectScreenTooltipCallback.class);

   void onGatherEffectScreenTooltip(EffectRenderingInventoryScreen<?> var1, MobEffectInstance var2, List<Component> var3);
}
