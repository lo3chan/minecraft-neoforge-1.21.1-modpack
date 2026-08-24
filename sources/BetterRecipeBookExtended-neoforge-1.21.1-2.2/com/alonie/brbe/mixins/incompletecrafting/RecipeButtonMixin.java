package com.alonie.brbe.mixins.incompletecrafting;

import com.alonie.brbe.BetterRecipeBook;
import com.alonie.brbe.mixins.accessors.RecipeCollectionAccessor;
import com.alonie.brbe.util.BrbeLogger;
import com.alonie.brbe.util.PartialCraftingUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({RecipeButton.class})
public abstract class RecipeButtonMixin extends AbstractWidget {
   @Shadow
   private RecipeCollection collection;
   @Shadow
   private int currentIndex;

   protected RecipeButtonMixin(int x, int y, int width, int height, Component message) {
      super(x, y, width, height, message);
   }

   @Shadow
   protected abstract List<RecipeHolder<?>> getOrderedRecipes();

   @Redirect(
      method = {"getOrderedRecipes"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/stats/RecipeBook;isFiltering(Lnet/minecraft/world/inventory/RecipeBookMenu;)Z"
      )
   )
   private boolean brbe$disableFilteringInOrdered(RecipeBook book, RecipeBookMenu<?, ?> menu) {
      return false;
   }

   @Redirect(
      method = {"renderWidget"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/stats/RecipeBook;isFiltering(Lnet/minecraft/world/inventory/RecipeBookMenu;)Z"
      )
   )
   private boolean brbe$disableFilteringInRender(RecipeBook book, RecipeBookMenu<?, ?> menu) {
      return false;
   }

   @Redirect(
      method = {"renderWidget"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeCollection;hasCraftable()Z"
      )
   )
   private boolean brbe$renderPartiallyCraftableAsCraftable(RecipeCollection collection, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      boolean hasPartial = PartialCraftingUtil.hasPartialMaterials(collection);
      if (hasPartial && !collection.hasCraftable()) {
         int healed = 0;
         RecipeCollectionAccessor ca = (RecipeCollectionAccessor)collection;

         for (RecipeHolder<?> holder : collection.getRecipes()) {
            if (PartialCraftingUtil.isPartiallyCraftable(collection, holder.id())) {
               ca.brbe$getCraftable().add(holder);
               healed++;
            }
         }

         BrbeLogger.log(BrbeLogger.Category.STATE, "RecipeButton self-heal: healed %d partials, hasCraftable=%s", healed, collection.hasCraftable());
      }

      return collection.hasCraftable() || hasPartial;
   }

   @Inject(
      method = {"renderWidget"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/GuiGraphics;renderFakeItem(Lnet/minecraft/world/item/ItemStack;II)V",
         shift = Shift.BEFORE
      )}
   )
   private void brbe$renderPartialOverlay(GuiGraphics gui, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      List<RecipeHolder<?>> recipes = this.getOrderedRecipes();
      if (!recipes.isEmpty()) {
         RecipeHolder<?> current = recipes.get(this.currentIndex % recipes.size());
         if (PartialCraftingUtil.isPartiallyCraftable(this.collection, current)) {
            gui.fill(this.getX() + 1, this.getY() + 1, this.getX() + this.width - 1, this.getY() + this.height - 1, 1627337523);
         }
      }
   }

   @Inject(
      method = {"getOrderedRecipes"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void brbe$filterOrderedRecipes(CallbackInfoReturnable<List<RecipeHolder<?>>> cir) {
      List<RecipeHolder<?>> result = new ArrayList<>((Collection<? extends RecipeHolder<?>>)cir.getReturnValue());
      List<RecipeHolder<?>> partials = PartialCraftingUtil.getPartiallyCraftableRecipes(this.collection);
      if (!partials.isEmpty()) {
         Set<ResourceLocation> existing = new HashSet<>();

         for (RecipeHolder<?> r : result) {
            existing.add(r.id());
         }

         for (RecipeHolder<?> p : partials) {
            if (!existing.contains(p.id())) {
               result.add(p);
            }
         }
      }

      boolean hasCraftable = this.collection.hasCraftable();
      boolean hasPartial = PartialCraftingUtil.hasPartialMaterials(this.collection);
      if ((hasCraftable || hasPartial) && result.size() > 1) {
         List<RecipeHolder<?>> filtered = new ArrayList<>(result.size());

         for (RecipeHolder<?> r : result) {
            if (this.collection.isCraftable(r) && !PartialCraftingUtil.isPartiallyCraftable(this.collection, r)) {
               filtered.add(r);
            }
         }

         for (RecipeHolder<?> rx : result) {
            if (PartialCraftingUtil.isPartiallyCraftable(this.collection, rx)) {
               filtered.add(rx);
            }
         }

         if (!filtered.isEmpty()) {
            cir.setReturnValue(filtered);
         }
      } else if (!result.equals(cir.getReturnValue())) {
         cir.setReturnValue(result);
      }

      if (!BetterRecipeBook.ctx().config().showAllRecipesInSurvival) {
         Minecraft client = Minecraft.getInstance();
         if (client.screen instanceof InventoryScreen) {
            List<RecipeHolder<?>> current = (List<RecipeHolder<?>>)cir.getReturnValue();
            if (current != null && !current.isEmpty()) {
               RecipeCollectionAccessor ca = (RecipeCollectionAccessor)this.collection;
               Set<RecipeHolder<?>> fits = ca.getFitsDimensions();
               if (!fits.isEmpty()) {
                  List<RecipeHolder<?>> filtered = new ArrayList<>();

                  for (RecipeHolder<?> rxx : current) {
                     if (fits.contains(rxx)) {
                        filtered.add(rxx);
                     }
                  }

                  if (!filtered.isEmpty()) {
                     cir.setReturnValue(filtered);
                  }
               }
            }
         }
      }

      List<RecipeHolder<?>> finalResult = (List<RecipeHolder<?>>)cir.getReturnValue();
      if (finalResult == null || finalResult.isEmpty()) {
         cir.setReturnValue(new ArrayList(this.collection.getRecipes()));
      }
   }

   @Inject(
      method = {"isOnlyOption"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void brbe$allowNestedAlternativeOverlay(CallbackInfoReturnable<Boolean> cir) {
      if ((Boolean)cir.getReturnValue()) {
         if (this.collection.getRecipes().size() > 1 && (this.collection.hasCraftable() || PartialCraftingUtil.hasPartialMaterials(this.collection))) {
            cir.setReturnValue(false);
         }
      }
   }
}
