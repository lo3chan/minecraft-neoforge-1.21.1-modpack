package dev.latvian.mods.kubejs.recipe.schema.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.util.Lazy;
import java.util.Map;

public record RecipeSchemaFunctionType<T extends RecipeSchemaFunction>(String id, MapCodec<T> mapCodec) {
   public static final Lazy<Map<String, RecipeSchemaFunctionType<?>>> REGISTRY = Lazy.map(
      map -> KubeJSPlugins.forEachPlugin(p -> p.registerRecipeSchemaFunctionTypes(type -> map.put(type.id(), type)))
   );
   public static final Codec<RecipeSchemaFunctionType<?>> CODEC = Codec.STRING.flatXmap(s -> {
      RecipeSchemaFunctionType<?> type = REGISTRY.get().get(s);
      return type != null ? DataResult.success(type) : DataResult.error(() -> "Unknown recipe schema function type: " + s);
   }, type -> DataResult.success(type.id));
}
