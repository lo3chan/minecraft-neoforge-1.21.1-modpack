package net.astralya.hexalia.item;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public final class ModToolTiers {
   public static final Tier ANCIENT = new Tier() {
      public int getUses() {
         return 250;
      }

      public float getSpeed() {
         return 8.0F;
      }

      public float getAttackDamageBonus() {
         return 3.0F;
      }

      public TagKey<Block> getIncorrectBlocksForDrops() {
         return BlockTags.INCORRECT_FOR_IRON_TOOL;
      }

      public int getEnchantmentValue() {
         return 22;
      }

      public Ingredient getRepairIngredient() {
         return Ingredient.of(new ItemLike[]{(ItemLike)ModItems.ANCIENT_SEED.get()});
      }
   };

   private ModToolTiers() {
   }
}
