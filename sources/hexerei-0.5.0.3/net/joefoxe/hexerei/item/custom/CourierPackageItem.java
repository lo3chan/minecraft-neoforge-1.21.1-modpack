package net.joefoxe.hexerei.item.custom;

import java.util.List;
import javax.annotation.Nullable;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.container.PackageContainer;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

public class CourierPackageItem extends BlockItem {
   public CourierPackageItem(Block block, Properties properties) {
      super(block, properties);
   }

   public InteractionResult place(BlockPlaceContext context) {
      return super.place(context);
   }

   public InteractionResult useOn(UseOnContext pContext) {
      if (isSealed(pContext.getItemInHand())) {
         return super.useOn(pContext);
      } else {
         if (!pContext.isSecondaryUseActive() && pContext.getPlayer() != null && !pContext.getLevel().isClientSide) {
            this.openMenu(pContext.getPlayer(), pContext.getHand(), pContext.getItemInHand());
         }

         return pContext.isSecondaryUseActive() ? super.useOn(pContext) : InteractionResult.CONSUME;
      }
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player playerIn, InteractionHand handIn) {
      ItemStack itemstack = playerIn.getItemInHand(handIn);
      if (!level.isClientSide) {
         this.openMenu(playerIn, handIn, itemstack);
      }

      return itemstack.getCount() == 1
         ? (isSealed(itemstack) ? InteractionResultHolder.fail(itemstack) : InteractionResultHolder.consume(itemstack))
         : InteractionResultHolder.fail(itemstack);
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
      CourierPackageItem.PackageInvWrapper wrapper = new CourierPackageItem.PackageInvWrapper(stack);
      if (Screen.hasShiftDown()) {
         tooltipComponents.add(
            Component.translatable(
                  "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
               )
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
         if (wrapper.isEmpty()) {
            tooltipComponents.add(Component.translatable("tooltip.hexerei.courier_package_use").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.courier_package_menu").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.courier_package_send").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            tooltipComponents.add(
               Component.translatable("tooltip.hexerei.courier_package_must_be_sealed").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
         } else if (wrapper.getSealed()) {
            tooltipComponents.add(Component.translatable("tooltip.hexerei.courier_package_send").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.courier_package_open").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
         } else {
            tooltipComponents.add(
               Component.translatable("tooltip.hexerei.courier_package_must_be_sealed").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
         }
      } else {
         tooltipComponents.add(
            Component.translatable(
                  "[%s]", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184640)))}
               )
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
      }
   }

   private boolean openMenu(Player player, InteractionHand hand, ItemStack stack) {
      if (!player.isSteppingCarefully() && stack.getCount() == 1 && !isSealed(stack)) {
         MenuProvider containerProvider = this.createContainerProvider(stack, hand);
         int slotIndex = hand == InteractionHand.OFF_HAND ? -1 : player.getInventory().selected;
         player.openMenu(containerProvider, b -> b.writeByte(hand == InteractionHand.MAIN_HAND ? 0 : 1).writeByte(slotIndex));
         return true;
      } else {
         return false;
      }
   }

   public static boolean isSealed(ItemStack stack) {
      CustomData data = (CustomData)stack.get(DataComponents.BLOCK_ENTITY_DATA);
      return data != null && data.copyTag().contains("Sealed") && data.copyTag().getBoolean("Sealed");
   }

   private MenuProvider createContainerProvider(final ItemStack itemStack, final InteractionHand hand) {
      return new MenuProvider() {
         @Nullable
         public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
            return new PackageContainer(i, itemStack, playerInventory, hand, hand == InteractionHand.OFF_HAND ? -1 : playerEntity.getInventory().selected);
         }

         public Component getDisplayName() {
            return Component.translatable("screen.hexerei.package");
         }
      };
   }

   public static class PackageInvWrapper implements IItemHandlerModifiable {
      private final ItemStack stack;
      private CompoundTag cachedTag;
      private NonNullList<ItemStack> itemStacksCache;
      private boolean sealed;

      public PackageInvWrapper(ItemStack stack) {
         this.stack = stack;
         this.sealed = this.getSealed();
      }

      public int getSlots() {
         return 5;
      }

      public boolean isEmpty() {
         for (ItemStack stack1 : this.getItemList()) {
            if (!stack1.isEmpty()) {
               return false;
            }
         }

         return true;
      }

      @NotNull
      public ItemStack getStackInSlot(int slot) {
         return !this.validateSlotIndex(slot) ? ItemStack.EMPTY : (ItemStack)this.getItemList().get(slot);
      }

      @NotNull
      public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
         if (stack.isEmpty()) {
            return ItemStack.EMPTY;
         } else if (!this.isItemValid(slot, stack)) {
            return stack;
         } else {
            this.validateSlotIndex(slot);
            NonNullList<ItemStack> itemStacks = this.getItemList();
            ItemStack existing = (ItemStack)itemStacks.get(slot);
            int limit = Math.min(this.getSlotLimit(slot), stack.getMaxStackSize());
            if (!existing.isEmpty()) {
               if (!ItemStack.isSameItemSameComponents(stack, existing)) {
                  return stack;
               }

               limit -= existing.getCount();
            }

            if (limit <= 0) {
               return stack;
            } else {
               boolean reachedLimit = stack.getCount() > limit;
               if (!simulate) {
                  if (existing.isEmpty()) {
                     itemStacks.set(slot, reachedLimit ? stack.copyWithCount(limit) : stack);
                  } else {
                     existing.grow(reachedLimit ? limit : stack.getCount());
                  }

                  this.setItemList(itemStacks);
               }

               return reachedLimit ? stack.copyWithCount(stack.getCount() - limit) : ItemStack.EMPTY;
            }
         }
      }

      @NotNull
      public ItemStack extractItem(int slot, int amount, boolean simulate) {
         NonNullList<ItemStack> itemStacks = this.getItemList();
         if (amount == 0) {
            return ItemStack.EMPTY;
         } else {
            this.validateSlotIndex(slot);
            ItemStack existing = (ItemStack)itemStacks.get(slot);
            if (existing.isEmpty()) {
               return ItemStack.EMPTY;
            } else {
               int toExtract = Math.min(amount, existing.getMaxStackSize());
               if (existing.getCount() <= toExtract) {
                  if (!simulate) {
                     itemStacks.set(slot, ItemStack.EMPTY);
                     this.setItemList(itemStacks);
                     return existing;
                  } else {
                     return existing.copy();
                  }
               } else {
                  if (!simulate) {
                     itemStacks.set(slot, existing.copyWithCount(existing.getCount() - toExtract));
                     this.setItemList(itemStacks);
                  }

                  return existing.copyWithCount(toExtract);
               }
            }
         }
      }

      private boolean validateSlotIndex(int slot) {
         if (slot >= 0 && slot < this.getSlots()) {
            return true;
         } else {
            System.out.println("invalid slot - " + slot);
            return false;
         }
      }

      public int getSlotLimit(int slot) {
         return 64;
      }

      public boolean isItemValid(int slot, @NotNull ItemStack stack) {
         if (stack.getItem() instanceof CourierPackageItem) {
            CourierPackageItem.PackageInvWrapper wrapper = new CourierPackageItem.PackageInvWrapper(stack);
            if (!wrapper.isEmpty()) {
               return false;
            }
         }

         return stack.getItem().canFitInsideContainerItems();
      }

      public void setStackInSlot(int slot, @NotNull ItemStack stack) {
         this.validateSlotIndex(slot);
         if (!this.isItemValid(slot, stack)) {
            throw new RuntimeException("Invalid stack " + stack + " for slot " + slot + ")");
         } else {
            NonNullList<ItemStack> itemStacks = this.getItemList();
            itemStacks.set(slot, stack);
            this.setItemList(itemStacks);
         }
      }

      private NonNullList<ItemStack> getItemList() {
         CustomData data = (CustomData)this.stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY);
         CompoundTag rootTag = data.copyTag();
         if (this.cachedTag == null || !this.cachedTag.equals(rootTag)) {
            this.itemStacksCache = this.refreshItemList(rootTag);
         }

         return this.itemStacksCache;
      }

      private NonNullList<ItemStack> refreshItemList(CompoundTag rootTag) {
         NonNullList<ItemStack> itemStacks = NonNullList.withSize(this.getSlots(), ItemStack.EMPTY);
         if (rootTag != null && rootTag.contains("Items", 9)) {
            ContainerHelper.loadAllItems(rootTag, itemStacks, Hexerei.DynamicRegistries.get());
         }

         this.cachedTag = rootTag;
         return itemStacks;
      }

      private void setItemList(NonNullList<ItemStack> itemStacks) {
         boolean isEmpty = true;

         for (ItemStack itemStack : itemStacks) {
            if (!itemStack.isEmpty()) {
               isEmpty = false;
            }
         }

         CompoundTag existing = ((CustomData)this.stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY)).copyTag();
         CompoundTag rootTag = ContainerHelper.saveAllItems(existing, itemStacks, Hexerei.DynamicRegistries.get());
         if (!isEmpty) {
            BlockItem.setBlockEntityData(this.stack, (BlockEntityType)ModTileEntities.COURIER_PACKAGE_TILE.get(), rootTag);
            this.cachedTag = rootTag;
         } else {
            this.stack.remove(DataComponents.BLOCK_ENTITY_DATA);
            this.cachedTag = null;
         }
      }

      public void setSealed(int sealed) {
         this.sealed = sealed == 1;
         CustomData existing = (CustomData)this.stack.get(DataComponents.BLOCK_ENTITY_DATA);
         if (existing != null) {
            CompoundTag tag = existing.copyTag();
            if (this.isEmpty()) {
               this.sealed = false;
               tag.putBoolean("Sealed", false);
               return;
            }

            tag.putBoolean("Sealed", this.sealed);
            BlockItem.setBlockEntityData(this.stack, (BlockEntityType)ModTileEntities.COURIER_PACKAGE_TILE.get(), tag);
            this.cachedTag = tag;
         } else {
            this.sealed = false;
         }
      }

      public boolean getSealed() {
         CustomData existing = (CustomData)this.stack.get(DataComponents.BLOCK_ENTITY_DATA);
         if (existing == null) {
            this.sealed = false;
            return false;
         } else {
            return existing.contains("Sealed") && existing.copyTag().getBoolean("Sealed");
         }
      }
   }
}
