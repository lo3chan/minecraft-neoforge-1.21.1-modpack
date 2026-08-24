package net.Pandarix.block.entity;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.List;
import java.util.Optional;
import net.Pandarix.BACommon;
import net.Pandarix.block.custom.ArchelogyTable;
import net.Pandarix.item.BetterBrushItem;
import net.Pandarix.recipe.IdentifyingRecipe;
import net.Pandarix.screen.IdentifyingMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ArcheologyTableBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, RecipeCraftingHolder, StackedContentsCompatible {
   public static final int INV_SIZE = 3;
   public static final int NO_PROP_DELEGATES = 2;
   protected NonNullList<ItemStack> items;
   private static final int[] SLOTS_FOR_UP = new int[]{0};
   private static final int[] SLOTS_FOR_DOWN = new int[]{2};
   private static final int[] SLOTS_FOR_SIDES = new int[]{1};
   private final Object2IntOpenHashMap<ResourceLocation> recipesUsed;
   protected final ContainerData data;
   private int progress = 0;
   private int maxProgress = 72;

   public ArcheologyTableBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)ModBlockEntities.ARCHEOLOGY_TABLE.get(), pos, state);
      this.items = NonNullList.withSize(3, ItemStack.EMPTY);
      this.recipesUsed = new Object2IntOpenHashMap();
      this.data = new ContainerData() {
         public int get(int index) {
            return switch (index) {
               case 0 -> ArcheologyTableBlockEntity.this.progress;
               case 1 -> ArcheologyTableBlockEntity.this.maxProgress;
               default -> 0;
            };
         }

         public void set(int index, int value) {
            switch (index) {
               case 0:
                  ArcheologyTableBlockEntity.this.progress = value;
                  break;
               case 1:
                  ArcheologyTableBlockEntity.this.maxProgress = value;
            }
         }

         public int getCount() {
            return 2;
         }
      };
   }

   @NotNull
   protected AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory) {
      return new IdentifyingMenu(id, inventory, this, this.data);
   }

   private void resetProgress() {
      this.progress = 0;
   }

   public int getProgress() {
      return this.progress;
   }

   private static Optional<RecipeHolder<IdentifyingRecipe>> getRecipeOrRandomMatching(SingleRecipeInput singleRecipeInput, ServerLevel serverLevel) {
      List<RecipeHolder<IdentifyingRecipe>> possibleRecipes = serverLevel.getRecipeManager()
         .getRecipesFor(IdentifyingRecipe.Type.INSTANCE, singleRecipeInput, serverLevel);
      int size = possibleRecipes.size();

      return switch (size) {
         case 0 -> Optional.empty();
         case 1 -> Optional.of((RecipeHolder)possibleRecipes.getFirst());
         default -> Optional.of(possibleRecipes.get(serverLevel.random.nextInt(size)));
      };
   }

   public static void tick(Level world, BlockPos blockPos, BlockState blockState, ArcheologyTableBlockEntity entity) {
      if (!world.isClientSide()) {
         ItemStack brushSlotContent = (ItemStack)entity.items.getFirst();
         boolean hasBrush = brushSlotContent.getItem() instanceof BrushItem;
         ItemStack inputSlotContent = (ItemStack)entity.items.get(1);
         if (hasBrush && !inputSlotContent.isEmpty()) {
            ServerLevel serverLevel = (ServerLevel)world;
            SingleRecipeInput singleRecipeInput = new SingleRecipeInput(inputSlotContent);
            Optional<RecipeHolder<IdentifyingRecipe>> recipeHolder = getRecipeOrRandomMatching(singleRecipeInput, serverLevel);
            if (recipeHolder.isEmpty()
               || !canBrush(serverLevel.registryAccess(), recipeHolder.get(), singleRecipeInput, entity.items, entity.getMaxStackSize())) {
               setBlockBrushing(world, blockPos, blockState, false);
               entity.resetProgress();
               return;
            }

            int brushSpeed = brushSlotContent.getItem() instanceof BetterBrushItem betterBrushItem ? betterBrushItem.getBrushingSpeed() : 10;
            if (entity.progress % brushSpeed == 0) {
               world.playSound(null, entity.worldPosition, SoundEvents.BRUSH_GENERIC, SoundSource.BLOCKS, 0.25F, 1.0F);
            }

            int progressStep = (int)Math.ceil(10.0F / brushSpeed);
            entity.progress += progressStep;
            setBlockBrushing(world, blockPos, blockState, true);
            setChanged(world, blockPos, blockState);
            if (entity.progress >= entity.maxProgress) {
               entity.craftItem(serverLevel, recipeHolder.get(), singleRecipeInput, entity.items);
            }
         } else {
            entity.resetProgress();
            setBlockBrushing(world, blockPos, blockState, false);
            entity.setChanged();
         }
      }
   }

   private static void setBlockBrushing(Level world, BlockPos blockPos, BlockState blockState, boolean brushing) {
      world.setBlock(blockPos, (BlockState)blockState.setValue(ArchelogyTable.DUSTING, brushing), 3);
      setChanged(world, blockPos, blockState);
   }

   public void setRecipeUsed(@Nullable RecipeHolder<?> recipeHolder) {
      if (recipeHolder != null) {
         ResourceLocation resourceLocation = recipeHolder.id();
         this.recipesUsed.addTo(resourceLocation, 1);
      }
   }

   @Nullable
   public RecipeHolder<?> getRecipeUsed() {
      return null;
   }

   private void craftItem(
      ServerLevel serverLevel, RecipeHolder<IdentifyingRecipe> recipeHolder, SingleRecipeInput singleRecipeInput, NonNullList<ItemStack> contents
   ) {
      if (recipeHolder != null && canBrush(serverLevel.registryAccess(), recipeHolder, singleRecipeInput, contents, this.getMaxStackSize())) {
         ItemStack stack = (ItemStack)this.items.get(1);
         stack.shrink(1);
         this.items.set(1, stack);
         ItemStack brush = (ItemStack)this.items.getFirst();
         brush.hurtAndBreak(
            1, serverLevel, null, item -> serverLevel.playSound(null, this.worldPosition, SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 0.25F, 1.0F)
         );
         serverLevel.playSound(null, this.worldPosition, SoundEvents.BRUSH_SAND_COMPLETED, SoundSource.BLOCKS, 0.5F, 1.0F);
         this.setRecipeUsed(recipeHolder);
         ItemStack resultStack = ((IdentifyingRecipe)recipeHolder.value()).assemble(singleRecipeInput, serverLevel.registryAccess());
         ItemStack stackInOutput = ((ItemStack)contents.get(2)).copy();
         if (stackInOutput.isEmpty()) {
            this.items.set(2, resultStack.copy());
         } else if (ItemStack.isSameItemSameComponents(stackInOutput, resultStack)) {
            stackInOutput.grow(resultStack.getCount());
            this.items.set(2, stackInOutput);
         }

         this.resetProgress();
         this.setChanged();
      }
   }

   private static boolean canBrush(
      RegistryAccess registryAccess,
      @Nullable RecipeHolder<IdentifyingRecipe> recipeHolder,
      SingleRecipeInput singleRecipeInput,
      NonNullList<ItemStack> nonNullList,
      int maxStackSize
   ) {
      if (!((ItemStack)nonNullList.getFirst()).isEmpty() && recipeHolder != null) {
         ItemStack potResult = ((IdentifyingRecipe)recipeHolder.value()).assemble(singleRecipeInput, registryAccess);
         if (potResult.isEmpty()) {
            return false;
         } else {
            ItemStack stackInOutput = (ItemStack)nonNullList.get(2);
            if (stackInOutput.isEmpty()) {
               return true;
            } else if (!ItemStack.isSameItemSameComponents(potResult, stackInOutput)) {
               return false;
            } else {
               return potResult.getCount() < maxStackSize && stackInOutput.getCount() < stackInOutput.getMaxStackSize()
                  ? true
                  : stackInOutput.getCount() < potResult.getMaxStackSize();
            }
         }
      } else {
         return false;
      }
   }

   @NotNull
   public int[] getSlotsForFace(Direction direction) {
      return switch (direction) {
         case DOWN -> SLOTS_FOR_DOWN;
         case UP -> SLOTS_FOR_UP;
         default -> SLOTS_FOR_SIDES;
      };
   }

   public boolean canPlaceItemThroughFace(int slot, @NotNull ItemStack itemStack, @Nullable Direction direction) {
      if (direction == null) {
         return false;
      } else {
         return switch (direction) {
            case DOWN -> false;
            case UP -> slot == 0 && itemStack.getItem() instanceof BrushItem;
            default -> slot == 1 && ((ItemStack)this.items.get(1)).isEmpty();
         };
      }
   }

   public boolean canTakeItemThroughFace(int slot, @NotNull ItemStack itemStack, @NotNull Direction direction) {
      return direction == Direction.DOWN && slot == 2;
   }

   @NotNull
   public NonNullList<ItemStack> getItems() {
      return this.items;
   }

   protected void setItems(@NotNull NonNullList<ItemStack> nonNullList) {
      for (int i = 0; i < this.items.size(); i++) {
         this.items.set(i, (ItemStack)nonNullList.get(i));
      }
   }

   public void setItem(int i, ItemStack itemStack) {
      ItemStack itemStack2 = (ItemStack)this.items.get(i);
      boolean isValid = !itemStack.isEmpty() && ItemStack.isSameItemSameComponents(itemStack2, itemStack);
      this.items.set(i, itemStack);
      itemStack.limitSize(this.getMaxStackSize(itemStack));
      if (i == 0 && !isValid) {
         this.setChanged();
      }
   }

   public int getContainerSize() {
      return this.items.size();
   }

   public void fillStackedContents(StackedContents stackedContents) {
      this.items.forEach(stackedContents::accountStack);
   }

   public void setChanged() {
      if (this.level != null) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
      }

      super.setChanged();
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   @NotNull
   public CompoundTag getUpdateTag(@NotNull Provider pRegistries) {
      CompoundTag nbt = super.getUpdateTag(pRegistries);
      this.saveAdditional(nbt, pRegistries);
      return nbt;
   }

   protected void saveAdditional(@NotNull CompoundTag pTag, @NotNull Provider pRegistries) {
      super.saveAdditional(pTag, pRegistries);
      ContainerHelper.saveAllItems(pTag, this.items, pRegistries);
      pTag.putInt("archeology_table.progress", this.progress);
      CompoundTag recipesUsed = new CompoundTag();
      this.recipesUsed.forEach((resourceLocation, integer) -> recipesUsed.putInt(resourceLocation.toString(), integer));
      pTag.put("RecipesUsed", recipesUsed);
   }

   protected void loadAdditional(@NotNull CompoundTag pTag, @NotNull Provider pRegistries) {
      super.loadAdditional(pTag, pRegistries);
      this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      ContainerHelper.loadAllItems(pTag, this.items, pRegistries);
      this.progress = pTag.getInt("archeology_table");
      CompoundTag recipesUsed = pTag.getCompound("RecipesUsed");
      recipesUsed.getAllKeys().forEach(string -> this.recipesUsed.put(ResourceLocation.parse(string), recipesUsed.getInt(string)));
      this.setChanged();
   }

   @NotNull
   public Component getDisplayName() {
      return Component.translatable(BACommon.createResource("archeology_table").toLanguageKey());
   }

   @NotNull
   protected Component getDefaultName() {
      return this.getDisplayName();
   }
}
