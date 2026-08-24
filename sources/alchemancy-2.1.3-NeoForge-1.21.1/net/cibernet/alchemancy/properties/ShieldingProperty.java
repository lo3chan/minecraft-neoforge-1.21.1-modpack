package net.cibernet.alchemancy.properties;

import java.util.Optional;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem;

public class ShieldingProperty extends Property {
   @Override
   public void onRightClickItem(RightClickItem event) {
      event.getEntity().startUsingItem(event.getHand());
      event.setCancellationResult(InteractionResult.CONSUME);
      event.setCanceled(true);
   }

   @Override
   public void modifyDamageReceived(LivingEntity user, ItemStack weapon, EquipmentSlot slot, Pre event) {
      if (user.getUseItem() == weapon && event.getNewDamage() > 0.0F && user.isDamageSourceBlocked(event.getSource())) {
         event.setNewDamage(event.getNewDamage() * 0.5F);
         this.damageOrConsumeItem(user, weapon, slot, 1);
      }
   }

   @Override
   public Optional<UseAnim> modifyUseAnimation(ItemStack stack, UseAnim original, Optional<UseAnim> current) {
      return current.isEmpty() ? Optional.of(UseAnim.BLOCK) : Optional.empty();
   }

   @Override
   public int modifyUseDuration(ItemStack stack, int original, int result) {
      return 72000;
   }

   @Override
   public int getPriority() {
      return -100;
   }

   @Override
   public int getColor(ItemStack stack) {
      return 9540507;
   }
}
