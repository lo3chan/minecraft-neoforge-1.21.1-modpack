package com.alonie.brbe.smithingtable;

import com.alonie.brbe.BetterRecipeBook;
import com.alonie.brbe.api.BRBBookCategories;
import com.alonie.brbe.api.BRBBookSettings;
import com.alonie.brbe.generic.GenericRecipeBookComponent;
import com.alonie.brbe.recipe.BRBSmithingRecipe;
import com.alonie.brbe.recipe.smithing.BRBSmithingTransformRecipe;
import com.alonie.brbe.recipe.smithing.BRBSmithingTrimRecipe;
import com.alonie.brbe.util.BRBHelper;
import com.alonie.brbe.util.ClientInventoryUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;

public class SmithingRecipeBookComponent extends GenericRecipeBookComponent<SmithingMenu, SmithingRecipeCollection, BRBSmithingRecipe> {
   private static final MutableComponent ONLY_CRAFTABLES_TOOLTIP = Component.translatable("brb.gui.smithable");

   public void init(
      int width,
      int height,
      Minecraft minecraft,
      boolean widthNarrow,
      SmithingMenu menu,
      Consumer<ItemStack> onGhostRecipeUpdate,
      RegistryAccess registryAccess,
      RecipeManager recipeManager
   ) {
      this.recipeManager = recipeManager;
      this.ghostRecipe = new SmithingGhostRecipe(onGhostRecipeUpdate, registryAccess);
      this.ghostRecipe.setDefaultRenderingPredicate((SmithingMenu & AbstractContainerMenu)this.menu);
      this.recipesPage = new SmithingRecipeBookPage(registryAccess, () -> BRBBookSettings.isFiltering(this.getRecipeBookType()));
      super.init(width, height, minecraft, widthNarrow, menu, onGhostRecipeUpdate, registryAccess);
   }

   @Override
   public Component getRecipeFilterName() {
      return ONLY_CRAFTABLES_TOOLTIP;
   }

   @Override
   public BRBHelper.Book getRecipeBookType() {
      return BetterRecipeBook.SMITHING;
   }

   @Override
   public void handlePlaceRecipe() {
      BRBSmithingRecipe result = this.recipesPage.getCurrentClickedRecipe();
      SmithingRecipeCollection recipeCollection = (SmithingRecipeCollection)this.recipesPage.getLastClickedRecipeCollection();
      if (result != null && recipeCollection != null) {
         this.ghostRecipe.clear();
         if (!result.hasMaterials(this.menu.slots, this.registryAccess)) {
            this.setupGhostRecipe(result, this.menu.slots);
         } else {
            int slotIndex = 0;
            boolean placedBase = false;

            for (Slot slot : this.menu.slots) {
               ItemStack itemStack = slot.getItem();
               if (result.getTemplate().test(itemStack)) {
                  ClientInventoryUtil.moveItemToSlot(this.menu, slotIndex, 0);
               } else if (!placedBase && !itemStack.has(DataComponents.TRIM) && result.getBase().getItem().equals(itemStack.getItem())) {
                  ClientInventoryUtil.moveItemToSlot(this.menu, slotIndex, 1);
                  placedBase = true;
               } else if (result.getAddition().test(itemStack)) {
                  ClientInventoryUtil.moveItemToSlot(this.menu, slotIndex, 2);
               }

               slotIndex++;
            }

            this.updateCollections(false);
         }
      }
   }

   public void setupGhostRecipe(BRBSmithingRecipe result, List<Slot> list) {
      this.ghostRecipe.setRecipe(result);
      this.ghostRecipe.addIngredient(2, result.getAddition(), 44, 48);
      this.ghostRecipe.addIngredient(0, result.getTemplate(), 8, 48);
      this.ghostRecipe.addIngredient(1, Ingredient.of(new ItemStack[]{result.getBase()}), 26, 48);
   }

   public boolean isShowingGhostRecipe() {
      return this.ghostRecipe != null && this.ghostRecipe.size() > 0;
   }

   @Override
   protected List<SmithingRecipeCollection> getCollectionsForCategory() {
      List<RecipeHolder<SmithingRecipe>> recipes = this.recipeManager.getAllRecipesFor(RecipeType.SMITHING);
      List<SmithingRecipeCollection> results = new ArrayList<>();
      BRBBookCategories.Category category = this.selectedTab.getCategory();

      for (RecipeHolder<SmithingRecipe> recipe : recipes) {
         SmithingRecipe value = (SmithingRecipe)recipe.value();
         if (category == BetterRecipeBook.SMITHING_SEARCH) {
            if (value instanceof SmithingTransformRecipe) {
               results.add(
                  new SmithingRecipeCollection(
                     List.of(BRBSmithingTransformRecipe.from((SmithingTransformRecipe)value, this.registryAccess)), this.menu, this.registryAccess
                  )
               );
            } else if (value instanceof SmithingTrimRecipe) {
               results.add(new SmithingRecipeCollection(BRBSmithingTrimRecipe.from((SmithingTrimRecipe)value), this.menu, this.registryAccess));
            }
         } else if (category == BetterRecipeBook.SMITHING_TRANSFORM) {
            if (value instanceof SmithingTransformRecipe) {
               results.add(
                  new SmithingRecipeCollection(
                     List.of(BRBSmithingTransformRecipe.from((SmithingTransformRecipe)value, this.registryAccess)), this.menu, this.registryAccess
                  )
               );
            }
         } else if (value instanceof SmithingTrimRecipe) {
            results.add(new SmithingRecipeCollection(BRBSmithingTrimRecipe.from((SmithingTrimRecipe)value), this.menu, this.registryAccess));
         }
      }

      return results;
   }
}
