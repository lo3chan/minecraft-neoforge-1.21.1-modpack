package io.wispforest.owo.ui.parsing;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonGrammar;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import blue.endless.jankson.api.SyntaxError;
import io.wispforest.owo.Owo;
import io.wispforest.owo.ops.TextOps;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.fml.loading.FMLPaths;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.xml.sax.SAXException;

public class UIModelLoader implements ResourceManagerReloadListener {
   private static final Map<ResourceLocation, UIModel> LOADED_MODELS = new HashMap<>();
   private static final Jankson JANKSON = Jankson.builder()
      .registerSerializer(Path.class, (path, marshaller) -> JsonPrimitive.of(path.toString()))
      .registerSerializer(ResourceLocation.class, (identifier, marshaller) -> new JsonPrimitive(identifier.toString()))
      .build();
   private static final Path HOT_RELOAD_LOCATIONS_PATH = FMLPaths.CONFIGDIR.get().resolve("owo_ui_hot_reload_locations.json5");
   private static final Map<ResourceLocation, Path> HOT_RELOAD_LOCATIONS = new HashMap<>();
   private static boolean loadedOnce = false;

   @Nullable
   public static UIModel get(ResourceLocation id) {
      if (Owo.DEBUG && HOT_RELOAD_LOCATIONS.containsKey(id)) {
         try {
            UIModel var2;
            try (InputStream stream = Files.newInputStream(HOT_RELOAD_LOCATIONS.get(id))) {
               var2 = UIModel.load(stream);
            }

            return var2;
         } catch (IOException | SAXException | ParserConfigurationException var6) {
            Minecraft.getInstance()
               .player
               .sendSystemMessage(
                  TextOps.concat(Owo.PREFIX, TextOps.withFormatting("hot ui model reload failed, check the log for details", ChatFormatting.RED))
               );
            Owo.LOGGER.error("Hot UI model reload failed", var6);
         }
      }

      return getPreloaded(id);
   }

   @Nullable
   public static UIModel getPreloaded(ResourceLocation id) {
      return LOADED_MODELS.getOrDefault(id, null);
   }

   public static void setHotReloadPath(ResourceLocation modelId, @Nullable Path reloadPath) {
      if (reloadPath != null) {
         HOT_RELOAD_LOCATIONS.put(modelId, reloadPath);
      } else {
         HOT_RELOAD_LOCATIONS.remove(modelId);
      }

      try {
         Files.writeString(HOT_RELOAD_LOCATIONS_PATH, JANKSON.toJson(HOT_RELOAD_LOCATIONS).toJson(JsonGrammar.JSON5));
      } catch (IOException var3) {
         Owo.LOGGER.warn("Could not save hot reload locations", var3);
      }
   }

   @Nullable
   public static Path getHotReloadPath(ResourceLocation modelId) {
      return HOT_RELOAD_LOCATIONS.get(modelId);
   }

   public static Set<ResourceLocation> allLoadedModels() {
      return Collections.unmodifiableSet(LOADED_MODELS.keySet());
   }

   public void onResourceManagerReload(ResourceManager manager) {
      LOADED_MODELS.clear();
      manager.listResources("owo_ui", identifier -> identifier.getPath().endsWith(".xml"))
         .forEach(
            (resourceId, resource) -> {
               try {
                  ResourceLocation modelId = ResourceLocation.fromNamespaceAndPath(
                     resourceId.getNamespace(), resourceId.getPath().substring(7, resourceId.getPath().length() - 4)
                  );
                  LOADED_MODELS.put(modelId, UIModel.load(resource.open()));
               } catch (IOException | SAXException | ParserConfigurationException var3) {
                  Owo.LOGGER.error("Could not parse UI model {}", resourceId, var3);
               }
            }
         );
      loadedOnce = true;
   }

   @Internal
   public static boolean hasCompletedInitialLoad() {
      return loadedOnce;
   }

   static {
      if (Owo.DEBUG && Files.exists(HOT_RELOAD_LOCATIONS_PATH)) {
         try (InputStream stream = Files.newInputStream(HOT_RELOAD_LOCATIONS_PATH)) {
            JsonObject associations = JANKSON.load(stream);
            associations.forEach((key, value) -> {
               if (value instanceof JsonPrimitive primitive) {
                  HOT_RELOAD_LOCATIONS.put(ResourceLocation.parse(key), Path.of(primitive.asString()));
               }
            });
         } catch (SyntaxError | IOException var5) {
         }
      }
   }
}
