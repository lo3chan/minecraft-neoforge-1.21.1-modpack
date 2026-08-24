package net.joefoxe.hexerei.tileentity;

import java.util.List;
import javax.annotation.Nullable;
import net.joefoxe.hexerei.block.custom.CourierPackage;
import net.joefoxe.hexerei.config.HexConfig;
import net.joefoxe.hexerei.container.CofferContainer;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.message.TESyncPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CourierPackageTile extends RandomizableContainerBlockEntity implements Container, Clearable {
   private NonNullList<ItemStack> itemStacks = NonNullList.withSize(5, ItemStack.EMPTY);
   private boolean sealed = false;

   public CourierPackageTile(BlockEntityType<?> tileEntityTypeIn, BlockPos blockPos, BlockState blockState) {
      super(tileEntityTypeIn, blockPos, blockState);
   }

   public CourierPackageTile(BlockPos blockPos, BlockState blockState) {
      this((BlockEntityType<?>)ModTileEntities.COURIER_PACKAGE_TILE.get(), blockPos, blockState);
   }

   public boolean interact(Player pPlayer) {
      this.unpackLootTable(pPlayer);
      if (!this.isEmpty() && this.hasLevel()) {
         if (this.level instanceof ServerLevel serverLevel) {
            for (ItemStack stack : this.itemStacks) {
               ItemEntity ie = new ItemEntity(
                  this.level,
                  this.getBlockPos().getX() + 0.5F,
                  this.getBlockPos().getY() + 0.5F,
                  this.getBlockPos().getZ() + 0.5F,
                  stack,
                  serverLevel.random.nextDouble() * 0.2 - 0.1,
                  0.3,
                  serverLevel.random.nextDouble() * 0.2 - 0.1
               );
               ie.setDefaultPickUpDelay();
               this.level.addFreshEntity(ie);
            }

            this.clearContent();
            this.setChanged();
            serverLevel.playSound(null, this.getBlockPos(), SoundEvents.BUNDLE_DROP_CONTENTS, SoundSource.BLOCKS, 1.0F, 1.0F);
            serverLevel.playSound(null, this.getBlockPos(), SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 1.0F, 0.2F);
            serverLevel.sendParticles(
               ParticleTypes.CLOUD,
               this.getBlockPos().getX() + 0.5,
               this.getBlockPos().getY() + 0.5,
               this.getBlockPos().getZ() + 0.5,
               10,
               0.25,
               0.25,
               0.25,
               0.02
            );
            serverLevel.sendParticles(
               new BlockParticleOption(ParticleTypes.BLOCK, this.getBlockState()),
               this.getBlockPos().getX() + 0.5,
               this.getBlockPos().getY() + 0.5,
               this.getBlockPos().getZ() + 0.5,
               25,
               0.25,
               0.25,
               0.25,
               0.02
            );
            this.level.setBlockAndUpdate(this.getBlockPos(), (BlockState)this.getBlockState().setValue(CourierPackage.STATE, CourierPackage.State.OPENED));
         }

         return true;
      } else {
         return false;
      }
   }

   public BlockEntityType<?> getType() {
      return super.getType();
   }

   public void setItem(int pIndex, ItemStack pStack) {
      super.setItem(pIndex, pStack);
   }

   protected NonNullList<ItemStack> getItems() {
      return this.itemStacks;
   }

   protected void setItems(NonNullList<ItemStack> itemsIn) {
      this.itemStacks = itemsIn;
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
      this.itemStacks = NonNullList.withSize(this.itemStacks.size(), ItemStack.EMPTY);
   }

   public CompoundTag save(CompoundTag pTag, Provider registries) {
      this.saveAdditional(pTag, registries);
      return pTag;
   }

   public CompoundTag saveData(CompoundTag pTag, Provider registries) {
      return !this.isEmpty() ? this.save(pTag, registries) : pTag;
   }

   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.loadFromTag(tag, registries);
   }

   protected void saveAdditional(CompoundTag pTag, Provider registries) {
      super.saveAdditional(pTag, registries);
      if (!this.trySaveLootTable(pTag)) {
         ContainerHelper.saveAllItems(pTag, this.itemStacks, false, registries);
      }

      pTag.putBoolean("Sealed", this.sealed);
   }

   public void loadFromTag(CompoundTag pTag, Provider registries) {
      this.itemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      if (!this.tryLoadLootTable(pTag) && pTag.contains("Items", 9)) {
         ContainerHelper.loadAllItems(pTag, this.itemStacks, registries);
      }

      if (pTag.contains("Sealed")) {
         this.sealed = pTag.getBoolean("Sealed");
      }
   }

   public CompoundTag getUpdateTag(Provider registries) {
      return this.save(new CompoundTag(), registries);
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this, (tag, registryAccess) -> this.getUpdateTag(registryAccess));
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

   public boolean isEmpty() {
      if (this.itemStacks != null) {
         for (int i = 0; i < this.getItems().size(); i++) {
            if (!((ItemStack)this.getItems().get(i)).isEmpty()) {
               return false;
            }
         }

         return true;
      } else {
         return true;
      }
   }

   public static double getDistanceToEntity(Entity entity, BlockPos pos) {
      double deltaX = entity.getX() - pos.getX();
      double deltaY = entity.getY() - pos.getY();
      double deltaZ = entity.getZ() - pos.getZ();
      return Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
   }

   public void tick() {
   }

   public int getContainerSize() {
      return this.itemStacks.size();
   }

   public int getMaxStackSize() {
      return super.getMaxStackSize();
   }
}
