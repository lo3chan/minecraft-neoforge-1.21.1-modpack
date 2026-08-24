package dev.latvian.mods.kubejs.recipe.viewer.server;

import dev.latvian.mods.kubejs.plugin.builtin.event.RecipeViewerEvents;
import dev.latvian.mods.kubejs.script.ScriptType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public record RecipeViewerData(
   List<ResourceLocation> removedCategories,
   List<ResourceLocation> removedGlobalRecipes,
   List<CategoryData> categoryData,
   ItemData itemData,
   FluidData fluidData
) {
   public static final StreamCodec<RegistryFriendlyByteBuf, RecipeViewerData> STREAM_CODEC = StreamCodec.composite(
      ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()),
      RecipeViewerData::removedCategories,
      ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()),
      RecipeViewerData::removedGlobalRecipes,
      CategoryData.STREAM_CODEC.apply(ByteBufCodecs.list()),
      RecipeViewerData::categoryData,
      ItemData.STREAM_CODEC,
      RecipeViewerData::itemData,
      FluidData.STREAM_CODEC,
      RecipeViewerData::fluidData,
      RecipeViewerData::new
   );

   @Nullable
   public static RecipeViewerData collect() {
      HashSet<ResourceLocation> removedCategories = new HashSet<>();
      HashSet<ResourceLocation> removedGlobalRecipes = new HashSet<>();
      HashMap<ResourceLocation, CategoryData> categoryData = new HashMap<>();
      if (RecipeViewerEvents.REMOVE_CATEGORIES.hasListeners()) {
         RecipeViewerEvents.REMOVE_CATEGORIES.post(ScriptType.SERVER, new ServerRemoveCategoriesKubeEvent(removedCategories));
      }

      if (RecipeViewerEvents.REMOVE_RECIPES.hasListeners()) {
         RecipeViewerEvents.REMOVE_RECIPES.post(ScriptType.SERVER, new ServerRemoveRecipesKubeEvent(removedGlobalRecipes, categoryData));
      }

      ItemData itemData = ItemData.collect();
      FluidData fluidData = FluidData.collect();
      RecipeViewerData data = new RecipeViewerData(
         List.copyOf(removedCategories),
         List.copyOf(removedGlobalRecipes),
         List.copyOf(categoryData.values().stream().map(CategoryData::lock).toList()),
         itemData,
         fluidData
      );
      return data.isEmpty() ? null : data;
   }

   public boolean isEmpty() {
      return this.itemData.isEmpty()
         && this.fluidData.isEmpty()
         && this.removedCategories.isEmpty()
         && this.removedGlobalRecipes.isEmpty()
         && this.categoryData.isEmpty();
   }
}
