package vectorwing.farmersdelight.common.block.entity;

import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.RecipeManager.CachedCheck;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.phys.Vec2;
import net.neoforged.neoforge.items.ItemStackHandler;
import vectorwing.farmersdelight.common.block.AbstractStoveBlock;
import vectorwing.farmersdelight.common.utility.ItemUtils;

public abstract class AbstractStoveBlockEntity extends BlockEntity implements Clearable {
   private final ItemStackHandler items;
   private final int[] cookingProgress;
   private final int[] cookingTime;
   private final CachedCheck<SingleRecipeInput, ? extends AbstractCookingRecipe> quickRecipeLookup;

   protected AbstractStoveBlockEntity(
      BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState, RecipeType<? extends AbstractCookingRecipe> recipeType
   ) {
      super(blockEntityType, blockPos, blockState);
      int inventorySlotCount = this.getInventorySlotCount();
      this.items = createHandler(inventorySlotCount);
      this.cookingProgress = new int[inventorySlotCount];
      this.cookingTime = new int[inventorySlotCount];
      this.quickRecipeLookup = RecipeManager.createCheck(recipeType);
   }

   protected abstract int getInventorySlotCount();

   public abstract Vec2 getStoveItemOffset(int var1);

   public ItemStackHandler getItems() {
      return this.items;
   }

   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      CompoundTag inventoryTag;
      if (tag.contains("Inventory")) {
         inventoryTag = tag.getCompound("Inventory");
      } else {
         inventoryTag = tag;
      }

      this.items.deserializeNBT(registries, inventoryTag);
      if (tag.contains("CookingTimes", 11)) {
         int[] arrayCookingTimes = tag.getIntArray("CookingTimes");
         System.arraycopy(arrayCookingTimes, 0, this.cookingProgress, 0, Math.min(this.cookingTime.length, arrayCookingTimes.length));
      }

      if (tag.contains("CookingTotalTimes", 11)) {
         int[] arrayCookingTimesTotal = tag.getIntArray("CookingTotalTimes");
         System.arraycopy(arrayCookingTimesTotal, 0, this.cookingTime, 0, Math.min(this.cookingTime.length, arrayCookingTimesTotal.length));
      }
   }

   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      tag.put("Inventory", this.items.serializeNBT(registries));
      tag.putIntArray("CookingTimes", this.cookingProgress);
      tag.putIntArray("CookingTotalTimes", this.cookingTime);
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag(Provider registries) {
      CompoundTag tag = super.getUpdateTag(registries);
      tag.put("Inventory", this.items.serializeNBT(registries));
      return tag;
   }

   public static void serverTick(Level level, BlockPos pos, BlockState state, AbstractStoveBlockEntity stoveEntity) {
      if (!stoveEntity.isEmpty()) {
         if (stoveEntity.shouldDropItems()) {
            stoveEntity.dropAllItems();
            stoveEntity.setChanged();
         } else {
            if ((Boolean)state.getValue(AbstractStoveBlock.LIT)) {
               stoveEntity.cookAndOutputItems();
            } else {
               stoveEntity.coolItems();
            }
         }
      }
   }

   private void cookAndOutputItems() {
      assert this.level != null;

      boolean didChange = false;

      for (int i = 0; i < this.items.getSlots(); i++) {
         ItemStack ingredient = this.items.getStackInSlot(i);
         if (!ingredient.isEmpty()) {
            didChange = true;
            this.cookingProgress[i]++;
            if (this.cookingProgress[i] >= this.cookingTime[i]) {
               SingleRecipeInput input = new SingleRecipeInput(ingredient);
               ItemStack result = this.quickRecipeLookup
                  .getRecipeFor(input, this.level)
                  .map(recipe -> ((AbstractCookingRecipe)recipe.value()).assemble(input, this.level.registryAccess()))
                  .orElse(ingredient);
               if (result.isItemEnabled(this.level.enabledFeatures())) {
                  ItemUtils.spawnItemEntity(
                     this.level,
                     result.copy(),
                     this.worldPosition.getX() + 0.5,
                     this.worldPosition.getY() + 1.0,
                     this.worldPosition.getZ() + 0.5,
                     this.level.random.nextGaussian() * 0.009999999776482582,
                     0.10000000149011612,
                     this.level.random.nextGaussian() * 0.009999999776482582
                  );
                  this.items.setStackInSlot(i, ItemStack.EMPTY);
                  BlockState state = this.getBlockState();
                  this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
                  this.level.gameEvent(GameEvent.BLOCK_CHANGE, this.worldPosition, Context.of(state));
               }
            }
         }
      }

      if (didChange) {
         this.setChanged();
      }
   }

   private void coolItems() {
      assert this.level != null;

      boolean didChange = false;

      for (int i = 0; i < this.items.getSlots(); i++) {
         int thisItemCookingProgress = this.cookingProgress[i];
         if (thisItemCookingProgress > 0) {
            didChange = true;
            this.cookingProgress[i] = Mth.clamp(thisItemCookingProgress - 2, 0, this.cookingTime[i]);
         }
      }

      if (didChange) {
         this.setChanged();
      }
   }

   public Optional<? extends RecipeHolder<? extends AbstractCookingRecipe>> getCookingRecipe(ItemStack itemStack) {
      assert this.level != null;

      return this.quickRecipeLookup.getRecipeFor(new SingleRecipeInput(itemStack), this.level);
   }

   public int getNextEmptySlot() {
      return IntStream.range(0, this.items.getSlots()).filter(i -> this.items.getStackInSlot(i).isEmpty()).findFirst().orElse(-1);
   }

   public boolean placeFood(@Nullable Entity entity, ItemStack foodStackToPlace, RecipeHolder<? extends AbstractCookingRecipe> recipe) {
      assert this.level != null;

      int emptySlotIndex = this.getNextEmptySlot();
      if (emptySlotIndex < 0) {
         return false;
      } else {
         assert this.items.getStackInSlot(emptySlotIndex).isEmpty();

         this.cookingTime[emptySlotIndex] = ((AbstractCookingRecipe)recipe.value()).getCookingTime();
         this.cookingProgress[emptySlotIndex] = 0;
         this.items.setStackInSlot(emptySlotIndex, foodStackToPlace.split(1));
         BlockState state = this.getBlockState();
         this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
         this.level.gameEvent(GameEvent.BLOCK_CHANGE, this.worldPosition, Context.of(entity, state));
         this.setChanged();
         return true;
      }
   }

   public boolean shouldDropItems() {
      return this.level == null ? false : AbstractStoveBlock.isStoveTopCovered(this.level, this.worldPosition, this.getBlockState());
   }

   public Stream<ItemStack> streamItems() {
      return IntStream.range(0, this.items.getSlots()).mapToObj(this.items::getStackInSlot);
   }

   public boolean isEmpty() {
      return this.streamItems().allMatch(ItemStack::isEmpty);
   }

   public boolean isFull() {
      return this.streamItems().noneMatch(ItemStack::isEmpty);
   }

   public void dropAllItems() {
      if (this.level != null) {
         ItemUtils.dropItems(this.level, this.worldPosition, this.items);
         BlockState state = this.getBlockState();
         this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
         this.level.gameEvent(GameEvent.BLOCK_CHANGE, this.worldPosition, Context.of(state));
      }
   }

   public void extinguish() {
      if (this.level != null) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
         this.setChanged();
      }
   }

   public void ignite() {
      if (this.level != null) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
         this.setChanged();
      }
   }

   public void clearContent() {
      this.streamItems().forEach(stack -> stack.setCount(0));
   }

   private static ItemStackHandler createHandler(int slotCount) {
      return new ItemStackHandler(slotCount) {
         public int getSlotLimit(int slot) {
            return 1;
         }
      };
   }
}
