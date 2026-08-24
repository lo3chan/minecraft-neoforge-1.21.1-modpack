package net.blay09.mods.balm.client;

import java.util.function.Function;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public interface BalmClientTooltipComponentRegistrar {
   <T extends TooltipComponent> void register(Class<T> var1, Function<? super T, ? extends ClientTooltipComponent> var2);
}
