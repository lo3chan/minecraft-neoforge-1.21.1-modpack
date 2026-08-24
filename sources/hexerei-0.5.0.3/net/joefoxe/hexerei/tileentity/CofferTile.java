package net.joefoxe.hexerei.tileentity;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.config.HexConfig;
import net.joefoxe.hexerei.container.CofferContainer;
import net.joefoxe.hexerei.data.coffer.ClientCofferData;
import net.joefoxe.hexerei.data.coffer.CofferInventorySavedData;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.message.CofferSyncCrowButtonToServer;
import net.joefoxe.hexerei.util.message.CofferUpdateWhitelistToServer;
import net.joefoxe.hexerei.util.message.SyncCofferInventoryPacket;
import net.joefoxe.hexerei.util.message.TESyncPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

public class CofferTile extends RandomizableContainerBlockEntity implements WorldlyContainer, Clearable {
   public static final int DEFAULT_COLOR = 4337438;
   public static final int DEFAULT_COLOR_ENTANGLED = 856599;
   public int degreesOpened;
   public int buttonToggled = 0;
   public static final int lidOpenAmount = 112;
   public int degreesOpenedPrev = 0;
   public int dyeColor = 4337438;
   public CofferTile.WhitelistMode mode = CofferTile.WhitelistMode.WHITELIST_INV;
   public Component customName;
   public UUID cofferId;
   private CompoundTag deferredInventoryNbt = null;
   public NonNullList<ItemStack> whitelist = NonNullList.withSize(9, ItemStack.EMPTY);
   private static final int[] SLOTS = IntStream.range(0, 36).toArray();

   public CofferTile(BlockEntityType<?> tileEntityTypeIn, BlockPos blockPos, BlockState blockState) {
      super(tileEntityTypeIn, blockPos, blockState);
      if (blockState.is(ModBlocks.ENTANGLED_COFFER)) {
         this.dyeColor = 856599;
      }
   }

   public CofferTile(BlockPos blockPos, BlockState blockState) {
      this((BlockEntityType<?>)ModTileEntities.COFFER_TILE.get(), blockPos, blockState);
   }

   public BlockEntityType<?> getType() {
      return super.getType();
   }

   public void setDyeColor(int dyeColor) {
      this.dyeColor = dyeColor;
   }

   public int getDyeColor() {
      DyeColor dye = HexereiUtil.getDyeColorNamed(this.getDisplayName().getString());
      return dye != null ? HexereiUtil.getColorValue(dye) : this.dyeColor;
   }

   public void syncCofferInventory() {
      if (this.level instanceof ServerLevel serverLevel) {
         UUID cofferId = this.getOrCreateCofferId();
         NonNullList<ItemStack> inventory = CofferInventorySavedData.get().getInventory(cofferId);
         HexereiPacketHandler.sendToAllPlayers(new SyncCofferInventoryPacket(cofferId, inventory), serverLevel.getServer());
      }
   }

   public UUID getOrCreateCofferId() {
      if (this.cofferId == null) {
         this.cofferId = UUID.randomUUID();
         this.setChanged();
         if (this.deferredInventoryNbt != null) {
            NonNullList<ItemStack> inv = CofferInventorySavedData.get().getInventory(this.cofferId);
            ListTag tagList = this.deferredInventoryNbt.getList("Items", 10);

            for (int i = 0; i < tagList.size(); i++) {
               CompoundTag itemTags = tagList.getCompound(i);
               int slot = itemTags.getInt("Slot");
               if (slot >= 0 && slot < inv.size()) {
                  ItemStack.parse(Hexerei.DynamicRegistries.get(), itemTags).ifPresent(stack -> inv.set(slot, stack));
               }
            }

            this.deferredInventoryNbt = null;
            this.syncCofferInventory();
         }

         this.sync();
      }

      return this.cofferId;
   }

   protected NonNullList<ItemStack> getItems() {
      return this.level instanceof ServerLevel serverLevel
         ? CofferInventorySavedData.get().getInventory(this.getOrCreateCofferId())
         : ClientCofferData.getInventory(this.cofferId);
   }

   protected void setItems(NonNullList<ItemStack> items) {
      if (this.level instanceof ServerLevel serverLevel) {
         CofferInventorySavedData.get().getInventory(this.getOrCreateCofferId()).clear();
         CofferInventorySavedData.get().getInventory(this.getOrCreateCofferId()).addAll(items);
         CofferInventorySavedData.get().setLastModified(this.getOrCreateCofferId());
         this.setChanged();
         this.syncCofferInventory();
      }
   }

   public ItemStack removeItem(int slot, int count) {
      this.unpackLootTable(null);
      ItemStack itemstack = slot >= 0 && slot < this.getItems().size() && !this.getItem(slot).isEmpty() && count > 0
         ? ((ItemStack)this.getItems().get(slot)).split(count)
         : ItemStack.EMPTY;
      if (!itemstack.isEmpty()) {
         this.syncCofferInventory();
      }

      return itemstack;
   }

   public ItemStack removeItemNoUpdate(int slot) {
      this.unpackLootTable(null);
      if (slot >= 0 && slot < this.getItems().size()) {
         ItemStack temp = this.getItem(slot).copy();
         this.setItem(slot, ItemStack.EMPTY);
         if (!temp.isEmpty()) {
            this.syncCofferInventory();
         }

         return temp;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public ItemStack getItem(int slot) {
      this.unpackLootTable(null);
      return this.getItems().size() > slot ? (ItemStack)this.getItems().get(slot) : ItemStack.EMPTY;
   }

   public void setItem(int slot, ItemStack stack) {
      this.unpackLootTable(null);
      NonNullList<ItemStack> stacks = this.getItems();
      if (this.getItems().size() > slot) {
         this.getItems().set(slot, stack);
         if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
         }

         this.syncCofferInventory();
      } else {
         System.out.println("out of bounds");
      }
   }

   public void setChanged() {
      super.setChanged();
   }

   public void startOpen(Player p_18955_) {
      super.startOpen(p_18955_);
   }

   public void stopOpen(Player p_18954_) {
      super.stopOpen(p_18954_);
   }

   public boolean canPlaceItem(int p_18952_, ItemStack stack) {
      String id = HexereiUtil.getRegistryName(stack.getItem()).toString();
      return !((List)HexConfig.COFFER_BLACKLIST.get()).contains(id) && !stack.is(ModItems.COFFER) && !stack.is(ModItems.ENTANGLED_COFFER)
         ? super.canPlaceItem(p_18952_, stack)
         : false;
   }

   public int countItem(Item p_18948_) {
      return super.countItem(p_18948_);
   }

   protected Component getDefaultName() {
      return Component.translatable("container.hexerei.coffer");
   }

   protected AbstractContainerMenu createMenu(int id, Inventory player) {
      return new CofferContainer(id, this.level, this.worldPosition, player, player.player);
   }

   public void clearContent() {
      super.clearContent();
      CofferTile.CofferInvWrapper wrapper = new CofferTile.CofferInvWrapper(this.cofferId, this.level);

      for (int i = 0; i < wrapper.getSlots(); i++) {
         wrapper.setStackInSlot(i, ItemStack.EMPTY);
      }
   }

   public void saveAdditional(CompoundTag compound, Provider reg) {
      if (!this.trySaveLootTable(compound) && this.cofferId != null) {
         compound.putUUID("CofferId", this.cofferId);
      }

      if (this.cofferId == null && this.deferredInventoryNbt != null) {
         compound.put("inv", this.deferredInventoryNbt);
      }

      ListTag itemsTag = new ListTag();

      for (int slot = 0; slot < this.whitelist.size(); slot++) {
         ItemStack stack = (ItemStack)this.whitelist.get(slot);
         if (!stack.isEmpty()) {
            CompoundTag slotTag = new CompoundTag();
            slotTag.putInt("Slot", slot);
            Tag itemTag = stack.save(reg, slotTag);
            slotTag.put("Item", itemTag);
            itemsTag.add(slotTag);
         }
      }

      compound.put("WhitelistItems", itemsTag);
      compound.putInt("WhitelistMode", this.mode.ordinal());
      if (this.customName != null) {
         compound.putString("CustomName", Serializer.toJson(this.customName, reg));
      }

      compound.putInt("ButtonToggled", this.buttonToggled);
      compound.putInt("DyeColor", this.dyeColor);
   }

   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      if (!this.tryLoadLootTable(tag) && tag.hasUUID("CofferId")) {
         this.cofferId = tag.getUUID("CofferId");
      }

      this.whitelist.clear();
      if (tag.contains("WhitelistItems", 9)) {
         ListTag itemsTag = tag.getList("WhitelistItems", 10);

         for (int i = 0; i < itemsTag.size(); i++) {
            CompoundTag slotTag = itemsTag.getCompound(i);
            int slot = slotTag.getInt("Slot");
            if (slot >= 0 && slot < this.whitelist.size()) {
               this.whitelist.set(slot, ItemStack.parse(registries, slotTag.getCompound("Item")).orElse(ItemStack.EMPTY));
            }
         }
      }

      if (tag.contains("WhitelistMode")) {
         this.mode = CofferTile.WhitelistMode.byId(tag.getInt("WhitelistMode"));
      }

      if (tag.contains("inv")) {
         this.deferredInventoryNbt = tag.getCompound("inv");
      }

      if (tag.contains("CustomName", 8)) {
         this.customName = Serializer.fromJson(tag.getString("CustomName"), registries);
      }

      if (tag.contains("ButtonToggled")) {
         this.buttonToggled = tag.getInt("ButtonToggled");
      }

      if (tag.contains("DyeColor")) {
         this.dyeColor = tag.getInt("DyeColor");
         if (this.dyeColor == 0) {
            this.dyeColor = 4337438;
         }
      }
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this, (tag, registryAccess) -> this.getUpdateTag(registryAccess));
   }

   public CompoundTag getUpdateTag(Provider registries) {
      CompoundTag tag = super.getUpdateTag(registries);
      this.saveAdditional(tag, registries);
      return tag;
   }

   public void sync() {
      this.setChanged();
      if (this.level != null) {
         if (!this.level.isClientSide) {
            CompoundTag tag = new CompoundTag();
            this.saveAdditional(tag, this.level.registryAccess());
            HexereiPacketHandler.sendToNearbyClient(this.level, this.worldPosition, new TESyncPacket(this.worldPosition, tag));
         }

         if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.level.getBlockState(this.worldPosition), this.level.getBlockState(this.worldPosition), 2);
         }
      }
   }

   public ItemStack getItemStackInSlot(int slot) {
      return this.getItem(slot);
   }

   public boolean hasItem(Item item) {
      CofferTile.CofferInvWrapper wrapper = new CofferTile.CofferInvWrapper(this.cofferId, this.level);

      for (int i = 0; i < wrapper.getSlots(); i++) {
         if (wrapper.getStackInSlot(i).is(item)) {
            return true;
         }
      }

      return false;
   }

   public boolean hasWhitelistItem(ItemStack item) {
      for (ItemStack stack : this.whitelist) {
         if (ItemStack.isSameItemSameComponents(stack, item)) {
            return true;
         }
      }

      return false;
   }

   public boolean hasNonMaxStackItemStack(ItemStack item) {
      CofferTile.CofferInvWrapper wrapper = new CofferTile.CofferInvWrapper(this.cofferId, this.level);

      for (int i = 0; i < wrapper.getSlots(); i++) {
         if (wrapper.getStackInSlot(i) == item && wrapper.getStackInSlot(i).getCount() < wrapper.getStackInSlot(i).getMaxStackSize()) {
            return true;
         }
      }

      return false;
   }

   public boolean isEmpty() {
      CofferTile.CofferInvWrapper wrapper = new CofferTile.CofferInvWrapper(this.cofferId, this.level);

      for (int i = 0; i < wrapper.getSlots(); i++) {
         if (!wrapper.getStackInSlot(i).isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public boolean isWhitelistEmpty() {
      for (ItemStack stack : this.whitelist) {
         if (!stack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public static double getDistanceToEntity(Entity entity, BlockPos pos) {
      double deltaX = entity.getX() - pos.getX();
      double deltaY = entity.getY() - pos.getY();
      double deltaZ = entity.getZ() - pos.getZ();
      return Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
   }

   public Component getDisplayName() {
      return (Component)(this.customName != null ? this.customName : Component.literal(""));
   }

   public Component getCustomName() {
      return this.customName;
   }

   public boolean hasCustomName() {
      return this.customName != null;
   }

   public Component getName() {
      return this.customName;
   }

   public int getDegreesOpened() {
      return this.degreesOpened;
   }

   public void setDegreesOpened(int degrees) {
      this.degreesOpened = degrees;
   }

   public void setButtonToggled(int buttonToggled) {
      this.buttonToggled = buttonToggled;
      if (this.level.isClientSide) {
         HexereiPacketHandler.sendToServer(new CofferSyncCrowButtonToServer(this, buttonToggled));
      }
   }

   public void setWhitelistSlot(int slot, ItemStack stack) {
      if (this.level.isClientSide) {
         HexereiPacketHandler.sendToServer(new CofferUpdateWhitelistToServer(this, slot, stack));
      }
   }

   public int getButtonToggled() {
      return this.buttonToggled;
   }

   public void tick() {
      this.degreesOpenedPrev = this.degreesOpened;
      boolean flag = false;
      Player playerEntity = this.level.getNearestPlayer(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), 5.0, false);
      if (playerEntity != null && Math.floor(getDistanceToEntity(playerEntity, this.worldPosition)) < 4.0) {
         if (!this.level.isClientSide && this.getOrCreateCofferId() != null && this.getLootTable() != null) {
            this.unpackLootTable(playerEntity);
         }

         int distanceFromSide = 56 - Math.abs(56 - this.degreesOpened);
         flag = true;
         if (this.degreesOpened + Math.floor(distanceFromSide / 56.0 * 6.0) + 2.0 < 112.0) {
            this.degreesOpened = (int)(this.degreesOpened + (Math.floor(distanceFromSide / 56.0 * 6.0) + 2.0));
         } else {
            this.degreesOpened = 112;
         }
      }

      if (!flag) {
         int distanceFromSide = 56 - Math.abs(56 - this.degreesOpened);
         if (this.degreesOpened + Math.floor(distanceFromSide / 56.0 * 6.0) + 2.0 > 0.0) {
            this.degreesOpened = (int)(this.degreesOpened - (Math.floor(distanceFromSide / 56.0 * 6.0) + 2.0));
            if (this.degreesOpened < 0) {
               this.degreesOpened = 0;
            }
         } else {
            this.degreesOpened = 0;
         }
      }
   }

   public int getContainerSize() {
      return 36;
   }

   public void unpackLootTable(@Nullable Player player) {
      Level level = this.getLevel();
      boolean flag = false;
      if (this.getLootTable() != null && !level.isClientSide) {
         flag = true;
         super.unpackLootTable(player);
      } else {
         super.unpackLootTable(player);
      }

      if (flag && this.getLootTable() == null) {
         this.syncCofferInventory();
      }
   }

   public int getMaxStackSize() {
      return super.getMaxStackSize();
   }

   public int[] getSlotsForFace(Direction pSide) {
      return SLOTS;
   }

   public boolean canPlaceItemThroughFace(int pIndex, ItemStack pItemStack, @org.jetbrains.annotations.Nullable Direction pDirection) {
      return new CofferTile.CofferInvWrapper(this.cofferId, this.level).isItemValid(pIndex, pItemStack);
   }

   public boolean canTakeItemThroughFace(int pIndex, ItemStack pStack, Direction pDirection) {
      return true;
   }

   public static class CofferInvWrapper extends ItemStackHandler {
      private final UUID cofferId;
      private final boolean isClientSide;
      private final int size;
      private CofferTile coffer = null;

      public CofferInvWrapper(UUID cofferId, Level level) {
         this.cofferId = cofferId;
         this.isClientSide = level == null || level.isClientSide();
         this.size = 36;
      }

      public CofferInvWrapper(CofferTile coffer) {
         this.coffer = coffer;
         this.cofferId = coffer.cofferId;
         this.isClientSide = coffer.getLevel() == null || coffer.getLevel().isClientSide();
         this.size = 36;
      }

      public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
         this.validateSlotIndex(slot);
         if (this.isClientSide) {
            NonNullList<ItemStack> items = ClientCofferData.getInventory(this.cofferId);
            items.set(slot, stack);
            ClientCofferData.storeInventory(this.cofferId, items);
         } else {
            NonNullList<ItemStack> items = CofferInventorySavedData.get().getInventory(this.cofferId);
            items.set(slot, stack);
            CofferInventorySavedData.get().setLastModified(this.cofferId);
            CofferInventorySavedData.get().setDirty();
         }

         this.onContentsChanged(slot);
      }

      public int getSlots() {
         return this.size;
      }

      @Nonnull
      public ItemStack getStackInSlot(int slot) {
         this.validateSlotIndex(slot);
         if (this.isClientSide) {
            NonNullList<ItemStack> items = ClientCofferData.getInventory(this.cofferId);
            return slot < items.size() ? (ItemStack)items.get(slot) : ItemStack.EMPTY;
         } else {
            NonNullList<ItemStack> items = CofferInventorySavedData.get().getInventory(this.cofferId);
            return slot < items.size() ? (ItemStack)items.get(slot) : ItemStack.EMPTY;
         }
      }

      @Nonnull
      public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
         if (stack.isEmpty()) {
            return ItemStack.EMPTY;
         } else {
            this.validateSlotIndex(slot);
            NonNullList<ItemStack> items = this.getInventory();
            ItemStack existing = (ItemStack)items.get(slot);
            int limit = Math.min(this.getSlotLimit(slot), stack.getMaxStackSize());
            if (!existing.isEmpty()) {
               if (!ItemStack.isSameItemSameComponents(existing, stack)) {
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
                     items.set(slot, reachedLimit ? stack.copyWithCount(limit) : stack.copy());
                  } else {
                     existing.grow(reachedLimit ? limit : stack.getCount());
                  }

                  this.updateInventory(items);
               }

               return reachedLimit ? stack.copyWithCount(stack.getCount() - limit) : ItemStack.EMPTY;
            }
         }
      }

      @Nonnull
      public ItemStack extractItem(int slot, int amount, boolean simulate) {
         if (amount == 0) {
            return ItemStack.EMPTY;
         } else {
            this.validateSlotIndex(slot);
            NonNullList<ItemStack> items = this.getInventory();
            ItemStack existing = (ItemStack)items.get(slot);
            if (existing.isEmpty()) {
               return ItemStack.EMPTY;
            } else {
               int toExtract = Math.min(amount, existing.getMaxStackSize());
               if (existing.getCount() <= toExtract) {
                  if (!simulate) {
                     items.set(slot, ItemStack.EMPTY);
                     this.updateInventory(items);
                     return existing;
                  } else {
                     return existing.copy();
                  }
               } else {
                  if (!simulate) {
                     items.set(slot, existing.copyWithCount(existing.getCount() - toExtract));
                     this.updateInventory(items);
                  }

                  return existing.copyWithCount(toExtract);
               }
            }
         }
      }

      public int getSlotLimit(int slot) {
         return 64;
      }

      public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
         return !((List)HexConfig.COFFER_BLACKLIST.get()).contains(HexereiUtil.getRegistryName(stack.getItem()).toString())
            && !stack.is(ModItems.COFFER)
            && !stack.is(ModItems.ENTANGLED_COFFER);
      }

      private NonNullList<ItemStack> getInventory() {
         return this.isClientSide ? ClientCofferData.getInventory(this.cofferId) : CofferInventorySavedData.get().getInventory(this.cofferId);
      }

      private void updateInventory(NonNullList<ItemStack> items) {
         if (this.isClientSide) {
            ClientCofferData.storeInventory(this.cofferId, items);
         } else {
            CofferInventorySavedData.get().setDirty();
            if (this.coffer != null) {
               this.coffer.syncCofferInventory();
            }
         }
      }

      public void validateSlotIndex(int slot) {
         if (slot < 0 || slot >= this.size) {
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + this.size + ")");
         }
      }
   }

   public static enum WhitelistMode {
      WHITELIST_INV("whitelist_inv"),
      WHITELIST("whitelist_only"),
      BLACKLIST_INV("blacklist_inv");

      private final String name;

      private WhitelistMode(String name) {
         this.name = name;
      }

      public String getName() {
         return this.name;
      }

      public static CofferTile.WhitelistMode byId(int id) {
         CofferTile.WhitelistMode[] type = values();
         return type[id >= 0 && id < type.length ? id : 0];
      }
   }
}
