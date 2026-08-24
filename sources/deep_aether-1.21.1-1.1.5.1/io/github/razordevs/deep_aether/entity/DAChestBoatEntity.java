package io.github.razordevs.deep_aether.entity;

import io.github.razordevs.deep_aether.init.DAEntities;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootTable;

public class DAChestBoatEntity extends DABoatEntity implements HasCustomInventoryScreen, ContainerEntity {
   private static final int CONTAINER_SIZE = 27;
   private NonNullList<ItemStack> itemStacks = NonNullList.withSize(27, ItemStack.EMPTY);
   @Nullable
   private ResourceKey<LootTable> lootTable;
   private long lootTableSeed;

   public DAChestBoatEntity(EntityType<? extends Boat> entityType, Level level) {
      super(entityType, level);
   }

   public DAChestBoatEntity(Level level, double x, double y, double z) {
      this((EntityType<? extends Boat>)DAEntities.CHEST_BOAT.get(), level);
      this.setPos(x, y, z);
      this.xo = x;
      this.yo = y;
      this.zo = z;
   }

   protected float getSinglePassengerXOffset() {
      return 0.15F;
   }

   protected int getMaxPassengers() {
      return 1;
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      this.addChestVehicleSaveData(pCompound, this.registryAccess());
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.readChestVehicleSaveData(compound, this.registryAccess());
   }

   public void destroy(DamageSource source) {
      super.destroy(source);
      this.chestVehicleDestroyed(source, this.level(), this);
   }

   public void remove(RemovalReason pReason) {
      if (!this.level().isClientSide && pReason.shouldDestroy()) {
         Containers.dropContents(this.level(), this, this);
      }

      super.remove(pReason);
   }

   public InteractionResult interact(Player pPlayer, InteractionHand pHand) {
      return this.canAddPassenger(pPlayer) && !pPlayer.isSecondaryUseActive() ? super.interact(pPlayer, pHand) : this.interactWithContainerVehicle(pPlayer);
   }

   public void openCustomInventoryScreen(Player player) {
      player.openMenu(this);
      if (!player.level().isClientSide) {
         this.gameEvent(GameEvent.CONTAINER_OPEN, player);
         PiglinAi.angerNearbyPiglins(player, true);
      }
   }

   @Override
   public Item getDropItem() {
      return this.getWoodType().getChestItem().get();
   }

   public void clearContent() {
      this.clearChestVehicleContent();
   }

   public int getContainerSize() {
      return 27;
   }

   public ItemStack getItem(int pIndex) {
      return this.getChestVehicleItem(pIndex);
   }

   public ItemStack removeItem(int pIndex, int pCount) {
      return this.removeChestVehicleItem(pIndex, pCount);
   }

   public ItemStack removeItemNoUpdate(int pIndex) {
      return this.removeChestVehicleItemNoUpdate(pIndex);
   }

   public void setItem(int pIndex, ItemStack pStack) {
      this.setChestVehicleItem(pIndex, pStack);
   }

   public SlotAccess getSlot(int pSlot) {
      return this.getChestVehicleSlot(pSlot);
   }

   public void setChanged() {
   }

   public boolean stillValid(Player pPlayer) {
      return this.isChestVehicleStillValid(pPlayer);
   }

   @Nullable
   public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
      if (this.lootTable != null && pPlayer.isSpectator()) {
         return null;
      } else {
         this.unpackLootTable(pInventory.player);
         return ChestMenu.threeRows(pContainerId, pInventory, this);
      }
   }

   public void unpackLootTable(@Nullable Player player) {
      this.unpackChestVehicleLootTable(player);
   }

   @Nullable
   public ResourceKey<LootTable> getLootTable() {
      return this.lootTable;
   }

   public void setLootTable(@Nullable ResourceKey<LootTable> location) {
      this.lootTable = location;
   }

   public long getLootTableSeed() {
      return this.lootTableSeed;
   }

   public void setLootTableSeed(long seed) {
      this.lootTableSeed = seed;
   }

   public NonNullList<ItemStack> getItemStacks() {
      return this.itemStacks;
   }

   public void clearItemStacks() {
      this.itemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
   }
}
