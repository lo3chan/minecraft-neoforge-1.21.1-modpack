package dev.latvian.mods.kubejs.recipe.schema;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.DataResult.Error;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.codec.KubeJSCodecs;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.plugin.builtin.event.ServerEvents;
import dev.latvian.mods.kubejs.recipe.RecipeTypeRegistryContext;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.kubejs.recipe.schema.postprocessing.RecipePostProcessor;
import dev.latvian.mods.kubejs.recipe.schema.postprocessing.RecipePostProcessorType;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.JsonUtils;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

public class RecipeSchemaStorage {
   private final ServerScriptManager manager;
   public final Map<ResourceLocation, KubeRecipeFactory> recipeTypes;
   public final Map<String, RecipeNamespace> namespaces;
   public final Map<String, ResourceLocation> mappings;
   public final Map<String, RecipeSchemaType> schemaTypes;
   public Codec<RecipeComponent<?>> recipeComponentCodec;
   public Codec<RecipePostProcessor> recipePostProcessorCodec;

   public RecipeSchemaStorage(ServerScriptManager manager) {
      this.manager = manager;
      this.recipeTypes = new HashMap<>();
      this.namespaces = new HashMap<>();
      this.mappings = new HashMap<>();
      this.schemaTypes = new HashMap<>();
   }

   public RecipeNamespace namespace(String namespace) {
      return this.namespaces.computeIfAbsent(namespace, n -> new RecipeNamespace(this, n));
   }

   RegistryAccessContainer getRegistries() {
      return this.manager.getRegistries();
   }

   public void fireEvents(RegistryAccessContainer registries, ResourceManager resourceManager) {
      this.recipeTypes.clear();
      this.namespaces.clear();
      this.mappings.clear();
      this.schemaTypes.clear();
      RegistryOps<JsonElement> jsonOps = registries.json();
      RecipeFactoryRegistry typeEvent = new RecipeFactoryRegistry(this);
      KubeJSPlugins.forEachPlugin(typeEvent, KubeJSPlugin::registerRecipeFactories);

      for (Entry<ResourceLocation, Resource> entry : resourceManager.listResources("kubejs", path -> path.getPath().endsWith("/recipe_mappings.json"))
         .entrySet()) {
         try (BufferedReader reader = entry.getValue().openAsReader()) {
            JsonObject json = (JsonObject)JsonUtils.GSON.fromJson(reader, JsonObject.class);

            for (Entry<String, JsonElement> entry1 : json.entrySet()) {
               ResourceLocation id = ResourceLocation.fromNamespaceAndPath(entry.getKey().getNamespace(), entry1.getKey());
               Object jsonx = entry1.getValue();
               if (jsonx instanceof JsonArray) {
                  for (JsonElement n : (JsonArray)jsonx) {
                     this.mappings.put(n.getAsString(), id);
                  }
               } else {
                  this.mappings.put(entry1.getValue().getAsString(), id);
               }
            }
         } catch (Exception var24) {
            var24.printStackTrace();
         }
      }

      RecipeMappingRegistry mappingRegistry = new RecipeMappingRegistry(this);
      KubeJSPlugins.forEachPlugin(mappingRegistry, KubeJSPlugin::registerRecipeMappings);
      ServerEvents.RECIPE_MAPPING_REGISTRY.post(ScriptType.SERVER, mappingRegistry);
      HashMap<ResourceLocation, RecipeSchemaStorage.StoredRecipeComponentType> componentTypes = new HashMap<>();
      Codec<RecipeSchemaStorage.StoredRecipeComponentType> typeCodec = KubeJSCodecs.KUBEJS_ID.comapFlatMap(idx -> {
         RecipeSchemaStorage.StoredRecipeComponentType storedx = componentTypes.get(idx);
         return storedx != null ? DataResult.success(storedx) : DataResult.error(() -> "Unknown recipe component type '" + ID.reduceKjs(idx) + "'");
      }, storedx -> storedx.type.id());
      Codec<RecipeComponent<?>> directComponentCodec = typeCodec.partialDispatch(
         "type",
         c -> {
            RecipeSchemaStorage.StoredRecipeComponentType storedx = componentTypes.get(c.type().id());
            return storedx != null
               ? DataResult.success(storedx)
               : DataResult.error(() -> "Missing stored recipe component type for '" + ID.reduceKjs(c.type().id()) + "'");
         },
         type -> DataResult.success(type.mapCodec)
      );
      this.recipeComponentCodec = Codec.either(typeCodec, directComponentCodec)
         .comapFlatMap(
            either -> (DataResult)either.map(
               storedx -> storedx.unit != null ? DataResult.success(storedx.unit) : storedx.mapCodec.decode(jsonOps, JsonUtils.MAP_LIKE), DataResult::success
            ),
            component -> component.type().isUnit() ? Either.left(componentTypes.get(component.type().id())) : Either.right(component)
         );
      KubeJSPlugins.forEachPlugin(
         type -> componentTypes.put(type.id(), new RecipeSchemaStorage.StoredRecipeComponentType(type)), KubeJSPlugin::registerRecipeComponents
      );
      RecipeTypeRegistryContext rcCtx = new RecipeTypeRegistryContext(registries, this);

      for (RecipeSchemaStorage.StoredRecipeComponentType stored : componentTypes.values()) {
         stored.init(rcCtx);
      }

      for (Entry<ResourceLocation, Resource> entry : resourceManager.listResources("kubejs", path -> path.getPath().endsWith("/recipe_components.json"))
         .entrySet()) {
         try (BufferedReader reader = entry.getValue().openAsReader()) {
            JsonObject json = (JsonObject)JsonUtils.GSON.fromJson(reader, JsonObject.class);

            for (Entry<String, JsonElement> entry1x : json.entrySet()) {
               ResourceLocation id = ID.kjs(entry1x.getKey());
               DataResult<RecipeComponent<?>> componentResult = this.recipeComponentCodec.parse(jsonOps, entry1x.getValue());
               if (componentResult.isSuccess()) {
                  RecipeSchemaStorage.StoredRecipeComponentType stored = new RecipeSchemaStorage.StoredRecipeComponentType(
                     RecipeComponentType.unit(id, (RecipeComponent)componentResult.getOrThrow())
                  );
                  componentTypes.put(id, stored);
                  stored.init(rcCtx);
               } else {
                  KubeJS.LOGGER
                     .error(
                        "Failed to load recipe component {} from {}: {}",
                        new Object[]{id, entry.getKey(), componentResult.error().<String>map(Error::message).orElse("Unknown Error")}
                     );
               }
            }
         } catch (Exception var22) {
            KubeJS.LOGGER.error("Failed to load recipe component file {}: {}", entry.getKey(), var22);
         }
      }

      this.recipePostProcessorCodec = RecipePostProcessorType.CODEC.dispatch("type", RecipePostProcessor::type, type -> (MapCodec)type.mapCodec().apply(rcCtx));

      for (Entry<ResourceKey<RecipeSerializer<?>>, RecipeSerializer<?>> entry : BuiltInRegistries.RECIPE_SERIALIZER.entrySet()) {
         RecipeNamespace ns = this.namespace(entry.getKey().location().getNamespace());
         ns.put(entry.getKey().location().getPath(), new UnknownRecipeSchemaType(ns, entry.getKey().location(), entry.getValue()));
      }

      RecipeSchemaRegistry schemaRegistry = new RecipeSchemaRegistry(this);
      JsonRecipeSchemaLoader.load(rcCtx, jsonOps, schemaRegistry, resourceManager);
      KubeJSPlugins.forEachPlugin(schemaRegistry, KubeJSPlugin::registerRecipeSchemas);
      ServerEvents.RECIPE_SCHEMA_REGISTRY.post(ScriptType.SERVER, schemaRegistry);
   }

   public static final class StoredRecipeComponentType {
      private final RecipeComponentType<?> type;
      private MapCodec<RecipeComponent<?>> mapCodec;
      private RecipeComponent<?> unit;

      public StoredRecipeComponentType(RecipeComponentType<?> type) {
         this.type = type;
      }

      @NotNull
      @Override
      public String toString() {
         return this.type.toString();
      }

      public void init(RecipeTypeRegistryContext ctx) {
         this.mapCodec = this.type.mapCodec(ctx);
         this.unit = this.type.isUnit()
            ? this.type.instance()
            : (RecipeComponent)this.mapCodec.decode(JsonOps.INSTANCE, JsonUtils.MAP_LIKE).result().orElse(null);
      }
   }
}
