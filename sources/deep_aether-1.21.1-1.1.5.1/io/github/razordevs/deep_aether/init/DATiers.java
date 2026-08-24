package io.github.razordevs.deep_aether.init;

import io.github.razordevs.deep_aether.datagen.tags.DATags;
import java.util.function.Supplier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public enum DATiers implements Tier {
   SKYJADE(BlockTags.INCORRECT_FOR_GOLD_TOOL, 150, 10.0F, 2.0F, 0, () -> Ingredient.of(DATags.Items.SKYJADE_REPAIRING)),
   STRATUS(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 2031, 9.0F, 4.0F, 15, () -> Ingredient.of(DATags.Items.STRATUS_REPAIRING)),
   FIRE(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 500, 0.0F, 2.0F, 0, () -> Ingredient.of(new ItemLike[]{Items.FIRE_CHARGE})),
   STORM(BlockTags.INCORRECT_FOR_IRON_TOOL, 502, 8.0F, 3.0F, 10, () -> Ingredient.of(DATags.Items.STORM_REPAIRING)),
   LUCK(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 500, 9.0F, 1.0F, 15, () -> Ingredient.of(new ItemStack[]{ItemStack.EMPTY}));

   private final TagKey<Block> incorrectBlocksForDrops;
   private final int maxUses;
   private final float efficiency;
   private final float attackDamage;
   private final int enchantability;
   private final Supplier<Ingredient> repairMaterial;

   private DATiers(
      TagKey<Block> incorrectBlocksForDrops, int maxUses, float efficiency, float attackDamage, int enchantability, Supplier<Ingredient> repairMaterial
   ) {
      this.incorrectBlocksForDrops = incorrectBlocksForDrops;
      this.maxUses = maxUses;
      this.efficiency = efficiency;
      this.attackDamage = attackDamage;
      this.enchantability = enchantability;
      this.repairMaterial = repairMaterial;
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
      return this.incorrectBlocksForDrops;
   }

   public int getEnchantmentValue() {
      return this.enchantability;
   }

   public Ingredient getRepairIngredient() {
      return this.repairMaterial.get();
   }
}
