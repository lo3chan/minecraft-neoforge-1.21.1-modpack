package net.mcreator.borninchaosv.item;

import java.util.List;
import net.mcreator.borninchaosv.init.BornInChaosV1ModBlocks;
import net.mcreator.borninchaosv.procedures.NutHammerDopolnitielnaiaInformatsiiaProcedure;
import net.mcreator.borninchaosv.procedures.NutHammerPriUdariePoSushchnostiInstrumientomProcedure;
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
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class NutHammerItem extends SwordItem {
   private static final Tier TOOL_TIER = new Tier() {
      public int getUses() {
         return 250;
      }

      public float getSpeed() {
         return 3.0F;
      }

      public float getAttackDamageBonus() {
         return 0.0F;
      }

      public TagKey<Block> getIncorrectBlocksForDrops() {
         return BlockTags.INCORRECT_FOR_WOODEN_TOOL;
      }

      public int getEnchantmentValue() {
         return 15;
      }

      public Ingredient getRepairIngredient() {
         return Ingredient.of(
            new ItemStack[]{
               new ItemStack(Blocks.OAK_LOG),
               new ItemStack(Blocks.SPRUCE_LOG),
               new ItemStack(Blocks.BIRCH_LOG),
               new ItemStack(Blocks.JUNGLE_LOG),
               new ItemStack(Blocks.ACACIA_LOG),
               new ItemStack(Blocks.DARK_OAK_LOG),
               new ItemStack(Blocks.MANGROVE_LOG),
               new ItemStack(Blocks.CHERRY_LOG),
               new ItemStack(Blocks.STRIPPED_OAK_LOG),
               new ItemStack(Blocks.STRIPPED_SPRUCE_LOG),
               new ItemStack(Blocks.STRIPPED_BIRCH_LOG),
               new ItemStack(Blocks.STRIPPED_JUNGLE_LOG),
               new ItemStack(Blocks.STRIPPED_ACACIA_LOG),
               new ItemStack(Blocks.STRIPPED_DARK_OAK_LOG),
               new ItemStack(Blocks.STRIPPED_MANGROVE_LOG),
               new ItemStack(Blocks.STRIPPED_CHERRY_LOG),
               new ItemStack(Blocks.OAK_WOOD),
               new ItemStack(Blocks.SPRUCE_WOOD),
               new ItemStack(Blocks.BIRCH_WOOD),
               new ItemStack(Blocks.JUNGLE_WOOD),
               new ItemStack(Blocks.ACACIA_WOOD),
               new ItemStack(Blocks.DARK_OAK_WOOD),
               new ItemStack(Blocks.MANGROVE_WOOD),
               new ItemStack(Blocks.CHERRY_WOOD),
               new ItemStack(Blocks.STRIPPED_OAK_WOOD),
               new ItemStack(Blocks.STRIPPED_SPRUCE_WOOD),
               new ItemStack(Blocks.STRIPPED_BIRCH_WOOD),
               new ItemStack(Blocks.STRIPPED_JUNGLE_WOOD),
               new ItemStack(Blocks.STRIPPED_ACACIA_WOOD),
               new ItemStack(Blocks.STRIPPED_DARK_OAK_WOOD),
               new ItemStack(Blocks.STRIPPED_MANGROVE_WOOD),
               new ItemStack(Blocks.STRIPPED_CHERRY_WOOD),
               new ItemStack((ItemLike)BornInChaosV1ModBlocks.SCORCHED_LOG.get()),
               new ItemStack((ItemLike)BornInChaosV1ModBlocks.SCORCHED_WOOD.get()),
               new ItemStack((ItemLike)BornInChaosV1ModBlocks.STRIPPED_SCORCHED_LOG.get())
            }
         );
      }
   };

   public NutHammerItem() {
      super(TOOL_TIER, new Properties().attributes(SwordItem.createAttributes(TOOL_TIER, 4.0F, -2.7F)));
   }

   public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
      boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
      NutHammerPriUdariePoSushchnostiInstrumientomProcedure.execute(entity.level(), entity);
      return retval;
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
      super.appendHoverText(itemstack, context, list, flag);
      if (itemstack.getEntityRepresentation() != null) {
         itemstack.getEntityRepresentation();
      }

      String hoverText = NutHammerDopolnitielnaiaInformatsiiaProcedure.execute();
      if (hoverText != null) {
         for (String line : hoverText.split("\n")) {
            list.add(Component.literal(line));
         }
      }
   }
}
