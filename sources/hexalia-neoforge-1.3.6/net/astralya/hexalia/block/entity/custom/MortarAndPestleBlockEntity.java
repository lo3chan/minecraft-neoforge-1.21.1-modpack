package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.recipe.ModRecipeTypes;
import net.astralya.hexalia.recipe.MortarAndPestleRecipe;
import net.astralya.hexalia.recipe.MortarAndPestleRecipeInput;
import net.astralya.hexalia.util.ItemInteractionHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class MortarAndPestleBlockEntity extends BlockEntity implements Container, Clearable, ItemInteractionHelper.ItemStorage {
   public static final int SPIN_TICKS = 20;
   public static final int REQUIRED_SPINS = 3;
   public static final int INPUT_0 = 0;
   public static final int INPUT_1 = 1;
   public static final int INPUT_2 = 2;
   public static final int OUTPUT = 3;
   private static final int SLOT_COUNT = 4;
   private static final String TAG_PESTLE_TICK = "PestleTick";
   private static final String TAG_PESTLE_COUNT = "PestleCount";
   private static final String TAG_PESTLING = "Pestling";
   private static final String TAG_PENDING_RESULT = "PendingResult";
   private final NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);
   private ItemStack pendingResult = ItemStack.EMPTY;
   private int pestleTick;
   private int pestleCount;
   private boolean pestling;

   public MortarAndPestleBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)ModBlockEntityTypes.MORTAR_AND_PESTLE.get(), pos, state);
   }

   public static void tick(Level level, BlockPos pos, BlockState state, MortarAndPestleBlockEntity mortar) {
      if (!level.isClientSide() && level.hasNeighborSignal(pos) && mortar.canStartSpin()) {
         mortar.startSpin();
      }

      if (mortar.pestleTick > 0) {
         mortar.pestleTick--;
         mortar.setChanged();
         if (!level.isClientSide() && mortar.pestleTick == 0) {
            mortar.tryFinishOnSpinEnd();
            mortar.inventoryChanged();
         }
      }
   }

   public ItemStack getItem(int slot) {
      return slot >= 0 && slot < 4 ? (ItemStack)this.items.get(slot) : ItemStack.EMPTY;
   }

   public int getContainerSize() {
      return 4;
   }

   public boolean isEmpty() {
      for (ItemStack stack : this.items) {
         if (!stack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public ItemStack removeItem(int slot, int amount) {
      ItemStack removed = ContainerHelper.removeItem(this.items, slot, amount);
      if (!removed.isEmpty()) {
         this.recomputeAndSync();
      }

      return removed;
   }

   public ItemStack removeItemNoUpdate(int slot) {
      return ContainerHelper.takeItem(this.items, slot);
   }

   public void setItem(int slot, ItemStack stack) {
      if (slot >= 0 && slot < 4) {
         this.items.set(slot, stack.copyWithCount(Math.min(stack.getCount(), this.getMaxStackSize())));
         this.recomputeAndSync();
      }
   }

   public int getMaxStackSize() {
      return 1;
   }

   public boolean canPlaceItem(int slot, ItemStack stack) {
      return slot >= 0 && slot <= 2 && !stack.isEmpty() && !this.hasOutput() && ((ItemStack)this.items.get(slot)).isEmpty();
   }

   public boolean canTakeItem(Container target, int slot, ItemStack stack) {
      return slot == 3 || slot >= 0 && slot <= 2 && !this.hasOutput();
   }

   public boolean stillValid(Player player) {
      return Container.stillValidBlockEntity(this, player);
   }

   public void clearContent() {
      this.items.clear();
      this.recomputeAndSync();
   }

   public int getPestleTick() {
      return this.pestleTick;
   }

   public boolean hasAnyInputs() {
      return !((ItemStack)this.items.get(0)).isEmpty() || !((ItemStack)this.items.get(1)).isEmpty() || !((ItemStack)this.items.get(2)).isEmpty();
   }

   public boolean hasOutput() {
      return !((ItemStack)this.items.get(3)).isEmpty();
   }

   public boolean canStartSpin() {
      return !this.hasOutput() && !this.pendingResult.isEmpty() && this.pestleTick <= 0 && this.pestleCount < 3;
   }

   public boolean startSpin() {
      if (this.level == null) {
         return false;
      } else {
         if (!this.level.isClientSide()) {
            this.recomputePestlingState();
         }

         if (!this.canStartSpin()) {
            return false;
         } else {
            this.pestling = true;
            this.pestleTick = 20;
            this.pestleCount++;
            if (this.level instanceof ServerLevel server) {
               this.spawnCrushParticles(server);
               this.inventoryChanged();
            } else {
               this.setChanged();
            }

            return true;
         }
      }
   }

   @Override
   public boolean canInsertItem(ItemStack stack) {
      return !stack.isEmpty() && !this.hasOutput() && this.firstEmptyInputSlot() != -1;
   }

   @Override
   public boolean addItem(ItemStack stack) {
      if (!this.canInsertItem(stack)) {
         return false;
      } else {
         int slot = this.firstEmptyInputSlot();
         this.items.set(slot, stack.split(1));
         this.recomputeAndSync();
         return true;
      }
   }

   @Override
   public boolean canExtractItem() {
      return this.hasOutput() || this.hasAnyInputs();
   }

   @Override
   public ItemStack removeItem() {
      return this.hasOutput() ? this.takeOutputOne() : this.extractOneInput();
   }

   public ItemStack extractOneInput() {
      for (int slot = 2; slot >= 0; slot--) {
         ItemStack stack = (ItemStack)this.items.get(slot);
         if (!stack.isEmpty()) {
            ItemStack out = stack.copyWithCount(1);
            this.items.set(slot, ItemStack.EMPTY);
            this.recomputeAndSync();
            return out;
         }
      }

      return ItemStack.EMPTY;
   }

   public ItemStack takeOutputOne() {
      ItemStack output = (ItemStack)this.items.get(3);
      if (output.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         ItemStack out = output.copyWithCount(1);
         output.shrink(1);
         this.items.set(3, output.isEmpty() ? ItemStack.EMPTY : output);
         this.inventoryChanged();
         return out;
      }
   }

   private void recomputePestlingState() {
      if (this.level != null && !this.level.isClientSide()) {
         if (!this.hasOutput() && this.hasAnyInputs()) {
            ItemStack newResult = this.computeRecipeResult();
            if (!ItemStack.isSameItemSameComponents(this.pendingResult, newResult) || this.pendingResult.getCount() != newResult.getCount()) {
               this.pestleTick = 0;
               this.pestleCount = 0;
            }

            this.pendingResult = newResult;
            this.pestling = !newResult.isEmpty();
            if (!this.pestling) {
               this.pestleTick = 0;
               this.pestleCount = 0;
            }
         } else {
            this.pendingResult = ItemStack.EMPTY;
            this.pestling = false;
            this.pestleTick = 0;
            this.pestleCount = 0;
         }
      }
   }

   private ItemStack computeRecipeResult() {
      if (this.level == null) {
         return ItemStack.EMPTY;
      } else {
         MortarAndPestleRecipeInput input = new MortarAndPestleRecipeInput(
            (ItemStack)this.items.get(0), (ItemStack)this.items.get(1), (ItemStack)this.items.get(2)
         );
         return this.level
            .getRecipeManager()
            .getRecipeFor((RecipeType)ModRecipeTypes.MORTAR_AND_PESTLE.get(), input, this.level)
            .<MortarAndPestleRecipe>map(RecipeHolder::value)
            .map(recipe -> recipe.getResultItem(this.level.registryAccess()).copy())
            .orElse(ItemStack.EMPTY);
      }
   }

   private void tryFinishOnSpinEnd() {
      if (this.level != null && !this.level.isClientSide() && this.pestling && this.pestleCount >= 3) {
         if (!this.pendingResult.isEmpty() && !this.hasOutput()) {
            this.items.set(3, this.pendingResult.copy());
            this.items.set(0, ItemStack.EMPTY);
            this.items.set(1, ItemStack.EMPTY);
            this.items.set(2, ItemStack.EMPTY);
            this.pendingResult = ItemStack.EMPTY;
            this.pestling = false;
            this.pestleTick = 0;
            this.pestleCount = 0;
         }
      }
   }

   private void spawnCrushParticles(ServerLevel server) {
      double x = this.worldPosition.getX() + 0.5;
      double y = this.worldPosition.getY() + 0.2;
      double z = this.worldPosition.getZ() + 0.5;

      for (int slot = 0; slot <= 2; slot++) {
         ItemStack stack = (ItemStack)this.items.get(slot);
         if (!stack.isEmpty()) {
            ItemParticleOption particle = new ItemParticleOption(ParticleTypes.ITEM, stack);

            for (int index = 0; index < 3; index++) {
               server.sendParticles(
                  particle,
                  x + (server.random.nextDouble() - 0.5) * 0.12,
                  y + server.random.nextDouble() * 0.06,
                  z + (server.random.nextDouble() - 0.5) * 0.12,
                  1,
                  (server.random.nextDouble() - 0.5) * 0.03,
                  0.02 + server.random.nextDouble() * 0.02,
                  (server.random.nextDouble() - 0.5) * 0.03,
                  0.0
               );
            }
         }
      }
   }

   public void drops() {
      if (this.level != null && !this.level.isClientSide()) {
         SimpleContainer container = new SimpleContainer(4);

         for (int index = 0; index < 4; index++) {
            container.setItem(index, (ItemStack)this.items.get(index));
            this.items.set(index, ItemStack.EMPTY);
         }

         Containers.dropContents(this.level, this.worldPosition, container);
      }
   }

   private int firstEmptyInputSlot() {
      for (int slot = 0; slot <= 2; slot++) {
         if (((ItemStack)this.items.get(slot)).isEmpty()) {
            return slot;
         }
      }

      return -1;
   }

   private void recomputeAndSync() {
      this.recomputePestlingState();
      this.inventoryChanged();
   }

   private void inventoryChanged() {
      this.setChanged();
      if (this.level != null && !this.level.isClientSide()) {
         this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
      }
   }

   protected void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      ContainerHelper.saveAllItems(tag, this.items, registries);
      tag.putInt("PestleTick", this.pestleTick);
      tag.putInt("PestleCount", this.pestleCount);
      tag.putBoolean("Pestling", this.pestling);
      if (!this.pendingResult.isEmpty()) {
         tag.put("PendingResult", this.pendingResult.save(registries));
      }
   }

   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);

      for (int index = 0; index < 4; index++) {
         this.items.set(index, ItemStack.EMPTY);
      }

      ContainerHelper.loadAllItems(tag, this.items, registries);
      this.pestleTick = tag.getInt("PestleTick");
      this.pestleCount = tag.getInt("PestleCount");
      this.pestling = tag.getBoolean("Pestling");
      this.pendingResult = tag.contains("PendingResult") ? ItemStack.parseOptional(registries, tag.getCompound("PendingResult")) : ItemStack.EMPTY;
   }

   public CompoundTag getUpdateTag(Provider registries) {
      return this.saveWithoutMetadata(registries);
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }
}
