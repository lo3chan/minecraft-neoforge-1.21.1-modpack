package vectorwing.farmersdelight.common.block.entity;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.DataComponentMap.Builder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeManager.CachedCheck;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity.DataComponentInput;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import vectorwing.farmersdelight.common.block.CookingPotBlock;
import vectorwing.farmersdelight.common.block.entity.container.CookingPotMenu;
import vectorwing.farmersdelight.common.block.entity.inventory.CookingPotItemHandler;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;
import vectorwing.farmersdelight.common.item.component.ItemStackWrapper;
import vectorwing.farmersdelight.common.registry.ModBlockEntityTypes;
import vectorwing.farmersdelight.common.registry.ModDataComponents;
import vectorwing.farmersdelight.common.registry.ModItems;
import vectorwing.farmersdelight.common.registry.ModParticleTypes;
import vectorwing.farmersdelight.common.registry.ModRecipeTypes;
import vectorwing.farmersdelight.common.utility.ItemUtils;
import vectorwing.farmersdelight.common.utility.TextUtils;

@EventBusSubscriber(
   modid = "farmersdelight"
)
public class CookingPotBlockEntity extends SyncedBlockEntity implements MenuProvider, HeatableBlockEntity, Nameable, RecipeCraftingHolder, Clearable {
   public static final int MEAL_DISPLAY_SLOT = 6;
   public static final int CONTAINER_SLOT = 7;
   public static final int OUTPUT_SLOT = 8;
   public static final int INVENTORY_SIZE = 9;
   public static final Map<Item, Item> INGREDIENT_REMAINDER_OVERRIDES = Map.ofEntries(
      Map.entry(Items.POWDER_SNOW_BUCKET, Items.BUCKET),
      Map.entry(Items.AXOLOTL_BUCKET, Items.BUCKET),
      Map.entry(Items.COD_BUCKET, Items.BUCKET),
      Map.entry(Items.PUFFERFISH_BUCKET, Items.BUCKET),
      Map.entry(Items.SALMON_BUCKET, Items.BUCKET),
      Map.entry(Items.TROPICAL_FISH_BUCKET, Items.BUCKET),
      Map.entry(Items.SUSPICIOUS_STEW, Items.BOWL),
      Map.entry(Items.MUSHROOM_STEW, Items.BOWL),
      Map.entry(Items.RABBIT_STEW, Items.BOWL),
      Map.entry(Items.BEETROOT_SOUP, Items.BOWL),
      Map.entry(Items.POTION, Items.GLASS_BOTTLE),
      Map.entry(Items.SPLASH_POTION, Items.GLASS_BOTTLE),
      Map.entry(Items.LINGERING_POTION, Items.GLASS_BOTTLE),
      Map.entry(Items.EXPERIENCE_BOTTLE, Items.GLASS_BOTTLE)
   );
   private final ItemStackHandler inventory = this.createHandler();
   private final IItemHandler inputHandler = new CookingPotItemHandler(this.inventory, Direction.UP);
   private final IItemHandler outputHandler = new CookingPotItemHandler(this.inventory, Direction.DOWN);
   private int cookTime;
   private int cookTimeTotal;
   private ItemStack mealContainerStack = ItemStack.EMPTY;
   private Component customName;
   protected final ContainerData cookingPotData = this.createIntArray();
   private final Object2IntOpenHashMap<ResourceLocation> usedRecipeTracker = new Object2IntOpenHashMap();
   private final CachedCheck<RecipeWrapper, CookingPotRecipe> quickCheck = RecipeManager.createCheck(ModRecipeTypes.COOKING.get());

   public CookingPotBlockEntity(BlockPos pos, BlockState state) {
      super(ModBlockEntityTypes.COOKING_POT.get(), pos, state);
   }

   @SubscribeEvent
   public static void registerCapabilities(RegisterCapabilitiesEvent event) {
      event.registerBlockEntity(
         ItemHandler.BLOCK, ModBlockEntityTypes.COOKING_POT.get(), (be, context) -> context == Direction.UP ? be.inputHandler : be.outputHandler
      );
   }

   public static ItemStack getMealFromItem(ItemStack cookingPotStack) {
      return !cookingPotStack.is(ModItems.COOKING_POT.get())
         ? ItemStack.EMPTY
         : ((ItemStackWrapper)cookingPotStack.getOrDefault(ModDataComponents.MEAL, ItemStackWrapper.EMPTY)).getStack();
   }

   public static void takeServingFromItem(ItemStack cookingPotStack) {
      if (cookingPotStack.is(ModItems.COOKING_POT.get())) {
         ItemStack mealStack = ((ItemStackWrapper)cookingPotStack.getOrDefault(ModDataComponents.MEAL, ItemStackWrapper.EMPTY)).getStack();
         mealStack.shrink(1);
         cookingPotStack.set(ModDataComponents.MEAL, new ItemStackWrapper(mealStack));
      }
   }

   public static ItemStack getContainerFromItem(ItemStack cookingPotStack) {
      return !cookingPotStack.is(ModItems.COOKING_POT.get())
         ? ItemStack.EMPTY
         : ((ItemStackWrapper)cookingPotStack.getOrDefault((DataComponentType)ModDataComponents.CONTAINER.get(), ItemStackWrapper.EMPTY)).getStack();
   }

   public void loadAdditional(CompoundTag compound, Provider registries) {
      super.loadAdditional(compound, registries);
      this.inventory.deserializeNBT(registries, compound.getCompound("Inventory"));
      this.cookTime = compound.getInt("CookTime");
      this.cookTimeTotal = compound.getInt("CookTimeTotal");
      this.mealContainerStack = ItemStack.parseOptional(registries, compound.getCompound("Container"));
      if (compound.contains("CustomName", 8)) {
         this.customName = Serializer.fromJson(compound.getString("CustomName"), registries);
      }

      CompoundTag compoundRecipes = compound.getCompound("RecipesUsed");

      for (String key : compoundRecipes.getAllKeys()) {
         this.usedRecipeTracker.put(ResourceLocation.parse(key), compoundRecipes.getInt(key));
      }
   }

   public void saveAdditional(CompoundTag compound, Provider registries) {
      super.saveAdditional(compound, registries);
      compound.putInt("CookTime", this.cookTime);
      compound.putInt("CookTimeTotal", this.cookTimeTotal);
      compound.put("Container", this.mealContainerStack.saveOptional(registries));
      if (this.customName != null) {
         compound.putString("CustomName", Serializer.toJson(this.customName, registries));
      }

      compound.put("Inventory", this.inventory.serializeNBT(registries));
      CompoundTag compoundRecipes = new CompoundTag();
      this.usedRecipeTracker.forEach((recipeId, craftedAmount) -> compoundRecipes.putInt(recipeId.toString(), craftedAmount));
      compound.put("RecipesUsed", compoundRecipes);
   }

   private CompoundTag writeItems(CompoundTag compound, Provider registries) {
      super.saveAdditional(compound, registries);
      compound.put("Container", this.mealContainerStack.saveOptional(registries));
      compound.put("Inventory", this.inventory.serializeNBT(registries));
      return compound;
   }

   public CompoundTag writeMeal(CompoundTag compound, Provider registries) {
      if (this.getMeal().isEmpty()) {
         return compound;
      } else {
         ItemStackHandler drops = new ItemStackHandler(9);

         for (int i = 0; i < 9; i++) {
            drops.setStackInSlot(i, i == 6 ? this.inventory.getStackInSlot(i) : ItemStack.EMPTY);
         }

         if (this.customName != null) {
            compound.putString("CustomName", Serializer.toJson(this.customName, registries));
         }

         compound.put("Container", this.mealContainerStack.save(registries));
         compound.put("Inventory", drops.serializeNBT(registries));
         return compound;
      }
   }

   public ItemStack getAsItem() {
      ItemStack stack = new ItemStack((ItemLike)ModItems.COOKING_POT.get());
      stack.applyComponents(this.collectComponents());
      return stack;
   }

   public static void cookingTick(Level level, BlockPos pos, BlockState state, CookingPotBlockEntity cookingPot) {
      boolean isHeated = cookingPot.isHeated(level, pos);
      boolean didInventoryChange = false;
      if (isHeated && cookingPot.hasInput()) {
         Optional<RecipeHolder<CookingPotRecipe>> recipe = cookingPot.getMatchingRecipe(new RecipeWrapper(cookingPot.inventory));
         if (recipe.isPresent() && cookingPot.canCook((CookingPotRecipe)recipe.get().value())) {
            didInventoryChange = cookingPot.processCooking(recipe.get(), cookingPot);
         } else {
            cookingPot.cookTime = Mth.clamp(cookingPot.cookTime - 2, 0, cookingPot.cookTimeTotal);
         }
      } else if (cookingPot.cookTime > 0) {
         cookingPot.cookTime = Mth.clamp(cookingPot.cookTime - 2, 0, cookingPot.cookTimeTotal);
      }

      ItemStack mealStack = cookingPot.getMeal();
      if (!mealStack.isEmpty()) {
         if (!cookingPot.doesMealHaveContainer(mealStack)) {
            cookingPot.moveMealToOutput();
            didInventoryChange = true;
         } else if (!cookingPot.inventory.getStackInSlot(7).isEmpty()) {
            cookingPot.useStoredContainersOnMeal();
            didInventoryChange = true;
         }
      }

      if (didInventoryChange) {
         cookingPot.inventoryChanged();
      }
   }

   public static void animationTick(Level level, BlockPos pos, BlockState state, CookingPotBlockEntity cookingPot) {
      if (cookingPot.isHeated(level, pos)) {
         RandomSource random = level.random;
         if (random.nextFloat() < 0.2F) {
            double x = pos.getX() + 0.5 + (random.nextDouble() * 0.6 - 0.3);
            double y = pos.getY() + 0.7;
            double z = pos.getZ() + 0.5 + (random.nextDouble() * 0.6 - 0.3);
            level.addParticle(ParticleTypes.BUBBLE_POP, x, y, z, 0.0, 0.0, 0.0);
         }

         if (random.nextFloat() < 0.05F) {
            double x = pos.getX() + 0.5 + (random.nextDouble() * 0.4 - 0.2);
            double y = pos.getY() + 0.5;
            double z = pos.getZ() + 0.5 + (random.nextDouble() * 0.4 - 0.2);
            double motionY = random.nextBoolean() ? 0.015 : 0.005;
            level.addParticle((ParticleOptions)ModParticleTypes.STEAM.get(), x, y, z, 0.0, motionY, 0.0);
         }
      }
   }

   private Optional<RecipeHolder<CookingPotRecipe>> getMatchingRecipe(RecipeWrapper inventoryWrapper) {
      if (this.level == null) {
         return Optional.empty();
      } else {
         return this.hasInput() ? this.quickCheck.getRecipeFor(inventoryWrapper, this.level) : Optional.empty();
      }
   }

   public ItemStack getContainer() {
      ItemStack mealStack = this.getMeal();
      return !mealStack.isEmpty() && !this.mealContainerStack.isEmpty() ? this.mealContainerStack : mealStack.getCraftingRemainingItem();
   }

   private boolean hasInput() {
      for (int i = 0; i < 6; i++) {
         if (!this.inventory.getStackInSlot(i).isEmpty()) {
            return true;
         }
      }

      return false;
   }

   protected boolean canCook(CookingPotRecipe recipe) {
      if (this.level == null) {
         return false;
      } else if (this.hasInput()) {
         ItemStack resultStack = recipe.assemble(new RecipeWrapper(this.inventory), this.level.registryAccess());
         if (resultStack.isEmpty()) {
            return false;
         } else {
            ItemStack storedMealStack = this.inventory.getStackInSlot(6);
            if (storedMealStack.isEmpty()) {
               return true;
            } else if (!ItemStack.isSameItemSameComponents(storedMealStack, resultStack)) {
               return false;
            } else {
               return storedMealStack.getCount() + resultStack.getCount() <= Math.max(64, storedMealStack.getMaxStackSize())
                  ? true
                  : storedMealStack.getCount() + resultStack.getCount() <= resultStack.getMaxStackSize();
            }
         }
      } else {
         return false;
      }
   }

   private boolean processCooking(RecipeHolder<CookingPotRecipe> recipe, CookingPotBlockEntity cookingPot) {
      if (this.level == null) {
         return false;
      } else {
         this.cookTime++;
         this.cookTimeTotal = ((CookingPotRecipe)recipe.value()).getCookTime();
         if (this.cookTime < this.cookTimeTotal) {
            return false;
         } else {
            this.cookTime = 0;
            this.mealContainerStack = ((CookingPotRecipe)recipe.value()).getOutputContainer();
            ItemStack resultStack = ((CookingPotRecipe)recipe.value()).assemble(new RecipeWrapper(this.inventory), this.level.registryAccess());
            ItemStack storedMealStack = this.inventory.getStackInSlot(6);
            if (storedMealStack.isEmpty()) {
               this.inventory.setStackInSlot(6, resultStack.copy());
            } else if (ItemStack.isSameItemSameComponents(storedMealStack, resultStack)) {
               storedMealStack.grow(resultStack.getCount());
            }

            cookingPot.setRecipeUsed(recipe);

            for (int i = 0; i < 6; i++) {
               ItemStack slotStack = this.inventory.getStackInSlot(i);
               if (slotStack.hasCraftingRemainingItem()) {
                  this.ejectIngredientRemainder(slotStack.getCraftingRemainingItem());
               } else if (INGREDIENT_REMAINDER_OVERRIDES.containsKey(slotStack.getItem())) {
                  this.ejectIngredientRemainder(INGREDIENT_REMAINDER_OVERRIDES.get(slotStack.getItem()).getDefaultInstance());
               }

               if (!slotStack.isEmpty()) {
                  slotStack.shrink(1);
               }
            }

            return true;
         }
      }
   }

   protected void ejectIngredientRemainder(ItemStack remainderStack) {
      Direction direction = ((Direction)this.getBlockState().getValue(CookingPotBlock.FACING)).getCounterClockWise();
      double x = this.worldPosition.getX() + 0.5 + direction.getStepX() * 0.25;
      double y = this.worldPosition.getY() + 0.7;
      double z = this.worldPosition.getZ() + 0.5 + direction.getStepZ() * 0.25;
      ItemUtils.spawnItemEntity(this.level, remainderStack, x, y, z, direction.getStepX() * 0.08F, 0.25, direction.getStepZ() * 0.08F);
   }

   public void setRecipeUsed(@Nullable RecipeHolder<?> recipe) {
      if (recipe != null) {
         ResourceLocation recipeID = recipe.id();
         this.usedRecipeTracker.addTo(recipeID, 1);
      }
   }

   @Nullable
   public RecipeHolder<?> getRecipeUsed() {
      return null;
   }

   public void awardUsedRecipes(Player player, List<ItemStack> items) {
      List<RecipeHolder<?>> usedRecipes = this.getUsedRecipesAndPopExperience(player.level(), player.position());
      player.awardRecipes(usedRecipes);
      this.usedRecipeTracker.clear();
   }

   public List<RecipeHolder<?>> getUsedRecipesAndPopExperience(Level level, Vec3 pos) {
      List<RecipeHolder<?>> list = Lists.newArrayList();
      ObjectIterator var4 = this.usedRecipeTracker.object2IntEntrySet().iterator();

      while (var4.hasNext()) {
         Entry<ResourceLocation> entry = (Entry<ResourceLocation>)var4.next();
         level.getRecipeManager().byKey((ResourceLocation)entry.getKey()).ifPresent(recipe -> {
            list.add((RecipeHolder<?>)recipe);
            splitAndSpawnExperience((ServerLevel)level, pos, entry.getIntValue(), ((CookingPotRecipe)recipe.value()).getExperience());
         });
      }

      return list;
   }

   private static void splitAndSpawnExperience(ServerLevel level, Vec3 pos, int craftedAmount, float experience) {
      int expTotal = Mth.floor(craftedAmount * experience);
      float expFraction = Mth.frac(craftedAmount * experience);
      if (expFraction != 0.0F && Math.random() < expFraction) {
         expTotal++;
      }

      ExperienceOrb.award(level, pos, expTotal);
   }

   public boolean isHeated() {
      return this.level == null ? false : this.isHeated(this.level, this.worldPosition);
   }

   public ItemStackHandler getInventory() {
      return this.inventory;
   }

   public ItemStack getMeal() {
      return this.inventory.getStackInSlot(6);
   }

   public NonNullList<ItemStack> getDroppableInventory() {
      NonNullList<ItemStack> drops = NonNullList.create();

      for (int i = 0; i < 9; i++) {
         if (i != 6) {
            drops.add(this.inventory.getStackInSlot(i));
         }
      }

      return drops;
   }

   private void moveMealToOutput() {
      ItemStack mealStack = this.inventory.getStackInSlot(6);
      ItemStack outputStack = this.inventory.getStackInSlot(8);
      int mealCount = Math.min(mealStack.getCount(), mealStack.getMaxStackSize() - outputStack.getCount());
      if (outputStack.isEmpty()) {
         this.inventory.setStackInSlot(8, mealStack.split(mealCount));
      } else if (ItemStack.isSameItemSameComponents(mealStack, outputStack)) {
         mealStack.shrink(mealCount);
         outputStack.grow(mealCount);
      }
   }

   private void useStoredContainersOnMeal() {
      ItemStack mealStack = this.inventory.getStackInSlot(6);
      ItemStack containerInputStack = this.inventory.getStackInSlot(7);
      ItemStack outputStack = this.inventory.getStackInSlot(8);
      if (this.isContainerValid(containerInputStack) && outputStack.getCount() < outputStack.getMaxStackSize()) {
         int smallerStackCount = Math.min(mealStack.getCount(), containerInputStack.getCount());
         int mealCount = Math.min(smallerStackCount, mealStack.getMaxStackSize() - outputStack.getCount());
         if (outputStack.isEmpty()) {
            containerInputStack.shrink(mealCount);
            this.inventory.setStackInSlot(8, mealStack.split(mealCount));
         } else if (ItemStack.isSameItemSameComponents(outputStack, mealStack)) {
            mealStack.shrink(mealCount);
            containerInputStack.shrink(mealCount);
            outputStack.grow(mealCount);
         }
      }
   }

   public ItemStack useHeldItemOnMeal(ItemStack container) {
      if (this.isContainerValid(container) && !this.getMeal().isEmpty()) {
         container.shrink(1);
         this.inventoryChanged();
         return this.getMeal().split(1);
      } else {
         return ItemStack.EMPTY;
      }
   }

   private boolean doesMealHaveContainer(ItemStack meal) {
      return !this.mealContainerStack.isEmpty() || meal.hasCraftingRemainingItem();
   }

   public boolean isContainerValid(ItemStack containerItem) {
      if (containerItem.isEmpty()) {
         return false;
      } else {
         return !this.mealContainerStack.isEmpty() ? ItemStack.isSameItem(this.mealContainerStack, containerItem) : false;
      }
   }

   public Component getName() {
      return (Component)(this.customName != null ? this.customName : TextUtils.container("cooking_pot"));
   }

   public Component getDisplayName() {
      return this.getName();
   }

   @Nullable
   public Component getCustomName() {
      return this.customName;
   }

   public AbstractContainerMenu createMenu(int id, Inventory player, Player entity) {
      return new CookingPotMenu(id, player, this, this.cookingPotData);
   }

   public void setRemoved() {
      super.setRemoved();
   }

   @Override
   public CompoundTag getUpdateTag(Provider registries) {
      return this.writeItems(new CompoundTag(), registries);
   }

   protected void applyImplicitComponents(DataComponentInput componentInput) {
      super.applyImplicitComponents(componentInput);
      this.customName = (Component)componentInput.get(DataComponents.CUSTOM_NAME);
      this.getInventory().setStackInSlot(6, ((ItemStackWrapper)componentInput.getOrDefault(ModDataComponents.MEAL, ItemStackWrapper.EMPTY)).getStack());
      this.mealContainerStack = ((ItemStackWrapper)componentInput.getOrDefault(ModDataComponents.CONTAINER, ItemStackWrapper.EMPTY)).getStack();
   }

   protected void collectImplicitComponents(Builder components) {
      super.collectImplicitComponents(components);
      components.set(DataComponents.CUSTOM_NAME, this.customName);
      if (!this.getMeal().isEmpty()) {
         components.set(ModDataComponents.MEAL, new ItemStackWrapper(this.getMeal()));
      }

      if (!this.getContainer().isEmpty()) {
         components.set(ModDataComponents.CONTAINER, new ItemStackWrapper(this.getContainer()));
      }
   }

   public void removeComponentsFromTag(CompoundTag tag) {
      tag.remove("CustomName");
      tag.remove("meal");
      tag.remove("container");
   }

   private ItemStackHandler createHandler() {
      return new ItemStackHandler(9) {
         protected int getStackLimit(int slot, ItemStack stack) {
            return slot == 6 ? Math.max(64, stack.getMaxStackSize()) : super.getStackLimit(slot, stack);
         }

         protected void onContentsChanged(int slot) {
            CookingPotBlockEntity.this.inventoryChanged();
         }
      };
   }

   private ContainerData createIntArray() {
      return new ContainerData() {
         public int get(int index) {
            return switch (index) {
               case 0 -> CookingPotBlockEntity.this.cookTime;
               case 1 -> CookingPotBlockEntity.this.cookTimeTotal;
               default -> 0;
            };
         }

         public void set(int index, int value) {
            switch (index) {
               case 0:
                  CookingPotBlockEntity.this.cookTime = value;
                  break;
               case 1:
                  CookingPotBlockEntity.this.cookTimeTotal = value;
            }
         }

         public int getCount() {
            return 2;
         }
      };
   }

   public void clearContent() {
      ItemUtils.clearItems(this.inventory);
   }
}
