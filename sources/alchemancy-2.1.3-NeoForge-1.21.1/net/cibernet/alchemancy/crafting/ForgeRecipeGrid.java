package net.cibernet.alchemancy.crafting;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import net.cibernet.alchemancy.blocks.AlchemancyForgeBlock;
import net.cibernet.alchemancy.blocks.InfusionPedestalBlock;
import net.cibernet.alchemancy.blocks.blockentities.ItemStackHolderBlockEntity;
import net.cibernet.alchemancy.item.components.InfusedPropertiesComponent;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyBlocks;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyRecipeTypes;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.RecipeManager.CachedCheck;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ForgeRecipeGrid implements RecipeInput {
   private final ArrayList<ItemStackHolderBlockEntity> items = new ArrayList<>();
   private final ArrayList<Object> slotOrder = new ArrayList<>();
   private final ItemStackHolderBlockEntity forge;
   private ItemStack currentOutput;
   private Optional<Boolean> performingTransmutation = Optional.empty();
   public Optional<Boolean> applyGlint = Optional.empty();
   private final ArrayList<Holder<Property>> warpResults = new ArrayList<>();
   private static final CachedCheck<ForgeRecipeGrid, AbstractForgeRecipe<?>> OUT_OF_FORGE_INTERACTIONS_CHECK = (input, level) -> level.getRecipeManager()
      .getRecipesFor((RecipeType)AlchemancyRecipeTypes.ALCHEMANCY_FORGE.get(), input, level)
      .stream()
      .filter(recipe -> ((AbstractForgeRecipe)recipe.value()).matches(input, level) && !((AbstractForgeRecipe)recipe.value()).isTransmutation())
      .min(Comparator.comparingInt(recipe -> ((AbstractForgeRecipe)recipe.value()).getRecipeCompareValue(input)));
   private final Pair<Integer, Integer>[] slotOffsets = new Pair[]{
      new Pair(1, 0), new Pair(1, 1), new Pair(0, 1), new Pair(-1, 1), new Pair(-1, 0), new Pair(-1, -1), new Pair(0, -1), new Pair(1, -1)
   };
   private List<Holder<Property>> cachedDormantProperties = null;

   public static ItemStack resolveInteractions(ItemStack input, Level level) {
      ItemStack gridInput = input.copy();
      gridInput.setCount(1);
      ForgeRecipeGrid grid = new ForgeRecipeGrid(gridInput);

      for (int i = 0; i < 128; i++) {
         Optional<RecipeHolder<AbstractForgeRecipe<?>>> recipe = OUT_OF_FORGE_INTERACTIONS_CHECK.getRecipeFor(grid, level);
         if (!recipe.isPresent()) {
            break;
         }

         grid.processRecipe((AbstractForgeRecipe<?>)recipe.get().value(), level.registryAccess());
      }

      ItemStack result = grid.getCurrentOutput();
      if (!result.isEmpty()) {
         result.setCount(result.getCount() * input.getCount());
      }

      return InfusedPropertiesHelper.truncateProperties(result);
   }

   public ItemStack getItem(int index) {
      return this.items.size() <= index ? ItemStack.EMPTY : this.items.get(0).getItem(0);
   }

   public ForgeRecipeGrid(ItemStack stack) {
      this.forge = new ItemStackHolderBlockEntity(BlockPos.ZERO, ((AlchemancyForgeBlock)AlchemancyBlocks.ALCHEMANCY_FORGE.get()).defaultBlockState());
      this.forge.setItem(stack);
      this.currentOutput = stack.copy();
   }

   public ForgeRecipeGrid(Level level, BlockPos pos, ItemStackHolderBlockEntity forge) {
      this.forge = forge;
      this.currentOutput = forge.getItem().copy();
      this.currentOutput.setCount(1);

      for (Pair<Integer, Integer> offset : this.slotOffsets) {
         Direction direction = (Direction)forge.getBlockState().getValue(InfusionPedestalBlock.FACING);
         BlockPos lookupPos = pos.relative(direction, (Integer)offset.getFirst()).relative(direction.getClockWise(), (Integer)offset.getSecond());
         BlockEntity lookupBlockEntity = level.getBlockEntity(lookupPos);
         BlockState lookupState = level.getBlockState(lookupPos);
         if (lookupState.is(AlchemancyBlocks.INFUSION_PEDESTAL) && lookupBlockEntity instanceof ItemStackHolderBlockEntity pedestal) {
            this.items.add(pedestal);
            this.slotOrder.add(pedestal);
         }
      }
   }

   public int size() {
      return this.items.size() + (this.forge.getItem().isEmpty() ? 0 : 1);
   }

   public boolean isEmpty() {
      return this.forge.isEmpty() && this.areIngredientsEmpty();
   }

   public boolean areIngredientsEmpty() {
      for (ItemStackHolderBlockEntity pedestal : this.items) {
         if (!pedestal.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public ItemStack getCurrentOutput() {
      return this.currentOutput;
   }

   public ItemStackHolderBlockEntity getForge() {
      return this.forge;
   }

   public int getSlot(Object o) {
      return this.slotOrder.indexOf(o);
   }

   public int getSlotFor(Ingredient infusable) {
      for (ItemStackHolderBlockEntity item : this.items) {
         if (infusable.test(item.getItem())) {
            return this.getSlot(item);
         }
      }

      return -1;
   }

   public int getRecipeCompareValue(AbstractForgeRecipe<?> recipe, List<Ingredient> infusables, List<Holder<Property>> properties, int priority) {
      int slots = 0;
      int slotValue = 0;
      if (!infusables.isEmpty()) {
         ArrayList<ItemStackHolderBlockEntity> items = new ArrayList<>(this.items);

         for (Ingredient infusable : infusables) {
            int i = 0;

            for (ItemStackHolderBlockEntity item : items) {
               if (infusable.test(item.getItem())) {
                  slots++;
                  items.remove(i);
                  slotValue += this.getSlot(item);
                  break;
               }

               i++;
            }
         }
      }

      int infusionValue = 0;
      if (!properties.isEmpty()) {
         List<Holder<Property>> infusions = InfusedPropertiesHelper.getInfusedProperties(this.getCurrentOutput())
            .stream()
            .sorted(Comparator.comparingInt(p -> ((Property)p.value()).getPriority()))
            .toList();

         for (int i = 0; i < infusions.size(); i++) {
            Holder<Property> infusion = infusions.get(i);
            infusionValue += properties.contains(infusion) ? 0 : i + 1;
         }
      }

      return (priority - AbstractForgeRecipe.MIN_PRIORITY << 9) + (8 - slots << 6) + Mth.clamp(slotValue, 0, 36) + infusionValue;
   }

   public ArrayList<ItemStackHolderBlockEntity> getItemPedestals() {
      return this.items;
   }

   public boolean consumeItem(ItemStackHolderBlockEntity pedestal) {
      if (!this.slotOrder.contains(pedestal)) {
         return false;
      } else {
         ItemStack stack = pedestal.getItem();
         if (stack.hasFoil()) {
            this.applyGlint = Optional.of(true);
         } else if (stack.is(AlchemancyTags.Items.INFUSION_REMOVES_GLINT)) {
            this.applyGlint = Optional.of(false);
         }

         pedestal.removeItem(1);
         this.markAsProcessed(pedestal);
         this.cachedDormantProperties = null;
         return true;
      }
   }

   public void markAsProcessed(ItemStackHolderBlockEntity pedestal) {
      this.slotOrder.remove(pedestal);
      this.items.remove(pedestal);
   }

   public boolean testInfusables(List<Ingredient> infusables, boolean consume) {
      if (infusables.isEmpty()) {
         return true;
      } else {
         ArrayList<ItemStackHolderBlockEntity> items = consume ? this.items : new ArrayList<>(this.items);

         for (Ingredient infusable : infusables) {
            int i = 0;

            for (ItemStackHolderBlockEntity item : items) {
               ItemStack stack = item.getItem();
               if (infusable.test(stack)) {
                  if (consume) {
                     this.consumeItem(item);
                  }
                  break;
               }

               i++;
            }

            if (i >= items.size()) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean testProperties(List<Holder<Property>> propertiesToTest, boolean consume) {
      if (propertiesToTest.isEmpty()) {
         return true;
      } else if (!this.currentOutput.has(AlchemancyItems.Components.INFUSED_PROPERTIES)) {
         return false;
      } else {
         InfusedPropertiesComponent properties = (InfusedPropertiesComponent)this.currentOutput.get(AlchemancyItems.Components.INFUSED_PROPERTIES);

         for (Holder<Property> propertyHolder : propertiesToTest) {
            if (!properties.hasProperty(propertyHolder)) {
               return false;
            }

            if (consume) {
               InfusedPropertiesHelper.removeProperty(this.currentOutput, propertyHolder);
            }
         }

         return true;
      }
   }

   public void processRecipe(AbstractForgeRecipe<?> recipe, RegistryAccess registryAccess) {
      this.currentOutput = recipe.assemble(this, registryAccess);
      this.performingTransmutation = Optional.of(recipe.isTransmutation());
   }

   public Optional<Boolean> getPerformingTransmutation() {
      return this.performingTransmutation;
   }

   public boolean canPerformTransmutation() {
      return this.performingTransmutation.isEmpty() || this.performingTransmutation.get();
   }

   public boolean isPerformingTransmutation() {
      return this.performingTransmutation.isPresent() && this.performingTransmutation.get();
   }

   public boolean handleDormantRecipes(boolean consume) {
      return this.handleDormantRecipes(this.currentOutput, consume);
   }

   public boolean handleDormantRecipes(ItemStack currentOutput, boolean consume) {
      if (this.forge.getItem().is(AlchemancyTags.Items.IMMUNE_TO_INFUSIONS)) {
         return false;
      } else {
         boolean success = false;

         for (ItemStackHolderBlockEntity pedestal : new ArrayList<>(this.items)) {
            ItemStack stack = pedestal.getItem();
            ItemStack target = !consume ? currentOutput.copy() : currentOutput;
            List<Holder<Property>> properties = AlchemancyProperties.getDormantProperties(stack);
            properties.addAll(
               ((InfusedPropertiesComponent)stack.getOrDefault(AlchemancyItems.Components.STORED_PROPERTIES, InfusedPropertiesComponent.EMPTY)).properties()
            );
            if (!properties.isEmpty()) {
               boolean perform = false;
               AtomicBoolean consumeItem = new AtomicBoolean(true);

               for (Holder<Property> property : List.copyOf(properties)) {
                  if (InfusedPropertiesHelper.canInfuseWithProperty(currentOutput, property)
                     && ((Property)property.value()).onInfusedByDormantProperty(target, stack, this, properties, consume)) {
                     perform = true;
                  }
               }

               if (perform) {
                  if (consume) {
                     properties.removeIf(propertyHolder -> !InfusedPropertiesHelper.canInfuseWithProperty(currentOutput, (Holder<Property>)propertyHolder));
                     InfusedPropertiesHelper.addProperties(target, properties);
                     if (consumeItem.get()) {
                        this.items.remove(pedestal);
                        this.consumeItem(pedestal);
                     }
                  }

                  success = true;
                  break;
               }
            }
         }

         return success;
      }
   }

   public List<Holder<Property>> getDormantProperties() {
      if (this.cachedDormantProperties != null) {
         return this.cachedDormantProperties;
      } else {
         List<Holder<Property>> result = new ArrayList<>();

         for (ItemStackHolderBlockEntity pedestal : new ArrayList<>(this.items)) {
            ItemStack stack = pedestal.getItem();
            ItemStack target = this.currentOutput.copy();
            if (stack.is(AlchemancyTags.Items.REMOVES_INFUSIONS)) {
               result.clear();
            } else {
               List<Holder<Property>> properties = AlchemancyProperties.getDormantProperties(stack);
               properties.addAll(
                  ((InfusedPropertiesComponent)stack.getOrDefault(AlchemancyItems.Components.STORED_PROPERTIES, InfusedPropertiesComponent.EMPTY)).properties()
               );
               if (!properties.isEmpty()) {
                  boolean perform = false;

                  for (Holder<Property> property : properties) {
                     if (((Property)property.value()).onInfusedByDormantProperty(target, stack, this, properties, false)) {
                        perform = true;
                     }
                  }

                  if (perform) {
                     result.addAll(properties);
                  }
               }
            }
         }

         this.cachedDormantProperties = result;
         return result;
      }
   }

   public boolean hasBeenWarped(List<Holder<Property>> properties) {
      for (Holder<Property> property : properties) {
         if (!this.warpResults.contains(property)) {
            return false;
         }
      }

      return true;
   }

   public void consumeWarped(List<Holder<Property>> properties) {
      this.warpResults.addAll(properties);
   }

   public boolean shouldConsumeWarped() {
      return !this.warpResults.isEmpty();
   }
}
