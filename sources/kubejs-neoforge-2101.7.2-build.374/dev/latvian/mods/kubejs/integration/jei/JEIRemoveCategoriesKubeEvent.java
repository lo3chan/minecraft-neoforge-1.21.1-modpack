package dev.latvian.mods.kubejs.integration.jei;

import dev.latvian.mods.kubejs.recipe.viewer.RemoveCategoriesKubeEvent;
import dev.latvian.mods.rhino.Context;
import java.util.Map;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.resources.ResourceLocation;

public class JEIRemoveCategoriesKubeEvent implements RemoveCategoriesKubeEvent {
   private final IRecipeManager recipeManager;
   private final Map<ResourceLocation, IRecipeCategory<?>> categories;

   public JEIRemoveCategoriesKubeEvent(IRecipeManager recipeManager, Map<ResourceLocation, IRecipeCategory<?>> categories) {
      this.recipeManager = recipeManager;
      this.categories = categories;
   }

   @Override
   public void remove(Context cx, ResourceLocation[] ids) {
      for (ResourceLocation c : ids) {
         IRecipeCategory<?> category = this.categories.get(c);
         if (category != null) {
            this.recipeManager.hideRecipeCategory(category.getRecipeType());
            this.categories.remove(c);
         }
      }
   }
}
