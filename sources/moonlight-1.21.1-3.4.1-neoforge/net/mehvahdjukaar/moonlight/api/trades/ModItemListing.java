package net.mehvahdjukaar.moonlight.api.trades;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.mehvahdjukaar.moonlight.api.MoonlightRegistry;
import net.mehvahdjukaar.moonlight.api.util.codec.CodecUtils;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;

public interface ModItemListing extends ItemListing {
   Codec<ModItemListing> CODEC = CodecUtils.remapNamespaceCodec(MoonlightRegistry.VILLAGER_TRADES_REGISTRY, "minecraft", "moonlight")
      .dispatch(ModItemListing::getCodec, mapCodec -> mapCodec);

   default int getLevel() {
      return 1;
   }

   MapCodec<? extends ModItemListing> getCodec();

   static int defaultXp(boolean buying, int villagerLevel) {
      return Math.max(1, 5 * (villagerLevel - 1)) * (buying ? 2 : 1);
   }
}
