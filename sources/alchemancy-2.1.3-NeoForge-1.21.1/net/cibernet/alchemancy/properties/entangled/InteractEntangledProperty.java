package net.cibernet.alchemancy.properties.entangled;

import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem;

public class InteractEntangledProperty extends AbstractEntangledProperty {
   @Override
   public void onRightClickItem(RightClickItem event) {
      if (!event.isCanceled() && !this.getData(event.getItemStack()).equals(this.getDefaultData())) {
         EquipmentSlot slot = event.getHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
         if (event.getItemStack() == event.getEntity().getItemBySlot(slot)) {
            event.getEntity().setItemSlot(slot, this.shift(event.getItemStack(), event.getEntity()));
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
         }
      }
   }

   @Override
   public void onStackedOverMe(
      ItemStack carriedItem, ItemStack stack, Player player, ClickAction clickAction, SlotAccess carriedSlot, Slot stackedOnSlot, AtomicBoolean isCancelled
   ) {
      if (!isCancelled.get() && clickAction == ClickAction.SECONDARY && carriedItem.isEmpty() && stack == stackedOnSlot.getItem()) {
         stackedOnSlot.set(this.shift(stack, player));
         isCancelled.set(true);
      } else {
         super.onStackedOverMe(carriedItem, stack, player, clickAction, carriedSlot, stackedOnSlot, isCancelled);
      }
   }
}
