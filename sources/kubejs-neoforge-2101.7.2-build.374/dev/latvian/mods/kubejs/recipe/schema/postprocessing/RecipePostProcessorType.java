package dev.latvian.mods.kubejs.recipe.schema.postprocessing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.recipe.RecipeTypeRegistryContext;
import dev.latvian.mods.kubejs.util.Lazy;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;

public record RecipePostProcessorType<T extends RecipePostProcessor>(ResourceLocation id, Function<RecipeTypeRegistryContext, MapCodec<T>> mapCodec) {
   public static final Lazy<Map<ResourceLocation, RecipePostProcessorType<?>>> MAP = Lazy.map(
      map -> KubeJSPlugins.forEachPlugin(type -> map.put(type.id, type), KubeJSPlugin::registerRecipePostProcessors)
   );
   public static final Codec<RecipePostProcessorType<?>> CODEC = ResourceLocation.CODEC.comapFlatMap(id -> {
      RecipePostProcessorType<?> type = MAP.get().get(id);
      return type != null ? DataResult.success(type) : DataResult.error(() -> "Recipe post-processor type not found: " + id);
   }, RecipePostProcessorType::id);
}
