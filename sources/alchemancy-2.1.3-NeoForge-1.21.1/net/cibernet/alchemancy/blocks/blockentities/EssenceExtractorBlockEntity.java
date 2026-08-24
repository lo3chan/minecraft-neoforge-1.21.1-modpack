package net.cibernet.alchemancy.blocks.blockentities;

import java.util.Optional;
import net.cibernet.alchemancy.crafting.EssenceExtractionRecipe;
import net.cibernet.alchemancy.registries.AlchemancyBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager.CachedCheck;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class EssenceExtractorBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, StackedContentsCompatible, RecipeInput, IEssenceHolder {
   protected NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
   public final EssenceContainer storedEssence = new EssenceContainer(1000);
   public static final int[] SLOTS = new int[]{0};
   private static final CachedCheck<EssenceExtractorBlockEntity, EssenceExtractionRecipe> RECIPE_CHECK = null;

   public EssenceExtractorBlockEntity(BlockPos pos, BlockState blockState) {
      super((BlockEntityType)AlchemancyBlockEntities.ESSENCE_EXTRACTOR.get(), pos, blockState);
   }

   public static void serverTick(Level level, BlockPos pos, BlockState state, EssenceExtractorBlockEntity blockEntity) {
      Optional<RecipeHolder<EssenceExtractionRecipe>> recipeHolder = RECIPE_CHECK.getRecipeFor(blockEntity, level);
      recipeHolder.ifPresent(recipe -> ((EssenceExtractionRecipe)recipe.value()).assemble(blockEntity, level.registryAccess()));
   }

   public int[] getSlotsForFace(Direction side) {
      return SLOTS;
   }

   public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction) {
      return true;
   }

   public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
      return true;
   }

   protected Component getDefaultName() {
      return Component.translatable("block.alchemancy.essence_extractor");
   }

   protected NonNullList<ItemStack> getItems() {
      return this.items;
   }

   protected void setItems(NonNullList<ItemStack> items) {
      this.items = items;
   }

   protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
      return null;
   }

   public int getContainerSize() {
      return this.items.size();
   }

   public void fillStackedContents(StackedContents helper) {
      for (ItemStack itemstack : this.items) {
         helper.accountStack(itemstack);
      }
   }

   protected void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      ContainerHelper.saveAllItems(tag, this.items, registries);
      tag.put("essence", this.storedEssence.saveToTag(new CompoundTag()));
   }

   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      ContainerHelper.loadAllItems(tag, this.items, registries);
      this.storedEssence.loadFromTag(tag.getCompound("essence"));
   }

   public int size() {
      return this.getContainerSize();
   }

   @Override
   public EssenceContainer getEssenceContainer() {
      return this.storedEssence;
   }
}
