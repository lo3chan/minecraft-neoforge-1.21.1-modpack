package com.alonie.brbe.mixins.incompletecrafting;

import com.alonie.brbe.BetterRecipeBook;
import com.alonie.brbe.mixins.accessors.RecipeCollectionAccessor;
import com.alonie.brbe.util.BrbeLogger;
import com.alonie.brbe.util.IncompatibleCraftingUtil;
import com.alonie.brbe.util.PartialCraftingUtil;
import com.alonie.brbe.util.PerfTimer;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({RecipeBookComponent.class})
public abstract class RecipeBookComponentMixin {
   @Shadow
   @Final
   protected RecipeBookMenu<?, ?> menu;
   @Shadow
   @Final
   protected Minecraft minecraft;
   @Unique
   private static long brbe$lastSlotHash;

   @Redirect(
      method = {"updateCollections"},
      at = @At(
         value = "INVOKE",
         target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V"
      )
   )
   private void betterRecipeBook$injectIntoDataSets(List<RecipeCollection> collections, Consumer<? super RecipeCollection> vanillaConsumer) {
      PerfTimer.begin();
      long slotHash = PartialCraftingUtil.slotHash(this.menu.slots);
      boolean forceRefresh = PartialCraftingUtil.consumeForceFullRefresh();
      boolean inventoryChanged = slotHash != brbe$lastSlotHash || forceRefresh;
      if (forceRefresh) {
         BrbeLogger.log(BrbeLogger.Category.PIPELINE, "@Redirect forceRefresh consumed — forcing full rebuild, hashChanged=%s", slotHash != brbe$lastSlotHash);
      }

      if (inventoryChanged) {
         PerfTimer.start("vanilla.forEach");
         collections.forEach(vanillaConsumer);
         PerfTimer.end("vanilla.forEach");
         brbe$lastSlotHash = slotHash;
      }

      boolean onInventory = this.minecraft != null && this.minecraft.screen instanceof InventoryScreen;
      boolean retainPartial = BetterRecipeBook.ctx().config().partialMarkingEnabled;
      boolean retainIncompatible = onInventory && BetterRecipeBook.ctx().config().showAllRecipesInSurvival;
      if (!retainPartial) {
         for (RecipeCollection coll : collections) {
            if (PartialCraftingUtil.hasPartialMaterialsRaw(coll)) {
               RecipeCollectionAccessor ca = (RecipeCollectionAccessor)coll;

               for (RecipeHolder<?> holder : coll.getRecipes()) {
                  if (PartialCraftingUtil.isPartiallyCraftableRaw(coll, holder.id())) {
                     ca.brbe$getCraftable().remove(holder);
                  }
               }
            }
         }

         PartialCraftingUtil.invalidateCaches();
         if (!retainIncompatible) {
            PerfTimer.logAndReset("updateCollections (disabled+cleaned)");
            return;
         }
      }

      if (!retainPartial && !retainIncompatible) {
         PerfTimer.logAndReset("updateCollections (no-op)");
      } else {
         if (retainIncompatible) {
            PerfTimer.start("incompatible.mark");

            for (RecipeCollection collection : collections) {
               IncompatibleCraftingUtil.markIncompatibleRecipes(collection);
            }

            PerfTimer.end("incompatible.mark");
         }

         if (!retainPartial) {
            PerfTimer.logAndReset("updateCollections (incompatible-only)");
         } else if (!inventoryChanged) {
            PerfTimer.logAndReset("updateCollections (cache-hit, fully skipped)");
         } else {
            PartialCraftingUtil.beginFilteringUpdate(true);
            Set<Item> inventoryItems = PartialCraftingUtil.hashInventory(this.menu.slots);
            PerfTimer.start("partial.step0-clear");

            for (RecipeCollection collx : collections) {
               if (PartialCraftingUtil.hasPartialMaterials(collx)) {
                  RecipeCollectionAccessor ca = (RecipeCollectionAccessor)collx;

                  for (RecipeHolder<?> holderx : collx.getRecipes()) {
                     if (PartialCraftingUtil.isPartiallyCraftable(collx, holderx.id())) {
                        ca.brbe$getCraftable().remove(holderx);
                     }
                  }
               }
            }

            PerfTimer.end("partial.step0-clear");
            PerfTimer.start("partial.markAndInject");
            int collCount = 0;

            for (RecipeCollection collxx : collections) {
               collCount++;
               PartialCraftingUtil.markAndInject(collxx, inventoryItems);
            }

            PerfTimer.end("partial.markAndInject");
            PartialCraftingUtil.beginFilteringUpdate(false);
            if (!BetterRecipeBook.ctx().config().showAllRecipesInSurvival) {
               for (RecipeCollection collxx : collections) {
                  if (PartialCraftingUtil.hasPartialMaterials(collxx)) {
                     RecipeCollectionAccessor ca = (RecipeCollectionAccessor)collxx;

                     for (RecipeHolder<?> holderxx : collxx.getRecipes()) {
                        if (PartialCraftingUtil.isPartiallyCraftable(collxx, holderxx.id()) && brbe$needsLargerGrid(holderxx)) {
                           ca.brbe$getCraftable().remove(holderxx);
                        }
                     }
                  }
               }
            }

            PerfTimer.logAndReset("updateCollections (" + collCount + " coll)");
         }
      }
   }

   @Unique
   private static boolean brbe$needsLargerGrid(RecipeHolder<?> holder) {
      Recipe<?> recipe = holder.value();
      if (!(recipe instanceof ShapedRecipe shaped)) {
         return recipe instanceof ShapelessRecipe shapeless ? shapeless.getIngredients().size() > 4 : false;
      } else {
         return shaped.getWidth() > 2 || shaped.getHeight() > 2;
      }
   }
}
