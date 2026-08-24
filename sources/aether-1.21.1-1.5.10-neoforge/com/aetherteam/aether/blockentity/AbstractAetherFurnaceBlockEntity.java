package com.aetherteam.aether.blockentity;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.mixin.mixins.common.accessor.AbstractFurnaceBlockEntityAccessor;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractAetherFurnaceBlockEntity extends AbstractFurnaceBlockEntity {
   private static final int[] SLOTS_FOR_UP = new int[]{0};
   private static final int[] SLOTS_FOR_DOWN = new int[]{2, 0};
   private static final int[] SLOTS_FOR_SIDES = new int[]{1};
   protected ItemStack remainderItem = ItemStack.EMPTY;

   public AbstractAetherFurnaceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, RecipeType<? extends AbstractCookingRecipe> recipeType) {
      super(type, pos, state, recipeType);
   }

   public static void serverTick(Level level, BlockPos pos, BlockState state, AbstractAetherFurnaceBlockEntity blockEntity) {
      AbstractFurnaceBlockEntityAccessor abstractFurnaceBlockEntityAccessor = (AbstractFurnaceBlockEntityAccessor)blockEntity;
      boolean flag = abstractFurnaceBlockEntityAccessor.callIsLit();
      boolean flag1 = false;
      if (abstractFurnaceBlockEntityAccessor.callIsLit()) {
         abstractFurnaceBlockEntityAccessor.aether$setLitTime(abstractFurnaceBlockEntityAccessor.aether$getLitTime() - 1);
      }

      ItemStack itemstack = (ItemStack)abstractFurnaceBlockEntityAccessor.aether$getItems().get(1);
      ItemStack itemstack1 = (ItemStack)abstractFurnaceBlockEntityAccessor.aether$getItems().get(0);
      boolean flag2 = !itemstack1.isEmpty();
      boolean flag3 = !itemstack.isEmpty();
      if (abstractFurnaceBlockEntityAccessor.callIsLit() || flag3 && flag2) {
         RecipeHolder<? extends AbstractCookingRecipe> recipe;
         if (flag2) {
            recipe = (RecipeHolder<? extends AbstractCookingRecipe>)abstractFurnaceBlockEntityAccessor.aether$getQuickCheck()
               .getRecipeFor(new SingleRecipeInput(itemstack1), level)
               .orElse(null);
         } else {
            recipe = null;
         }

         int i = blockEntity.getMaxStackSize();
         if (!abstractFurnaceBlockEntityAccessor.callIsLit()
            && AbstractFurnaceBlockEntityAccessor.callCanBurn(
               level.registryAccess(), recipe, abstractFurnaceBlockEntityAccessor.aether$getItems(), i, blockEntity
            )) {
            abstractFurnaceBlockEntityAccessor.aether$setLitTime(abstractFurnaceBlockEntityAccessor.callGetBurnDuration(itemstack));
            abstractFurnaceBlockEntityAccessor.aether$setLitDuration(abstractFurnaceBlockEntityAccessor.aether$getLitTime());
            if (abstractFurnaceBlockEntityAccessor.callIsLit()) {
               flag1 = true;
               if (itemstack.hasCraftingRemainingItem()) {
                  abstractFurnaceBlockEntityAccessor.aether$getItems().set(1, itemstack.getCraftingRemainingItem());
               } else if (flag3) {
                  itemstack.shrink(1);
                  if (itemstack.isEmpty()) {
                     abstractFurnaceBlockEntityAccessor.aether$getItems().set(1, itemstack.getCraftingRemainingItem());
                  }
               }
            }
         }

         if (abstractFurnaceBlockEntityAccessor.callIsLit()
            && AbstractFurnaceBlockEntityAccessor.callCanBurn(
               level.registryAccess(), recipe, abstractFurnaceBlockEntityAccessor.aether$getItems(), i, blockEntity
            )) {
            abstractFurnaceBlockEntityAccessor.aether$setCookingProgress(abstractFurnaceBlockEntityAccessor.aether$getCookingProgress() + 1);
            if (abstractFurnaceBlockEntityAccessor.aether$getCookingProgress() == abstractFurnaceBlockEntityAccessor.aether$getCookingTotalTime()) {
               abstractFurnaceBlockEntityAccessor.aether$setCookingProgress(0);
               abstractFurnaceBlockEntityAccessor.aether$setCookingTotalTime(AbstractFurnaceBlockEntityAccessor.callGetTotalCookTime(level, blockEntity));
               if (blockEntity.burn(level.registryAccess(), recipe, blockEntity.items, i)) {
                  blockEntity.setRecipeUsed(recipe);
               }

               flag1 = true;
            }
         } else {
            abstractFurnaceBlockEntityAccessor.aether$setCookingProgress(0);
         }
      } else if (!abstractFurnaceBlockEntityAccessor.callIsLit() && abstractFurnaceBlockEntityAccessor.aether$getCookingProgress() > 0) {
         abstractFurnaceBlockEntityAccessor.aether$setCookingProgress(
            Mth.clamp(abstractFurnaceBlockEntityAccessor.aether$getCookingProgress() - 2, 0, abstractFurnaceBlockEntityAccessor.aether$getCookingTotalTime())
         );
      }

      if (flag != abstractFurnaceBlockEntityAccessor.callIsLit()) {
         flag1 = true;
         state = (BlockState)state.setValue(AbstractFurnaceBlock.LIT, abstractFurnaceBlockEntityAccessor.callIsLit());
         level.setBlock(pos, state, 3);
      }

      if (flag1) {
         setChanged(level, pos, state);
      }

      if (((ItemStack)abstractFurnaceBlockEntityAccessor.aether$getItems().get(0)).isEmpty()
         && ((ItemStack)abstractFurnaceBlockEntityAccessor.aether$getItems().get(2)).isEmpty()) {
         blockEntity.remainderItem = ItemStack.EMPTY;
      }
   }

   private boolean burn(RegistryAccess registryAccess, @Nullable RecipeHolder<?> recipe, NonNullList<ItemStack> stacks, int stackSize) {
      AbstractFurnaceBlockEntityAccessor abstractFurnaceBlockEntityAccessor = (AbstractFurnaceBlockEntityAccessor)this;
      if (recipe != null && AbstractFurnaceBlockEntityAccessor.callCanBurn(registryAccess, recipe, stacks, stackSize, this)) {
         ItemStack inputSlotStack = (ItemStack)stacks.get(0);
         ItemStack resultStack = recipe.value()
            .assemble(new SingleRecipeInput((ItemStack)abstractFurnaceBlockEntityAccessor.aether$getItems().getFirst()), registryAccess);
         ItemStack resultSlotStack = (ItemStack)stacks.get(2);
         if (inputSlotStack.is(resultStack.getItem()) || resultStack.is(AetherTags.Items.SAVE_NBT_IN_RECIPE)) {
            resultStack = new ItemStack(resultStack.getItemHolder(), 1, inputSlotStack.getComponentsPatch());
         }

         if (inputSlotStack.is(resultStack.getItem())) {
            resultStack.setDamageValue(0);
         }

         if (resultSlotStack.isEmpty()) {
            stacks.set(2, resultStack.copy());
         } else if (resultSlotStack.is(resultStack.getItem())) {
            resultSlotStack.grow(resultStack.getCount());
         }

         if (inputSlotStack.hasCraftingRemainingItem() && !inputSlotStack.getCraftingRemainingItem().is(resultStack.getCraftingRemainingItem().getItem())) {
            stacks.set(0, inputSlotStack.getCraftingRemainingItem());
         } else {
            inputSlotStack.shrink(1);
         }

         return true;
      } else {
         return false;
      }
   }

   public int[] getSlotsForFace(Direction direction) {
      if (direction == Direction.DOWN) {
         return SLOTS_FOR_DOWN;
      } else {
         return direction == Direction.UP ? SLOTS_FOR_UP : SLOTS_FOR_SIDES;
      }
   }

   public boolean canPlaceItem(int index, ItemStack stack) {
      if (index == 2) {
         return false;
      } else {
         return index != 1 ? true : this.getBurnDuration(stack) > 0;
      }
   }

   public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
      AbstractFurnaceBlockEntityAccessor abstractFurnaceBlockEntityAccessor = (AbstractFurnaceBlockEntityAccessor)this;
      Optional<NonNullList<Ingredient>> ingredient = abstractFurnaceBlockEntityAccessor.aether$getQuickCheck()
         .getRecipeFor(new SingleRecipeInput((ItemStack)abstractFurnaceBlockEntityAccessor.aether$getItems().getFirst()), this.level)
         .map(recipe -> ((AbstractCookingRecipe)recipe.value()).getIngredients());
      if (this.remainderItem.isEmpty()) {
         ingredient.ifPresent(ing -> this.remainderItem = stack.getCraftingRemainingItem());
      }

      if (direction != Direction.DOWN || index != 0) {
         return true;
      } else {
         return !this.remainderItem.isEmpty() ? stack.is(this.remainderItem.getItem()) : false;
      }
   }

   public void setItem(int index, ItemStack stack) {
      super.setItem(index, stack);
      if (this.getLevel() != null) {
         this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
      }
   }

   public void handleUpdateTag(CompoundTag tag, Provider registry) {
      this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      ContainerHelper.loadAllItems(tag, this.items, registry);
   }

   public CompoundTag getUpdateTag(Provider registry) {
      CompoundTag tag = super.getUpdateTag(registry);
      ContainerHelper.saveAllItems(tag, this.items, registry);
      return tag;
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }
}
