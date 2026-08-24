package net.mehvahdjukaar.amendments.common.recipe;

import com.google.common.base.Preconditions;
import com.google.common.collect.Multimap;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import net.mehvahdjukaar.amendments.reg.ModRegistry;
import net.mehvahdjukaar.moonlight.api.fluids.MLBuiltinSoftFluids;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluid;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluidStack;
import net.mehvahdjukaar.moonlight.api.fluids.FluidContainerList.Category;
import net.minecraft.Util;
import net.minecraft.core.NonNullList;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class CauldronCraftingContainer implements RecipeInput {
   private final List<ItemStack> originalItems;
   private final NonNullList<ItemStack> items;
   private final SoftFluidStack fluid;
   private final int fluidContainerSize;
   private final Multimap<Category, ItemStack> equivalentFluidContainers;
   private final int dimension;
   private final int fluidPosition;
   private final boolean isBoiling;
   private static final Category DUMMY_WATER_BOWL_CATEGORY = (Category)Util.make(
      () -> {
         JsonElement j = JsonParser.parseString(
            "{\n    \"capacity\":"
               + SoftFluid.BOWL_COUNT
               + ",        \"empty\": \"minecraft:bowl\",\n        \"filled\": [\n        \"minecraft:mushroom_stew\"\n        ]\n    }\n"
         );
         return (Category)((Pair)Category.CODEC.decode(JsonOps.INSTANCE, j).getOrThrow()).getFirst();
      }
   );

   private CauldronCraftingContainer(int fluidContainerSize, SoftFluidStack fluidStack, Collection<ItemStack> items, int fluidPosition, boolean isBoiling) {
      this.dimension = Mth.ceil(Math.sqrt(items.size() + 1));
      this.originalItems = List.copyOf(items);
      this.items = NonNullList.withSize(this.dimension * this.dimension, ItemStack.EMPTY);
      this.fluid = fluidStack;
      this.equivalentFluidContainers = fluidStack.toAllPossibleFilledItems();
      if (fluidStack.is(MLBuiltinSoftFluids.WATER)) {
         this.equivalentFluidContainers.put(DUMMY_WATER_BOWL_CATEGORY, Items.BOWL.getDefaultInstance());
      }

      this.fluidPosition = fluidPosition;
      Preconditions.checkArgument(fluidPosition <= items.size());
      this.fluidContainerSize = fluidContainerSize;
      this.isBoiling = isBoiling;
   }

   private Collection<ItemStack> splitItems(Collection<ItemStack> items) {
      return null;
   }

   public static CauldronCraftingContainer surround8(boolean boiling, int fluidContainerSize, SoftFluidStack fluid, ItemStack item) {
      return new CauldronCraftingContainer(fluidContainerSize, fluid, List.of(item, item, item, item, item, item, item, item), 4, boiling);
   }

   public static CauldronCraftingContainer of(boolean boiling, int fluidContainerSize, SoftFluidStack fluid, Collection<ItemStack> items) {
      return new CauldronCraftingContainer(fluidContainerSize, fluid, items, items.size(), boiling);
   }

   public int size() {
      return this.items.size();
   }

   public boolean isEmpty() {
      return this.items.isEmpty();
   }

   public ItemStack getItem(int slot) {
      return slot >= this.size() ? ItemStack.EMPTY : (ItemStack)this.items.get(slot);
   }

   public SoftFluidStack getFluid() {
      return this.fluid;
   }

   public int getMaxAllowedFluidCount() {
      return this.fluidContainerSize;
   }

   protected CraftingInput makeCraftingInput() {
      return CraftingInput.of(this.dimension, this.dimension, this.items);
   }

   @Nullable
   public FluidAndItemCraftResult craftWithCauldronRecipes(Level level) {
      for (int j = 0; j < this.originalItems.size(); j++) {
         this.items.set(j, this.originalItems.get(j));
      }

      for (RecipeHolder<CauldronRecipe> h : level.getRecipeManager().getRecipesFor(ModRegistry.CAULDRON_RECIPE_TYPE.get(), this, level)) {
         CauldronRecipe r = (CauldronRecipe)h.value();
         if (r.matches(this, level)) {
            FluidAndItemCraftResult resultFluid = r.assembleFluid(this, level.registryAccess());
            NonNullList<ItemStack> remainingItems = r.getRemainingItems(this);
            if (remainingItems.stream().allMatch(ItemStack::isEmpty)) {
               return resultFluid;
            }
         }
      }

      return null;
   }

   @Nullable
   public FluidAndItemCraftResult craftWithCraftingRecipes(Level level) {
      for (Entry<Category, ItemStack> cont : this.equivalentFluidContainers.entries()) {
         Category category = cont.getKey();
         ItemStack fluidInBottle = cont.getValue();
         this.setupFluidItem(fluidInBottle);
         CraftingInput input = this.makeCraftingInput();

         for (RecipeHolder<CraftingRecipe> h : level.getRecipeManager().getRecipesFor(RecipeType.CRAFTING, input, level)) {
            CraftingRecipe r = (CraftingRecipe)h.value();
            if (r.matches(input, level)) {
               int newFluidCount = this.fluid.getCount() - category.getCapacity();
               if (newFluidCount >= 0 && newFluidCount <= this.fluidContainerSize) {
                  ItemStack craftedItem = r.assemble(input, level.registryAccess());
                  if (!craftedItem.isEmpty()) {
                     NonNullList<ItemStack> remainingItems = r.getRemainingItems(input);
                     Item emptyContainer = category.getEmptyContainer();
                     if (remainingItems.stream().allMatch(ItemStack::isEmpty)) {
                        if (fluidInBottle.is(Items.LINGERING_POTION)) {
                           return FluidAndItemCraftResult.of(craftedItem, this.fluid.copyWithCount(newFluidCount));
                        }

                        Pair<SoftFluidStack, Category> equivalentFluid = SoftFluidStack.fromItem(craftedItem, level.registryAccess());
                        if (equivalentFluid != null) {
                           Category catt = (Category)equivalentFluid.getSecond();
                           if (catt.getEmptyContainer() == emptyContainer) {
                              SoftFluidStack f = (SoftFluidStack)equivalentFluid.getFirst();
                              return FluidAndItemCraftResult.of(ItemStack.EMPTY, f.copyWithCount(this.fluid.getCount()));
                           }
                        }
                     } else if (remainingItems.stream().allMatch(i -> i.isEmpty() || i.getItem() == emptyContainer)) {
                        return FluidAndItemCraftResult.of(craftedItem, this.fluid.copyWithCount(newFluidCount));
                     }
                  }
               }
            }
         }
      }

      return null;
   }

   private void setupFluidItem(ItemStack filledFluidBottle) {
      this.items.clear();
      int j = 0;

      for (ItemStack item : this.originalItems) {
         if (j == this.fluidPosition) {
            j++;
         }

         this.items.set(j, item);
         j++;
      }

      this.items.set(this.fluidPosition, filledFluidBottle);
   }

   public boolean isBoiling() {
      return this.isBoiling;
   }
}
