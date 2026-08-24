package dev.latvian.mods.kubejs.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.core.RecipeLikeKJS;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.error.MissingComponentException;
import dev.latvian.mods.kubejs.error.RecipeComponentException;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.StringUtilsWrapper;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentValue;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentValueMap;
import dev.latvian.mods.kubejs.recipe.component.RecipeValidationContext;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.ingredientaction.ConsumeAction;
import dev.latvian.mods.kubejs.recipe.ingredientaction.CustomIngredientAction;
import dev.latvian.mods.kubejs.recipe.ingredientaction.DamageAction;
import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientAction;
import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientActionHolder;
import dev.latvian.mods.kubejs.recipe.ingredientaction.KeepAction;
import dev.latvian.mods.kubejs.recipe.ingredientaction.ReplaceAction;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.postprocessing.RecipePostProcessor;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.ErrorStack;
import dev.latvian.mods.kubejs.util.KubeResourceLocation;
import dev.latvian.mods.kubejs.util.SlotFilter;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.CustomJavaToJsWrapper;
import dev.latvian.mods.rhino.util.HideFromJS;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

public class KubeRecipe implements RecipeLikeKJS, CustomJavaToJsWrapper {
   public static final String CHANGED_MARKER = "_kubejs_changed_marker";
   public static final TypeInfo TYPE_INFO = TypeInfo.of(KubeRecipe.class);
   public ResourceLocation id;
   public RecipeTypeFunction type;
   public boolean newRecipe;
   public boolean removed;
   public SourceLine sourceLine = SourceLine.UNKNOWN;
   public String modifyResult = "";
   private RecipeComponentValueMap valueMap = RecipeComponentValueMap.EMPTY;
   private RecipeComponentValue<?>[] inputValues;
   private RecipeComponentValue<?>[] outputValues;
   public JsonObject originalJson = null;
   private MutableObject<Recipe<?>> originalRecipe = null;
   public JsonObject json = null;
   public boolean changed = false;
   public boolean creationError = false;
   protected List<IngredientActionHolder> recipeIngredientActions;

   public final Scriptable convertJavaToJs(Context cx, Scriptable scope, TypeInfo staticType) {
      return new RecipeFunction(cx, scope, staticType, this);
   }

   public void deserialize(boolean merge) {
      for (RecipeComponentValue<?> v : this.valueMap.holders) {
         try {
            v.key.component.readFromJson(this, Cast.to(v), this.json);
         } catch (Exception var7) {
            if (!v.key.optional()) {
               throw new RecipeComponentException("Failed to read required component '%s'".formatted(v.key), var7, v).source(this.sourceLine);
            }

            ConsoleJS.SERVER
               .warn(
                  "Failed to read component '%s' from recipe %s, falling back to default value".formatted(v.key, this),
                  this.sourceLine,
                  var7,
                  RecipesKubeEvent.POST_SKIP_ERROR
               );
         }

         if (v.value != null) {
            if (merge) {
               v.write();
            }
         } else if (!v.key.optional()) {
            throw new MissingComponentException(v.key.name, v.key, this.valueMap.keySet()).source(this.sourceLine);
         }
      }
   }

   public void serialize() {
      for (RecipeComponentValue<?> v : this.valueMap.holders) {
         if (v.shouldWrite()) {
            if (v.value == null) {
               throw new KubeRuntimeException("Value not set for " + v.key + " in recipe " + this).source(this.sourceLine);
            }

            v.key.component.writeToJson(this, Cast.to(v), this.json);
         }
      }
   }

   @Nullable
   public <T> T getValue(RecipeKey<T> key) {
      RecipeComponentValue<?> v = this.valueMap.getHolder(key);
      if (v == null) {
         throw new MissingComponentException(key.name, key, this.valueMap.keySet()).source(this.sourceLine);
      } else {
         return Cast.to(v.value);
      }
   }

   public <T> KubeRecipe setValue(RecipeKey<T> key, T value) {
      RecipeComponentValue<T> v = Cast.to(this.valueMap.getHolder(key));
      if (v == null) {
         throw new MissingComponentException(key.name, key, this.valueMap.keySet()).source(this.sourceLine);
      } else {
         v.value = value;
         v.write();
         this.save();
         return this;
      }
   }

   @Nullable
   public Object get(String key) {
      for (RecipeComponentValue<?> h : this.valueMap.holders) {
         for (String name : h.key.names) {
            if (name.equals(key)) {
               return h.value;
            }
         }
      }

      throw new MissingComponentException(key, null, this.valueMap.keySet()).source(this.sourceLine);
   }

   public KubeRecipe set(Context cx, String key, Object value) {
      for (RecipeComponentValue<?> h : this.valueMap.holders) {
         for (String name : h.key.names) {
            if (name.equals(key)) {
               ErrorStack errors = new ErrorStack();
               h.value = Cast.to(h.key.component.wrap(new RecipeScriptContext.Impl(cx, this, errors), Wrapper.unwrapped(value)));
               h.write();
               this.save();
               return this;
            }
         }
      }

      throw new MissingComponentException(key, null, this.valueMap.keySet()).source(this.sourceLine);
   }

   public void initValues(boolean save) {
      if (save) {
         this.save();
      }

      if (!this.type.schemaType.schema.keys.isEmpty()) {
         this.valueMap = new RecipeComponentValueMap(this.type.schemaType.schema.keys);
         if (save) {
            for (RecipeComponentValue<?> v : this.valueMap.holders) {
               if (v.key.optional()) {
                  v.value = Cast.to(v.key.optional.getDefaultValue(this.type.schemaType));
               }

               if (v.key.alwaysWrite) {
                  v.write();
               }
            }
         }
      }
   }

   @HideFromJS
   public RecipeComponentValue<?>[] getRecipeComponentValues() {
      return this.valueMap.holders;
   }

   public final void afterLoaded(ErrorStack stack) {
      this.afterLoaded(new RecipeValidationContext.Impl(this, stack));
   }

   public final void afterLoaded(RecipeValidationContext cx) {
      cx.errors().push(this);
      List<RecipePostProcessor> postProcessors = this.type.schemaType.schema.postProcessors();
      if (!postProcessors.isEmpty()) {
         cx.errors().push("Post Processors");

         for (int i = 0; i < postProcessors.size(); i++) {
            cx.errors().setKey(i);
            postProcessors.get(i).process(cx, this);
         }

         cx.errors().pop();
      }

      for (RecipeComponentValue<?> v : this.valueMap.holders) {
         cx.errors().setKey(v.key.name);
         v.validate(cx, this.sourceLine);
      }

      this.validate(cx);
      cx.errors().pop();
   }

   public void validate(RecipeValidationContext cx) {
   }

   public final void save() {
      this.changed = true;
   }

   public KubeRecipe id(KubeResourceLocation id) {
      this.id = id.wrapped();
      this.save();
      return this;
   }

   public KubeRecipe group(String g) {
      this.kjs$setGroup(g);
      return this;
   }

   public KubeRecipe merge(JsonObject j) {
      if (j != null && !j.isEmpty()) {
         for (Entry<String, JsonElement> entry : j.entrySet()) {
            this.json.add(entry.getKey(), entry.getValue());
         }

         this.save();
         this.deserialize(true);
      }

      return this;
   }

   public final boolean hasChanged() {
      if (this.changed) {
         return true;
      } else {
         for (RecipeComponentValue<?> vc : this.valueMap.holders) {
            if (vc.shouldWrite()) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public ResourceKey<RecipeSerializer<?>> kjs$getTypeKey() {
      return this.type.serializerKey;
   }

   @Deprecated
   @Override
   public final String kjs$getGroup() {
      JsonElement e = this.json.get("group");
      return e instanceof JsonPrimitive ? e.getAsString() : "";
   }

   @Deprecated
   @HideFromJS
   @Override
   public final void kjs$setGroup(String group) {
      if (!this.kjs$getGroup().equals(group)) {
         if (group.isEmpty()) {
            this.json.remove("group");
         } else {
            this.json.addProperty("group", group);
         }

         this.save();
      }
   }

   @Deprecated
   @Override
   public final ResourceLocation kjs$getOrCreateId() {
      return this.getOrCreateId();
   }

   @Deprecated
   @Override
   public final RecipeSchema kjs$getSchema(Context cx) {
      return this.type.schemaType.schema;
   }

   @Override
   public RecipeSerializer<?> kjs$getSerializer() {
      return this.type.schemaType.getSerializer();
   }

   public final RecipeComponentValue<?>[] inputValues() {
      if (this.inputValues == null) {
         if (this.type.schemaType.schema.inputCount() == 0) {
            this.inputValues = Cast.to(RecipeComponentValue.EMPTY_ARRAY);
         } else {
            ArrayList<Object> list = new ArrayList<>(this.type.schemaType.schema.inputCount());

            for (RecipeComponentValue<?> v : this.valueMap.holders) {
               if (v.key.role.isInput()) {
                  list.add(v);
               }
            }

            this.inputValues = list.toArray(new RecipeComponentValue[list.size()]);
         }
      }

      return this.inputValues;
   }

   public final RecipeComponentValue<?>[] outputValues() {
      if (this.outputValues == null) {
         if (this.type.schemaType.schema.outputCount() == 0) {
            this.outputValues = Cast.to(RecipeComponentValue.EMPTY_ARRAY);
         } else {
            ArrayList<Object> list = new ArrayList<>(this.type.schemaType.schema.outputCount());

            for (RecipeComponentValue<?> v : this.valueMap.holders) {
               if (v.key.role.isOutput()) {
                  list.add(v);
               }
            }

            this.outputValues = list.toArray(new RecipeComponentValue[list.size()]);
         }
      }

      return this.outputValues;
   }

   @Override
   public boolean hasInput(RecipeMatchContext cx, ReplacementMatchInfo match) {
      for (RecipeComponentValue<?> v : this.inputValues()) {
         if (v.matches(cx, match)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean replaceInput(RecipeScriptContext cx, ReplacementMatchInfo match, Object with) {
      boolean replaced = false;

      for (RecipeComponentValue<?> v : this.inputValues()) {
         replaced = v.replace(cx, match, with) || replaced;
      }

      if (replaced) {
         this.save();
      }

      return replaced;
   }

   @Override
   public boolean hasOutput(RecipeMatchContext cx, ReplacementMatchInfo match) {
      for (RecipeComponentValue<?> v : this.outputValues()) {
         if (v.matches(cx, match)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean replaceOutput(RecipeScriptContext cx, ReplacementMatchInfo match, Object with) {
      boolean replaced = false;

      for (RecipeComponentValue<?> v : this.outputValues()) {
         replaced = v.replace(cx, match, with) || replaced;
      }

      if (replaced) {
         this.save();
      }

      return replaced;
   }

   @Override
   public String toString() {
      return this.id == null && this.json == null ? "<no id> [" + this.type + "]" : this.getOrCreateId() + "[" + this.type + "]";
   }

   public String getId() {
      return this.getOrCreateId().toString();
   }

   public String getPath() {
      return this.getOrCreateId().getPath();
   }

   @HideFromJS
   public ResourceLocation getOrCreateId() {
      if (this.id == null) {
         RecipeTypeFunction js = this.getSerializationTypeFunction();
         String ids = CommonProperties.get().ignoreCustomUniqueRecipeIds ? null : js.schemaType.schema.buildUniqueId(this);
         String prefix = js.id.getNamespace() + ":kjs/";
         if (ids != null && !ids.isEmpty()) {
            ids = ids.replace(':', '_');
         } else {
            ids = StringUtilsWrapper.getUniqueId(this.json);
         }

         this.id = this.type.event.takeId(this, prefix, ids);
      }

      return this.id;
   }

   public String getFromToString() {
      StringBuilder sb = new StringBuilder();
      sb.append('[');

      for (RecipeComponentValue<?> v : this.inputValues()) {
         if (sb.length() > 1) {
            sb.append(",");
         }

         sb.append(v.value);
      }

      sb.append("] -> [");

      for (RecipeComponentValue<?> v : this.outputValues()) {
         if (sb.length() > 1) {
            sb.append(",");
         }

         sb.append(v.value);
      }

      return sb.append(']').toString();
   }

   public final void remove() {
      if (!this.removed) {
         this.removed = true;
         if (DevProperties.get().logRemovedRecipes) {
            ConsoleJS.SERVER.info("- " + this + ": " + this.getFromToString());
         } else if (ConsoleJS.SERVER.shouldPrintDebug()) {
            ConsoleJS.SERVER.debug("- " + this + ": " + this.getFromToString());
         }
      }
   }

   @Deprecated(
      forRemoval = true
   )
   public KubeRecipe stage(String s) {
      throw new KubeRuntimeException("recipe.stage() is no longer supported by default due to vanilla changes!").source(this.sourceLine);
   }

   public RecipeTypeFunction getSerializationTypeFunction() {
      return this.type;
   }

   public KubeRecipe serializeChanges() {
      if (this.newRecipe || this.hasChanged()) {
         this.serialize();
         if (!this.modifyResult.isEmpty()) {
            this.json.addProperty("kubejs:modify_result", this.modifyResult);
         }

         if (this.recipeIngredientActions != null && !this.recipeIngredientActions.isEmpty()) {
            try {
               this.json
                  .add(
                     "kubejs:ingredient_actions",
                     (JsonElement)IngredientActionHolder.LIST_CODEC.encodeStart(this.type.event.ops.json(), this.recipeIngredientActions).getOrThrow()
                  );
            } catch (Throwable var2) {
               ConsoleJS.SERVER.error("Failed to encode kubejs:ingredient_actions", this.sourceLine, var2, RecipesKubeEvent.CREATE_RECIPE_SKIP_ERROR);
            }
         }

         if (this.newRecipe) {
            this.json.addProperty("type", this.getSerializationTypeFunction().schemaType.serializerType);
         }

         this.json.add("_kubejs_changed_marker", this.sourceLine.toJson());
      }

      return this;
   }

   @Nullable
   public Recipe<?> getOriginalRecipe() {
      if (this.originalRecipe == null) {
         this.originalRecipe = new MutableObject();

         try {
            RecipeSerializer<?> serializer = this.type.schemaType.getSerializer();
            RegistryOps<JsonElement> ops = this.type.event.ops.json();
            Optional.ofNullable(serializer.codec())
               .<DataResult>map(DataResult::success)
               .orElseGet(() -> DataResult.error(() -> "Codec for " + serializer.getClass().getName() + " is null!"))
               .flatMap(codec -> ops.getMap(this.json).flatMap(map -> codec.decode(ops, map)))
               .mapError(err -> "Error parsing recipe " + this.id + ": " + err)
               .ifSuccess(this.originalRecipe::setValue)
               .ifError(err -> {
                  if (DevProperties.get().logErroringParsedRecipes) {
                     ConsoleJS.SERVER.error(err.message());
                  } else {
                     RecipeManager.LOGGER.error(err.message());
                  }
               });
         } catch (Throwable var3) {
            ConsoleJS.SERVER.error("Could not create recipe from json for " + this, var3);
         }
      }

      return (Recipe<?>)this.originalRecipe.getValue();
   }

   public ItemStack getOriginalRecipeResult() {
      Recipe<?> original = this.getOriginalRecipe();
      if (original == null) {
         ConsoleJS.SERVER.warn("Original recipe is null - could not get result");
         return ItemStack.EMPTY;
      } else {
         ItemStack result = original.getResultItem(this.type.event.registries.access());
         return result == null ? ItemStack.EMPTY : result;
      }
   }

   public List<Ingredient> getOriginalRecipeIngredients() {
      Recipe<?> original = this.getOriginalRecipe();
      if (original == null) {
         ConsoleJS.SERVER.warn("Original recipe is null - could not get ingredients");
         return List.of();
      } else {
         return List.copyOf(original.getIngredients());
      }
   }

   public KubeRecipe ingredientAction(SlotFilter filter, IngredientAction action) {
      if (this.recipeIngredientActions == null) {
         this.recipeIngredientActions = new ArrayList<>(2);
      }

      this.recipeIngredientActions.add(new IngredientActionHolder(action, filter));
      this.save();
      return this;
   }

   public final KubeRecipe damageIngredient(SlotFilter filter, int damage) {
      return this.ingredientAction(filter, new DamageAction(damage));
   }

   public final KubeRecipe damageIngredient(SlotFilter filter) {
      return this.damageIngredient(filter, 1);
   }

   public final KubeRecipe replaceIngredient(SlotFilter filter, ItemStack item) {
      return this.ingredientAction(filter, new ReplaceAction(item));
   }

   public final KubeRecipe customIngredientAction(SlotFilter filter, String id) {
      return this.ingredientAction(filter, new CustomIngredientAction(id));
   }

   public final KubeRecipe keepIngredient(SlotFilter filter) {
      return this.ingredientAction(filter, KeepAction.INSTANCE);
   }

   public final KubeRecipe consumeIngredient(SlotFilter filter) {
      return this.ingredientAction(filter, ConsumeAction.INSTANCE);
   }

   public final KubeRecipe modifyResult(String id) {
      this.modifyResult = id;
      this.save();
      return this;
   }
}
