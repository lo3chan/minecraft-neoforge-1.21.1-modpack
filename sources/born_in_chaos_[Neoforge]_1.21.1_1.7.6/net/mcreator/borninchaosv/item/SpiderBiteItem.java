package net.mcreator.borninchaosv.item;

import java.util.List;
import net.mcreator.borninchaosv.init.BornInChaosV1ModItems;
import net.mcreator.borninchaosv.procedures.SpiderBiteAtackProcedure;
import net.mcreator.borninchaosv.procedures.SpiderBiteDopolnitielnaiaInformatsiiaProcedure;
import net.mcreator.borninchaosv.procedures.SpiderBiteKazhdyiTikVInvientarieProcedure;
import net.mcreator.borninchaosv.procedures.SpiderBitePriPoluchieniiPriedmietaPoRietsieptuProcedure;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class SpiderBiteItem extends SwordItem {
   private static final Tier TOOL_TIER = new Tier() {
      public int getUses() {
         return 1100;
      }

      public float getSpeed() {
         return 9.0F;
      }

      public float getAttackDamageBonus() {
         return 0.0F;
      }

      public TagKey<Block> getIncorrectBlocksForDrops() {
         return BlockTags.INCORRECT_FOR_NETHERITE_TOOL;
      }

      public int getEnchantmentValue() {
         return 20;
      }

      public Ingredient getRepairIngredient() {
         return Ingredient.of(
            new ItemStack[]{
               new ItemStack((ItemLike)BornInChaosV1ModItems.DARK_METAL_INGOT.get()), new ItemStack((ItemLike)BornInChaosV1ModItems.SPIDER_MANDIBLE.get())
            }
         );
      }
   };

   public SpiderBiteItem() {
      super(TOOL_TIER, new Properties().attributes(SwordItem.createAttributes(TOOL_TIER, 5.0F, -2.4F)).fireResistant());
   }

   public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
      boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
      SpiderBiteAtackProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity);
      return retval;
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
      super.appendHoverText(itemstack, context, list, flag);
      if (itemstack.getEntityRepresentation() != null) {
         itemstack.getEntityRepresentation();
      }

      String hoverText = SpiderBiteDopolnitielnaiaInformatsiiaProcedure.execute();
      if (hoverText != null) {
         for (String line : hoverText.split("\n")) {
            list.add(Component.literal(line));
         }
      }
   }

   public void onCraftedBy(ItemStack itemstack, Level world, Player entity) {
      super.onCraftedBy(itemstack, world, entity);
      SpiderBitePriPoluchieniiPriedmietaPoRietsieptuProcedure.execute(entity);
   }

   public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
      super.inventoryTick(itemstack, world, entity, slot, selected);
      SpiderBiteKazhdyiTikVInvientarieProcedure.execute(world, entity.getX(), entity.getY(), entity.getZ(), entity);
   }
}
