package net.joefoxe.hexerei.item;

import java.util.function.Supplier;
import net.minecraft.tags.TagKey;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public enum ModItemTier implements Tier {
   INFUSED_FABRIC(4, 2031, 9.0F, 4.0F, 15, () -> Ingredient.of(new ItemLike[]{(ItemLike)ModItems.INFUSED_FABRIC.get()}));

   private final int harvestLevel;
   private final int maxUses;
   private final float efficiency;
   private final float attackDamage;
   private final int enchantability;
   private final LazyLoadedValue<Ingredient> repairMaterial;

   private ModItemTier(int harvestLevel, int maxUses, float efficiency, float attackDamage, int enchantability, Supplier<Ingredient> repairMaterial) {
      this.harvestLevel = harvestLevel;
      this.maxUses = maxUses;
      this.efficiency = efficiency;
      this.attackDamage = attackDamage;
      this.enchantability = enchantability;
      this.repairMaterial = new LazyLoadedValue(repairMaterial);
   }

   public int getUses() {
      return this.maxUses;
   }

   public float getSpeed() {
      return this.efficiency;
   }

   public float getAttackDamageBonus() {
      return this.attackDamage;
   }

   public TagKey<Block> getIncorrectBlocksForDrops() {
      return null;
   }

   public int getEnchantmentValue() {
      return this.enchantability;
   }

   public Ingredient getRepairIngredient() {
      return (Ingredient)this.repairMaterial.get();
   }
}
