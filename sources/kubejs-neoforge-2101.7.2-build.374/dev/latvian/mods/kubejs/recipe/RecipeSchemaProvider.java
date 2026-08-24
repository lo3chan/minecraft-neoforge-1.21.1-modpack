package dev.latvian.mods.kubejs.recipe;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.recipe.schema.RecipeOptional;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaData;
import dev.latvian.mods.kubejs.recipe.schema.function.RecipeSchemaFunction;
import dev.latvian.mods.kubejs.recipe.schema.postprocessing.RecipePostProcessor;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput.PathProvider;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Experimental;

@Experimental
public abstract class RecipeSchemaProvider implements DataProvider {
   private final CompletableFuture<Provider> lookupProvider;
   private final String name;
   private final RegistryAccessContainer registryAccessContainer;
   private final PathProvider path;
   private final Builder<ResourceLocation, RecipeSchemaData> map;
   private final ServerScriptManager scriptManager;
   private final RecipeTypeRegistryContext regCtx;
   private final Codec<RecipeSchemaData> codec;

   public RecipeSchemaProvider(String name, GatherDataEvent event) {
      this(name, event, RegistryAccessContainer.BUILTIN);
   }

   public RecipeSchemaProvider(String name, GatherDataEvent event, RegistryAccessContainer registryAccessContainer) {
      this.lookupProvider = event.getLookupProvider();
      this.name = name;
      this.registryAccessContainer = registryAccessContainer;
      this.path = event.getGenerator().getPackOutput().createPathProvider(Target.DATA_PACK, "kubejs/recipe_schema");
      this.map = ImmutableMap.builder();
      this.scriptManager = ServerScriptManager.createForDataGen();
      this.regCtx = new RecipeTypeRegistryContext(registryAccessContainer, this.scriptManager.recipeSchemaStorage);
      this.scriptManager.recipeSchemaStorage.fireEvents(registryAccessContainer, event.getResourceManager(PackType.SERVER_DATA));
      this.codec = RecipeSchemaData.CODEC.apply(this.regCtx);
   }

   public final RegistryAccessContainer registryAccessContainer() {
      return this.registryAccessContainer;
   }

   public final ServerScriptManager serverScriptManager() {
      return this.scriptManager;
   }

   public final RecipeTypeRegistryContext recipeTypeRegistryContext() {
      return this.regCtx;
   }

   public abstract void add(Provider lookup);

   public void add(ResourceLocation id, RecipeSchemaData schema) {
      this.map.put(id, schema);
   }

   public void add(ResourceLocation id, Consumer<RecipeSchemaProvider.SchemaDataBuilder> builder) {
      this.add(id, ((RecipeSchemaProvider.SchemaDataBuilder)Util.make(new RecipeSchemaProvider.SchemaDataBuilder(), builder)).build());
   }

   public void onlyKeys(ResourceLocation id, RecipeKey<?>... keys) {
      this.add(id, b -> b.keys(keys));
   }

   public RecipeSchemaData.RecipeKeyData keyData(RecipeKey<?> key) {
      if (key.functionNames == null) {
         key.noFunctions();
      }

      return new RecipeSchemaData.RecipeKeyData(
         key.name,
         key.role,
         key.component,
         Optional.ofNullable(key.optional)
            .map(RecipeOptional::getInformativeValue)
            .map(value -> (JsonElement)key.codec.encodeStart(this.registryAccessContainer.json(), Cast.to(value)).getOrThrow()),
         key.optional == RecipeOptional.DEFAULT,
         new ArrayList<>(key.names),
         key.excluded,
         key.functionNames,
         key.alwaysWrite
      );
   }

   public CompletableFuture<?> run(CachedOutput output) {
      return this.lookupProvider
         .thenCompose(
            p -> {
               this.add(p);
               return CompletableFuture.allOf(
                  this.map
                     .buildOrThrow()
                     .entrySet()
                     .stream()
                     .map(e -> DataProvider.saveStable(output, p, this.codec, (RecipeSchemaData)e.getValue(), this.path.json((ResourceLocation)e.getKey())))
                     .toArray(CompletableFuture[]::new)
               );
            }
         );
   }

   public String getName() {
      return this.name;
   }

   public class SchemaDataBuilder {
      private ResourceLocation parent;
      private ResourceLocation overrideType;
      private ResourceLocation recipeFactory;
      private List<RecipeSchemaData.RecipeKeyData> keys;
      private List<RecipeSchemaData.ConstructorData> constructors;
      private Map<String, RecipeSchemaFunction> functions;
      private final Map<String, JsonElement> overrideKeys = new HashMap<>();
      boolean hidden = false;
      private final List<String> mappings = new ArrayList<>();
      private List<String> unique;
      private List<RecipePostProcessor> postProcessors;
      private RecipeSchemaData.MergeData mergeData = RecipeSchemaData.MergeData.DEFAULT;

      public RecipeSchemaProvider.SchemaDataBuilder parent(ResourceLocation parent) {
         this.parent = parent;
         return this;
      }

      public RecipeSchemaProvider.SchemaDataBuilder overrideType(ResourceLocation type) {
         this.overrideType = type;
         return this;
      }

      public RecipeSchemaProvider.SchemaDataBuilder recipeFactory(ResourceLocation factory) {
         this.recipeFactory = factory;
         return this;
      }

      public RecipeSchemaProvider.SchemaDataBuilder keys(RecipeKey<?>... keys) {
         return this.keys(List.of(keys));
      }

      public RecipeSchemaProvider.SchemaDataBuilder keys(List<RecipeKey<?>> keys) {
         return this.keyDatas(keys.stream().map(RecipeSchemaProvider.this::keyData).toList());
      }

      public RecipeSchemaProvider.SchemaDataBuilder keyDatas(RecipeSchemaData.RecipeKeyData... keys) {
         return this.keyDatas(List.of(keys));
      }

      public RecipeSchemaProvider.SchemaDataBuilder keyDatas(List<RecipeSchemaData.RecipeKeyData> keys) {
         if (this.keys == null) {
            this.keys = new ArrayList<>(keys);
         } else {
            this.keys.addAll(keys);
         }

         return this;
      }

      public RecipeSchemaProvider.SchemaDataBuilder constructors(RecipeSchemaData.ConstructorData... constructors) {
         return this.constructors(List.of(constructors));
      }

      public RecipeSchemaProvider.SchemaDataBuilder constructors(List<RecipeSchemaData.ConstructorData> constructors) {
         if (this.constructors == null) {
            this.constructors = new ArrayList<>(constructors);
         } else {
            this.constructors.addAll(constructors);
         }

         return this;
      }

      public RecipeSchemaProvider.SchemaDataBuilder function(String name, RecipeSchemaFunction function) {
         return this.functions(Map.of(name, function));
      }

      public RecipeSchemaProvider.SchemaDataBuilder functions(Map<String, RecipeSchemaFunction> functions) {
         if (this.functions == null) {
            this.functions = new HashMap<>(functions);
         } else {
            this.functions.putAll(functions);
         }

         return this;
      }

      public <T> RecipeSchemaProvider.SchemaDataBuilder overrideKey(RecipeKey<T> key, @Nullable T optionalValue) {
         JsonElement encoded = optionalValue != null
            ? (JsonElement)key.codec.encodeStart(RecipeSchemaProvider.this.registryAccessContainer.json(), optionalValue).getOrThrow()
            : null;
         this.overrideKeys.put(key.name, encoded);
         return this;
      }

      public RecipeSchemaProvider.SchemaDataBuilder hidden() {
         return this.hidden(true);
      }

      public RecipeSchemaProvider.SchemaDataBuilder hidden(boolean hidden) {
         this.hidden = hidden;
         return this;
      }

      public RecipeSchemaProvider.SchemaDataBuilder mappings(String... mappings) {
         return this.mappings(List.of(mappings));
      }

      public RecipeSchemaProvider.SchemaDataBuilder mappings(List<String> mappings) {
         this.mappings.addAll(mappings);
         return this;
      }

      public RecipeSchemaProvider.SchemaDataBuilder keysForUniqueId(String... keys) {
         return this.keysForUniqueId(List.of(keys));
      }

      public RecipeSchemaProvider.SchemaDataBuilder keysForUniqueId(List<String> keys) {
         if (this.unique == null) {
            this.unique = new ArrayList<>(keys);
         } else {
            this.unique.addAll(keys);
         }

         return this;
      }

      public RecipeSchemaProvider.SchemaDataBuilder postProcessors(RecipePostProcessor... processors) {
         return this.postProcessors(List.of(processors));
      }

      public RecipeSchemaProvider.SchemaDataBuilder postProcessors(List<RecipePostProcessor> processors) {
         if (this.postProcessors == null) {
            this.postProcessors = new ArrayList<>(processors);
         } else {
            this.postProcessors.addAll(processors);
         }

         return this;
      }

      public RecipeSchemaProvider.SchemaDataBuilder mergeData(boolean keys, boolean constructors, boolean unique, boolean postProcessors) {
         this.mergeData = new RecipeSchemaData.MergeData(keys, constructors, unique, postProcessors);
         return this;
      }

      RecipeSchemaData build() {
         return new RecipeSchemaData(
            this.parent,
            this.overrideType,
            this.recipeFactory,
            this.keys,
            this.constructors,
            this.functions,
            this.overrideKeys,
            this.hidden,
            this.mappings,
            this.unique,
            this.postProcessors,
            this.mergeData
         );
      }
   }
}
