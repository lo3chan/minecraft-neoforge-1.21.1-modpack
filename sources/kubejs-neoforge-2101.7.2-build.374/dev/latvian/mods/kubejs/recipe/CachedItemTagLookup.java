package dev.latvian.mods.kubejs.recipe;

import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagLoader.EntryWithSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class CachedItemTagLookup extends CachedTagLookup<Item> {
   public CachedItemTagLookup(Registry<Item> registry, Map<ResourceLocation, List<EntryWithSource>> originalMap) {
      super(registry, originalMap);
   }

   @Override
   public boolean isEmpty(TagKey<Item> key) {
      Set<Item> set = this.values(key);
      return set.size() - ((set.contains(Items.AIR) ? 1 : 0) + (set.contains(Items.BARRIER) ? 1 : 0)) <= 0;
   }
}
