package com.alonie.brbe.mixins.search;

import com.alonie.brbe.search.SearchCache;
import com.alonie.brbe.search.SearchQuery;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({RecipeBookComponent.class})
public class RecipeBookComponentMixin {
   @Shadow
   @Final
   protected Minecraft minecraft;
   @Shadow
   protected EditBox searchBox;
   @Unique
   private String betterRecipeBook$savedSearchText;
   @Unique
   private SearchQuery betterRecipeBook$parsedQuery;

   @Inject(
      method = {"updateCollections"},
      at = {@At("HEAD")}
   )
   private void betterRecipeBook$onUpdateCollections(boolean resetPageNumber, CallbackInfo ci) {
      this.betterRecipeBook$savedSearchText = null;
      this.betterRecipeBook$parsedQuery = null;
      if (this.searchBox != null) {
         String text = this.searchBox.getValue();
         if (text != null && !text.isEmpty()) {
            this.betterRecipeBook$savedSearchText = text;
            this.betterRecipeBook$parsedQuery = SearchQuery.parse(text);
            this.searchBox.setValue("");
         }
      }
   }

   @ModifyArg(
      method = {"updateCollections"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeBookPage;updateCollections(Ljava/util/List;Z)V"
      ),
      index = 0
   )
   private List<RecipeCollection> betterRecipeBook$applyAdvancedSearch(List<RecipeCollection> collections) {
      if (this.betterRecipeBook$parsedQuery != null && this.minecraft.level != null) {
         SearchCache cache = new SearchCache();
         RegistryAccess registryAccess = this.minecraft.level.registryAccess();
         List<RecipeCollection> filtered = new ArrayList<>();

         for (RecipeCollection collection : collections) {
            for (RecipeHolder<?> recipe : collection.getRecipes()) {
               ItemStack result = recipe.value().getResultItem(registryAccess);
               if (result != null && !result.isEmpty() && this.betterRecipeBook$parsedQuery.matches(result, cache)) {
                  filtered.add(collection);
                  break;
               }
            }
         }

         return filtered;
      } else {
         return collections;
      }
   }

   @Inject(
      method = {"updateCollections"},
      at = {@At("TAIL")}
   )
   private void betterRecipeBook$restoreSearchText(boolean resetPageNumber, CallbackInfo ci) {
      if (this.betterRecipeBook$savedSearchText != null && this.searchBox != null) {
         this.searchBox.setValue(this.betterRecipeBook$savedSearchText);
         this.betterRecipeBook$savedSearchText = null;
         this.betterRecipeBook$parsedQuery = null;
      }
   }
}
