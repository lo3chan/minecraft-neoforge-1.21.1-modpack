package dev.latvian.mods.kubejs.recipe.schema;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.DataResult.Error;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.RecipeTypeRegistryContext;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.schema.function.RecipeFunctionInstance;
import dev.latvian.mods.kubejs.recipe.schema.function.RecipeSchemaFunction;
import dev.latvian.mods.kubejs.recipe.schema.function.ResolvedRecipeSchemaFunction;
import dev.latvian.mods.kubejs.recipe.schema.postprocessing.RecipePostProcessor;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.JsonUtils;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.SequencedMap;
import java.util.Map.Entry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

public class JsonRecipeSchemaLoader {
   public static void load(RecipeTypeRegistryContext ctx, DynamicOps<JsonElement> jsonOps, RecipeSchemaRegistry event, ResourceManager resourceManager) {
      HashMap<ResourceLocation, JsonRecipeSchemaLoader.RecipeSchemaBuilder> map = new HashMap<>();
      Codec<RecipeSchemaData> recipeSchemaDataCodec = RecipeSchemaData.CODEC.apply(ctx);

      for (Entry<ResourceLocation, Resource> entry : resourceManager.listResources("kubejs/recipe_schema", path -> path.getPath().endsWith(".json")).entrySet()) {
         try (BufferedReader reader = entry.getValue().openAsReader()) {
            JsonObject json = (JsonObject)JsonUtils.GSON.fromJson(reader, JsonObject.class);
            ResourceLocation id = entry.getKey()
               .withPath(entry.getKey().getPath().substring("kubejs/recipe_schema/".length(), entry.getKey().getPath().length() - ".json".length()));
            DataResult<RecipeSchemaData> data = recipeSchemaDataCodec.parse(jsonOps, json);
            if (data.isSuccess()) {
               map.put(id, new JsonRecipeSchemaLoader.RecipeSchemaBuilder(id, (RecipeSchemaData)data.getOrThrow()));
            } else {
               ConsoleJS.SERVER
                  .error("Error parsing recipe schema json " + entry.getKey() + ": " + data.error().<String>map(Error::message).orElse("Unknown Error"));
            }
         } catch (Exception var18) {
            ConsoleJS.SERVER.error("Error reading recipe schema json " + entry.getKey(), var18);
         }
      }

      for (JsonRecipeSchemaLoader.RecipeSchemaBuilder builder : map.values()) {
         for (String m : builder.data.mappings()) {
            ctx.storage().mappings.put(m, builder.id);
         }
      }

      for (JsonRecipeSchemaLoader.RecipeSchemaBuilder builder : map.values()) {
         builder.hidden = builder.data.hidden().orElse(null);
         builder.parent = builder.data.parent().map(map::get).orElse(null);
         builder.overrideType = builder.data.overrideType().orElse(null);
         if (builder.data.recipeFactory().isPresent()) {
            ResourceLocation fname = builder.data.recipeFactory().get();
            builder.recipeFactory = ctx.storage().recipeTypes.get(fname);
            if (builder.recipeFactory == null) {
               throw new NullPointerException("Recipe factory '" + fname + "' not found for recipe schema '" + builder.id + "'");
            }
         }

         if (builder.data.keys().isPresent()) {
            builder.keys = new ArrayList<>();

            for (RecipeSchemaData.RecipeKeyData keyData : builder.data.keys().get()) {
               try {
                  RecipeComponent<?> type = keyData.type();
                  RecipeKey<?> key = type.key(keyData.name(), keyData.role());
                  if (keyData.defaultOptional()) {
                     key.defaultOptional();
                  } else if (keyData.optional().isPresent()) {
                     JsonElement optionalJson = keyData.optional().get();

                     try {
                        key.optional = RecipeOptional.unit(((Pair)key.codec.decode(jsonOps, optionalJson).getOrThrow()).getFirst());
                     } catch (Exception var14) {
                        throw new IllegalArgumentException(
                           "Failed to create optional value for key '" + key + "' of '" + builder.id + "' from " + optionalJson, var14
                        );
                     }
                  }

                  if (!keyData.alternativeNames().isEmpty()) {
                     key.names.addAll(keyData.alternativeNames());
                  }

                  key.excluded = keyData.excluded();
                  if (!keyData.functionNames().isEmpty()) {
                     key.functionNames = keyData.functionNames();
                  }

                  key.alwaysWrite = keyData.alwaysWrite();
                  builder.keys.add(key);
               } catch (Exception var15) {
                  ConsoleJS.SERVER.error("Error parsing recipe schema '" + builder.id + "' key " + keyData.name(), var15);
               }
            }
         }

         if (builder.data.constructors().isPresent()) {
            builder.constructors = builder.data.constructors().get();
         }

         if (builder.data.unique().isPresent()) {
            builder.unique = builder.data.unique().get();
         }

         if (builder.data.functions().isPresent()) {
            builder.functions = builder.data.functions().get();
         }

         if (!builder.data.overrideKeys().isEmpty()) {
            builder.overrideKeys = builder.data.overrideKeys();
         }

         if (builder.data.postProcessors().isPresent()) {
            builder.postProcessors = builder.data.postProcessors().get();
         }
      }

      for (JsonRecipeSchemaLoader.RecipeSchemaBuilder builder : map.values()) {
         RecipeSchema schema = builder.getSchema(jsonOps);
         ObjectIterator var34 = schema.constructors().values().iterator();

         while (var34.hasNext()) {
            RecipeConstructor constructor = (RecipeConstructor)var34.next();

            for (RecipeKey<?> keyx : schema.keys) {
               if (keyx.optional != null && !constructor.keys.contains(keyx) && !constructor.overrides.containsKey(keyx)) {
                  constructor.defaultValue(keyx, Cast.to(keyx.optional));
               }
            }
         }
      }

      for (JsonRecipeSchemaLoader.RecipeSchemaBuilder builder : map.values()) {
         RecipeSchema schema = builder.getSchema(jsonOps);
         event.namespace(builder.id.getNamespace()).register(builder.id.getPath(), schema);
      }
   }

   private static final class RecipeSchemaBuilder {
      private final ResourceLocation id;
      private final RecipeSchemaData data;
      private RecipeSchema schema;
      private JsonRecipeSchemaLoader.RecipeSchemaBuilder parent;
      private ResourceLocation overrideType;
      private List<RecipeKey<?>> keys;
      private List<RecipeSchemaData.ConstructorData> constructors;
      private Map<String, RecipeSchemaFunction> functions;
      private KubeRecipeFactory recipeFactory;
      private List<String> unique;
      private Boolean hidden;
      private Map<String, JsonElement> overrideKeys;
      private List<RecipePostProcessor> postProcessors;

      private RecipeSchemaBuilder(ResourceLocation id, RecipeSchemaData data) {
         this.id = id;
         this.data = data;
      }

      private List<RecipeKey<?>> getKeys() {
         if (this.keys != null) {
            if (!this.data.mergeKeys()) {
               return this.keys;
            } else {
               LinkedHashMap<String, RecipeKey<?>> merged = new LinkedHashMap<>();
               if (this.parent != null) {
                  for (RecipeKey<?> key : this.parent.getKeys()) {
                     merged.put(key.name, key);
                  }
               }

               int newOptionals = 0;

               for (RecipeKey<?> key : this.keys) {
                  if (newOptionals != 0 && !key.optional()) {
                     throw new IllegalArgumentException(
                        "Required key '%s' must be before optional keys %s"
                           .formatted(key.name, this.requiredFirst(merged).stream().skip(merged.size() - newOptionals).map(k -> k.name).toList())
                     );
                  }

                  boolean oldKeyOptional = merged.containsKey(key.name) && merged.get(key.name).optional();
                  if (key.optional() && !oldKeyOptional) {
                     newOptionals++;
                  } else if (!key.optional() && oldKeyOptional) {
                     throw new IllegalArgumentException("Optional key '%s' from parent may not be replaced by required key!".formatted(key.name));
                  }

                  merged.put(key.name, key);
               }

               return this.requiredFirst(merged);
            }
         } else {
            return this.parent != null ? this.parent.getKeys() : List.of();
         }
      }

      private List<RecipeKey<?>> requiredFirst(SequencedMap<String, RecipeKey<?>> map) {
         ArrayList<RecipeKey<?>> required = new ArrayList<>(map.size());
         ArrayList<RecipeKey<?>> optional = new ArrayList<>(map.size());

         for (RecipeKey<?> key : map.sequencedValues()) {
            (key.optional() ? optional : required).add(key);
         }

         required.addAll(optional);
         return required;
      }

      private List<RecipeSchemaData.ConstructorData> getConstructors() {
         if (this.constructors != null) {
            if (this.data.mergeConstructors()) {
               ArrayList<RecipeSchemaData.ConstructorData> list = new ArrayList<>();
               if (this.parent != null) {
                  list.addAll(this.parent.getConstructors());
               }

               list.addAll(this.constructors);
               return list;
            } else {
               return this.constructors;
            }
         } else {
            return this.parent != null ? this.parent.getConstructors() : List.of();
         }
      }

      private void gatherFunctions(Map<String, RecipeSchemaFunction> list) {
         if (this.parent != null) {
            this.parent.gatherFunctions(list);
         }

         if (this.functions != null) {
            list.putAll(this.functions);
         }
      }

      private KubeRecipeFactory getRecipeFactory() {
         if (this.recipeFactory != null) {
            return this.recipeFactory;
         } else {
            return this.parent != null ? this.parent.getRecipeFactory() : null;
         }
      }

      private List<String> getUnique() {
         if (this.unique != null) {
            if (this.data.mergeUnique()) {
               LinkedHashSet<String> u = new LinkedHashSet<>();
               if (this.parent != null) {
                  u.addAll(this.parent.getUnique());
               }

               u.addAll(this.unique);
               return List.copyOf(u);
            } else {
               return this.unique;
            }
         } else {
            return this.parent != null ? this.parent.getUnique() : List.of();
         }
      }

      private boolean isHidden() {
         if (this.hidden != null) {
            return this.hidden;
         } else {
            return this.parent != null ? this.parent.isHidden() : false;
         }
      }

      private List<RecipePostProcessor> getPostProcessors() {
         if (this.postProcessors != null) {
            if (this.data.mergePostProcessors()) {
               ArrayList<RecipePostProcessor> list = new ArrayList<>();
               if (this.parent != null) {
                  list.addAll(this.parent.getPostProcessors());
               }

               list.addAll(this.postProcessors);
               return list;
            } else {
               return this.postProcessors;
            }
         } else {
            return this.parent != null ? this.parent.getPostProcessors() : List.of();
         }
      }

      private RecipeSchema getSchema(DynamicOps<JsonElement> jsonOps) {
         if (this.schema == null) {
            if (this.overrideType == null
               && this.keys == null
               && this.constructors == null
               && this.functions == null
               && this.recipeFactory == null
               && this.unique == null
               && this.overrideKeys == null) {
               if (this.parent != null) {
                  this.schema = this.parent.getSchema(jsonOps);
               } else {
                  this.schema = new RecipeSchema(Map.of(), List.of());
                  this.schema.constructor();
               }
            } else {
               List<RecipeKey<?>> keys = this.getKeys();
               HashMap<String, RecipeKey<?>> keyMap = new HashMap<>();

               for (RecipeKey<?> key : keys) {
                  keyMap.put(key.name, key);
               }

               HashMap<String, RecipeSchemaFunction> functionMap = new HashMap<>();
               this.gatherFunctions(functionMap);
               Map<RecipeKey<?>, RecipeOptional<?>> keyOverrides = Map.of();
               if (this.overrideKeys != null) {
                  keyOverrides = new Reference2ObjectOpenHashMap(this.overrideKeys.size());

                  for (Entry<String, JsonElement> entry : this.overrideKeys.entrySet()) {
                     RecipeKey<?> key = keyMap.get(entry.getKey());
                     if (key == null) {
                        throw new NullPointerException("Key '" + entry.getKey() + "' not found in key overrides of recipe schema '" + this.id + "'");
                     }

                     try {
                        keyOverrides.put(key, RecipeOptional.unit(((Pair)key.codec.decode(jsonOps, entry.getValue()).getOrThrow()).getFirst()));
                     } catch (Exception var17) {
                        throw new IllegalArgumentException(
                           "Failed to create optional value for key '" + key + "' of '" + this.id + "' from " + entry.getValue(), var17
                        );
                     }
                  }
               }

               this.schema = new RecipeSchema(keyOverrides, this.getKeys());
               this.schema.typeOverride = this.overrideType;
               KubeRecipeFactory rf = this.getRecipeFactory();
               if (rf != null) {
                  this.schema.recipeFactory = rf;
               }

               List<RecipeSchemaData.ConstructorData> constructors = this.getConstructors();
               if (!constructors.isEmpty()) {
                  for (RecipeSchemaData.ConstructorData c : constructors) {
                     ArrayList<RecipeKey<?>> cKeys = new ArrayList<>();

                     for (String keyName : c.keys()) {
                        RecipeKey<?> key = keyMap.get(keyName);
                        if (key == null) {
                           throw new NullPointerException("Key '" + keyName + "' not found in constructor of recipe schema '" + this.id + "'");
                        }

                        cKeys.add(key);
                     }

                     RecipeConstructor constructor = new RecipeConstructor(List.copyOf(cKeys));
                     if (!c.overrides().isEmpty()) {
                        for (Entry<String, JsonElement> entry : c.overrides().entrySet()) {
                           RecipeKey<?> key = keyMap.get(entry.getKey());
                           if (key == null) {
                              throw new NullPointerException(
                                 "Key '" + entry.getKey() + "' not found in overrides of constructor of recipe schema '" + this.id + "'"
                              );
                           }

                           try {
                              constructor.overrideValue(key, Cast.to(key.codec.parse(jsonOps, entry.getValue()).getOrThrow()));
                           } catch (Exception var16) {
                              throw new IllegalArgumentException(
                                 "Failed to create optional value for key '" + key + "' of '" + this.id + "' from " + entry.getValue(), var16
                              );
                           }
                        }
                     }

                     this.schema.constructor(constructor);
                  }
               }

               for (Entry<String, RecipeSchemaFunction> entry : functionMap.entrySet()) {
                  DataResult<ResolvedRecipeSchemaFunction> func = entry.getValue().resolve(jsonOps, this.schema);
                  if (!func.isSuccess()) {
                     throw new NullPointerException(
                        "Failed to parse function '"
                           + entry.getKey()
                           + "' of recipe schema '"
                           + this.id
                           + "': "
                           + func.error().<String>map(Error::message).orElse("Unknown Error")
                     );
                  }

                  this.schema.function(new RecipeFunctionInstance(entry.getKey(), (ResolvedRecipeSchemaFunction)func.getOrThrow()));
               }

               List<String> uniqueKeyNames = this.getUnique();
               if (!uniqueKeyNames.isEmpty()) {
                  ArrayList<RecipeKey<?>> uniqueKeys = new ArrayList<>();

                  for (String keyName : uniqueKeyNames) {
                     RecipeKey<?> key = keyMap.get(keyName);
                     if (key == null) {
                        throw new NullPointerException("Key '" + keyName + "' not found in unique keys of recipe schema '" + this.id + "'");
                     }

                     uniqueKeys.add(key);
                  }

                  this.schema.uniqueIds(uniqueKeys);
               }

               this.schema.hidden = this.isHidden();

               for (RecipePostProcessor postProcessor : this.getPostProcessors()) {
                  this.schema.postProcessor(postProcessor);
               }
            }
         }

         return this.schema;
      }
   }
}
