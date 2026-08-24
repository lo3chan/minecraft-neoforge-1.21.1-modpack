package com.aetherteam.aether.blockentity;

import com.aetherteam.aether.advancement.AetherAdvancementTriggers;
import com.aetherteam.aether.advancement.IncubationTrigger;
import com.aetherteam.aether.data.resources.registries.AetherDataMaps;
import com.aetherteam.aether.inventory.menu.IncubatorMenu;
import com.aetherteam.aether.recipe.AetherRecipeTypes;
import com.aetherteam.aether.recipe.recipes.item.IncubationRecipe;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
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
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.RecipeManager.CachedCheck;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;

public class IncubatorBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, RecipeCraftingHolder, StackedContentsCompatible {
   private static final int[] SLOTS_NS = new int[]{0};
   private static final int[] SLOTS_EW = new int[]{1};
   protected NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);
   private ServerPlayer player;
   private int litTime;
   private int litDuration;
   private int incubationProgress;
   private int incubationTotalTime;
   private int x;
   private int y;
   private int z;
   protected final ContainerData dataAccess = new ContainerData() {
      public int get(int index) {
         return switch (index) {
            case 0 -> IncubatorBlockEntity.this.litTime;
            case 1 -> IncubatorBlockEntity.this.litDuration;
            case 2 -> IncubatorBlockEntity.this.incubationProgress;
            case 3 -> IncubatorBlockEntity.this.incubationTotalTime;
            case 4 -> IncubatorBlockEntity.this.x;
            case 5 -> IncubatorBlockEntity.this.y;
            case 6 -> IncubatorBlockEntity.this.z;
            default -> 0;
         };
      }

      public void set(int index, int value) {
         switch (index) {
            case 0:
               IncubatorBlockEntity.this.litTime = value;
               break;
            case 1:
               IncubatorBlockEntity.this.litDuration = value;
               break;
            case 2:
               IncubatorBlockEntity.this.incubationProgress = value;
               break;
            case 3:
               IncubatorBlockEntity.this.incubationTotalTime = value;
               break;
            case 4:
               IncubatorBlockEntity.this.x = value;
               break;
            case 5:
               IncubatorBlockEntity.this.y = value;
               break;
            case 6:
               IncubatorBlockEntity.this.z = value;
         }
      }

      public int getCount() {
         return 7;
      }
   };
   private final Object2IntOpenHashMap<ResourceLocation> recipesUsed = new Object2IntOpenHashMap();
   private final CachedCheck<SingleRecipeInput, IncubationRecipe> quickCheck;

   public IncubatorBlockEntity(BlockPos pos, BlockState state) {
      this(pos, state, (RecipeType<IncubationRecipe>)AetherRecipeTypes.INCUBATION.get());
   }

   public IncubatorBlockEntity(BlockPos pos, BlockState state, RecipeType<IncubationRecipe> recipeType) {
      super((BlockEntityType)AetherBlockEntityTypes.INCUBATOR.get(), pos, state);
      this.quickCheck = RecipeManager.createCheck(recipeType);
      this.x = pos.getX();
      this.y = pos.getY();
      this.z = pos.getZ();
   }

   protected AbstractContainerMenu createMenu(int id, Inventory playerInventory) {
      return new IncubatorMenu(id, playerInventory, this, this.dataAccess);
   }

   public static void serverTick(Level level, BlockPos pos, BlockState state, IncubatorBlockEntity blockEntity) {
      boolean flag = blockEntity.isLit();
      boolean flag1 = false;
      if (blockEntity.isLit()) {
         blockEntity.litTime--;
      }

      ItemStack itemstack = (ItemStack)blockEntity.items.get(1);
      ItemStack itemstack1 = (ItemStack)blockEntity.items.get(0);
      boolean flag2 = !itemstack1.isEmpty();
      boolean flag3 = !itemstack.isEmpty();
      if (blockEntity.isLit() || flag3 && flag2) {
         RecipeHolder<IncubationRecipe> recipe;
         if (flag2) {
            recipe = (RecipeHolder<IncubationRecipe>)blockEntity.quickCheck.getRecipeFor(new SingleRecipeInput(itemstack1), level).orElse(null);
         } else {
            recipe = null;
         }

         if (!blockEntity.isLit() && blockEntity.canIncubate(recipe, blockEntity.items)) {
            blockEntity.litTime = blockEntity.getBurnDuration(itemstack);
            blockEntity.litDuration = blockEntity.litTime;
            if (blockEntity.isLit()) {
               flag1 = true;
               if (itemstack.hasCraftingRemainingItem()) {
                  blockEntity.items.set(1, itemstack.getCraftingRemainingItem());
               } else if (flag3) {
                  itemstack.shrink(1);
                  if (itemstack.isEmpty()) {
                     blockEntity.items.set(1, itemstack.getCraftingRemainingItem());
                  }
               }
            }
         }

         if (blockEntity.isLit() && blockEntity.canIncubate(recipe, blockEntity.items)) {
            blockEntity.incubationProgress++;
            if (blockEntity.incubationProgress == blockEntity.incubationTotalTime) {
               blockEntity.incubationProgress = 0;
               blockEntity.incubationTotalTime = getTotalIncubationTime(level, blockEntity);
               if (blockEntity.incubate(recipe, blockEntity.items)) {
                  blockEntity.setRecipeUsed(recipe);
               }

               flag1 = true;
            }
         } else {
            blockEntity.incubationProgress = 0;
         }
      } else if (!blockEntity.isLit() && blockEntity.incubationProgress > 0) {
         blockEntity.incubationProgress = Mth.clamp(blockEntity.incubationProgress - 2, 0, blockEntity.incubationTotalTime);
      }

      if (flag != blockEntity.isLit()) {
         flag1 = true;
         state = (BlockState)state.setValue(AbstractFurnaceBlock.LIT, blockEntity.isLit());
         level.setBlock(pos, state, 3);
      }

      if (flag1) {
         setChanged(level, pos, state);
      }

      if (blockEntity.x != pos.getX()) {
         blockEntity.x = pos.getX();
      }

      if (blockEntity.y != pos.getY()) {
         blockEntity.y = pos.getY();
      }

      if (blockEntity.z != pos.getZ()) {
         blockEntity.z = pos.getZ();
      }
   }

   private boolean incubate(@Nullable RecipeHolder<IncubationRecipe> recipe, NonNullList<ItemStack> stacks) {
      if (recipe != null && this.canIncubate(recipe, stacks)) {
         ItemStack itemStack = (ItemStack)stacks.getFirst();
         EntityType<?> entityType = ((IncubationRecipe)recipe.value()).getEntity();
         BlockPos spawnPos = this.getBlockPos().above();
         if (this.level != null && !this.level.isClientSide() && this.level instanceof ServerLevel serverLevel) {
            CompoundTag tag;
            if (((IncubationRecipe)recipe.value()).getTag().isPresent()) {
               tag = ((IncubationRecipe)recipe.value()).getTag().get();
            } else {
               tag = null;
            }

            Component customName = itemStack.has(DataComponents.CUSTOM_NAME) ? itemStack.getHoverName() : null;
            Entity entity = entityType.spawn(serverLevel, EntityType.appendDefaultStackConfig(consumerEntity -> {
               if (tag != null && consumerEntity instanceof LivingEntity livingEntity) {
                  livingEntity.readAdditionalSaveData(tag);
               }
            }, serverLevel, itemStack, this.player), spawnPos, MobSpawnType.TRIGGERED, true, false);
            if (entity != null) {
               entity.setCustomName(customName);
               if (this.player != null) {
                  ((IncubationTrigger)AetherAdvancementTriggers.INCUBATION_TRIGGER.get()).trigger(this.player, itemStack);
               }
            }
         }

         itemStack.shrink(1);
         return true;
      } else {
         return false;
      }
   }

   private boolean canIncubate(@Nullable RecipeHolder<IncubationRecipe> recipe, NonNullList<ItemStack> stacks) {
      return !((ItemStack)stacks.getFirst()).isEmpty() && recipe != null;
   }

   protected int getBurnDuration(ItemStack fuelStack) {
      if (!fuelStack.isEmpty()) {
         FurnaceFuel datamap = (FurnaceFuel)fuelStack.getItemHolder().getData(AetherDataMaps.INCUBATOR_FUEL);
         if (datamap != null) {
            return datamap.burnTime();
         }
      }

      return 0;
   }

   private static int getTotalIncubationTime(Level level, IncubatorBlockEntity blockEntity) {
      return blockEntity.quickCheck
         .getRecipeFor(new SingleRecipeInput((ItemStack)blockEntity.items.getFirst()), level)
         .map(recipe -> ((IncubationRecipe)recipe.value()).getIncubationTime())
         .orElse(5700);
   }

   private boolean isLit() {
      return this.litTime > 0;
   }

   public void setPlayer(ServerPlayer player) {
      this.player = player;
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

   public void clearContent() {
      this.items.clear();
   }

   public void setItem(int index, ItemStack stack) {
      ItemStack itemstack = (ItemStack)this.items.get(index);
      boolean flag = !stack.isEmpty() && ItemStack.isSameItemSameComponents(itemstack, stack);
      this.items.set(index, stack);
      if (stack.getCount() > this.getMaxStackSize()) {
         stack.setCount(this.getMaxStackSize());
      }

      if (index == 0 && !flag) {
         this.incubationTotalTime = getTotalIncubationTime(this.level, this);
         this.incubationProgress = 0;
         this.setChanged();
      }
   }

   public int getContainerSize() {
      return this.items.size();
   }

   protected NonNullList<ItemStack> getItems() {
      return this.items;
   }

   protected void setItems(NonNullList<ItemStack> items) {
      this.items = items;
   }

   public void fillStackedContents(StackedContents helper) {
      for (ItemStack itemstack : this.items) {
         helper.accountStack(itemstack);
      }
   }

   public int[] getSlotsForFace(Direction direction) {
      return direction != Direction.NORTH && direction != Direction.SOUTH ? SLOTS_EW : SLOTS_NS;
   }

   public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
      return this.canPlaceItem(index, stack);
   }

   public boolean canPlaceItem(int index, ItemStack stack) {
      return index == 1 ? this.getBurnDuration(stack) > 0 : true;
   }

   public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
      return false;
   }

   public void setRecipeUsed(@Nullable RecipeHolder<?> recipe) {
      if (recipe != null) {
         ResourceLocation resourcelocation = recipe.id();
         this.recipesUsed.addTo(resourcelocation, 1);
      }
   }

   @Nullable
   public RecipeHolder<?> getRecipeUsed() {
      return null;
   }

   public void awardUsedRecipes(Player player, List<ItemStack> items) {
   }

   public boolean stillValid(Player player) {
      return this.level.getBlockEntity(this.getBlockPos()) != this
         ? false
         : player.distanceToSqr(this.getBlockPos().getX() + 0.5, this.getBlockPos().getY() + 0.5, this.getBlockPos().getZ() + 0.5) <= 64.0;
   }

   protected Component getDefaultName() {
      return Component.translatable("menu.aether.incubator");
   }

   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      ContainerHelper.loadAllItems(tag, this.items, registries);
      this.litTime = tag.getInt("LitTime");
      this.litDuration = this.getBurnDuration((ItemStack)this.items.get(1));
      this.incubationProgress = tag.getInt("IncubationProgress");
      this.incubationTotalTime = tag.getInt("IncubationTotalTime");
      CompoundTag compoundtag = tag.getCompound("RecipesUsed");

      for (String string : compoundtag.getAllKeys()) {
         this.recipesUsed.put(ResourceLocation.parse(string), compoundtag.getInt(string));
      }
   }

   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      tag.putInt("LitTime", this.litTime);
      tag.putInt("IncubationProgress", this.incubationProgress);
      tag.putInt("IncubationTotalTime", this.incubationTotalTime);
      ContainerHelper.saveAllItems(tag, this.items, registries);
      CompoundTag compoundTag = new CompoundTag();
      this.recipesUsed.forEach((location, integer) -> compoundTag.putInt(location.toString(), integer));
      tag.put("RecipesUsed", compoundTag);
   }
}
