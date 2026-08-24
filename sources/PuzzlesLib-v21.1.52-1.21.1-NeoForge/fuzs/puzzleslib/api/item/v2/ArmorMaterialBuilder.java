package fuzs.puzzleslib.api.item.v2;

import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorMaterial.Layer;
import net.minecraft.world.item.crafting.Ingredient;

public final class ArmorMaterialBuilder {
   private int durability;
   private Map<Type, Integer> defense = (Map<Type, Integer>)Util.make(new EnumMap(Type.class), map -> {
      for (Type armorType : Type.values()) {
         map.put(armorType, 0);
      }
   });
   private int enchantmentValue = 1;
   private Holder<SoundEvent> equipSound = SoundEvents.ARMOR_EQUIP_GENERIC;
   private float toughness;
   private float knockbackResistance;
   private Either<TagKey<Item>, Supplier<Ingredient>> repairIngredient;
   private Either<ResourceLocation, List<Layer>> assetId;

   private ArmorMaterialBuilder() {
   }

   public static ArmorMaterialBuilder of(TagKey<Item> repairIngredient) {
      return new ArmorMaterialBuilder().setRepairIngredient(repairIngredient);
   }

   public static ArmorMaterialBuilder of(ResourceLocation assetId, TagKey<Item> repairIngredient) {
      return new ArmorMaterialBuilder().setRepairIngredient(repairIngredient).setAssetId(assetId);
   }

   public static ArmorMaterialBuilder copyOf(ArmorMaterial armorMaterial) {
      return new ArmorMaterialBuilder()
         .setRepairIngredient(armorMaterial.repairIngredient())
         .setAssetId(armorMaterial.layers())
         .setDefense(armorMaterial.defense())
         .setEnchantmentValue(armorMaterial.enchantmentValue())
         .setEquipSound(armorMaterial.equipSound())
         .setToughness(armorMaterial.toughness())
         .setKnockbackResistance(armorMaterial.knockbackResistance());
   }

   @Deprecated
   public ArmorMaterialBuilder setDurability(int durability) {
      this.durability = durability;
      return this;
   }

   public ArmorMaterialBuilder setDefense(int defense) {
      return this.setDefense(defense, defense, defense, defense, defense);
   }

   public ArmorMaterialBuilder setDefense(int boots, int leggings, int chestplate, int helmet) {
      return this.setDefense(boots, leggings, chestplate, helmet, 0);
   }

   public ArmorMaterialBuilder setDefense(int boots, int leggings, int chestplate, int helmet, int body) {
      return this.setDefense(Type.BOOTS, boots)
         .setDefense(Type.LEGGINGS, leggings)
         .setDefense(Type.CHESTPLATE, chestplate)
         .setDefense(Type.HELMET, helmet)
         .setDefense(Type.BODY, body);
   }

   public ArmorMaterialBuilder setDefense(Type armorType, int defense) {
      this.defense.put(armorType, defense);
      return this;
   }

   private ArmorMaterialBuilder setDefense(Map<Type, Integer> defense) {
      this.defense = defense;
      return this;
   }

   public ArmorMaterialBuilder setEnchantmentValue(int enchantmentValue) {
      this.enchantmentValue = enchantmentValue;
      return this;
   }

   public ArmorMaterialBuilder setEquipSound(Holder<SoundEvent> equipSound) {
      Objects.requireNonNull(equipSound, "equip sound is null");
      this.equipSound = equipSound;
      return this;
   }

   public ArmorMaterialBuilder setToughness(float toughness) {
      this.toughness = toughness;
      return this;
   }

   public ArmorMaterialBuilder setKnockbackResistance(float knockbackResistance) {
      this.knockbackResistance = knockbackResistance;
      return this;
   }

   @Deprecated
   public ArmorMaterialBuilder setRepairIngredient(Supplier<Ingredient> repairIngredient) {
      Objects.requireNonNull(repairIngredient, "repair ingredient is null");
      this.repairIngredient = Either.right(repairIngredient);
      return this;
   }

   public ArmorMaterialBuilder setRepairIngredient(TagKey<Item> repairIngredient) {
      Objects.requireNonNull(repairIngredient, "repair ingredient is null");
      this.repairIngredient = Either.left(repairIngredient);
      return this;
   }

   @Deprecated
   public ArmorMaterialBuilder setAssetId(List<Layer> layers) {
      Objects.requireNonNull(layers, "layers is null");
      this.assetId = Either.right(layers);
      return this;
   }

   public ArmorMaterialBuilder setAssetId(ResourceLocation assetId) {
      Objects.requireNonNull(assetId, "asset id is null");
      this.assetId = Either.left(assetId);
      return this;
   }

   public ArmorMaterialBuilder setNoAssetId() {
      this.assetId = Either.right(Collections.emptyList());
      return this;
   }

   public ArmorMaterial build() {
      Objects.requireNonNull(this.defense, "defense map is null");
      Objects.requireNonNull(this.equipSound, "equip sound is null");
      Objects.requireNonNull(this.repairIngredient, "repair ingredient is null");
      Objects.requireNonNull(this.assetId, "asset id is null");
      return new ArmorMaterial(
         Maps.immutableEnumMap(this.defense),
         this.enchantmentValue,
         this.equipSound,
         (Supplier)this.repairIngredient.map(tagKey -> Suppliers.memoize(() -> Ingredient.of(tagKey)), Function.identity()),
         (List)this.assetId
            .map(
               resourceLocation -> Collections.singletonList(
                  new Layer(resourceLocation) {
                     protected ResourceLocation resolveTexture(boolean innerTexture) {
                        return this.assetName
                           .withPath(
                              string -> "textures/entity/equipment/"
                                 + (innerTexture ? "humanoid_leggings" : "humanoid")
                                 + "/"
                                 + this.assetName.getPath()
                                 + ".png"
                           );
                     }
                  }
               ),
               Function.identity()
            ),
         this.toughness,
         this.knockbackResistance
      );
   }
}
