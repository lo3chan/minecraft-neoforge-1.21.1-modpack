package dev.latvian.mods.kubejs.recipe.schema;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.recipe.RecipeTypeRegistryContext;
import dev.latvian.mods.kubejs.recipe.component.ComponentRole;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.schema.function.RecipeSchemaFunction;
import dev.latvian.mods.kubejs.recipe.schema.postprocessing.RecipePostProcessor;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

public record RecipeSchemaData(
   Optional<ResourceLocation> parent,
   Optional<ResourceLocation> overrideType,
   Optional<ResourceLocation> recipeFactory,
   Optional<List<RecipeSchemaData.RecipeKeyData>> keys,
   Optional<List<RecipeSchemaData.ConstructorData>> constructors,
   Optional<Map<String, RecipeSchemaFunction>> functions,
   Map<String, JsonElement> overrideKeys,
   Optional<Boolean> hidden,
   List<String> mappings,
   Optional<List<String>> unique,
   Optional<List<RecipePostProcessor>> postProcessors,
   RecipeSchemaData.MergeData merge
) {
   public static Function<RecipeTypeRegistryContext, Codec<RecipeSchemaData>> CODEC = ctx -> RecordCodecBuilder.create(
      instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("parent").forGetter(RecipeSchemaData::parent),
            ResourceLocation.CODEC.optionalFieldOf("override_type").forGetter(RecipeSchemaData::overrideType),
            ResourceLocation.CODEC.optionalFieldOf("recipe_factory").forGetter(RecipeSchemaData::recipeFactory),
            RecipeSchemaData.RecipeKeyData.CODEC.apply(ctx).listOf().optionalFieldOf("keys").forGetter(RecipeSchemaData::keys),
            RecipeSchemaData.ConstructorData.CODEC.listOf().optionalFieldOf("constructors").forGetter(RecipeSchemaData::constructors),
            Codec.unboundedMap(Codec.STRING, RecipeSchemaFunction.CODEC).optionalFieldOf("functions").forGetter(RecipeSchemaData::functions),
            Codec.unboundedMap(Codec.STRING, ExtraCodecs.JSON).optionalFieldOf("override_keys", Map.of()).forGetter(RecipeSchemaData::overrideKeys),
            Codec.BOOL.optionalFieldOf("hidden").forGetter(RecipeSchemaData::hidden),
            Codec.STRING.listOf().optionalFieldOf("mappings", List.of()).forGetter(RecipeSchemaData::mappings),
            Codec.STRING.listOf().optionalFieldOf("unique").forGetter(RecipeSchemaData::unique),
            ctx.recipePostProcessorCodec().listOf().optionalFieldOf("post_processors").forGetter(RecipeSchemaData::postProcessors),
            RecipeSchemaData.MergeData.CODEC.optionalFieldOf("merge", RecipeSchemaData.MergeData.DEFAULT).forGetter(RecipeSchemaData::merge)
         )
         .apply(instance, RecipeSchemaData::new)
   );

   public RecipeSchemaData(
      ResourceLocation parent,
      ResourceLocation overrideType,
      ResourceLocation recipeFactory,
      List<RecipeSchemaData.RecipeKeyData> keys,
      List<RecipeSchemaData.ConstructorData> constructors,
      Map<String, RecipeSchemaFunction> functions,
      Map<String, JsonElement> overrideKeys,
      boolean hidden,
      List<String> mappings,
      List<String> unique,
      List<RecipePostProcessor> postProcessors,
      RecipeSchemaData.MergeData merge
   ) {
      this(
         Optional.ofNullable(parent),
         Optional.ofNullable(overrideType),
         Optional.ofNullable(recipeFactory),
         Optional.ofNullable(keys),
         Optional.ofNullable(constructors),
         Optional.ofNullable(functions),
         overrideKeys,
         Optional.of(hidden),
         mappings,
         Optional.ofNullable(unique),
         Optional.ofNullable(postProcessors),
         merge
      );
   }

   public boolean mergeKeys() {
      return this.merge.keys;
   }

   public boolean mergeConstructors() {
      return this.merge.constructors;
   }

   public boolean mergeUnique() {
      return this.merge.unique;
   }

   public boolean mergePostProcessors() {
      return this.merge.postProcessors;
   }

   public record ConstructorData(List<String> keys, Map<String, JsonElement> overrides) {
      public static Codec<RecipeSchemaData.ConstructorData> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(
               Codec.STRING.listOf().fieldOf("keys").forGetter(RecipeSchemaData.ConstructorData::keys),
               Codec.unboundedMap(Codec.STRING, ExtraCodecs.JSON).optionalFieldOf("overrides", Map.of()).forGetter(RecipeSchemaData.ConstructorData::overrides)
            )
            .apply(instance, RecipeSchemaData.ConstructorData::new)
      );
   }

   public record MergeData(boolean keys, boolean constructors, boolean unique, boolean postProcessors) {
      public static final RecipeSchemaData.MergeData DEFAULT = new RecipeSchemaData.MergeData(false, false, false, false);
      public static Codec<RecipeSchemaData.MergeData> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(
               Codec.BOOL.optionalFieldOf("keys", false).forGetter(RecipeSchemaData.MergeData::keys),
               Codec.BOOL.optionalFieldOf("constructors", false).forGetter(RecipeSchemaData.MergeData::constructors),
               Codec.BOOL.optionalFieldOf("unique", false).forGetter(RecipeSchemaData.MergeData::unique),
               Codec.BOOL.optionalFieldOf("post_processors", false).forGetter(RecipeSchemaData.MergeData::postProcessors)
            )
            .apply(instance, RecipeSchemaData.MergeData::new)
      );
   }

   public record RecipeKeyData(
      String name,
      ComponentRole role,
      RecipeComponent<?> type,
      Optional<JsonElement> optional,
      boolean defaultOptional,
      List<String> alternativeNames,
      boolean excluded,
      List<String> functionNames,
      boolean alwaysWrite
   ) {
      public static Function<RecipeTypeRegistryContext, Codec<RecipeSchemaData.RecipeKeyData>> CODEC = ctx -> RecordCodecBuilder.create(
         instance -> instance.group(
               Codec.STRING.fieldOf("name").forGetter(RecipeSchemaData.RecipeKeyData::name),
               ComponentRole.CODEC.optionalFieldOf("role", ComponentRole.OTHER).forGetter(RecipeSchemaData.RecipeKeyData::role),
               ctx.recipeComponentCodec().fieldOf("type").forGetter(RecipeSchemaData.RecipeKeyData::type),
               ExtraCodecs.JSON.optionalFieldOf("optional").forGetter(RecipeSchemaData.RecipeKeyData::optional),
               Codec.BOOL.optionalFieldOf("default_optional", false).forGetter(RecipeSchemaData.RecipeKeyData::defaultOptional),
               Codec.STRING.listOf().optionalFieldOf("alternative_names", List.of()).forGetter(RecipeSchemaData.RecipeKeyData::alternativeNames),
               Codec.BOOL.optionalFieldOf("excluded", false).forGetter(RecipeSchemaData.RecipeKeyData::excluded),
               Codec.STRING.listOf().optionalFieldOf("function_names", List.of()).forGetter(RecipeSchemaData.RecipeKeyData::functionNames),
               Codec.BOOL.optionalFieldOf("always_write", false).forGetter(RecipeSchemaData.RecipeKeyData::alwaysWrite)
            )
            .apply(instance, RecipeSchemaData.RecipeKeyData::new)
      );
   }
}
