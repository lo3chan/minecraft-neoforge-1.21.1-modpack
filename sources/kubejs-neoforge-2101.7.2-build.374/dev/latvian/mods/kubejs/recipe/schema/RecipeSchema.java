package dev.latvian.mods.kubejs.recipe.schema;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.RecipeTypeFunction;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.UniqueIdBuilder;
import dev.latvian.mods.kubejs.recipe.schema.function.AddToListFunction;
import dev.latvian.mods.kubejs.recipe.schema.function.RecipeFunctionInstance;
import dev.latvian.mods.kubejs.recipe.schema.function.SetFunction;
import dev.latvian.mods.kubejs.recipe.schema.postprocessing.RecipePostProcessor;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.JsonUtils;
import dev.latvian.mods.rhino.util.RemapForJS;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SequencedCollection;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class RecipeSchema {
   public KubeRecipeFactory recipeFactory = KubeRecipeFactory.DEFAULT;
   public ResourceLocation typeOverride = null;
   public final List<RecipeKey<?>> keys;
   public final List<RecipeKey<?>> includedKeys;
   public final Map<RecipeKey<?>, RecipeOptional<?>> keyOverrides;
   public final Map<String, RecipeFunctionInstance> functions;
   private int inputCount;
   private int outputCount;
   private int minRequiredArguments;
   private Int2ObjectMap<RecipeConstructor> constructors;
   private boolean constructorsGenerated;
   private List<RecipeKey<?>> uniqueIds;
   boolean hidden;
   private List<RecipePostProcessor> postProcessors;

   public RecipeSchema(Map<RecipeKey<?>, RecipeOptional<?>> keyOverrides, List<RecipeKey<?>> keys) {
      this.keys = List.copyOf(keys);
      this.keyOverrides = Map.copyOf(keyOverrides);
      this.includedKeys = List.copyOf(this.keys.stream().filter(kx -> (kx.optional == null || !kx.excluded) && !this.keyOverrides.containsKey(kx)).toList());
      this.functions = new LinkedHashMap<>(0);
      this.minRequiredArguments = 0;
      this.inputCount = 0;
      this.outputCount = 0;
      HashSet<String> set = new HashSet<>();

      for (int i = 0; i < this.includedKeys.size(); i++) {
         RecipeKey<?> k = this.includedKeys.get(i);
         if (k.optional()) {
            if (this.minRequiredArguments == 0) {
               this.minRequiredArguments = i;
            }
         } else if (this.minRequiredArguments > 0) {
            throw new IllegalArgumentException("Required key '" + k.name + "' must be ahead of optional keys!");
         }

         if (!set.add(k.name)) {
            throw new IllegalArgumentException("Duplicate key '" + k.name + "' found!");
         }

         if (k.role.isInput()) {
            this.inputCount++;
         } else if (k.role.isOutput()) {
            this.outputCount++;
         }

         if (k.alwaysWrite && k.optional() && k.optional.isDefault()) {
            throw new IllegalArgumentException("Key '" + k + "' can't have alwaysWrite() enabled with defaultOptional()!");
         }
      }

      if (this.minRequiredArguments == 0) {
         this.minRequiredArguments = this.includedKeys.size();
      }

      this.uniqueIds = List.of();
      this.hidden = false;
   }

   public RecipeSchema(RecipeKey<?>... keys) {
      this(Map.of(), List.of(keys));
   }

   public RecipeSchema factory(KubeRecipeFactory factory) {
      this.recipeFactory = factory;
      return this;
   }

   public RecipeSchema typeOverride(ResourceLocation id) {
      this.typeOverride = id;
      return this;
   }

   public RecipeSchema constructor(RecipeConstructor constructor) {
      if (this.constructors == null) {
         this.constructors = new Int2ObjectArrayMap(this.keys.size() - this.minRequiredArguments() + 1);
      }

      if (this.constructors.put(constructor.keys.size(), constructor) != null) {
         throw new IllegalStateException("Constructor with " + constructor.keys.size() + " arguments already exists!");
      } else {
         return this;
      }
   }

   @RemapForJS("addConstructor")
   public RecipeSchema constructor(RecipeKey<?>... keys) {
      return this.constructor(new RecipeConstructor(keys));
   }

   public RecipeSchema uniqueId(RecipeKey<?> key) {
      this.uniqueIds = List.of(key);
      return this;
   }

   public RecipeSchema uniqueIds(SequencedCollection<RecipeKey<?>> keys) {
      this.uniqueIds = List.copyOf(keys);
      return this;
   }

   @Nullable
   public String buildUniqueId(KubeRecipe r) {
      if (this.uniqueIds.isEmpty()) {
         return null;
      } else if (this.uniqueIds.size() == 1) {
         RecipeKey<?> key = (RecipeKey<?>)this.uniqueIds.getFirst();
         Object value = r.getValue(key);
         if (value != null) {
            UniqueIdBuilder builder = new UniqueIdBuilder(new StringBuilder());
            ((RecipeComponent<Object>)key.component).buildUniqueId(builder, Cast.to(value));
            return builder.build();
         } else {
            return null;
         }
      } else {
         StringBuilder sb = new StringBuilder();
         UniqueIdBuilder builder = new UniqueIdBuilder(new StringBuilder());
         boolean first = true;

         for (RecipeKey<?> key : this.keys) {
            Object value = r.getValue(key);
            if (value != null) {
               ((RecipeComponent<Object>)key.component).buildUniqueId(builder, Cast.to(value));
               String result = builder.build();
               if (result != null) {
                  if (first) {
                     first = false;
                  } else {
                     sb.append('/');
                  }

                  sb.append(result);
               }
            }
         }

         return sb.isEmpty() ? null : sb.toString();
      }
   }

   public Int2ObjectMap<RecipeConstructor> constructors() {
      if (this.constructors == null) {
         this.constructorsGenerated = true;
         this.constructors = this.includedKeys.isEmpty()
            ? new Int2ObjectArrayMap()
            : new Int2ObjectArrayMap(this.includedKeys.size() - this.minRequiredArguments + 1);
         boolean dev = DevProperties.get().logRecipeDebug;
         if (dev) {
            KubeJS.LOGGER.info("Generating constructors for {}", new RecipeConstructor(this.includedKeys));
         }

         for (int a = this.minRequiredArguments; a <= this.includedKeys.size(); a++) {
            RecipeConstructor c = new RecipeConstructor(List.copyOf(this.includedKeys.subList(0, a)));
            this.constructors.put(a, c);
            if (dev) {
               KubeJS.LOGGER.info("> {}: {}", a, c);
            }
         }
      }

      return this.constructors;
   }

   public List<RecipeKey<?>> uniqueIds() {
      return this.uniqueIds;
   }

   public int minRequiredArguments() {
      return this.minRequiredArguments;
   }

   public int inputCount() {
      return this.inputCount;
   }

   public int outputCount() {
      return this.outputCount;
   }

   public boolean isHidden() {
      return this.hidden;
   }

   public boolean constructorsGenerated() {
      this.constructors();
      return this.constructorsGenerated;
   }

   public KubeRecipe deserialize(SourceLine sourceLine, RecipeTypeFunction type, @Nullable ResourceLocation id, JsonObject json) {
      KubeRecipe r = this.recipeFactory.create(type, sourceLine, id == null);
      r.id = id;
      r.json = json;
      r.newRecipe = id == null;
      r.originalJson = json != null && id != null ? (JsonObject)JsonUtils.copy(json) : null;
      r.deserialize(false);
      return r;
   }

   public RecipeSchema function(RecipeFunctionInstance function) {
      this.functions.put(function.name(), function);
      return this;
   }

   public <T> RecipeSchema setOpFunction(String name, RecipeKey<T> key, T value) {
      return this.function(new RecipeFunctionInstance(name, new SetFunction.Resolved<>(key, value)));
   }

   public <T> RecipeSchema addToListOpFunction(String name, RecipeKey<List<T>> key) {
      return this.function(new RecipeFunctionInstance(name, new AddToListFunction.Resolved<>(key)));
   }

   public RecipeSchema postProcessor(RecipePostProcessor processor) {
      if (this.postProcessors == null) {
         this.postProcessors = new ArrayList<>(1);
      }

      this.postProcessors.add(processor);
      return this;
   }

   @Nullable
   public <T> RecipeKey<T> getOptionalKey(String id) {
      for (RecipeKey<?> key : this.keys) {
         if (key.name.equals(id)) {
            return (RecipeKey<T>)key;
         }
      }

      return null;
   }

   public <T> RecipeKey<T> getKey(String id) {
      RecipeKey<T> key = this.getOptionalKey(id);
      if (key != null) {
         return key;
      } else {
         throw new NullPointerException("Key '" + id + "' not found");
      }
   }

   public List<RecipePostProcessor> postProcessors() {
      return this.postProcessors == null ? List.of() : this.postProcessors;
   }

   public JsonObject toJson(RecipeSchemaStorage storage, RecipeSchemaType schemaType, RegistryOps<JsonElement> ops) {
      JsonObject json = new JsonObject();
      if (this.keys != null && !this.keys.isEmpty()) {
         JsonArray a = new JsonArray();

         for (RecipeKey<?> key : this.keys) {
            a.add(key.toJson(storage, schemaType, ops));
         }

         json.add("keys", a);
      }

      if (!this.uniqueIds.isEmpty()) {
         JsonArray a = new JsonArray();

         for (RecipeKey<?> key : this.uniqueIds) {
            a.add(key.name);
         }

         if (!a.isEmpty()) {
            json.add("unique", a);
         }
      }

      if (!this.constructorsGenerated()) {
         JsonArray a = new JsonArray();
         ObjectIterator var12 = this.constructors().values().iterator();

         while (var12.hasNext()) {
            RecipeConstructor c = (RecipeConstructor)var12.next();
            a.add(c.toJson(schemaType, ops));
         }

         if (!a.isEmpty()) {
            json.add("constructors", a);
         }
      }

      if (this.postProcessors != null && !this.postProcessors.isEmpty()) {
         JsonArray a = new JsonArray();

         for (RecipePostProcessor p : this.postProcessors) {
            a.add((JsonElement)storage.recipePostProcessorCodec.encodeStart(ops, p).getOrThrow());
         }

         json.add("post_processors", a);
      }

      return json;
   }
}
