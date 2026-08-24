package com.aetherteam.aether.blockentity;

import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.item.components.AetherDataComponents;
import com.aetherteam.aether.item.components.DungeonKind;
import java.util.stream.IntStream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponentMap.Builder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity.DataComponentInput;
import net.minecraft.world.level.block.state.BlockState;

public class TreasureChestBlockEntity extends RandomizableContainerBlockEntity implements LidBlockEntity, WorldlyContainer {
   private NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);
   private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
      protected void onOpen(Level level, BlockPos pos, BlockState state) {
         TreasureChestBlockEntity.playSound(level, pos, state, SoundEvents.CHEST_OPEN);
      }

      protected void onClose(Level level, BlockPos pos, BlockState state) {
         TreasureChestBlockEntity.playSound(level, pos, state, SoundEvents.CHEST_CLOSE);
      }

      protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int count, int openCount) {
         TreasureChestBlockEntity.this.signalOpenCount(level, pos, state, count, openCount);
      }

      protected boolean isOwnContainer(Player player) {
         if (!(player.containerMenu instanceof ChestMenu chestMenu)) {
            return false;
         } else {
            Container container = chestMenu.getContainer();
            return container == TreasureChestBlockEntity.this
               || container instanceof CompoundContainer compoundContainer && compoundContainer.contains(TreasureChestBlockEntity.this);
         }
      }
   };
   private final ChestLidController chestLidController = new ChestLidController();
   private boolean locked;
   private ResourceLocation kind = ResourceLocation.fromNamespaceAndPath("aether", "bronze");

   public TreasureChestBlockEntity() {
      this((BlockEntityType<?>)AetherBlockEntityTypes.TREASURE_CHEST.get(), BlockPos.ZERO, ((Block)AetherBlocks.TREASURE_CHEST.get()).defaultBlockState());
   }

   public TreasureChestBlockEntity(BlockPos pos, BlockState state) {
      this((BlockEntityType<?>)AetherBlockEntityTypes.TREASURE_CHEST.get(), pos, state);
   }

   protected TreasureChestBlockEntity(BlockEntityType<?> tileEntityType, BlockPos pos, BlockState state) {
      super(tileEntityType, pos, state);
      this.locked = true;
   }

   public boolean tryUnlock(Player player) {
      if (this.getLocked() && this.level != null) {
         this.setLocked(false);
         this.setChanged();
         this.level.markAndNotifyBlock(this.worldPosition, this.level.getChunkAt(this.worldPosition), this.getBlockState(), this.getBlockState(), 2, 512);
         return true;
      } else {
         return false;
      }
   }

   protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
      return ChestMenu.threeRows(id, inventory, this);
   }

   public int[] getSlotsForFace(Direction direction) {
      return IntStream.range(0, this.getContainerSize()).toArray();
   }

   public boolean canPlaceItemThroughFace(int index, ItemStack stack, Direction direction) {
      return direction != Direction.DOWN && stack.getItem().canFitInsideContainerItems() ? this.canPlaceItem(index, stack) : false;
   }

   public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
      return direction == Direction.DOWN ? this.canTakeItem(this, index, stack) : false;
   }

   public boolean canPlaceItem(int index, ItemStack stack) {
      return this.getLocked() ? false : super.canPlaceItem(index, stack);
   }

   public boolean canTakeItem(Container container, int index, ItemStack stack) {
      return this.getLocked() ? false : super.canTakeItem(container, index, stack);
   }

   protected NonNullList<ItemStack> getItems() {
      return this.items;
   }

   protected void setItems(NonNullList<ItemStack> stacks) {
      this.items = stacks;
   }

   public int getContainerSize() {
      return 27;
   }

   protected Component getDefaultName() {
      return Component.translatable("menu." + this.getKind().getNamespace() + "." + this.getKind().getPath() + "_treasure_chest");
   }

   public static void setDungeonType(BlockGetter level, BlockPos pos, ResourceLocation dungeonType) {
      if (level.getBlockEntity(pos) instanceof TreasureChestBlockEntity treasure) {
         treasure.setKind(dungeonType);
      }
   }

   public void setKind(ResourceLocation kind) {
      this.kind = kind;
   }

   public ResourceLocation getKind() {
      return this.kind;
   }

   public void setLocked(boolean locked) {
      this.locked = locked;
   }

   public boolean getLocked() {
      return this.locked;
   }

   public void startOpen(Player player) {
      if (!this.remove && !player.isSpectator()) {
         this.openersCounter.incrementOpeners(player, this.level, this.getBlockPos(), this.getBlockState());
      }
   }

   public void stopOpen(Player player) {
      if (!this.remove && !player.isSpectator()) {
         this.openersCounter.decrementOpeners(player, this.level, this.getBlockPos(), this.getBlockState());
      }
   }

   public void recheckOpen() {
      if (!this.remove) {
         this.openersCounter.recheckOpeners(this.level, this.getBlockPos(), this.getBlockState());
      }
   }

   protected void signalOpenCount(Level level, BlockPos pos, BlockState state, int count, int openCount) {
      level.blockEvent(pos, state.getBlock(), 1, openCount);
   }

   public boolean triggerEvent(int id, int type) {
      if (id == 1) {
         this.chestLidController.shouldBeOpen(type > 0);
         return true;
      } else {
         return super.triggerEvent(id, type);
      }
   }

   public static void lidAnimateTick(Level level, BlockPos pos, BlockState state, TreasureChestBlockEntity blockEntity) {
      blockEntity.chestLidController.tickLid();
   }

   public float getOpenNess(float partialTicks) {
      return this.chestLidController.getOpenness(partialTicks);
   }

   private static void playSound(Level level, BlockPos pos, BlockState state, SoundEvent sound) {
      double d0 = pos.getX() + 0.5;
      double d1 = pos.getY() + 0.5;
      double d2 = pos.getZ() + 0.5;
      level.playSound(null, d0, d1, d2, sound, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
   }

   protected void applyImplicitComponents(DataComponentInput componentInput) {
      super.applyImplicitComponents(componentInput);
      this.setLocked((Boolean)componentInput.getOrDefault(AetherDataComponents.LOCKED, true));
      DungeonKind kind = (DungeonKind)componentInput.get(AetherDataComponents.DUNGEON_KIND);
      if (kind != null) {
         this.setKind(kind.id());
      } else {
         this.setKind(ResourceLocation.fromNamespaceAndPath("aether", "bronze"));
      }
   }

   protected void collectImplicitComponents(Builder components) {
      super.collectImplicitComponents(components);
      components.set(AetherDataComponents.LOCKED, this.getLocked());
      components.set(AetherDataComponents.DUNGEON_KIND, new DungeonKind(ResourceLocation.fromNamespaceAndPath("aether", "bronze")));
   }

   public void removeComponentsFromTag(CompoundTag tag) {
      super.removeComponentsFromTag(tag);
      tag.remove("Locked");
      tag.remove("Kind");
   }

   public CompoundTag getUpdateTag(Provider registries) {
      return this.saveWithoutMetadata(registries);
   }

   public void handleUpdateTag(CompoundTag tag, Provider registries) {
      this.loadAdditional(tag, registries);
   }

   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      tag.putBoolean("Locked", this.getLocked());
      tag.putString("Kind", this.getKind().toString());
      if (!this.trySaveLootTable(tag)) {
         ContainerHelper.saveAllItems(tag, this.items, registries);
      }
   }

   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.locked = !tag.contains("Locked") || tag.getBoolean("Locked");
      this.kind = tag.contains("Kind") ? ResourceLocation.parse(tag.getString("Kind")) : ResourceLocation.fromNamespaceAndPath("aether", "bronze");
      this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      if (!this.tryLoadLootTable(tag)) {
         ContainerHelper.loadAllItems(tag, this.items, registries);
      }
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet, Provider lookupProvider) {
      CompoundTag compound = packet.getTag();
      this.handleUpdateTag(compound, lookupProvider);
   }
}
