package net.blay09.mods.balm.neoforge.client.color.item.internal;

import java.util.function.Supplier;
import net.blay09.mods.balm.client.color.item.BalmItemColorRegistrar;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent.Item;

public class NeoForgeBalmItemColorRegistrar implements BalmItemColorRegistrar {
   private final Item event;

   public NeoForgeBalmItemColorRegistrar(Item event) {
      this.event = event;
   }

   @Override
   public void register(ItemColor color, Supplier<ItemLike[]> items) {
      this.event.register(color, items.get());
   }
}
