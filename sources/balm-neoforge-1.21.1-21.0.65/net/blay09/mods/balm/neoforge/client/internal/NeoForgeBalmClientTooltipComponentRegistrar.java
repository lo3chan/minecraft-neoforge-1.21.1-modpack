package net.blay09.mods.balm.neoforge.client.internal;

import java.util.function.Function;
import net.blay09.mods.balm.client.BalmClientTooltipComponentRegistrar;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;

public class NeoForgeBalmClientTooltipComponentRegistrar implements BalmClientTooltipComponentRegistrar {
   private final RegisterClientTooltipComponentFactoriesEvent event;

   public NeoForgeBalmClientTooltipComponentRegistrar(RegisterClientTooltipComponentFactoriesEvent event) {
      this.event = event;
   }

   @Override
   public <T extends TooltipComponent> void register(Class<T> type, Function<? super T, ? extends ClientTooltipComponent> factory) {
      this.event.register(type, factory);
   }
}
