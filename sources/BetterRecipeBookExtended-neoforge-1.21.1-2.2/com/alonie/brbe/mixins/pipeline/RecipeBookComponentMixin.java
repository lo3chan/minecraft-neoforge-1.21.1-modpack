package com.alonie.brbe.mixins.pipeline;

import com.alonie.brbe.BetterRecipeBook;
import com.alonie.brbe.config.AppContext;
import com.alonie.brbe.mixins.accessors.RecipeBookComponentAccessor;
import com.alonie.brbe.mixins.accessors.RecipeCollectionAccessor;
import com.alonie.brbe.util.BrbeDiagnostic;
import com.alonie.brbe.util.BrbeLogger;
import com.alonie.brbe.util.CollectionPipeline;
import com.alonie.brbe.util.PartialCraftingUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeBookTabButton;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({RecipeBookComponent.class})
public abstract class RecipeBookComponentMixin implements RecipeBookComponentAccessor {
   @Shadow
   @Final
   protected Minecraft minecraft;
   @Shadow
   @Final
   protected RecipeBookMenu<?, ?> menu;
   @Shadow
   private ClientRecipeBook book;
   @Shadow
   @Final
   private RecipeBookPage recipeBookPage;

   @Inject(
      method = {"tick"},
      at = {@At("RETURN")}
   )
   private void brbe$refreshOnConfigChange(CallbackInfo ci) {
      if (BetterRecipeBook.DIAGNOSTIC_MAPPING.consumeClick()) {
         BrbeDiagnostic.dump();
      }

      if (AppContext.instance().events().consumeConfigChange()) {
         if (this.getVisible()) {
            BrbeLogger.log(BrbeLogger.Category.RENDER, "configChanged — tick rebuild");
            if (this.minecraft != null && this.minecraft.player != null) {
               this.minecraft.player.getRecipeBook().setFiltering(this.menu.getRecipeBookType(), false);
            }

            this.updateStackedContentsInvoker();
            PartialCraftingUtil.requestForceFullRefresh();
            this.initVisualsInvoker();
         }
      }
   }

   @Inject(
      method = {"initVisuals"},
      at = {@At("TAIL")}
   )
   private void brbe$resetPageAfterInitVisuals(CallbackInfo ci) {
      if (BetterRecipeBook.ctx().config().partialCraftingEnabled && this.minecraft != null && this.minecraft.player != null) {
         this.minecraft.player.getRecipeBook().setFiltering(this.menu.getRecipeBookType(), false);
      }

      RecipeBookTabButton tab = this.getSelectedTab();
      if (tab != null) {
         List<RecipeCollection> collections = new ArrayList<>(this.book.getCollection(tab.getCategory()));
         if (!collections.isEmpty()) {
            BrbeLogger.log(BrbeLogger.Category.STATE, "initVisuals TAIL — %d collections, running pipeline", collections.size());
            this.brbe$runPipeline(this.recipeBookPage, collections, true);
         }
      }
   }

   @Redirect(
      method = {"updateCollections"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeBookPage;updateCollections(Ljava/util/List;Z)V"
      )
   )
   private void brbe$runPipelineRedirect(RecipeBookPage page, List<RecipeCollection> list, boolean resetPageNumber) {
      this.brbe$runPipeline(page, list, resetPageNumber);
   }

   @Unique
   private void brbe$runPipeline(RecipeBookPage page, List<RecipeCollection> list, boolean resetPageNumber) {
      if (list != null && !list.isEmpty()) {
         boolean isFiltering = this.minecraft != null && this.minecraft.player != null && this.minecraft.player.getRecipeBook().isFiltering(this.menu);
         CollectionPipeline.applyPins(list);
         boolean shouldSort = BetterRecipeBook.ctx().config().partialCraftingEnabled || isFiltering;
         boolean hasPartialData = BetterRecipeBook.ctx().config().partialMarkingEnabled;
         if (shouldSort) {
            List<RecipeCollection> sorted = CollectionPipeline.applyPartialSort(list, hasPartialData);
            list.clear();
            list.addAll(sorted);
         }

         boolean onInventory = this.minecraft != null && this.minecraft.screen instanceof InventoryScreen;
         boolean skipFallback = onInventory && !BetterRecipeBook.ctx().config().showAllRecipesInSurvival;
         if (!skipFallback) {
            for (RecipeCollection c : list) {
               RecipeCollectionAccessor ca = (RecipeCollectionAccessor)c;
               if (ca.getFitsDimensions().isEmpty()) {
                  ca.getFitsDimensions().addAll(c.getRecipes());
               }
            }
         }

         if (onInventory && !BetterRecipeBook.ctx().config().showAllRecipesInSurvival) {
            for (RecipeCollection cx : list) {
               RecipeCollectionAccessor ca = (RecipeCollectionAccessor)cx;
               Set<RecipeHolder<?>> fits = ca.getFitsDimensions();
               if (!fits.isEmpty()) {
                  fits.removeIf(r -> !brbe$fitsInventoryGrid((RecipeHolder<?>)r));
               }
            }

            list.removeIf(cxx -> {
               RecipeCollectionAccessor cax = (RecipeCollectionAccessor)cxx;
               return cax.getFitsDimensions().isEmpty();
            });
         }

         list = CollectionPipeline.applyFilterToggle(list, isFiltering);
         page.updateCollections(list, resetPageNumber);
      } else {
         page.updateCollections(list, resetPageNumber);
      }
   }

   @Unique
   private static boolean brbe$fitsInventoryGrid(RecipeHolder<?> holder) {
      Recipe<?> recipe = holder.value();
      if (!(recipe instanceof ShapedRecipe shaped)) {
         return recipe instanceof ShapelessRecipe shapeless ? shapeless.getIngredients().size() <= 4 : true;
      } else {
         return shaped.getWidth() <= 2 && shaped.getHeight() <= 2;
      }
   }
}
