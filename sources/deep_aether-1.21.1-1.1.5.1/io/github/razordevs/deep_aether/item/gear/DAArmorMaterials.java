package io.github.razordevs.deep_aether.item.gear;

import io.github.razordevs.deep_aether.datagen.tags.DATags;
import io.github.razordevs.deep_aether.init.DASounds;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorMaterial.Layer;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DAArmorMaterials {
   public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(BuiltInRegistries.ARMOR_MATERIAL, "deep_aether");
   public static final DeferredHolder<ArmorMaterial, ArmorMaterial> STRATUS = ARMOR_MATERIALS.register(
      "stratus",
      () -> new ArmorMaterial(
         (Map)Util.make(new EnumMap(Type.class), map -> {
            map.put(Type.BOOTS, 3);
            map.put(Type.LEGGINGS, 6);
            map.put(Type.CHESTPLATE, 8);
            map.put(Type.HELMET, 3);
         }),
         15,
         DASounds.ITEM_ARMOR_EQUIP_STRATUS,
         () -> Ingredient.of(DATags.Items.STRATUS_REPAIRING),
         List.of(new Layer(ResourceLocation.fromNamespaceAndPath("deep_aether", "stratus"))),
         1.5F,
         0.15F
      )
   );
   public static final DeferredHolder<ArmorMaterial, ArmorMaterial> STORMFORGED = ARMOR_MATERIALS.register(
      "stormforged",
      () -> new ArmorMaterial(
         (Map)Util.make(new EnumMap(Type.class), map -> {
            map.put(Type.BOOTS, 2);
            map.put(Type.LEGGINGS, 5);
            map.put(Type.CHESTPLATE, 7);
            map.put(Type.HELMET, 3);
         }),
         15,
         DASounds.ITEM_ARMOR_EQUIP_STORMFORGED,
         () -> Ingredient.of(new ItemStack[]{ItemStack.EMPTY}),
         List.of(new Layer(ResourceLocation.fromNamespaceAndPath("deep_aether", "stormforged"))),
         0.0F,
         0.15F
      )
   );
   public static final DeferredHolder<ArmorMaterial, ArmorMaterial> SKYJADE = ARMOR_MATERIALS.register(
      "skyjade",
      () -> new ArmorMaterial(
         (Map)Util.make(new EnumMap(Type.class), map -> {
            map.put(Type.BOOTS, 3);
            map.put(Type.LEGGINGS, 6);
            map.put(Type.CHESTPLATE, 8);
            map.put(Type.HELMET, 3);
         }),
         10,
         DASounds.ITEM_ARMOR_EQUIP_SKYJADE,
         () -> Ingredient.of(DATags.Items.SKYJADE_REPAIRING),
         List.of(new Layer(ResourceLocation.fromNamespaceAndPath("deep_aether", "skyjade"))),
         0.0F,
         0.0F
      )
   );
}
