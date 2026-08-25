package com.alonie.recipebookispain_extended.mixin.groups;

import com.alonie.brbe.util.RecipeBookDebugLogger;
import com.alonie.recipebookispain_extended.RecipeBookIsPain;
import com.alonie.recipebookispain_extended.RecipeBookIsPainExtendedConfig;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.BlastFurnaceMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.SmokerMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({RecipeBookComponent.class})
public abstract class ClientRecipeBookMixin {
   @Shadow
   protected RecipeBookMenu menu;

   @Redirect(
      method = {"updateCollections"},
      require = 0,
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/ClientRecipeBook;getCollection(Lnet/minecraft/client/RecipeBookCategories;)Ljava/util/List;"
      )
   )
   private List<RecipeCollection> rbip$getFilteredCollections(ClientRecipeBook book, RecipeBookCategories category) {
      if (RecipeBookIsPainExtendedConfig.enabled() && RecipeBookIsPain.activeCreativeTab != null && category == RecipeBookCategories.UNKNOWN) {
         CreativeModeTab activeTab = RecipeBookIsPain.activeCreativeTab;
         RecipeBookCategories searchCategory;
         if (this.menu instanceof AbstractFurnaceMenu furnaceMenu) {
            if (furnaceMenu instanceof SmokerMenu) {
               searchCategory = RecipeBookCategories.SMOKER_SEARCH;
            } else if (furnaceMenu instanceof BlastFurnaceMenu) {
               searchCategory = RecipeBookCategories.BLAST_FURNACE_SEARCH;
            } else {
               searchCategory = RecipeBookCategories.FURNACE_SEARCH;
            }
         } else {
            searchCategory = RecipeBookCategories.CRAFTING_SEARCH;
         }

         List<RecipeCollection> allBase = book.getCollection(searchCategory);
         List<RecipeCollection> matching = new ArrayList<>();

         for (RecipeCollection collection : allBase) {
            if (rbip$anyRecipeInTab(collection, activeTab)) {
               matching.add(collection);
            }
         }

         RecipeBookDebugLogger.onRbipFilterCollections(searchCategory.name(), allBase.size(), matching.size(), true);
         return matching;
      } else {
         if (RecipeBookDebugLogger.enabled && category == RecipeBookCategories.UNKNOWN) {
            RecipeBookDebugLogger.onRbipFilterCollections(category.name(), 0, 0, RecipeBookIsPain.activeCreativeTab != null);
         }

         return book.getCollection(category);
      }
   }

   @Unique
   private static boolean rbip$anyRecipeInTab(RecipeCollection collection, CreativeModeTab tab) {
      for (RecipeHolder<?> holder : collection.getRecipes()) {
         ItemStack result = holder.value().getResultItem(Minecraft.getInstance().level.registryAccess());
         if (!result.isEmpty() && RecipeBookIsPain.isItemInTab(result, tab)) {
            return true;
         }
      }

      return false;
   }
}
