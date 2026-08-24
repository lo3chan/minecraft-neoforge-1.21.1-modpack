package net.mcreator.undeadrevamp.item;

import java.util.List;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModItems;
import net.mcreator.undeadrevamp.procedures.ChainswordLivingEntityIsHitWithToolProcedure;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ChainswordItem extends SwordItem {
   private static final Tier TOOL_TIER = new Tier() {
      public int getUses() {
         return 6500;
      }

      public float getSpeed() {
         return 5.0F;
      }

      public float getAttackDamageBonus() {
         return 0.0F;
      }

      public TagKey<Block> getIncorrectBlocksForDrops() {
         return BlockTags.INCORRECT_FOR_STONE_TOOL;
      }

      public int getEnchantmentValue() {
         return 10;
      }

      public Ingredient getRepairIngredient() {
         return Ingredient.of(new ItemStack[]{new ItemStack((ItemLike)UndeadRevamp2ModItems.HEAVYTOOTH.get())});
      }
   };

   public ChainswordItem() {
      super(TOOL_TIER, new Properties().attributes(SwordItem.createAttributes(TOOL_TIER, 3.0F, 96.0F)));
   }

   public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
      boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
      ChainswordLivingEntityIsHitWithToolProcedure.execute(entity, sourceentity);
      return retval;
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
      super.appendHoverText(itemstack, context, list, flag);
      list.add(Component.translatable("item.undead_revamp2.chainsword.description_0"));
      list.add(Component.translatable("item.undead_revamp2.chainsword.description_1"));
   }
}
