package net.joefoxe.hexerei.item;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorMaterial.Layer;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModArmorMaterial {
   public static final DeferredRegister<ArmorMaterial> MATERIALS = DeferredRegister.create(BuiltInRegistries.ARMOR_MATERIAL, "hexerei");
   public static final Holder<ArmorMaterial> INFUSED_FABRIC = MATERIALS.register(
      "infused_fabric", () -> register("infused_fabric", (EnumMap<Type, Integer>)Util.make(new EnumMap(Type.class), map -> {
         map.put(Type.BOOTS, 3);
         map.put(Type.LEGGINGS, 4);
         map.put(Type.CHESTPLATE, 8);
         map.put(Type.HELMET, 3);
         map.put(Type.BODY, 8);
      }), 25, SoundEvents.ARMOR_EQUIP_LEATHER, 1.0F, 0.0F, () -> Ingredient.of(new ItemLike[]{(ItemLike)ModItems.INFUSED_FABRIC.get()}), true)
   );

   private static ArmorMaterial register(
      String pName,
      EnumMap<Type, Integer> pDefense,
      int pEnchantmentValue,
      Holder<SoundEvent> pEquipSound,
      float pToughness,
      float pKnockbackResistance,
      Supplier<Ingredient> pRepairIngredient,
      boolean dyeable
   ) {
      List<Layer> list = new ArrayList<>(List.of(new Layer(HexereiUtil.getResource(pName))));
      if (dyeable) {
         list.add(new Layer(HexereiUtil.getResource(pName), "", true));
      }

      return register(pDefense, pEnchantmentValue, pEquipSound, pToughness, pKnockbackResistance, pRepairIngredient, list);
   }

   private static ArmorMaterial register(
      EnumMap<Type, Integer> pDefense,
      int pEnchantmentValue,
      Holder<SoundEvent> pEquipSound,
      float pToughness,
      float pKnockbackResistance,
      Supplier<Ingredient> pRepairIngridient,
      List<Layer> pLayers
   ) {
      EnumMap<Type, Integer> enummap = new EnumMap<>(Type.class);

      for (Type armoritem$type : Type.values()) {
         enummap.put(armoritem$type, pDefense.get(armoritem$type));
      }

      return new ArmorMaterial(enummap, pEnchantmentValue, pEquipSound, pRepairIngridient, pLayers, pToughness, pKnockbackResistance);
   }
}
