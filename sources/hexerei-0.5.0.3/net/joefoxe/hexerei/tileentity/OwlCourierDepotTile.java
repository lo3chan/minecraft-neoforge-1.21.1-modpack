package net.joefoxe.hexerei.tileentity;

import javax.annotation.Nullable;
import net.joefoxe.hexerei.data.owl.ClientOwlCourierDepotData;
import net.joefoxe.hexerei.data.owl.OwlCourierDepotData;
import net.joefoxe.hexerei.data.owl.OwlCourierDepotSavedData;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.message.OpenOwlCourierDepotNameEditorPacket;
import net.joefoxe.hexerei.util.message.TESyncPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Clearable;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class OwlCourierDepotTile extends RandomizableContainerBlockEntity implements Clearable, MenuProvider {
   protected NonNullList<ItemStack> items = NonNullList.withSize(8, ItemStack.EMPTY);

   public OwlCourierDepotTile(BlockEntityType<?> tileEntityTypeIn, BlockPos blockPos, BlockState blockState) {
      super(tileEntityTypeIn, blockPos, blockState);
   }

   public CompoundTag getUpdateTag(Provider reg) {
      return this.save(new CompoundTag(), reg);
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this, (tag, registryAccess) -> this.getUpdateTag(registryAccess));
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

   public CompoundTag save(CompoundTag tag, Provider reg) {
      super.saveAdditional(tag, reg);
      return tag;
   }

   protected void saveAdditional(CompoundTag pTag, Provider reg) {
      super.saveAdditional(pTag, reg);
      ContainerHelper.saveAllItems(pTag, this.items, reg);
   }

   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      if (!this.tryLoadLootTable(tag)) {
         ContainerHelper.loadAllItems(tag, this.items, registries);
      }
   }

   public ItemInteractionResult interact(Player player, InteractionHand handIn) {
      GlobalPos globalPos = GlobalPos.of(this.getLevel().dimension(), this.getBlockPos());
      if (player instanceof ServerPlayer serverPlayer) {
         if (!OwlCourierDepotSavedData.get().getDepots().containsKey(globalPos)) {
            HexereiPacketHandler.sendToPlayerClient(new OpenOwlCourierDepotNameEditorPacket(this.getBlockPos()), serverPlayer);
            return ItemInteractionResult.SUCCESS;
         }

         OwlCourierDepotData depot = OwlCourierDepotSavedData.get().getDepots().get(globalPos);
         if (!((ItemStack)depot.items.get(0)).isEmpty()) {
            ItemStack stack = depot.takeFirstSlotAndSlide();
            if (player.getItemInHand(handIn).isEmpty()) {
               player.setItemInHand(handIn, stack);
            } else {
               player.getInventory().placeItemBackInInventory(stack);
            }

            OwlCourierDepotSavedData.get().syncInvToClient(globalPos);
            OwlCourierDepotSavedData.get().setDirty();
            return ItemInteractionResult.SUCCESS;
         }
      } else {
         if (!ClientOwlCourierDepotData.getDepots().containsKey(globalPos)) {
            return ItemInteractionResult.SUCCESS;
         }

         OwlCourierDepotData depot = ClientOwlCourierDepotData.getDepots().get(globalPos);
         if (!((ItemStack)depot.items.get(0)).isEmpty()) {
            return ItemInteractionResult.SUCCESS;
         }
      }

      return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
   }

   protected Component getDefaultName() {
      return null;
   }

   protected AbstractContainerMenu createMenu(int i, Inventory inventory) {
      return null;
   }

   protected NonNullList<ItemStack> getItems() {
      return this.items;
   }

   public ItemStack removeItem(int index, int count) {
      this.unpackLootTable(null);
      ItemStack itemstack = index >= 0 && index < this.items.size() && !((ItemStack)this.items.get(index)).isEmpty() && count > 0
         ? ((ItemStack)this.getItems().get(index)).split(count)
         : ItemStack.EMPTY;
      if (!itemstack.isEmpty()) {
         this.setChanged();
      }

      return itemstack;
   }

   public ItemStack removeItemNoUpdate(int index) {
      return ContainerHelper.takeItem(this.items, index);
   }

   public ItemStack getItem(int index) {
      this.unpackLootTable(null);
      return (ItemStack)this.items.get(index);
   }

   public void setItem(int index, ItemStack stack) {
      this.unpackLootTable(null);
      if (stack.getCount() > this.getMaxStackSize()) {
         stack.setCount(this.getMaxStackSize());
      }

      this.items.set(index, stack);
      this.setChanged();
   }

   protected void setItems(NonNullList<ItemStack> itemsIn) {
      this.items = itemsIn;
      this.setChanged();
   }

   public void clearContent() {
      super.clearContent();
      this.setChanged();
   }

   public OwlCourierDepotTile(BlockPos blockPos, BlockState blockState) {
      this((BlockEntityType<?>)ModTileEntities.OWL_COURIER_DEPOT_TILE.get(), blockPos, blockState);
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
