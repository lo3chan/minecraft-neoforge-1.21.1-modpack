package dev.latvian.mods.kubejs.integration.rei;

import dev.latvian.mods.kubejs.recipe.viewer.RemoveRecipesKubeEvent;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.rhino.Context;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry.CategoryConfiguration;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class REIRemoveRecipeKubeEvent implements RemoveRecipesKubeEvent {
   private final Map<CategoryIdentifier<?>, Collection<ResourceLocation>> recipesRemoved;
   private final CategoryRegistry categories;

   public REIRemoveRecipeKubeEvent(Map<CategoryIdentifier<?>, Collection<ResourceLocation>> recipesRemoved) {
      this.recipesRemoved = recipesRemoved;
      this.categories = CategoryRegistry.getInstance();
   }

   @Override
   public void remove(Context cx, ResourceLocation[] recipesToRemove) {
      List<ResourceLocation> asList = List.of(recipesToRemove);

      for (CategoryConfiguration<?> catId : this.categories) {
         this.recipesRemoved.computeIfAbsent(catId.getCategoryIdentifier(), _0 -> new HashSet<>()).addAll(asList);
      }
   }

   @Override
   public void removeFromCategory(Context cx, @Nullable ResourceLocation category, ResourceLocation[] recipesToRemove) {
      if (category == null) {
         this.remove(cx, recipesToRemove);
      } else {
         CategoryIdentifier<Display> catId = CategoryIdentifier.of(category);
         if (this.categories.tryGet(catId).isEmpty()) {
            ((KubeJSContext)cx)
               .getConsole()
               .error("Failed to remove recipes for type '" + category + "': Category doesn't exist! Use 'event.categories' to get a list of all categories.");
         } else {
            this.recipesRemoved.computeIfAbsent(catId, _0 -> new HashSet<>()).addAll(List.of(recipesToRemove));
         }
      }
   }
}
