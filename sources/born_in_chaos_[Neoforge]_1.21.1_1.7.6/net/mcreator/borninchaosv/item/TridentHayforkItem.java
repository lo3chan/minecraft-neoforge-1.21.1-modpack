package net.mcreator.borninchaosv.item;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

public class TridentHayforkItem extends SwordItem {
   private static final Tier TOOL_TIER = new Tier() {
      public int getUses() {
         return 270;
      }

      public float getSpeed() {
         return 19.0F;
      }

      public float getAttackDamageBonus() {
         return 0.0F;
      }

      public TagKey<Block> getIncorrectBlocksForDrops() {
         return BlockTags.INCORRECT_FOR_STONE_TOOL;
      }

      public int getEnchantmentValue() {
         return 6;
      }

      public Ingredient getRepairIngredient() {
         return Ingredient.of(new ItemStack[]{new ItemStack(Items.IRON_INGOT)});
      }
   };

   public TridentHayforkItem() {
      super(TOOL_TIER, new Properties().attributes(SwordItem.createAttributes(TOOL_TIER, 8.0F, -2.7F)));
   }
}
