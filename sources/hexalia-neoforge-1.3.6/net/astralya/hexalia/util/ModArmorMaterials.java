package net.astralya.hexalia.util;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorMaterial.Layer;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public final class ModArmorMaterials {
   public static final Holder<ArmorMaterial> SILKWEAVE = Holder.direct(
      new ArmorMaterial(
         (Map)Util.make(new EnumMap(Type.class), map -> {
            map.put(Type.BOOTS, 2);
            map.put(Type.LEGGINGS, 5);
            map.put(Type.CHESTPLATE, 6);
            map.put(Type.HELMET, 2);
            map.put(Type.BODY, 0);
         }),
         22,
         SoundEvents.ARMOR_EQUIP_LEATHER,
         () -> Ingredient.of(new ItemLike[]{(ItemLike)ModItems.SILK_FIBER.get()}),
         List.of(new Layer(ResourceLocation.fromNamespaceAndPath("hexalia", "silkweave"))),
         0.0F,
         0.0F
      )
   );
   public static final Holder<ArmorMaterial> MOONWEAVE = Holder.direct(
      new ArmorMaterial(
         (Map)Util.make(new EnumMap(Type.class), map -> {
            map.put(Type.BOOTS, 3);
            map.put(Type.LEGGINGS, 6);
            map.put(Type.CHESTPLATE, 8);
            map.put(Type.HELMET, 3);
            map.put(Type.BODY, 0);
         }),
         22,
         SoundEvents.ARMOR_EQUIP_LEATHER,
         () -> Ingredient.of(new ItemLike[]{(ItemLike)ModItems.CELESTIAL_CRYSTAL.get()}),
         List.of(new Layer(ResourceLocation.fromNamespaceAndPath("hexalia", "moonweave"))),
         0.0F,
         0.0F
      )
   );

   private ModArmorMaterials() {
   }
}
