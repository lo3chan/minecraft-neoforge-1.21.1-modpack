package net.cibernet.alchemancy.properties;

import java.util.concurrent.atomic.AtomicBoolean;
import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancySoundEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import org.jetbrains.annotations.Nullable;

public class ToggleableProperty extends Property implements IDataHolder<Boolean> {
   @Override
   public void onStackedOverMe(
      ItemStack carriedItem,
      ItemStack stackedOnItem,
      Player player,
      ClickAction clickAction,
      SlotAccess carriedSlot,
      Slot stackedOnSlot,
      AtomicBoolean isCancelled
   ) {
      if (carriedItem.isEmpty() && clickAction == ClickAction.SECONDARY) {
         this.toggle(stackedOnItem, player);
         isCancelled.set(true);
      }
   }

   @Nullable
   @Override
   public ItemInteractionResult onRootedRightClick(RootedItemBlockEntity root, Player user, InteractionHand hand, BlockHitResult hitResult) {
      this.toggle(root.getItem(), user);
      return ItemInteractionResult.SUCCESS;
   }

   @Override
   public void onRightClickItem(RightClickItem event) {
      if (event.getEntity().isShiftKeyDown()) {
         this.toggle(event.getItemStack(), event.getEntity());
         event.setCancellationResult(InteractionResult.SUCCESS);
         event.setCanceled(true);
      }
   }

   @Override
   public void onRightClickItemPost(RightClickItem event) {
      if (!event.isCanceled() && !event.getEntity().isShiftKeyDown()) {
         this.toggle(event.getItemStack(), event.getEntity());
      }
   }

   public void toggle(ItemStack stack, @org.checkerframework.checker.nullness.qual.Nullable Entity entity) {
      boolean active = this.getData(stack);
      this.setData(stack, !active);
      if (entity != null) {
         entity.playSound((SoundEvent)AlchemancySoundEvents.TOGGLEABLE.value(), 0.3F, active ? 0.5F : 0.6F);
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return this.getData(stack) ? 15007744 : 4261120;
   }

   public Boolean readData(CompoundTag tag) {
      return tag.getBoolean("active");
   }

   public CompoundTag writeData(final Boolean data) {
      return new CompoundTag() {
         {
            this.putBoolean("active", data);
         }
      };
   }

   public Boolean getDefaultData() {
      return true;
   }
}
