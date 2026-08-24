package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorMaterial.Layer;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

@ReturnsSelf
public class ArmorMaterialBuilder extends BuilderBase<ArmorMaterial> {
   public transient Map<Type, Integer> defense = Map.of();
   public transient int enchantmentValue = 9;
   public transient Holder<SoundEvent> equipSound = null;
   public transient Supplier<Ingredient> repairIngredient = null;
   public transient List<Layer> layers = null;
   public transient float toughness = 0.0F;
   public transient float knockbackResistance = 0.0F;

   public ArmorMaterialBuilder(ResourceLocation i) {
      super(i);
   }

   public ArmorMaterial createObject() {
      return new ArmorMaterial(
         this.defense == null ? Map.of(Type.BOOTS, 2, Type.LEGGINGS, 5, Type.CHESTPLATE, 6, Type.HELMET, 2, Type.BODY, 5) : this.defense,
         this.enchantmentValue,
         this.equipSound == null ? SoundEvents.ARMOR_EQUIP_IRON : this.equipSound,
         this.repairIngredient == null ? () -> Ingredient.of(new ItemLike[]{Items.IRON_INGOT}) : this.repairIngredient,
         this.layers == null ? List.of(new Layer(this.id)) : this.layers,
         this.toughness,
         this.knockbackResistance
      );
   }

   public ArmorMaterialBuilder defense(Map<Type, Integer> v) {
      this.defense = v;
      return this;
   }

   public ArmorMaterialBuilder enchantmentValue(int v) {
      this.enchantmentValue = v;
      return this;
   }

   public ArmorMaterialBuilder equipSound(Holder<SoundEvent> sound) {
      this.equipSound = sound;
      return this;
   }

   public ArmorMaterialBuilder repairIngredient(Supplier<Ingredient> v) {
      this.repairIngredient = v;
      return this;
   }

   public ArmorMaterialBuilder layers(Layer[] v) {
      this.layers = List.of(v);
      return this;
   }

   public ArmorMaterialBuilder toughness(float v) {
      this.toughness = v;
      return this;
   }

   public ArmorMaterialBuilder knockbackResistance(float v) {
      this.knockbackResistance = v;
      return this;
   }
}
