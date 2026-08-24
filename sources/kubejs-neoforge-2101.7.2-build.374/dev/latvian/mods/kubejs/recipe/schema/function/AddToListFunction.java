package dev.latvian.mods.kubejs.recipe.schema.function;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import java.util.ArrayList;
import java.util.List;

public record AddToListFunction(String key) implements RecipeSchemaFunction {
   public static final RecipeSchemaFunctionType<AddToListFunction> TYPE = new RecipeSchemaFunctionType<>(
      "add_to_list",
      RecordCodecBuilder.mapCodec(
         instance -> instance.group(Codec.STRING.fieldOf("key").forGetter(AddToListFunction::key)).apply(instance, AddToListFunction::new)
      )
   );

   @Override
   public RecipeSchemaFunctionType<?> type() {
      return TYPE;
   }

   @Override
   public DataResult<ResolvedRecipeSchemaFunction> resolve(DynamicOps<JsonElement> jsonOps, RecipeSchema schema) {
      RecipeKey<Object> k = schema.getOptionalKey(this.key);
      return k == null ? DataResult.error(() -> "Key '" + this.key + "' not found") : DataResult.success(new AddToListFunction.Resolved(k));
   }

   public record Resolved<T>(RecipeKey<List<T>> key) implements ResolvedRecipeSchemaFunction {
      @Override
      public List<RecipeComponent<?>> arguments() {
         return List.of(this.key.component);
      }

      @Override
      public void execute(RecipeScriptContext cx, List<Object> args) {
         KubeRecipe recipe = cx.recipe();
         List<T> value = recipe.getValue(this.key);
         ArrayList<T> list = value == null ? new ArrayList<>() : new ArrayList<>(value);
         list.addAll(this.key.component.wrap(cx, args.getFirst()));
         recipe.setValue(this.key, list);
      }
   }
}
