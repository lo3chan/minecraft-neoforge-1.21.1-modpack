package net.cibernet.alchemancy.properties;

import java.util.Optional;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent.Stop;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem;

public class BowProperty extends Property {
   @Override
   public void onStopUsingItem(ItemStack stack, LivingEntity user, Stop event) {
      if (event.getDuration() > 5) {
         Items.BOW.asItem().releaseUsing(new ItemStack(Items.BOW), user.level(), user, event.getDuration());
         event.setCanceled(true);
      }
   }

   @Override
   public void onRightClickItem(RightClickItem event) {
      if (!event.isCanceled() && this.canUse(event.getLevel(), event.getEntity(), event.getHand())) {
         event.getEntity().startUsingItem(event.getHand());
         event.setCancellationResult(InteractionResult.CONSUME);
         event.setCanceled(true);
      }
   }

   @Override
   public void onRightClickEntity(EntityInteractSpecific event) {
      if (!event.isCanceled() && this.canUse(event.getLevel(), event.getEntity(), event.getHand())) {
         event.getEntity().startUsingItem(event.getHand());
         event.setCancellationResult(InteractionResult.CONSUME);
         event.setCanceled(true);
      }
   }

   public boolean canUse(Level level, Player player, InteractionHand hand) {
      ItemStack itemstack = new ItemStack(Items.BOW);
      boolean flag = !player.getProjectile(itemstack).isEmpty();
      InteractionResultHolder<ItemStack> ret = EventHooks.onArrowNock(itemstack, level, player, hand, flag);
      return ret != null ? ret.getResult().consumesAction() : player.hasInfiniteMaterials() || flag;
   }

   @Override
   public Optional<UseAnim> modifyUseAnimation(ItemStack stack, UseAnim original, Optional<UseAnim> current) {
      return current.isEmpty() ? Optional.of(UseAnim.BOW) : current;
   }

   @Override
   public int modifyUseDuration(ItemStack stack, int original, int result) {
      return 72000;
   }

   @Override
   public int getColor(ItemStack stack) {
      return 8790440;
   }

   @Override
   public int getPriority() {
      return -100;
   }
}
