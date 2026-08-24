package net.cibernet.alchemancy.properties;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem;

public class DeadProperty extends Property {
   @Override
   public void applyAttributes(ItemAttributeModifierEvent event) {
      event.clearModifiers();
   }

   @Override
   public void onRightClickItemPost(RightClickItem event) {
      if (!event.isCanceled()) {
         event.setCancellationResult(InteractionResult.PASS);
         event.setCanceled(true);
      }
   }

   @Override
   public void onRightClickEntity(EntityInteractSpecific event) {
      if (!event.isCanceled()) {
         event.setCancellationResult(InteractionResult.PASS);
         event.setCanceled(true);
      }
   }

   @Override
   public int getPriority() {
      return -2147483648;
   }

   @Override
   public int getColor(ItemStack stack) {
      return 7629414;
   }
}
