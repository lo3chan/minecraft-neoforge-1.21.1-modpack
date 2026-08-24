package top.theillusivec4.curios.api;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class CuriosTags {
   public static final TagKey<Item> BACK = createItemTag("back");
   public static final TagKey<Item> BELT = createItemTag("belt");
   public static final TagKey<Item> BODY = createItemTag("body");
   public static final TagKey<Item> BRACELET = createItemTag("bracelet");
   public static final TagKey<Item> CHARM = createItemTag("charm");
   public static final TagKey<Item> CURIO = createItemTag("curio");
   public static final TagKey<Item> HANDS = createItemTag("hands");
   public static final TagKey<Item> HEAD = createItemTag("head");
   public static final TagKey<Item> NECKLACE = createItemTag("necklace");
   public static final TagKey<Item> RING = createItemTag("ring");

   public static TagKey<Item> createItemTag(String id) {
      return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("curios", id));
   }
}
