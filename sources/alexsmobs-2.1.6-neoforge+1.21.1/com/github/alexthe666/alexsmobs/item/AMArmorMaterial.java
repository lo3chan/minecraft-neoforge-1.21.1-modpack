package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.misc.AMCompat;
import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorMaterial.Layer;
import net.minecraft.world.item.crafting.Ingredient;

public class AMArmorMaterial {
   protected static final int[] MAX_DAMAGE_ARRAY = new int[]{13, 15, 16, 11};
   private final String name;
   private final int durability;
   private final int[] damageReduction;
   private final int encantability;
   private final Holder<SoundEvent> sound;
   private final float toughness;
   private Supplier<Ingredient> ingredient = null;
   public float knockbackResistance = 0.0F;
   private Holder<ArmorMaterial> holder;

   public AMArmorMaterial(String name, int durability, int[] damageReduction, int encantability, Holder<SoundEvent> sound, float toughness) {
      this.name = name;
      this.durability = durability;
      this.damageReduction = damageReduction;
      this.encantability = encantability;
      this.sound = sound;
      this.toughness = toughness;
      this.knockbackResistance = 0.0F;
   }

   public AMArmorMaterial(
      String name, int durability, int[] damageReduction, int encantability, Holder<SoundEvent> sound, float toughness, float knockbackResist
   ) {
      this.name = name;
      this.durability = durability;
      this.damageReduction = damageReduction;
      this.encantability = encantability;
      this.sound = sound;
      this.toughness = toughness;
      this.knockbackResistance = knockbackResist;
   }

   public int getDurabilityForType(Type type) {
      return (type.ordinal() < MAX_DAMAGE_ARRAY.length ? MAX_DAMAGE_ARRAY[type.ordinal()] : 1) * this.durability;
   }

   public int getDefenseForType(Type type) {
      return type.ordinal() < this.damageReduction.length ? this.damageReduction[type.ordinal()] : 0;
   }

   public int getEnchantmentValue() {
      return this.encantability;
   }

   public Holder<SoundEvent> getEquipSound() {
      return this.sound;
   }

   public Ingredient getRepairIngredient() {
      return this.ingredient == null ? Ingredient.EMPTY : this.ingredient.get();
   }

   public void setRepairMaterial(Ingredient ingredient) {
      this.ingredient = () -> ingredient;
   }

   public void setRepairMaterial(Supplier<Ingredient> ingredient) {
      this.ingredient = AMCompat.lazyIngredient(ingredient);
   }

   public String getName() {
      return this.name;
   }

   public float getToughness() {
      return this.toughness;
   }

   public float getKnockbackResistance() {
      return this.knockbackResistance;
   }

   public int getDurability() {
      return this.durability;
   }

   public Holder<ArmorMaterial> holder() {
      if (this.holder == null) {
         EnumMap<Type, Integer> defense = new EnumMap<>(Type.class);

         for (Type type : Type.values()) {
            defense.put(type, type.ordinal() < this.damageReduction.length ? this.damageReduction[type.ordinal()] : 0);
         }

         this.holder = Holder.direct(
            new ArmorMaterial(
               defense,
               this.encantability,
               this.sound,
               () -> this.ingredient == null ? Ingredient.EMPTY : this.ingredient.get(),
               List.of(new Layer(AMCompat.rl("alexsmobs", this.name))),
               this.toughness,
               this.knockbackResistance
            )
         );
      }

      return this.holder;
   }
}
