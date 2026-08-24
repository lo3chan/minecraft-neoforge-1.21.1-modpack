package com.alonie.brbe.neoforge.compat.jei;

import com.alonie.brbe.compat.jei.JeiCompat;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import java.util.List;
import java.util.Optional;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IJeiKeyMappings;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public final class BetterRecipeBookJEIPlugin implements IModPlugin {
   private static final ResourceLocation PLUGIN_UID = ResourceLocation.fromNamespaceAndPath("brbe", "jei");

   public ResourceLocation getPluginUid() {
      return PLUGIN_UID;
   }

   public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
      final IJeiKeyMappings km = jeiRuntime.getKeyMappings();
      JeiCompat.setHandler(new JeiCompat.JeiHandler() {
         @Override
         public boolean openRecipeView(ItemStack stack) {
            return BetterRecipeBookJEIPlugin.open(jeiRuntime, RecipeIngredientRole.OUTPUT, stack);
         }

         @Override
         public boolean openUsageView(ItemStack stack) {
            return BetterRecipeBookJEIPlugin.open(jeiRuntime, RecipeIngredientRole.INPUT, stack);
         }

         @Override
         public boolean matchesShowRecipe(int keyCode, int scanCode) {
            Key key = InputConstants.getKey(keyCode, scanCode);
            return km.getShowRecipe().isActiveAndMatches(key);
         }

         @Override
         public boolean matchesShowUses(int keyCode, int scanCode) {
            Key key = InputConstants.getKey(keyCode, scanCode);
            return km.getShowUses().isActiveAndMatches(key);
         }
      });
   }

   public void onRuntimeUnavailable() {
      JeiCompat.setHandler(null);
   }

   private static boolean open(IJeiRuntime jeiRuntime, RecipeIngredientRole role, ItemStack stack) {
      if (stack.isEmpty()) {
         return false;
      } else {
         Optional<ITypedIngredient<ItemStack>> typedIngredient = jeiRuntime.getIngredientManager().createTypedIngredient(VanillaTypes.ITEM_STACK, stack, false);
         if (typedIngredient.isEmpty()) {
            return false;
         } else {
            IFocusFactory focusFactory = jeiRuntime.getJeiHelpers().getFocusFactory();
            IFocus<ItemStack> focus = focusFactory.createFocus(role, typedIngredient.get());
            jeiRuntime.getRecipesGui().show(List.of(focus));
            return true;
         }
      }
   }
}
