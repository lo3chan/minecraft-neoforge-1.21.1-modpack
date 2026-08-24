package dev.latvian.mods.kubejs.recipe;

import com.google.common.base.Stopwatch;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DataResult.Error;
import com.mojang.serialization.DataResult.Success;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.core.RecipeManagerKJS;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.error.RecipeComponentException;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.plugin.builtin.event.ServerEvents;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.StringUtilsWrapper;
import dev.latvian.mods.kubejs.recipe.filter.ConstantFilter;
import dev.latvian.mods.kubejs.recipe.filter.IDFilter;
import dev.latvian.mods.kubejs.recipe.filter.OrFilter;
import dev.latvian.mods.kubejs.recipe.filter.RecipeFilter;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.filter.RegexIDFilter;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.recipe.schema.RecipeConstructor;
import dev.latvian.mods.kubejs.recipe.schema.RecipeNamespace;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaStorage;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaType;
import dev.latvian.mods.kubejs.recipe.schema.UnknownRecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.function.RecipeFunctionInstance;
import dev.latvian.mods.kubejs.recipe.special.SpecialRecipeSerializerManager;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.server.ChangesForChat;
import dev.latvian.mods.kubejs.server.DataExport;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import dev.latvian.mods.kubejs.util.ErrorStack;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.JsonIO;
import dev.latvian.mods.kubejs.util.JsonUtils;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.kubejs.util.RegistryOpsContainer;
import dev.latvian.mods.kubejs.util.TimeJS;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.HideFromJS;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Reference2ObjectLinkedOpenHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import org.jetbrains.annotations.Nullable;

public class RecipesKubeEvent implements KubeEvent {
   public static final Pattern POST_SKIP_ERROR = ConsoleJS.methodPattern(RecipesKubeEvent.class, "post");
   public static final Pattern CREATE_RECIPE_SKIP_ERROR = ConsoleJS.methodPattern(RecipesKubeEvent.class, "createRecipe");
   private static final Predicate<KubeRecipe> RECIPE_NOT_REMOVED = r -> r != null && !r.removed;
   private static final Predicate<KubeRecipe> RECIPE_IS_SYNTHETIC = r -> !r.newRecipe;
   private final Stopwatch overallTimer;
   public final RecipeSchemaStorage recipeSchemaStorage;
   public final RegistryAccessContainer registries;
   public final ResourceManager resourceManager;
   public final RegistryOpsContainer ops;
   public final Map<ResourceLocation, KubeRecipe> originalRecipes;
   public final Collection<KubeRecipe> addedRecipes;
   public final Collection<KubeRecipe> removedRecipes;
   int modifiedCount;
   int failedCount;
   private final Map<ResourceLocation, KubeRecipe> takenIds;
   private final Map<String, Object> recipeFunctions;
   public final transient RecipeTypeFunction vanillaShaped;
   public final transient RecipeTypeFunction vanillaShapeless;
   public final RecipeTypeFunction shaped;
   public final RecipeTypeFunction shapeless;
   public final RecipeTypeFunction smelting;
   public final RecipeTypeFunction blasting;
   public final RecipeTypeFunction smoking;
   public final RecipeTypeFunction campfireCooking;
   public final RecipeTypeFunction stonecutting;
   public final RecipeTypeFunction smithing;
   public final RecipeTypeFunction smithingTrim;

   public RecipesKubeEvent(ServerScriptManager manager, ResourceManager resourceManager) {
      ConsoleJS.SERVER.info("Initializing recipe event...");
      this.overallTimer = Stopwatch.createStarted();
      this.recipeSchemaStorage = manager.recipeSchemaStorage;
      this.registries = manager.getRegistries();
      this.resourceManager = resourceManager;
      this.ops = new RegistryOpsContainer(
         new KubeRecipeEventOps<Tag>(this, this.registries.nbt()),
         new KubeRecipeEventOps<JsonElement>(this, this.registries.json()),
         new KubeRecipeEventOps<Object>(this, this.registries.java())
      );
      this.originalRecipes = new HashMap<>();
      this.addedRecipes = new ConcurrentLinkedQueue<>();
      this.removedRecipes = new ConcurrentLinkedQueue<>();
      this.recipeFunctions = new HashMap<>();
      this.takenIds = new ConcurrentHashMap<>();

      for (RecipeNamespace namespace : this.recipeSchemaStorage.namespaces.values()) {
         HashMap<String, RecipeTypeFunction> nsMap = new HashMap<>();
         this.recipeFunctions.put(namespace.name, new NamespaceFunction(namespace, nsMap));

         for (Entry<String, RecipeSchemaType> entry : namespace.entrySet()) {
            RecipeTypeFunction func = new RecipeTypeFunction(this, entry.getValue());
            nsMap.put(entry.getValue().id.getPath(), func);
            this.recipeFunctions.put(entry.getValue().id.toString(), func);
         }
      }

      this.vanillaShaped = (RecipeTypeFunction)this.recipeFunctions.get("minecraft:crafting_shaped");
      this.vanillaShapeless = (RecipeTypeFunction)this.recipeFunctions.get("minecraft:crafting_shapeless");
      this.shaped = CommonProperties.get().serverOnly ? this.vanillaShaped : (RecipeTypeFunction)this.recipeFunctions.get("kubejs:shaped");
      this.shapeless = CommonProperties.get().serverOnly ? this.vanillaShapeless : (RecipeTypeFunction)this.recipeFunctions.get("kubejs:shapeless");
      this.smelting = (RecipeTypeFunction)this.recipeFunctions.get("minecraft:smelting");
      this.blasting = (RecipeTypeFunction)this.recipeFunctions.get("minecraft:blasting");
      this.smoking = (RecipeTypeFunction)this.recipeFunctions.get("minecraft:smoking");
      this.campfireCooking = (RecipeTypeFunction)this.recipeFunctions.get("minecraft:campfire_cooking");
      this.stonecutting = (RecipeTypeFunction)this.recipeFunctions.get("minecraft:stonecutting");
      this.smithing = (RecipeTypeFunction)this.recipeFunctions.get("minecraft:smithing_transform");
      this.smithingTrim = (RecipeTypeFunction)this.recipeFunctions.get("minecraft:smithing_trim");

      for (Entry<String, Object> entry : new ArrayList<>(this.recipeFunctions.entrySet())) {
         if (entry.getValue() instanceof RecipeTypeFunction && entry.getKey().indexOf(58) != -1) {
            String s = StringUtilsWrapper.snakeCaseToCamelCase(entry.getKey());
            if (!s.equals(entry.getKey())) {
               this.recipeFunctions.put(s, entry.getValue());
            }
         }
      }

      for (Entry<String, ResourceLocation> entryx : this.recipeSchemaStorage.mappings.entrySet()) {
         Object type = this.recipeFunctions.get(entryx.getValue().toString());
         if (type instanceof RecipeTypeFunction) {
            this.recipeFunctions.put(entryx.getKey(), type);
         }
      }

      this.recipeFunctions.put("shaped", this.shaped);
      this.recipeFunctions.put("shapeless", this.shapeless);
      this.recipeFunctions.put("smelting", this.smelting);
      this.recipeFunctions.put("blasting", this.blasting);
      this.recipeFunctions.put("smoking", this.smoking);
      this.recipeFunctions.put("campfireCooking", this.campfireCooking);
      this.recipeFunctions.put("stonecutting", this.stonecutting);
      this.recipeFunctions.put("smithing", this.smithing);
      this.recipeFunctions.put("smithingTrim", this.smithingTrim);
   }

   @HideFromJS
   public void post(RecipeManagerKJS recipeManager, Map<ResourceLocation, JsonElement> datapackRecipeMap) {
      this.discoverRecipes(recipeManager, datapackRecipeMap);
      this.postEvent();
      this.applyChanges(datapackRecipeMap);
   }

   @HideFromJS
   public void discoverRecipes(RecipeManagerKJS recipeManager, Map<ResourceLocation, JsonElement> datapackRecipeMap) {
      Stopwatch timer = Stopwatch.createStarted();
      KubeJSPlugins.forEachPlugin(p -> p.beforeRecipeLoading(this, recipeManager, datapackRecipeMap));
      int skippedRecipes = 0;

      for (Entry<ResourceLocation, JsonElement> entry : datapackRecipeMap.entrySet()) {
         ResourceLocation recipeId = entry.getKey();
         if (recipeId != null && !recipeId.getPath().startsWith("_")) {
            JsonElement originalJsonElement = entry.getValue();
            if (originalJsonElement instanceof JsonObject originalJson) {
               if (!originalJson.has("type")) {
                  this.warnSkip("Skipping recipe %s, not a json object".formatted(recipeId));
               } else {
                  Codec<Optional<JsonObject>> codec = ConditionalOps.createConditionalCodec(Codec.unit(originalJson));
                  switch (codec.parse(this.ops.json(), originalJson)) {
                     case Success var13:
                        Success var20 = var13;

                        try {
                           var21 = var20.value();
                        } catch (Throwable var18) {
                           throw new MatchException(var18.toString(), var18);
                        }

                        Optional var19 = (Optional)var21;
                        var20 = var13;

                        try {
                           var20.lifecycle();
                        } catch (Throwable var17) {
                           throw new MatchException(var17.toString(), var17);
                        }

                        if (var19.isEmpty()) {
                           this.infoSkip("Skipping recipe %s, conditions not met".formatted(recipeId));
                           skippedRecipes++;
                        } else {
                           this.parseOriginalRecipe((JsonObject)var19.get(), recipeId);
                        }
                        break;
                     case Error<?> error:
                        this.errorSkip("Skipping recipe %s, error parsing conditions: %s".formatted(recipeId, error.message()));
                        break;
                     default:
                        throw new MatchException(null, null);
                  }
               }
            } else {
               this.warnSkip("Skipping recipe %s, not a json object".formatted(recipeId));
            }
         } else {
            this.infoSkip("Skipping recipe %s, filename starts with _".formatted(recipeId));
            skippedRecipes++;
         }
      }

      this.takenIds.putAll(this.originalRecipes);
      ConsoleJS.SERVER.info("Found %,d recipes (skipped %,d) in %s".formatted(this.originalRecipes.size(), skippedRecipes, timer.stop()));
   }

   private void parseOriginalRecipe(JsonObject json, ResourceLocation recipeId) {
      String typeStr = GsonHelper.getAsString(json, "type");
      String recipeIdAndType = recipeId + "[" + typeStr + "]";
      RecipeTypeFunction type = this.getRecipeFunction(typeStr);
      if (type == null) {
         this.warnSkip("Skipping recipe %s, unknown type: %s".formatted(recipeId, typeStr));
      } else {
         ErrorStack stack = new ErrorStack();

         try {
            KubeRecipe recipe = type.schemaType.schema.deserialize(SourceLine.UNKNOWN, type, recipeId, json);
            recipe.afterLoaded(stack);
            this.originalRecipes.put(recipeId, recipe);
            if (ConsoleJS.SERVER.shouldPrintDebug()) {
               Recipe<?> original = recipe.getOriginalRecipe();
               if (original != null && !SpecialRecipeSerializerManager.INSTANCE.isSpecial(original)) {
                  ConsoleJS.SERVER.debug("Loaded recipe " + recipeIdAndType + ": " + recipe.getFromToString());
               } else {
                  ConsoleJS.SERVER.debug("Loaded recipe " + recipeIdAndType + ": <dynamic>");
               }
            }
         } catch (Throwable var12) {
            String recipeStr = "'%s'%s".formatted(recipeIdAndType, stack.atString());
            if (var12 instanceof RecipeComponentException || DevProperties.get().logErroringParsedRecipes) {
               ConsoleJS.SERVER.warn("Failed to parse recipe %s! Falling back to vanilla".formatted(recipeStr), var12, POST_SKIP_ERROR);
            }

            try {
               this.originalRecipes.put(recipeId, UnknownRecipeSchema.SCHEMA.deserialize(SourceLine.UNKNOWN, type, recipeId, json));
            } catch (IllegalArgumentException | JsonParseException | NullPointerException var10) {
               if (DevProperties.get().logErroringParsedRecipes) {
                  ConsoleJS.SERVER.error("Failed to parse recipe %s".formatted(recipeStr), var10, POST_SKIP_ERROR);
               }
            } catch (Exception var11) {
               ConsoleJS.SERVER.error("Failed to parse recipe %s".formatted(recipeStr), var11, POST_SKIP_ERROR);
            }
         }
      }
   }

   private void infoSkip(String s) {
      if (DevProperties.get().logSkippedRecipes) {
         ConsoleJS.SERVER.info(s);
      } else {
         RecipeManager.LOGGER.debug(s);
      }
   }

   private void warnSkip(String s) {
      if (DevProperties.get().logSkippedRecipes) {
         ConsoleJS.SERVER.warn(s);
      } else {
         RecipeManager.LOGGER.warn(s);
      }
   }

   private void errorSkip(String s) {
      if (DevProperties.get().logSkippedRecipes) {
         ConsoleJS.SERVER.error(s);
      } else {
         RecipeManager.LOGGER.error(s);
      }
   }

   @HideFromJS
   public void postEvent() {
      Stopwatch timer = Stopwatch.createStarted();
      ServerEvents.RECIPES.post(ScriptType.SERVER, this);

      for (KubeRecipe r : this.originalRecipes.values()) {
         if (r.removed) {
            this.removedRecipes.add(r);
         } else if (r.hasChanged()) {
            this.modifiedCount++;
         }
      }

      ConsoleJS.SERVER.info("Posted recipe events in " + TimeJS.msToString(timer.stop().elapsed(TimeUnit.MILLISECONDS)));
   }

   @HideFromJS
   public void applyChanges(Map<ResourceLocation, JsonElement> map) {
      Stopwatch timer = Stopwatch.createStarted();
      this.addedRecipes.removeIf(RECIPE_IS_SYNTHETIC);
      map.clear();
      map.putAll(
         this.originalRecipes
            .values()
            .parallelStream()
            .filter(RECIPE_NOT_REMOVED)
            .map(KubeRecipe::serializeChanges)
            .peek(this::addToExport)
            .collect(Collectors.toConcurrentMap(KubeRecipe::getOrCreateId, recipe -> recipe.json, (a, b) -> b))
      );
      map.putAll(
         this.addedRecipes
            .parallelStream()
            .filter(RECIPE_NOT_REMOVED)
            .map(KubeRecipe::serializeChanges)
            .peek(this::addToExport)
            .collect(Collectors.toConcurrentMap(KubeRecipe::getOrCreateId, recipe -> recipe.json, (a, b) -> {
               ConsoleJS.SERVER.warn("KubeJS has found two recipes with the same ID in your custom recipes! Picking the last one encountered!");
               ConsoleJS.SERVER.warn("Recipe A JSON: " + a);
               ConsoleJS.SERVER.warn("Recipe B JSON: " + b);
               return b;
            }))
      );
      ConsoleJS.SERVER.info("KubeJS modifications to recipe manager finished in %s".formatted(timer.stop()));
   }

   @HideFromJS
   public void finishEvent() {
      ChangesForChat.recipesAdded = this.addedRecipes.size();
      ChangesForChat.recipesModified = this.modifiedCount;
      ChangesForChat.recipesRemoved = this.removedRecipes.size();
      ChangesForChat.recipesMs = this.overallTimer.stop().elapsed(TimeUnit.MILLISECONDS);
      ConsoleJS.SERVER
         .info(
            "Added %d recipes, removed %d recipes, modified %d recipes, with %d failed recipes taking %s in total"
               .formatted(
                  this.addedRecipes.size(), this.removedRecipes.size(), this.modifiedCount, this.failedCount, TimeJS.msToString(ChangesForChat.recipesMs)
               )
         );
      if (DataExport.export != null) {
         for (KubeRecipe r : this.removedRecipes) {
            DataExport.export.addJson("removed_recipes/" + r.getId() + ".json", r.json);
         }
      }

      if (DevProperties.get().logRecipeDebug) {
         ConsoleJS.SERVER.info("======== Debug output of all added recipes ========");

         for (KubeRecipe r : this.addedRecipes) {
            ConsoleJS.SERVER.info(r.getOrCreateId() + ": " + r.json);
         }

         ConsoleJS.SERVER.info("======== Debug output of all modified recipes ========");

         for (KubeRecipe r : this.originalRecipes.values()) {
            if (!r.removed && r.hasChanged()) {
               ConsoleJS.SERVER.info(r.getOrCreateId() + ": " + r.json + " FROM " + r.originalJson);
            }
         }

         ConsoleJS.SERVER.info("======== Debug output of all removed recipes ========");

         for (KubeRecipe rx : this.removedRecipes) {
            ConsoleJS.SERVER.info(rx.getOrCreateId() + ": " + rx.json);
         }
      }

      RegexIDFilter.clearInternCache();
   }

   private void addToExport(KubeRecipe r) {
      String path = r.kjs$getMod() + "/" + r.getPath();
      if (DataExport.export != null) {
         DataExport.export.addJson("recipes/%s.json".formatted(path), r.json);
         if (r.newRecipe) {
            DataExport.export.addJson("added_recipes/%s.json".formatted(path), r.json);
         }
      }
   }

   @HideFromJS
   public void handleFailedRecipe(ResourceLocation id, JsonElement json, Throwable ex) {
      if (json instanceof JsonObject obj && obj.has("_kubejs_changed_marker")) {
         SourceLine sourceLine = SourceLine.fromJson(obj.remove("_kubejs_changed_marker").getAsJsonObject());
         if (DevProperties.get().logErroringRecipes) {
            ConsoleJS.SERVER.error("Error parsing recipe %s (details below this line)".formatted(id), sourceLine, ex, null);
            ConsoleJS.SERVER.stopCapturingErrors();
            ConsoleJS.SERVER.error("Recipe JSON for %s: %s".formatted(id, json), sourceLine, null, null);
            ConsoleJS.SERVER.startCapturingErrors();
         }

         this.failedCount++;
      }
   }

   public Map<String, Object> getRecipes() {
      return this.recipeFunctions;
   }

   public KubeRecipe addRecipe(KubeRecipe r, boolean json) {
      this.addedRecipes.add(r);
      if (DevProperties.get().logAddedRecipes) {
         ConsoleJS.SERVER.info("+ " + r.kjs$getType() + ": " + r.getFromToString() + (json ? " [json]" : ""));
      } else if (ConsoleJS.SERVER.shouldPrintDebug()) {
         ConsoleJS.SERVER.debug("+ " + r.kjs$getType() + ": " + r.getFromToString() + (json ? " [json]" : ""));
      }

      return r;
   }

   public Stream<KubeRecipe> recipeStream(Context cx, RecipeFilter filter) {
      if (filter == ConstantFilter.FALSE) {
         return Stream.empty();
      } else if (filter instanceof IDFilter id) {
         KubeRecipe r = this.originalRecipes.get(id.id);
         return r != null && !r.removed ? Stream.of(r) : Stream.empty();
      } else {
         if (filter instanceof OrFilter or) {
            if (or.list.isEmpty()) {
               return Stream.empty();
            }

            Iterator r = or.list.iterator();

            RecipeFilter recipeFilter;
            do {
               if (!r.hasNext()) {
                  return or.list.stream().map(idf -> this.originalRecipes.get(((IDFilter)idf).id)).filter(RECIPE_NOT_REMOVED);
               }

               recipeFilter = (RecipeFilter)r.next();
            } while (recipeFilter instanceof IDFilter);
         }

         return this.originalRecipes.values().stream().filter(new RecipesKubeEvent.RecipeStreamFilter(cx, filter));
      }
   }

   private <T> T reduceRecipesAsync(Context cx, RecipeFilter filter, Function<Stream<KubeRecipe>, T> function) {
      return function.apply(this.recipeStream(cx, filter));
   }

   public void forEachRecipe(Context cx, RecipeFilter filter, Consumer<KubeRecipe> consumer) {
      if (filter instanceof IDFilter id) {
         KubeRecipe r = this.originalRecipes.get(id.id);
         if (r != null && !r.removed) {
            consumer.accept(r);
         }
      } else {
         this.recipeStream(cx, filter).forEach(consumer);
      }
   }

   public int countRecipes(Context cx, RecipeFilter filter) {
      return this.reduceRecipesAsync(cx, filter, s -> (int)s.count());
   }

   public boolean containsRecipe(Context cx, RecipeFilter filter) {
      return this.reduceRecipesAsync(cx, filter, s -> s.findAny().isPresent());
   }

   public Collection<KubeRecipe> findRecipes(Context cx, RecipeFilter filter) {
      return this.reduceRecipesAsync(cx, filter, Stream::toList);
   }

   public Collection<ResourceLocation> findRecipeIds(Context cx, RecipeFilter filter) {
      return this.reduceRecipesAsync(cx, filter, s -> s.map(KubeRecipe::getOrCreateId).toList());
   }

   public void remove(Context cx, RecipeFilter filter) {
      this.forEachRecipe(cx, filter, KubeRecipe::remove);
   }

   public void replaceInput(Context cx, RecipeFilter filter, ReplacementMatchInfo match, Object with) {
      String dstring = !DevProperties.get().logModifiedRecipes && !ConsoleJS.SERVER.shouldPrintDebug() ? "" : ": IN " + match + " -> " + with;
      this.forEachRecipe(cx, filter, r -> {
         if (r.replaceInput(new RecipeScriptContext.Impl(cx, r), match, with)) {
            if (DevProperties.get().logModifiedRecipes) {
               ConsoleJS.SERVER.info("~ " + r + dstring);
            } else if (ConsoleJS.SERVER.shouldPrintDebug()) {
               ConsoleJS.SERVER.debug("~ " + r + dstring);
            }
         }
      });
   }

   public void replaceOutput(Context cx, RecipeFilter filter, ReplacementMatchInfo match, Object with) {
      String dstring = !DevProperties.get().logModifiedRecipes && !ConsoleJS.SERVER.shouldPrintDebug() ? "" : ": OUT " + match + " -> " + with;
      this.forEachRecipe(cx, filter, r -> {
         if (r.replaceOutput(new RecipeScriptContext.Impl(cx, r), match, with)) {
            if (DevProperties.get().logModifiedRecipes) {
               ConsoleJS.SERVER.info("~ " + r + dstring);
            } else if (ConsoleJS.SERVER.shouldPrintDebug()) {
               ConsoleJS.SERVER.debug("~ " + r + dstring);
            }
         }
      });
   }

   public RecipeTypeFunction getRecipeFunction(@Nullable String id) {
      if (id != null && !id.isEmpty()) {
         return this.recipeFunctions.get(ID.string(id)) instanceof RecipeTypeFunction fn ? fn : null;
      } else {
         return null;
      }
   }

   public KubeRecipe custom(Context cx, JsonObject json) {
      return (KubeRecipe)this.parseJson(json, SourceLine.of(cx)).getPartialOrThrow(KubeRuntimeException::new);
   }

   @HideFromJS
   public DataResult<KubeRecipe> parseJson(JsonObject json, SourceLine sourceLine) {
      if (json != null && json.has("type")) {
         RecipeTypeFunction type = this.getRecipeFunction(json.get("type").getAsString());
         if (type == null) {
            return DataResult.error(() -> "Unknown recipe type: " + json.get("type").getAsString());
         } else {
            ErrorStack stack = new ErrorStack();

            try {
               KubeRecipe recipe = type.schemaType.schema.deserialize(sourceLine, type, null, json);
               recipe.afterLoaded(stack);
               return DataResult.success(this.addRecipe(recipe, true));
            } catch (Throwable var8) {
               KubeRecipe recipex = type.schemaType.schema.recipeFactory.create(type, sourceLine, true);
               recipex.creationError = true;
               String errorString = "Failed to create custom recipe" + stack.atString() + " from json " + JsonUtils.toString(json);
               ConsoleJS.SERVER.error(errorString, sourceLine, var8, POST_SKIP_ERROR);
               recipex.json = json;
               recipex.newRecipe = true;
               return DataResult.error(() -> errorString, recipex);
            }
         }
      } else {
         return DataResult.error(() -> "JSON must contain 'type'!");
      }
   }

   private void printTypes(Predicate<RecipeSchemaType> predicate, boolean all) {
      int t = 0;
      Reference2ObjectLinkedOpenHashMap<RecipeSchema, Set<ResourceLocation>> map = new Reference2ObjectLinkedOpenHashMap();

      for (RecipeNamespace ns : this.recipeSchemaStorage.namespaces.values()) {
         for (RecipeSchemaType type : ns.values()) {
            if (predicate.test(type)) {
               t++;
               ((Set)map.computeIfAbsent(type.schema, s -> new LinkedHashSet())).add(type.id);
            }
         }
      }

      if (all) {
         ConsoleJS.SERVER.info("- All recipe types");
         ConsoleJS.SERVER.info("  - .id(id)");
         ConsoleJS.SERVER.info("  - .group(string)");
         ConsoleJS.SERVER.info("  - .set(key, value)");
         ConsoleJS.SERVER.info("  - .merge(json)");
         ConsoleJS.SERVER.info("- All crafting table recipe types");
         ConsoleJS.SERVER.info("  - .stage(string)");
         ConsoleJS.SERVER.info("  - .damageIngredient(filter, int?)");
         ConsoleJS.SERVER.info("  - .replaceIngredient(filter, item_stack)");
         ConsoleJS.SERVER.info("  - .customIngredientAction(filter, string)");
         ConsoleJS.SERVER.info("  - .keepIngredient(filter)");
         ConsoleJS.SERVER.info("  - .consumeIngredient(filter)");
         ConsoleJS.SERVER.info("  - .modifyResult(string)");
      }

      ObjectBidirectionalIterator var10 = map.entrySet().iterator();

      while (var10.hasNext()) {
         Entry<RecipeSchema, Set<ResourceLocation>> entry = (Entry<RecipeSchema, Set<ResourceLocation>>)var10.next();
         ConsoleJS.SERVER.info("- " + entry.getValue().stream().<CharSequence>map(ResourceLocation::toString).collect(Collectors.joining(", ")));
         ObjectIterator var12 = entry.getKey().constructors().values().iterator();

         while (var12.hasNext()) {
            RecipeConstructor c = (RecipeConstructor)var12.next();
            ConsoleJS.SERVER.info("  - " + c.toString());
         }

         for (RecipeKey<?> key : entry.getKey().keys) {
            String name = key.getPrimaryFunctionName();
            if (RecipeFunction.isValidIdentifier(name.toCharArray())) {
               ConsoleJS.SERVER.info("  - ." + name + "(" + key.component + ")");
            }
         }

         for (RecipeFunctionInstance f : entry.getKey().functions.values()) {
            if (RecipeFunction.isValidIdentifier(f.name().toCharArray())) {
               ConsoleJS.SERVER.info("  - ." + f);
            }
         }
      }

      ConsoleJS.SERVER.info(t + " types");
   }

   public void printTypes(Context cx) {
      ConsoleJS.SERVER.info("== All recipe types [used] ==");
      Set<ResourceLocation> set = this.reduceRecipesAsync(cx, ConstantFilter.TRUE, s -> s.<ResourceLocation>map(r -> r.type.id).collect(Collectors.toSet()));
      this.printTypes(t -> set.contains(t.id), false);
   }

   public void printAllTypes() {
      ConsoleJS.SERVER.info("== All recipe types [available] ==");
      this.printTypes(t -> BuiltInRegistries.RECIPE_SERIALIZER.get(t.id) != null, true);
   }

   public void printExamples(String type) {
      List<KubeRecipe> list = this.originalRecipes.values().stream().filter(recipeJS -> recipeJS.type.toString().equals(type)).collect(Collectors.toList());
      Collections.shuffle(list);
      ConsoleJS.SERVER.info("== Random examples of '" + type + "' ==");

      for (int i = 0; i < Math.min(list.size(), 5); i++) {
         KubeRecipe r = list.get(i);
         ConsoleJS.SERVER.info("- " + r.getOrCreateId() + ":\n" + JsonIO.toPrettyString(r.json));
      }
   }

   public synchronized ResourceLocation takeId(KubeRecipe recipe, String prefix, String ids) {
      int i = 2;

      ResourceLocation id;
      for (id = ResourceLocation.parse(prefix + ids); this.takenIds.containsKey(id); i++) {
         id = ResourceLocation.parse(prefix + ids + "_" + i);
      }

      this.takenIds.put(id, recipe);
      return id;
   }

   public void stage(Context cx, RecipeFilter filter, String stage) {
      this.forEachRecipe(cx, filter, r -> r.stage(stage));
   }

   private record RecipeStreamFilter(Context cx, RecipeFilter filter) implements Predicate<KubeRecipe> {
      public boolean test(KubeRecipe r) {
         return r != null && !r.removed && this.filter.test((RecipeMatchContext)(new RecipeMatchContext.Impl(this.cx, r)));
      }
   }
}
