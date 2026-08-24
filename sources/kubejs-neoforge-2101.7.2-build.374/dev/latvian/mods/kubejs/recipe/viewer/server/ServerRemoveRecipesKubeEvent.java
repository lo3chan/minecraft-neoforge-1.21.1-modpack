package dev.latvian.mods.kubejs.recipe.viewer.server;

import dev.latvian.mods.kubejs.recipe.viewer.RemoveRecipesKubeEvent;
import dev.latvian.mods.rhino.Context;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class ServerRemoveRecipesKubeEvent implements RemoveRecipesKubeEvent {
   private final Set<ResourceLocation> global;
   private final Map<ResourceLocation, CategoryData> categoryData;

   public ServerRemoveRecipesKubeEvent(Set<ResourceLocation> global, Map<ResourceLocation, CategoryData> categoryData) {
      this.global = global;
      this.categoryData = categoryData;
   }

   @Override
   public void remove(Context cx, ResourceLocation[] recipesToRemove) {
      this.global.addAll(Arrays.asList(recipesToRemove));
   }

   @Override
   public void removeFromCategory(Context cx, @Nullable ResourceLocation category, ResourceLocation[] recipesToRemove) {
      if (category == null) {
         this.remove(cx, recipesToRemove);
      } else {
         this.categoryData.computeIfAbsent(category, CategoryData::new).removedRecipes().addAll(Arrays.asList(recipesToRemove));
      }
   }
}
