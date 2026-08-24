package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.rhino.util.RemapForJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

public class MutableToolTier implements Tier {
   public final Tier parent;
   private int uses;
   private float speed;
   private float attackDamageBonus;
   private TagKey<Block> incorrectBlocksForDrops;
   private int enchantmentValue;
   private Ingredient repairIngredient;

   public MutableToolTier(Tier p) {
      this.parent = p;
      this.uses = this.parent.getUses();
      this.speed = this.parent.getSpeed();
      this.attackDamageBonus = this.parent.getAttackDamageBonus();
      this.incorrectBlocksForDrops = this.parent.getIncorrectBlocksForDrops();
      this.enchantmentValue = this.parent.getEnchantmentValue();
      this.repairIngredient = this.parent.getRepairIngredient();
   }

   @RemapForJS("getUses")
   public int getUses() {
      return this.uses;
   }

   public void setUses(int i) {
      this.uses = i;
   }

   @RemapForJS("getSpeed")
   public float getSpeed() {
      return this.speed;
   }

   public void setSpeed(float f) {
      this.speed = f;
   }

   @RemapForJS("getAttackDamageBonus")
   public float getAttackDamageBonus() {
      return this.attackDamageBonus;
   }

   public void setIncorrectBlocksForDropsTag(ResourceLocation tag) {
      this.incorrectBlocksForDrops = BlockTags.create(tag);
   }

   public ResourceLocation getIncorrectBlocksForDropsTag() {
      return this.incorrectBlocksForDrops.location();
   }

   public TagKey<Block> getIncorrectBlocksForDrops() {
      return this.incorrectBlocksForDrops;
   }

   public void setAttackDamageBonus(float f) {
      this.attackDamageBonus = f;
   }

   @RemapForJS("getEnchantmentValue")
   public int getEnchantmentValue() {
      return this.enchantmentValue;
   }

   public void setEnchantmentValue(int i) {
      this.enchantmentValue = i;
   }

   @RemapForJS("getVanillaRepairIngredient")
   public Ingredient getRepairIngredient() {
      return this.repairIngredient;
   }

   public void setRepairIngredient(Ingredient in) {
      this.repairIngredient = in;
   }
}
