package net.nycto_team.overpacked.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.nycto_team.overpacked.util.ModLoc;

public class ModTags {
   public static class Items {
      public static final TagKey<Item> giant_backpacks = tag("giant_backpacks");

      public static TagKey<Item> tag(String name) {
         return TagKey.create(Registries.ITEM, ModLoc.get(name));
      }
   }
}
