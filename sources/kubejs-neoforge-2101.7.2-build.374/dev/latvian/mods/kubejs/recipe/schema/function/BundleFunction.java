package dev.latvian.mods.kubejs.recipe.schema.function;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import java.util.ArrayList;
import java.util.List;

public record BundleFunction(List<RecipeSchemaFunction> functions) implements RecipeSchemaFunction {
   public static final RecipeSchemaFunctionType<BundleFunction> TYPE = new RecipeSchemaFunctionType<>(
      "bundle",
      RecordCodecBuilder.mapCodec(
         instance -> instance.group(RecipeSchemaFunction.CODEC.listOf().fieldOf("functions").forGetter(BundleFunction::functions))
            .apply(instance, BundleFunction::new)
      )
   );

   @Override
   public RecipeSchemaFunctionType<?> type() {
      return TYPE;
   }

   @Override
   public DataResult<ResolvedRecipeSchemaFunction> resolve(DynamicOps<JsonElement> jsonOps, RecipeSchema schema) {
      ArrayList<ResolvedRecipeSchemaFunction> list = new ArrayList<>(this.functions.size());

      for (int i = 0; i < this.functions.size(); i++) {
         RecipeSchemaFunction function = this.functions.get(i);
         DataResult<ResolvedRecipeSchemaFunction> r = function.resolve(jsonOps, schema);
         if (r.isError()) {
            int j = i + 1;
            return r.ap(DataResult.error(() -> "Failed to parse function #" + j));
         }

         list.add((ResolvedRecipeSchemaFunction)r.getOrThrow());
      }

      return list.isEmpty() ? DataResult.error(() -> "Bundled function list is empty") : DataResult.success(new BundleFunction.Resolved(list));
   }

   public record Resolved(List<ResolvedRecipeSchemaFunction> functions) implements ResolvedRecipeSchemaFunction {
      @Override
      public void execute(RecipeScriptContext cx, List<Object> args) {
         for (ResolvedRecipeSchemaFunction function : this.functions) {
            function.execute(cx, args);
         }
      }
   }
}
