package net.cibernet.alchemancy.properties;

import java.util.concurrent.atomic.AtomicBoolean;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem;

public class DisguisedProperty extends Property implements IDataHolder<ItemStack> {
   @Override
   public void onRightClickItem(RightClickItem event) {
      ItemStack stack = event.getItemStack();
      if (event.getHand() == InteractionHand.MAIN_HAND) {
         ItemStack disguise = event.getEntity().getOffhandItem();
         if (this.getData(stack).isEmpty() && !disguise.isEmpty()) {
            this.setData(stack, disguise);
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
         }
      }
   }

   @Override
   public void onStackedOverMe(
      ItemStack disguise, ItemStack stack, Player player, ClickAction clickAction, SlotAccess carriedSlot, Slot stackedOnSlot, AtomicBoolean isCancelled
   ) {
      if (clickAction == ClickAction.SECONDARY && this.getData(stack).isEmpty() && !disguise.isEmpty()) {
         this.setData(stack, disguise);
         isCancelled.set(true);
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return 9587497;
   }

   public ItemStack readData(CompoundTag tag) {
      return tag.isEmpty() ? this.getDefaultData() : ItemStack.parse(CommonUtils.registryAccessStatic(), tag.getCompound("item")).orElse(this.getDefaultData());
   }

   public CompoundTag writeData(final ItemStack data) {
      return new CompoundTag() {
         {
            if (!data.isEmpty()) {
               this.put("item", data.save(CommonUtils.registryAccessStatic()));
            }
         }
      };
   }

   public ItemStack getDefaultData() {
      return ItemStack.EMPTY;
   }

   @Override
   public Component getDisplayText(ItemStack stack) {
      Component name = super.getDisplayText(stack);
      ItemStack disguise = this.getData(stack);
      return (Component)(!disguise.isEmpty()
         ? Component.translatable("property.detail", new Object[]{name, disguise.getHoverName()}).withColor(this.getColor(stack))
         : name);
   }
}
