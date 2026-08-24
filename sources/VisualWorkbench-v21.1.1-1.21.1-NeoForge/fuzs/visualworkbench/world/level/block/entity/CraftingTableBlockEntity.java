package fuzs.visualworkbench.world.level.block.entity;

import fuzs.puzzleslib.api.block.v1.entity.TickingBlockEntity;
import fuzs.puzzleslib.api.container.v1.ContainerMenuHelper;
import fuzs.puzzleslib.api.container.v1.ContainerSerializationHelper;
import fuzs.visualworkbench.VisualWorkbench;
import fuzs.visualworkbench.init.ModRegistry;
import fuzs.visualworkbench.world.inventory.VisualCraftingMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CraftingTableBlockEntity extends RandomizableContainerBlockEntity implements TickingBlockEntity, WorkbenchVisualsProvider {
   public static final MutableComponent COMPONENT_CRAFTING = Component.translatable("container.crafting");
   public static final String TAG_RESULT = VisualWorkbench.id("result").toString();
   private final CraftingTableAnimationController animationController;
   private final NonNullList<ItemStack> items = NonNullList.withSize(9, ItemStack.EMPTY);
   private final NonNullList<ItemStack> resultItems = NonNullList.withSize(1, ItemStack.EMPTY);

   public CraftingTableBlockEntity(BlockPos pos, BlockState blockState) {
      super((BlockEntityType)ModRegistry.CRAFTING_TABLE_BLOCK_ENTITY_TYPE.value(), pos, blockState);
      this.animationController = new CraftingTableAnimationController(pos);
   }

   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.items.clear();
      this.resultItems.clear();
      if (!this.tryLoadLootTable(tag)) {
         ContainerHelper.loadAllItems(tag, this.items, registries);
         ContainerSerializationHelper.loadAllItems(TAG_RESULT, tag, this.resultItems, registries);
      }
   }

   protected void saveAdditional(CompoundTag compoundTag, Provider registries) {
      super.saveAdditional(compoundTag, registries);
      if (!this.trySaveLootTable(compoundTag)) {
         ContainerHelper.saveAllItems(compoundTag, this.items, registries);
         ContainerSerializationHelper.saveAllItems(TAG_RESULT, compoundTag, this.resultItems, registries);
      }
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag(Provider registries) {
      return this.saveWithoutMetadata(registries);
   }

   public boolean canPlaceItem(int slot, ItemStack stack) {
      ItemStack itemStackInSlot = (ItemStack)this.items.get(slot);
      return itemStackInSlot.isEmpty()
         ? !this.smallerStackExist(stack.getMaxStackSize(), stack, -1)
         : !this.smallerStackExist(itemStackInSlot.getCount(), itemStackInSlot, slot);
   }

   private boolean smallerStackExist(int currentSize, ItemStack itemStackInSlot, int slot) {
      for (int i = slot + 1; i < this.getContainerSize(); i++) {
         ItemStack itemStack = this.getItem(i);
         if (!itemStack.isEmpty() && itemStack.getCount() < currentSize && ItemStack.isSameItemSameComponents(itemStack, itemStackInSlot)) {
            return true;
         }
      }

      return false;
   }

   public boolean canTakeItem(Container target, int slot, ItemStack stack) {
      return false;
   }

   public void setChanged() {
      super.setChanged();
      if (this.level != null) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
      }
   }

   public NonNullList<ItemStack> getItems() {
      return this.items;
   }

   protected void setItems(NonNullList<ItemStack> items) {
      ContainerMenuHelper.copyItemsIntoContainer(items, this);
   }

   protected Component getDefaultName() {
      return COMPONENT_CRAFTING;
   }

   protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
      return new VisualCraftingMenu(containerId, inventory, this, ContainerLevelAccess.create(this.getLevel(), this.getBlockPos()));
   }

   public int getContainerSize() {
      return this.items.size();
   }

   public boolean stillValid(Player player) {
      return Container.stillValidBlockEntity(this, player);
   }

   public void clientTick() {
      this.animationController.tick(this.getLevel());
   }

   public NonNullList<ItemStack> getResultItems() {
      return this.resultItems;
   }

   @Override
   public ItemStack getCraftingResult() {
      return (ItemStack)this.resultItems.get(0);
   }

   @Override
   public CraftingTableAnimationController getAnimationController() {
      return this.animationController;
   }
}
