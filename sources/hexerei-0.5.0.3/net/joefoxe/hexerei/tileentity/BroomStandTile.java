package net.joefoxe.hexerei.tileentity;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.joefoxe.hexerei.item.custom.BroomItem;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.message.TESyncPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemStackHandler;

public class BroomStandTile extends RandomizableContainerBlockEntity implements Clearable, MenuProvider {
   public final ItemStackHandler itemHandler = this.createHandler();

   public BroomStandTile(BlockEntityType<?> tileEntityTypeIn, BlockPos blockPos, BlockState blockState) {
      super(tileEntityTypeIn, blockPos, blockState);
   }

   private ItemStackHandler createHandler() {
      return new ItemStackHandler(1) {
         protected void onContentsChanged(int slot) {
            BroomStandTile.this.setChanged();
         }

         public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return stack.getItem() instanceof BroomItem;
         }

         public int getSlotLimit(int slot) {
            return 1;
         }

         @Nonnull
         public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return !this.isItemValid(slot, stack) ? stack : super.insertItem(slot, stack, simulate);
         }
      };
   }

   public CompoundTag getUpdateTag(Provider registries) {
      CompoundTag tag = new CompoundTag();
      this.saveAdditional(tag, registries);
      return tag;
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this, (tag, registryAccess) -> this.getUpdateTag(registryAccess));
   }

   public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, Provider lookupProvider) {
      super.onDataPacket(net, pkt, lookupProvider);
   }

   public void setChanged() {
      super.setChanged();
      this.sync();
   }

   public void sync() {
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

   protected void saveAdditional(CompoundTag tag, Provider registries) {
      tag.put("inv", this.itemHandler.serializeNBT(registries));
      super.saveAdditional(tag, registries);
   }

   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.itemHandler.deserializeNBT(registries, tag.getCompound("inv"));
   }

   public int interact(Player player, InteractionHand handIn, boolean withItem) {
      ItemStack stack = this.itemHandler.getStackInSlot(0);
      if (stack.isEmpty()) {
         if (withItem) {
            Random rand = new Random();
            if (stack.isEmpty() && this.itemHandler.isItemValid(0, player.getItemInHand(handIn))) {
               this.itemHandler.setStackInSlot(0, player.getItemInHand(handIn));
               this.level.playSound(null, this.worldPosition, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, rand.nextFloat() * 0.4F + 1.0F);
               player.setItemInHand(handIn, ItemStack.EMPTY);
               this.setChanged();
               return 1;
            }
         }

         return 0;
      } else {
         if (player.getMainHandItem().isEmpty()) {
            player.setItemInHand(InteractionHand.MAIN_HAND, stack.copy());
         } else {
            player.getInventory().placeItemBackInInventory(stack.copy());
         }

         this.level
            .playSound(null, this.worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.4F + 1.0F);
         this.itemHandler.setStackInSlot(0, ItemStack.EMPTY);
         this.setChanged();
         return 1;
      }
   }

   public void requestModelDataUpdate() {
      super.requestModelDataUpdate();
   }

   public void onLoad() {
      super.onLoad();
   }

   protected Component getDefaultName() {
      return null;
   }

   protected AbstractContainerMenu createMenu(int i, Inventory inventory) {
      return null;
   }

   protected NonNullList<ItemStack> getItems() {
      NonNullList<ItemStack> items = NonNullList.withSize(36, ItemStack.EMPTY);

      for (int i = 0; i < this.itemHandler.getSlots(); i++) {
         items.set(i, this.itemHandler.getStackInSlot(i));
      }

      return items;
   }

   public ItemStack removeItem(int p_59613_, int p_59614_) {
      this.unpackLootTable(null);
      ItemStack itemstack = p_59613_ >= 0 && p_59613_ < this.itemHandler.getSlots() && !this.itemHandler.getStackInSlot(p_59613_).isEmpty() && p_59614_ > 0
         ? ((ItemStack)this.getItems().get(p_59613_)).split(p_59614_)
         : ItemStack.EMPTY;
      if (!itemstack.isEmpty()) {
         this.setChanged();
      }

      return itemstack;
   }

   public ItemStack removeItemNoUpdate(int p_59630_) {
      this.unpackLootTable(null);
      if (p_59630_ >= 0 && p_59630_ < this.itemHandler.getSlots()) {
         this.itemHandler.setStackInSlot(p_59630_, ItemStack.EMPTY);
         return this.itemHandler.getStackInSlot(p_59630_);
      } else {
         return ItemStack.EMPTY;
      }
   }

   public ItemStack getItem(int p_59611_) {
      this.unpackLootTable(null);
      return this.itemHandler.getStackInSlot(p_59611_);
   }

   public void setItem(int p_59616_, ItemStack p_59617_) {
      this.unpackLootTable(null);
      this.itemHandler.setStackInSlot(p_59616_, p_59617_);
      if (p_59617_.getCount() > this.getMaxStackSize()) {
         p_59617_.setCount(this.getMaxStackSize());
      }

      this.setChanged();
   }

   protected void setItems(NonNullList<ItemStack> itemsIn) {
      for (int i = 0; i < Math.min(itemsIn.size(), this.itemHandler.getSlots()); i++) {
         this.itemHandler.setStackInSlot(i, (ItemStack)itemsIn.get(i));
      }
   }

   public void clearContent() {
      super.clearContent();

      for (int i = 0; i < this.itemHandler.getSlots(); i++) {
         this.itemHandler.setStackInSlot(i, ItemStack.EMPTY);
      }
   }

   public BroomStandTile(BlockPos blockPos, BlockState blockState) {
      this((BlockEntityType<?>)ModTileEntities.BROOM_STAND_TILE.get(), blockPos, blockState);
   }

   public float getAngle(Vec3 pos) {
      float angle = (float)Math.toDegrees(Math.atan2(pos.z() - this.worldPosition.getZ() - 0.5, pos.x() - this.worldPosition.getX() - 0.5));
      if (angle < 0.0F) {
         angle += 360.0F;
      }

      return angle;
   }

   public void tick() {
   }

   public int getContainerSize() {
      return 1;
   }
}
