package net.mcreator.undeadrevamp.item;

import java.util.List;
import net.mcreator.undeadrevamp.procedures.SleepingsmokebombOnPlayerStoppedUsingProcedure;
import net.mcreator.undeadrevamp.procedures.SleepingsmokebombRightclickedProcedure;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class SleepingsmokebombItem extends Item {
   public SleepingsmokebombItem() {
      super(new Properties().durability(1).rarity(Rarity.COMMON));
   }

   public UseAnim getUseAnimation(ItemStack itemstack) {
      return UseAnim.SPEAR;
   }

   public int getUseDuration(ItemStack itemstack, LivingEntity livingEntity) {
      return 20;
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
      super.appendHoverText(itemstack, context, list, flag);
      list.add(Component.translatable("item.undead_revamp2.sleepingsmokebomb.description_0"));
   }

   public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
      InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
      entity.startUsingItem(hand);
      SleepingsmokebombRightclickedProcedure.execute(world, entity.getX(), entity.getY(), entity.getZ());
      return ar;
   }

   public void releaseUsing(ItemStack itemstack, Level world, LivingEntity entity, int time) {
      SleepingsmokebombOnPlayerStoppedUsingProcedure.execute(world, entity.getX(), entity.getY(), entity.getZ(), entity, itemstack);
   }
}
