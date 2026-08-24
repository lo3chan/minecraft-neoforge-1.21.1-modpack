package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.block.custom.CenserBlock;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.gameplay.censer.CenserEffectHandler;
import net.astralya.hexalia.gameplay.censer.HerbCombination;
import net.astralya.hexalia.util.ItemInteractionHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CenserBlockEntity extends BlockEntity implements Container, Clearable, ItemInteractionHelper.ItemStorage {
   private static final int SIZE = 2;
   private static final int SLOT_0 = 0;
   private static final int SLOT_1 = 1;
   private static final String TAG_BURN_TIME = "BurnTime";
   private static final String TAG_ACTIVE_COMBINATION = "ActiveCombination";
   private static final String TAG_FIRST = "First";
   private static final String TAG_SECOND = "Second";
   private final NonNullList<ItemStack> inventory = NonNullList.withSize(2, ItemStack.EMPTY);
   @Nullable
   private HerbCombination activeCombination;
   private int burnTime;

   public CenserBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)ModBlockEntityTypes.CENSER.get(), pos, state);
   }

   public void tick(Level level, BlockPos pos, BlockState state) {
      if ((Boolean)state.getValue(CenserBlock.LIT)) {
         if (this.activeCombination != null && this.burnTime > 0 && this.burnTime % 40 == 0) {
            CenserEffectHandler.applyEffect(level, pos, this.activeCombination);
         }

         if (this.burnTime > 0) {
            this.burnTime--;
         }

         if (this.burnTime <= 0) {
            this.extinguish(level, pos, state);
         } else {
            this.setChanged();
         }
      }
   }

   public ItemStack getItem(int slot) {
      return slot >= 0 && slot < 2 ? (ItemStack)this.inventory.get(slot) : ItemStack.EMPTY;
   }

   public int getContainerSize() {
      return 2;
   }

   public boolean isEmpty() {
      return ((ItemStack)this.inventory.get(0)).isEmpty() && ((ItemStack)this.inventory.get(1)).isEmpty();
   }

   public ItemStack removeItem(int slot, int amount) {
      if (slot >= 0 && slot < 2 && amount > 0 && this.canExtractItem()) {
         ItemStack removed = ContainerHelper.removeItem(this.inventory, slot, amount);
         if (!removed.isEmpty()) {
            this.inventoryChanged();
         }

         return removed;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public ItemStack removeItemNoUpdate(int slot) {
      return slot >= 0 && slot < 2 ? ContainerHelper.takeItem(this.inventory, slot) : ItemStack.EMPTY;
   }

   public void setItem(int slot, ItemStack stack) {
      if (slot >= 0 && slot < 2) {
         this.inventory.set(slot, stack.copyWithCount(Math.min(stack.getCount(), this.getMaxStackSize())));
         this.inventoryChanged();
      }
   }

   public int getMaxStackSize() {
      return 1;
   }

   public boolean canPlaceItem(int slot, ItemStack stack) {
      return slot >= 0
         && slot < 2
         && !(Boolean)this.getBlockState().getValue(CenserBlock.LIT)
         && !stack.isEmpty()
         && ((ItemStack)this.inventory.get(slot)).isEmpty();
   }

   public boolean canTakeItem(Container target, int slot, ItemStack stack) {
      return slot >= 0 && slot < 2 && !(Boolean)this.getBlockState().getValue(CenserBlock.LIT);
   }

   public boolean stillValid(Player player) {
      return Container.stillValidBlockEntity(this, player);
   }

   public void clearContent() {
      this.inventory.clear();
      this.inventoryChanged();
   }

   public void setBurnTime(int burnTime) {
      this.burnTime = burnTime;
      this.inventoryChanged();
   }

   public int getBurnTime() {
      return this.burnTime;
   }

   public void setActiveCombination(@Nullable HerbCombination activeCombination) {
      this.activeCombination = activeCombination;
      this.inventoryChanged();
   }

   @Nullable
   public HerbCombination getActiveCombination() {
      return this.activeCombination;
   }

   public void clearItems() {
      this.inventory.set(0, ItemStack.EMPTY);
      this.inventory.set(1, ItemStack.EMPTY);
      this.inventoryChanged();
   }

   public SimpleContainer getDropsContainer() {
      SimpleContainer container = new SimpleContainer(2);

      for (int index = 0; index < 2; index++) {
         container.setItem(index, (ItemStack)this.inventory.get(index));
      }

      return container;
   }

   public void dropContents(Level level) {
      if (level != null && !level.isClientSide()) {
         Containers.dropContents(level, this.worldPosition, this.getDropsContainer());
         this.clearItems();
      }
   }

   @Override
   public boolean canInsertItem(ItemStack stack) {
      return !(Boolean)this.getBlockState().getValue(CenserBlock.LIT) && !stack.isEmpty() && this.firstEmptySlot() != -1;
   }

   @Override
   public boolean addItem(ItemStack stack) {
      if (!this.canInsertItem(stack)) {
         return false;
      } else {
         this.inventory.set(this.firstEmptySlot(), stack.split(1));
         this.inventoryChanged();
         return true;
      }
   }

   @Override
   public boolean canExtractItem() {
      return !(Boolean)this.getBlockState().getValue(CenserBlock.LIT)
         && (!((ItemStack)this.inventory.get(0)).isEmpty() || !((ItemStack)this.inventory.get(1)).isEmpty());
   }

   @Override
   public ItemStack removeItem() {
      if (!this.canExtractItem()) {
         return ItemStack.EMPTY;
      } else {
         for (int slot = 1; slot >= 0; slot--) {
            ItemStack stack = (ItemStack)this.inventory.get(slot);
            if (!stack.isEmpty()) {
               this.inventory.set(slot, ItemStack.EMPTY);
               this.inventoryChanged();
               return stack;
            }
         }

         return ItemStack.EMPTY;
      }
   }

   public boolean hasTwoHerbs() {
      return !((ItemStack)this.inventory.get(0)).isEmpty() && !((ItemStack)this.inventory.get(1)).isEmpty();
   }

   public HerbCombination getStoredCombination() {
      return new HerbCombination(((ItemStack)this.inventory.get(0)).getItem(), ((ItemStack)this.inventory.get(1)).getItem());
   }

   private void extinguish(Level level, BlockPos pos, BlockState state) {
      this.activeCombination = null;
      this.burnTime = 0;
      if ((Boolean)state.getValue(CenserBlock.LIT)) {
         level.setBlockAndUpdate(pos, (BlockState)state.setValue(CenserBlock.LIT, false));
      }

      this.inventoryChanged();
   }

   private int firstEmptySlot() {
      for (int index = 0; index < 2; index++) {
         if (((ItemStack)this.inventory.get(index)).isEmpty()) {
            return index;
         }
      }

      return -1;
   }

   private void inventoryChanged() {
      this.setChanged();
      if (this.level != null && !this.level.isClientSide()) {
         this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
      }
   }

   protected void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      ContainerHelper.saveAllItems(tag, this.inventory, registries);
      tag.putInt("BurnTime", this.burnTime);
      if (this.activeCombination != null) {
         CompoundTag combo = new CompoundTag();
         combo.putString("First", BuiltInRegistries.ITEM.getKey(this.activeCombination.first()).toString());
         combo.putString("Second", BuiltInRegistries.ITEM.getKey(this.activeCombination.second()).toString());
         tag.put("ActiveCombination", combo);
      }
   }

   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);

      for (int index = 0; index < 2; index++) {
         this.inventory.set(index, ItemStack.EMPTY);
      }

      ContainerHelper.loadAllItems(tag, this.inventory, registries);
      this.burnTime = tag.getInt("BurnTime");
      this.activeCombination = loadCombination(tag);
   }

   @Nullable
   private static HerbCombination loadCombination(CompoundTag tag) {
      if (!tag.contains("ActiveCombination")) {
         return null;
      } else {
         CompoundTag combo = tag.getCompound("ActiveCombination");
         Item first = itemFromString(combo.getString("First"));
         Item second = itemFromString(combo.getString("Second"));
         return first != null && second != null ? new HerbCombination(first, second) : null;
      }
   }

   @Nullable
   private static Item itemFromString(String id) {
      ResourceLocation key = ResourceLocation.tryParse(id);
      return key != null && BuiltInRegistries.ITEM.containsKey(key) ? (Item)BuiltInRegistries.ITEM.get(key) : null;
   }

   public CompoundTag getUpdateTag(Provider registries) {
      return this.saveWithoutMetadata(registries);
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }
}
