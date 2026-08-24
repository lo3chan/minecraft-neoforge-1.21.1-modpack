package net.blay09.mods.balm.world.entity.npc.villager;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;

public interface BalmVillagerTradeRegistrar {
   void registerTrade(ResourceKey<VillagerProfession> var1, int var2, ItemListing... var3);
}
