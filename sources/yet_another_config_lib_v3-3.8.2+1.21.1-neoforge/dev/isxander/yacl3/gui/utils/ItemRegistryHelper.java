package dev.isxander.yacl3.gui.utils;

import dev.isxander.yacl3.platform.YACLPlatform;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public final class ItemRegistryHelper {
   public static boolean isRegisteredItem(String identifier) {
      try {
         ResourceLocation itemResourceLocation = YACLPlatform.parseRl(identifier.toLowerCase());
         return BuiltInRegistries.ITEM.containsKey(itemResourceLocation);
      } catch (ResourceLocationException var2) {
         return false;
      }
   }

   public static Item getItemFromName(String identifier, Item defaultItem) {
      try {
         ResourceLocation itemResourceLocation = YACLPlatform.parseRl(identifier.toLowerCase());
         if (BuiltInRegistries.ITEM.containsKey(itemResourceLocation)) {
            return MiscUtil.getFromRegistry(BuiltInRegistries.ITEM, itemResourceLocation);
         }
      } catch (ResourceLocationException var3) {
      }

      return defaultItem;
   }

   public static Item getItemFromName(String identifier) {
      return getItemFromName(identifier, Items.AIR);
   }

   public static Stream<ResourceLocation> getMatchingItemResourceLocations(String value) {
      int sep = value.indexOf(58);
      Predicate<ResourceLocation> filterPredicate;
      if (sep == -1) {
         filterPredicate = identifier -> identifier.getPath().contains(value)
            || MiscUtil.<Item>getFromRegistry(BuiltInRegistries.ITEM, identifier).getDescription().getString().toLowerCase().contains(value.toLowerCase());
      } else {
         String namespace = value.substring(0, sep);
         String path = value.substring(sep + 1);
         filterPredicate = identifier -> identifier.getNamespace().equals(namespace) && identifier.getPath().startsWith(path);
      }

      return BuiltInRegistries.ITEM.keySet().stream().filter(filterPredicate).sorted((id1, id2) -> {
         String pathx = (sep == -1 ? value : value.substring(sep + 1)).toLowerCase();
         boolean id1StartsWith = id1.getPath().toLowerCase().startsWith(pathx);
         boolean id2StartsWith = id2.getPath().toLowerCase().startsWith(pathx);
         if (id1StartsWith) {
            return id2StartsWith ? id1.compareTo(id2) : -1;
         } else {
            return id2StartsWith ? 1 : id1.compareTo(id2);
         }
      });
   }
}
