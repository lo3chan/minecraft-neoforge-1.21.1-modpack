package net.mcreator.borninchaosv.item;

import java.util.List;
import net.mcreator.borninchaosv.init.BornInChaosV1ModItems;
import net.mcreator.borninchaosv.procedures.DarkBlacksmithPProcedure;
import net.mcreator.borninchaosv.procedures.FrostbittenBladeDopolnitielnaiaInformatsiiaProcedure;
import net.mcreator.borninchaosv.procedures.FrostbittenBladePriShchielchkiePKMProcedure;
import net.mcreator.borninchaosv.procedures.FrostbittenBladePriUdariePoSushchnostiInstrumientomProcedure;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
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

public class FrostbittenBladeItem extends SwordItem {
   private static final Tier TOOL_TIER = new Tier() {
      public int getUses() {
         return 1100;
      }

      public float getSpeed() {
         return 7.0F;
      }

      public float getAttackDamageBonus() {
         return 0.0F;
      }

      public TagKey<Block> getIncorrectBlocksForDrops() {
         return BlockTags.INCORRECT_FOR_NETHERITE_TOOL;
      }

      public int getEnchantmentValue() {
         return 17;
      }

      public Ingredient getRepairIngredient() {
         return Ingredient.of(
            new ItemStack[]{
               new ItemStack((ItemLike)BornInChaosV1ModItems.DARK_METAL_INGOT.get()), new ItemStack((ItemLike)BornInChaosV1ModItems.PERMAFROST_SHARD.get())
            }
         );
      }
   };

   public FrostbittenBladeItem() {
      super(TOOL_TIER, new Properties().attributes(SwordItem.createAttributes(TOOL_TIER, 5.0F, -2.6F)).fireResistant());
   }

   public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
      boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
      FrostbittenBladePriUdariePoSushchnostiInstrumientomProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity);
      return retval;
   }

   public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
      InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
      FrostbittenBladePriShchielchkiePKMProcedure.execute(world, entity.getX(), entity.getY(), entity.getZ(), entity, (ItemStack)ar.getObject());
      return ar;
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
      super.appendHoverText(itemstack, context, list, flag);
      if (itemstack.getEntityRepresentation() != null) {
         itemstack.getEntityRepresentation();
      }

      String hoverText = FrostbittenBladeDopolnitielnaiaInformatsiiaProcedure.execute();
      if (hoverText != null) {
         for (String line : hoverText.split("\n")) {
            list.add(Component.literal(line));
         }
      }
   }

   public void onCraftedBy(ItemStack itemstack, Level world, Player entity) {
      super.onCraftedBy(itemstack, world, entity);
      DarkBlacksmithPProcedure.execute(entity);
   }
}
