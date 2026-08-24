package net.mcreator.borninchaosv.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;

public class SerpumpkinheadMItem extends Item {
   public SerpumpkinheadMItem() {
      super(
         new Properties()
            .stacksTo(1)
            .rarity(Rarity.RARE)
            .jukeboxPlayable(ResourceKey.create(Registries.JUKEBOX_SONG, ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "serpumpkinhead_m")))
      );
   }
}
