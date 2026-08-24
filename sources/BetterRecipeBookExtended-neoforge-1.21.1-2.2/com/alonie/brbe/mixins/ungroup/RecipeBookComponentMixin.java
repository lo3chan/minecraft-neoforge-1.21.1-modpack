package com.alonie.brbe.mixins.ungroup;

import com.alonie.brbe.BetterRecipeBook;
import com.alonie.brbe.util.BrbeLogger;
import com.alonie.brbe.util.CollectionPipeline;
import com.alonie.brbe.util.PartialCraftingUtil;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({RecipeBookComponent.class})
public class RecipeBookComponentMixin {
   @Shadow
   private String lastSearch;
   @Shadow
   private ClientRecipeBook book;
   @Shadow
   protected RecipeBookMenu<?, ?> menu;
   @Shadow
   @Final
   private RecipeBookPage recipeBookPage;

   @Inject(
      method = {"updateCollections"},
      locals = LocalCapture.CAPTURE_FAILSOFT,
      at = {@At(
         value = "INVOKE",
         target = "Lit/unimi/dsi/fastutil/objects/ObjectLinkedOpenHashSet;<init>(Ljava/util/Collection;)V"
      )},
      cancellable = true
   )
   private void refreshSearchResults(boolean bl, CallbackInfo ci, List<RecipeCollection> list, List<RecipeCollection> list2, String string) {
      if (BetterRecipeBook.ctx().config().alternativeRecipes.noGrouped) {
         BrbeLogger.log(
            BrbeLogger.Category.PIPELINE,
            "ungroup ENTER — n=%d pCE=%s isFiltering=%s",
            list2.size(),
            BetterRecipeBook.ctx().config().partialCraftingEnabled,
            this.book.isFiltering(this.menu)
         );
         list2.removeIf(
            collectionx -> {
               Iterator<RecipeHolder<?>> it = collectionx.getRecipes().iterator();
               if (!it.hasNext()) {
                  return false;
               } else {
                  RecipeHolder<?> recipe = it.next();
                  return !recipe.value()
                     .getResultItem(collectionx.registryAccess())
                     .getHoverName()
                     .getString()
                     .toLowerCase(Locale.ROOT)
                     .contains(this.lastSearch.toLowerCase(Locale.ROOT));
               }
            }
         );
         boolean brbeManagesFilter = BetterRecipeBook.ctx().config().partialCraftingEnabled;
         if (brbeManagesFilter || this.book.isFiltering(this.menu)) {
            PartialCraftingUtil.beginFilteringUpdate(true);
            Set<Item> inventoryItems = PartialCraftingUtil.hashInventory(this.menu.slots);
            if (brbeManagesFilter) {
               for (RecipeCollection collection : list2) {
                  PartialCraftingUtil.markPartialMaterials(collection, inventoryItems);
               }
            } else {
               list2.removeIf(collectionx -> {
                  PartialCraftingUtil.markPartialMaterials(collectionx, inventoryItems);
                  return !collectionx.hasCraftable() && !PartialCraftingUtil.hasPartialMaterials(collectionx);
               });
            }
         }

         CollectionPipeline.applyPins(list2);
         BrbeLogger.log(BrbeLogger.Category.PIPELINE, "ungroup EXIT — final n=%d calling page.updateCollections", list2.size());
         this.recipeBookPage.updateCollections(list2, bl);
         ci.cancel();
      }
   }
}
