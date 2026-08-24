package net.blay09.mods.balm.neoforge.world.entity.npc.villager.internal;

import java.util.ArrayList;
import java.util.List;
import net.blay09.mods.balm.world.entity.npc.villager.BalmVillagerTradeRegistrar;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

public class NeoForgeBalmVillagerTradeRegistrar implements BalmVillagerTradeRegistrar {
   private final VillagerTradesEvent event;

   public NeoForgeBalmVillagerTradeRegistrar(VillagerTradesEvent event) {
      this.event = event;
   }

   @Override
   public void registerTrade(ResourceKey<VillagerProfession> profession, int level, ItemListing... listings) {
      if (this.event.getType().equals(profession)) {
         List<ItemListing> trades = (List<ItemListing>)this.event.getTrades().computeIfAbsent(level, key -> new ArrayList());
         trades.addAll(List.of(listings));
      }
   }
}
