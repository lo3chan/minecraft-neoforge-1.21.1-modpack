package io.github.razordevs.deep_aether.entity.block;

import io.github.razordevs.deep_aether.block.utility.CombinerBlock;
import io.github.razordevs.deep_aether.init.DABlockEntityTypes;
import io.github.razordevs.deep_aether.recipe.DARecipeTypes;
import io.github.razordevs.deep_aether.recipe.combiner.CombinerRecipe;
import io.github.razordevs.deep_aether.recipe.combiner.CombinerRecipeInput;
import io.github.razordevs.deep_aether.screen.CombinerMenu;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.RecipeManager.CachedCheck;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CombinerBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, RecipeCraftingHolder, StackedContentsCompatible {
   private static final int FIRST_SLOT = 0;
   private static final int SECOND_SLOT = 1;
   private static final int THIRD_SLOT = 2;
   private static final int OUTPUT_SLOT = 3;
   protected final ContainerData data;
   protected NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);
   int processingProgress = 0;
   int processingTotalTime = 78;
   boolean combining;
   int combiningDuration;
   private final Object2IntOpenHashMap<ResourceLocation> recipesUsed = new Object2IntOpenHashMap();
   private final CachedCheck<CombinerRecipeInput, CombinerRecipe> quickCheck = RecipeManager.createCheck((RecipeType)DARecipeTypes.COMBINING.get());

   public CombinerBlockEntity(BlockPos pPos, BlockState pBlockState) {
      this((BlockEntityType<?>)DABlockEntityTypes.COMBINER.get(), pPos, pBlockState);
   }

   public CombinerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
      super(type, pos, state);
      this.data = new ContainerData() {
         public int get(int pIndex) {
            return switch (pIndex) {
               case 0 -> CombinerBlockEntity.this.processingProgress;
               case 1 -> CombinerBlockEntity.this.processingTotalTime;
               default -> 0;
            };
         }

         public void set(int pIndex, int pValue) {
            switch (pIndex) {
               case 0:
                  CombinerBlockEntity.this.processingProgress = pValue;
                  break;
               case 1:
                  CombinerBlockEntity.this.processingTotalTime = pValue;
            }
         }

         public int getCount() {
            return 2;
         }
      };
   }

   public Component getDisplayName() {
      return Component.translatable("block.deep_aether.combiner");
   }

   protected Component getDefaultName() {
      return Component.translatable("block.deep_aether.combiner");
   }

   public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory) {
      return new CombinerMenu(pContainerId, pPlayerInventory, this, this.data);
   }

   public void loadAdditional(CompoundTag tag, Provider registry) {
      super.loadAdditional(tag, registry);
      this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      ContainerHelper.loadAllItems(tag, this.items, registry);
      this.processingProgress = tag.getInt("ProcessingTime");
      this.processingTotalTime = tag.getInt("ProcessingTimeTotal");
      CompoundTag recipesUsedTag = tag.getCompound("RecipesUsed");

      for (String key : recipesUsedTag.getAllKeys()) {
         this.recipesUsed.put(ResourceLocation.tryParse(key), recipesUsedTag.getInt(key));
      }
   }

   protected void saveAdditional(CompoundTag tag, Provider registry) {
      super.saveAdditional(tag, registry);
      tag.putInt("ProcessingTime", this.processingProgress);
      tag.putInt("ProcessingTimeTotal", this.processingTotalTime);
      ContainerHelper.saveAllItems(tag, this.items, registry);
      CompoundTag recipesUsedTag = new CompoundTag();
      this.recipesUsed.forEach((location, integer) -> recipesUsedTag.putInt(location.toString(), integer));
      tag.put("RecipesUsed", recipesUsedTag);
   }

   public NonNullList<ItemStack> getIngredients() {
      ArrayList<ItemStack> ingredients = new ArrayList<>();
      ingredients.add((ItemStack)this.items.get(0));
      ingredients.add((ItemStack)this.items.get(1));
      ingredients.add((ItemStack)this.items.get(2));
      return NonNullList.copyOf(ingredients);
   }

   public static void serverTick(Level level, BlockPos pos, BlockState state, CombinerBlockEntity blockEntity) {
      boolean changed = false;
      RecipeHolder<CombinerRecipe> recipeHolder = (RecipeHolder<CombinerRecipe>)blockEntity.quickCheck
         .getRecipeFor(new CombinerRecipeInput(blockEntity.getIngredients()), level)
         .orElse(null);
      int i = blockEntity.getMaxStackSize();
      boolean isCharging = false;
      if (blockEntity.canProcess(level.registryAccess(), recipeHolder, blockEntity.items, i)) {
         changed = true;
         blockEntity.processingProgress++;
         isCharging = true;
         if (blockEntity.processingProgress == blockEntity.processingTotalTime) {
            blockEntity.processingProgress = 0;
            blockEntity.processingTotalTime = getTotalProcessingTime(level, blockEntity);
            if (blockEntity.process(level.registryAccess(), recipeHolder, blockEntity.items, i)) {
               blockEntity.setRecipeUsed(recipeHolder);
            }

            isCharging = false;
            blockEntity.combining = true;
            blockEntity.combiningDuration = 15;
         }
      } else {
         blockEntity.processingProgress = 0;
      }

      if (blockEntity.combiningDuration <= 0) {
         blockEntity.combining = false;
      }

      if ((Boolean)state.getValue(CombinerBlock.CHARGING) != isCharging) {
         changed = true;
         state = (BlockState)state.setValue(CombinerBlock.CHARGING, isCharging);
         level.setBlock(pos, state, 3);
      }

      if ((Boolean)state.getValue(CombinerBlock.COMBINING) != blockEntity.combining) {
         changed = true;
         state = (BlockState)state.setValue(CombinerBlock.COMBINING, blockEntity.combining);
         level.setBlock(pos, state, 3);
      }

      if (changed) {
         setChanged(level, pos, state);
      }
   }

   private boolean canProcess(
      RegistryAccess registryAccess, @Nullable RecipeHolder<CombinerRecipe> recipeHolder, NonNullList<ItemStack> stacks, int maxStackSize
   ) {
      ItemStack left = (ItemStack)stacks.getFirst();
      ItemStack middle = (ItemStack)stacks.get(1);
      ItemStack right = (ItemStack)stacks.get(2);
      if (!left.isEmpty() && !middle.isEmpty() && !right.isEmpty() && recipeHolder != null) {
         ItemStack result = ((CombinerRecipe)recipeHolder.value()).assemble(new CombinerRecipeInput(this.getIngredients()), registryAccess);
         if (result.isEmpty()) {
            return false;
         } else {
            ItemStack inResultSlot = (ItemStack)stacks.get(3);
            if (inResultSlot.isEmpty()) {
               return true;
            } else if (!ItemStack.isSameItemSameComponents(inResultSlot, result)) {
               return false;
            } else {
               return inResultSlot.getCount() + result.getCount() <= maxStackSize
                     && inResultSlot.getCount() + result.getCount() <= inResultSlot.getMaxStackSize()
                  ? true
                  : inResultSlot.getCount() + result.getCount() <= result.getMaxStackSize();
            }
         }
      } else {
         return false;
      }
   }

   private boolean process(RegistryAccess registryAccess, @Nullable RecipeHolder<CombinerRecipe> recipeHolder, NonNullList<ItemStack> stacks, int maxStackSize) {
      if (recipeHolder != null && this.canProcess(registryAccess, recipeHolder, stacks, maxStackSize)) {
         ItemStack left = (ItemStack)stacks.getFirst();
         ItemStack middle = (ItemStack)stacks.get(1);
         ItemStack right = (ItemStack)stacks.get(2);
         ItemStack result = ((CombinerRecipe)recipeHolder.value()).assemble(new CombinerRecipeInput(this.getIngredients()), registryAccess);
         ItemStack output = (ItemStack)stacks.get(3);
         if (output.isEmpty()) {
            stacks.set(3, result.copy());
         } else if (output.is(result.getItem())) {
            output.grow(result.getCount());
         }

         left.shrink(1);
         middle.shrink(1);
         right.shrink(1);
         return true;
      } else {
         return false;
      }
   }

   private static int getTotalProcessingTime(Level level, CombinerBlockEntity blockEntity) {
      return blockEntity.quickCheck
         .getRecipeFor(new CombinerRecipeInput(blockEntity.getIngredients()), level)
         .map(recipeHolder -> ((CombinerRecipe)recipeHolder.value()).getProcessingTime())
         .orElse(200);
   }

   public void drops() {
      if (this.level != null) {
         Containers.dropContents(this.level, this.worldPosition, this.items);
      }
   }

   public int[] getSlotsForFace(Direction side) {
      return side == Direction.DOWN ? new int[]{3} : new int[]{0, 1, 2};
   }

   public boolean canPlaceItemThroughFace(int i, ItemStack itemStack, @Nullable Direction direction) {
      return this.canPlaceItem(i, itemStack);
   }

   public boolean canTakeItemThroughFace(int i, ItemStack itemStack, Direction direction) {
      return direction != Direction.DOWN;
   }

   protected NonNullList<ItemStack> getItems() {
      return this.items;
   }

   protected void setItems(NonNullList<ItemStack> pItems) {
      this.items = pItems;
   }

   public int getContainerSize() {
      return this.items.size();
   }

   public boolean isEmpty() {
      for (ItemStack itemstack : this.items) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public ItemStack getItem(int index) {
      return (ItemStack)this.items.get(index);
   }

   public ItemStack removeItem(int index, int count) {
      return ContainerHelper.removeItem(this.items, index, count);
   }

   public ItemStack removeItemNoUpdate(int index) {
      return ContainerHelper.takeItem(this.items, index);
   }

   public void setItem(int index, ItemStack stack) {
      ItemStack itemstack = (ItemStack)this.items.get(index);
      boolean continueProcess = !stack.isEmpty() && ItemStack.isSameItemSameComponents(itemstack, stack);
      this.items.set(index, stack);
      if (stack.getCount() > this.getMaxStackSize()) {
         stack.setCount(this.getMaxStackSize());
      }

      if (index == 0 && !continueProcess) {
         this.processingTotalTime = getTotalProcessingTime(this.level, this);
         this.processingProgress = 0;
         this.setChanged();
      }
   }

   public boolean stillValid(Player player) {
      return Container.stillValidBlockEntity(this, player);
   }

   public void clearContent() {
      this.items.clear();
   }

   public void setRecipeUsed(@Nullable RecipeHolder<?> recipeHolder) {
      if (recipeHolder != null) {
         ResourceLocation location = recipeHolder.id();
         this.recipesUsed.addTo(location, 1);
      }
   }

   @Nullable
   public RecipeHolder<?> getRecipeUsed() {
      return null;
   }

   public void awardUsedRecipes(Player player, List<ItemStack> items) {
   }

   public void fillStackedContents(StackedContents stackedContents) {
      for (ItemStack itemstack : this.items) {
         stackedContents.accountStack(itemstack);
      }
   }
}
