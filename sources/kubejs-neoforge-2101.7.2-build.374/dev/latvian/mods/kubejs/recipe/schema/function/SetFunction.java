package dev.latvian.mods.kubejs.recipe.schema.function;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import java.util.List;
import net.minecraft.util.ExtraCodecs;

public record SetFunction(String key, JsonElement value) implements RecipeSchemaFunction {
   public static final RecipeSchemaFunctionType<SetFunction> TYPE = new RecipeSchemaFunctionType<>(
      "set",
      RecordCodecBuilder.mapCodec(
         instance -> instance.group(Codec.STRING.fieldOf("key").forGetter(SetFunction::key), ExtraCodecs.JSON.fieldOf("value").forGetter(SetFunction::value))
            .apply(instance, SetFunction::new)
      )
   );

   @Override
   public RecipeSchemaFunctionType<?> type() {
      return TYPE;
   }

   @Override
   public DataResult<ResolvedRecipeSchemaFunction> resolve(DynamicOps<JsonElement> jsonOps, RecipeSchema schema) {
      RecipeKey<Object> k = schema.getOptionalKey(this.key);
      if (k == null) {
         return DataResult.error(() -> "Key '" + this.key + "' not found");
      } else {
         DataResult<Object> v = k.codec.parse(jsonOps, this.value);
         return v.isSuccess()
            ? DataResult.success(new SetFunction.Resolved<>(k, v.getOrThrow()))
            : v.ap(DataResult.error(() -> "Failed to parse value for key '" + this.key));
      }
   }

   public record Resolved<T>(RecipeKey<T> key, T to) implements ResolvedRecipeSchemaFunction {
      @Override
      public void execute(RecipeScriptContext cx, List<Object> args) {
         cx.recipe().setValue(this.key, this.to);
      }
   }
}
