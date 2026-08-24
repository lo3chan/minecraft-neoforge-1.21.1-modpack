package net.mcreator.borninchaosv.item;

import net.mcreator.borninchaosv.procedures.WoodSplitterAxePriUdariePoSushchnostiInstrumientomProcedure;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

public class WoodSplitterAxeItem extends AxeItem {
   private static final Tier TOOL_TIER = new Tier() {
      public int getUses() {
         return 270;
      }

      public float getSpeed() {
         return 10.0F;
      }

      public float getAttackDamageBonus() {
         return 0.0F;
      }

      public TagKey<Block> getIncorrectBlocksForDrops() {
         return BlockTags.INCORRECT_FOR_DIAMOND_TOOL;
      }

      public int getEnchantmentValue() {
         return 17;
      }

      public Ingredient getRepairIngredient() {
         return Ingredient.of(new ItemStack[]{new ItemStack(Items.IRON_INGOT)});
      }
   };

   public WoodSplitterAxeItem() {
      super(TOOL_TIER, new Properties().attributes(DiggerItem.createAttributes(TOOL_TIER, 9.0F, -3.0F)));
   }

   public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
      boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
      WoodSplitterAxePriUdariePoSushchnostiInstrumientomProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity);
      return retval;
   }
}
