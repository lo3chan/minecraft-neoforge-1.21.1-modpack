package dev.shadowsoffire.placebo.systems.wanderer;

import dev.shadowsoffire.placebo.codec.CodecProvider;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;

public interface WandererTrade extends ItemListing, CodecProvider<WandererTrade> {
   boolean isRare();
}
