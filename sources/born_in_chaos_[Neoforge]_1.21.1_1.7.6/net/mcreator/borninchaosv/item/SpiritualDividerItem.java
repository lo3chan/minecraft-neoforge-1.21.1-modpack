package net.mcreator.borninchaosv.item;

import java.util.List;
import net.mcreator.borninchaosv.init.BornInChaosV1ModItems;
import net.mcreator.borninchaosv.procedures.SpiritualDividerKoghdaZhivaiaSushchnostPopadaietSPomoshchiuInstrumientaProcedure;
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

public class SpiritualDividerItem extends SwordItem {
   private static final Tier TOOL_TIER = new Tier() {
      public int getUses() {
         return 500;
      }

      public float getSpeed() {
         return 5.0F;
      }

      public float getAttackDamageBonus() {
         return 0.0F;
      }

      public TagKey<Block> getIncorrectBlocksForDrops() {
         return BlockTags.INCORRECT_FOR_DIAMOND_TOOL;
      }

      public int getEnchantmentValue() {
         return 15;
      }

      public Ingredient getRepairIngredient() {
         return Ingredient.of(new ItemStack[]{new ItemStack((ItemLike)BornInChaosV1ModItems.SEEDOF_CHAOS.get())});
      }
   };

   public SpiritualDividerItem() {
      super(TOOL_TIER, new Properties().attributes(SwordItem.createAttributes(TOOL_TIER, 4.0F, -2.2F)).fireResistant());
   }

   public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
      boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
      SpiritualDividerKoghdaZhivaiaSushchnostPopadaietSPomoshchiuInstrumientaProcedure.execute(
         entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity, sourceentity
      );
      return retval;
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
      super.appendHoverText(itemstack, context, list, flag);
      list.add(Component.translatable("item.born_in_chaos_v1.spiritual_sword.description_0"));
   }
}
