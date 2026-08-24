package fuzs.puzzleslib.impl.item;

import java.util.function.Supplier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

public record TierImpl(
   TagKey<Block> incorrectBlocksForDrops,
   int itemDurability,
   float miningSpeed,
   float attackDamage,
   int enchantmentValue,
   Supplier<Ingredient> repairIngredient
) implements Tier {
   public int getUses() {
      return this.itemDurability;
   }

   public float getSpeed() {
      return this.miningSpeed;
   }

   public float getAttackDamageBonus() {
      return this.attackDamage;
   }

   public TagKey<Block> getIncorrectBlocksForDrops() {
      return this.incorrectBlocksForDrops;
   }

   public int getEnchantmentValue() {
      return this.enchantmentValue;
   }

   public Ingredient getRepairIngredient() {
      return this.repairIngredient.get();
   }
}
