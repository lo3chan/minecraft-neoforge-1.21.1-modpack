package net.mehvahdjukaar.moonlight.api.resources;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.management.openmbean.InvalidOpenTypeException;
import net.mehvahdjukaar.moonlight.api.client.TextureCache;
import net.mehvahdjukaar.moonlight.api.misc.TriResult;
import net.mehvahdjukaar.moonlight.api.resources.pack.DynamicTexturePack;
import net.mehvahdjukaar.moonlight.api.resources.pack.ResourceSink;
import net.mehvahdjukaar.moonlight.api.set.BlockType;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodType;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodTypeRegistry;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class RPUtils {
   public static String serializeJson(JsonElement json) throws IOException {
      try {
         String var3;
         try (StringWriter stringWriter = new StringWriter()) {
            JsonWriter jsonWriter = new JsonWriter(stringWriter);

            try {
               jsonWriter.setLenient(true);
               jsonWriter.setIndent("  ");
               Streams.write(json, jsonWriter);
               var3 = stringWriter.toString();
            } catch (Throwable var7) {
               try {
                  jsonWriter.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }

               throw var7;
            }

            jsonWriter.close();
         }

         return var3;
      } catch (Exception var9) {
         throw new IOException(var9);
      }
   }

   public static JsonObject deserializeJson(InputStream stream) {
      return GsonHelper.parse(new InputStreamReader(stream, StandardCharsets.UTF_8));
   }

   public static String readTextFile(ResourceManager manager, ResourceLocation path) {
      Optional<Resource> resource = manager.getResource(path);
      if (resource.isPresent()) {
         try {
            String var4;
            try (BufferedReader reader = resource.get().openAsReader()) {
               var4 = reader.lines().collect(Collectors.joining("\n"));
            }

            return var4;
         } catch (IOException var8) {
         }
      }

      Moonlight.LOGGER.error("Failed to read text file {}", path);
      return "";
   }

   public static ResourceLocation findFirstBlockTextureLocation(ResourceManager manager, Block block) throws FileNotFoundException {
      return findFirstBlockTextureLocation(manager, block, t -> true);
   }

   public static ResourceLocation findFirstBlockTextureLocation(ResourceManager manager, Block block, Predicate<String> texturePredicate) throws FileNotFoundException {
      ResourceLocation blockId = Utils.getID(block);
      TriResult<String> cached = TextureCache.getCachedTexture(block, texturePredicate);
      if (cached.isSuccess()) {
         return ResourceLocation.parse(cached.getObject());
      } else {
         if (cached.isPass()) {
            Optional<Resource> blockState = manager.getResource(ResType.BLOCKSTATES.getPath(blockId));

            try {
               label86: {
                  ResourceLocation var21;
                  try (InputStream bsStream = blockState.orElseThrow().open()) {
                     JsonElement bsElement = deserializeJson(bsStream);

                     for (String modelPath : findAllResourcesInJsonRecursive(bsElement.getAsJsonObject(), s -> s.equals("model"))) {
                        for (String t : findAllTexturesInModelRecursive(manager, modelPath)) {
                           TextureCache.add(block, t);
                        }
                     }

                     cached = TextureCache.getCachedTexture(block, texturePredicate);
                     if (!cached.isSuccess()) {
                        break label86;
                     }

                     var21 = ResourceLocation.parse(cached.getObject());
                  }

                  return var21;
               }
            } catch (Exception var16) {
            }

            for (String t : guessBlockTextureLocation(blockId, block)) {
               TextureCache.add(block, t);
               if (texturePredicate.test(t)) {
                  return ResourceLocation.parse(t);
               }
            }
         }

         throw new FileNotFoundException("Could not find any texture associated to the given block " + blockId);
      }
   }

   private static Set<String> guessItemTextureLocation(ResourceLocation id, Item item) {
      return Set.of(id.getNamespace() + ":item/" + item);
   }

   private static List<String> guessBlockTextureLocation(ResourceLocation id, Block block) {
      String name = id.getPath();
      List<String> textures = new ArrayList<>();
      WoodType w = WoodTypeRegistry.INSTANCE.getBlockTypeOf(block);
      if (w != null) {
         String key = w.getChildKey(block);
         if (Objects.equals(key, "log") || Objects.equals(key, "stripped_log")) {
            textures.add(id.getNamespace() + ":block/" + name + "_top");
            textures.add(id.getNamespace() + ":block/" + name + "_side");
         }
      }

      textures.add(id.getNamespace() + ":block/" + name);
      return textures;
   }

   @NotNull
   private static List<String> findAllTexturesInModelRecursive(ResourceManager manager, String modelPath) throws Exception {
      JsonObject modelElement;
      try (InputStream modelStream = ((Resource)manager.getResource(ResType.MODELS.getPath(modelPath)).get()).open()) {
         modelElement = deserializeJson(modelStream).getAsJsonObject();
      } catch (Exception var8) {
         throw new Exception("Failed to parse model at " + modelPath);
      }

      ArrayList<String> textures = new ArrayList<>(findAllResourcesInJsonRecursive(modelElement.getAsJsonObject("textures")));
      if (textures.isEmpty() && modelElement.has("parent")) {
         String parentPath = modelElement.get("parent").getAsString();
         textures.addAll(findAllTexturesInModelRecursive(manager, parentPath));
      }

      return textures;
   }

   public static ResourceLocation findFirstItemTextureLocation(ResourceManager manager, Item block) throws FileNotFoundException {
      return findFirstItemTextureLocation(manager, block, t -> true);
   }

   public static ResourceLocation findFirstItemTextureLocation(ResourceManager manager, Item item, Predicate<String> texturePredicate) throws FileNotFoundException {
      ResourceLocation itemId = Utils.getID(item);
      TriResult<String> cached = TextureCache.getCachedTexture(item, texturePredicate);
      if (cached.isSuccess()) {
         return ResourceLocation.parse(cached.getObject());
      } else {
         if (cached.isPass()) {
            Optional<Resource> itemModel = manager.getResource(ResType.ITEM_MODELS.getPath(itemId));

            Set<String> textures;
            try (InputStream stream = itemModel.orElseThrow().open()) {
               JsonElement bsElement = deserializeJson(stream);
               textures = findAllResourcesInJsonRecursive(bsElement.getAsJsonObject().getAsJsonObject("textures"));
            } catch (Exception var12) {
               textures = guessItemTextureLocation(itemId, item);
            }

            for (String t : textures) {
               TextureCache.add(item, t);
            }

            cached = TextureCache.getCachedTexture(item, texturePredicate);
            if (cached.isSuccess()) {
               return ResourceLocation.parse(cached.getObject());
            }
         }

         throw new FileNotFoundException("Could not find any texture associated to the given item " + itemId);
      }
   }

   public static String findFirstResourceInJsonRecursive(JsonElement element) throws NoSuchElementException {
      if (element instanceof JsonArray array) {
         return findFirstResourceInJsonRecursive(array.get(0));
      } else if (element instanceof JsonObject) {
         Set<Entry<String, JsonElement>> entries = element.getAsJsonObject().entrySet();
         JsonElement child = entries.stream().findAny().get().getValue();
         return findFirstResourceInJsonRecursive(child);
      } else {
         return element.getAsString();
      }
   }

   public static Set<String> findAllResourcesInJsonRecursive(JsonElement element) {
      return findAllResourcesInJsonRecursive(element, s -> true);
   }

   public static Set<String> findAllResourcesInJsonRecursive(JsonElement element, Predicate<String> filter) {
      if (element instanceof JsonArray array) {
         Set<String> list = new HashSet<>();
         array.forEach(e -> list.addAll(findAllResourcesInJsonRecursive(e, filter)));
         return list;
      } else if (!(element instanceof JsonObject json)) {
         return Set.of(element.getAsString());
      } else {
         Set<Entry<String, JsonElement>> entries = json.entrySet();
         Set<String> list = new HashSet<>();

         for (Entry<String, JsonElement> c : entries) {
            if (!c.getValue().isJsonPrimitive() || filter.test(c.getKey())) {
               Set<String> l = findAllResourcesInJsonRecursive(c.getValue(), filter);
               list.addAll(l);
            }
         }

         return list;
      }
   }

   public static Recipe<?> readRecipe(ResourceManager manager, String location) {
      return readRecipeAbsolute(manager, ResType.RECIPES.getPath(location));
   }

   public static Recipe<?> readRecipe(ResourceManager manager, ResourceLocation location) {
      return readRecipeAbsolute(manager, ResType.RECIPES.getPath(location));
   }

   private static Recipe<?> readRecipeAbsolute(ResourceManager manager, ResourceLocation location) {
      Optional<Resource> resource = manager.getResource(location);

      try {
         Recipe var5;
         try (InputStream stream = resource.orElseThrow().open()) {
            JsonObject element = deserializeJson(stream);
            var5 = readRecipe(element);
         }

         return var5;
      } catch (Exception var8) {
         throw new InvalidOpenTypeException(String.format("Failed to get recipe at %s: %s", location, var8));
      }
   }

   public static Recipe<?> readRecipe(JsonElement element) {
      return (Recipe<?>)Recipe.CODEC.parse(JsonOps.INSTANCE, element).getOrThrow();
   }

   public static <T extends Recipe<?>> JsonElement writeRecipe(T recipe) {
      return (JsonElement)Recipe.CODEC.encodeStart(JsonOps.INSTANCE, recipe).getOrThrow();
   }

   @Deprecated(
      forRemoval = true
   )
   public static <T extends BlockType> RecipeHolder<?> makeSimilarRecipe(Recipe<?> original, T originalMat, T destinationMat, String baseID) {
      return makeSimilarRecipe(original, originalMat, destinationMat, ResourceLocation.parse(baseID));
   }

   @Deprecated(
      forRemoval = true
   )
   public static <T extends BlockType> RecipeHolder<?> makeSimilarRecipe(Recipe<?> original, T originalMat, T destinationMat, ResourceLocation baseID) {
      return RecipeTemplate.makeSimilarRecipe(original, originalMat, destinationMat, baseID);
   }

   public static Path getResourcePath(Path path, ResourceLocation k, PackType packType) {
      return path.resolve(packType.getDirectory()).resolve(k.getNamespace()).resolve(k.getPath());
   }

   public static void writeResource(ResourceLocation id, byte[] bytes, Path path, PackType packType) {
      Path p = getResourcePath(path, id, packType);

      try {
         Files.createDirectories(p.getParent());
         Files.write(p, bytes);
      } catch (Exception var6) {
         throw new RuntimeException(var6);
      }
   }

   @Deprecated(
      forRemoval = true
   )
   public static void appendModelOverride(
      ResourceManager manager, DynamicTexturePack pack, ResourceLocation modelRes, Consumer<RPUtils.OverrideAppender> modelConsumer
   ) {
      Optional<Resource> o = manager.getResource(ResType.ITEM_MODELS.getPath(modelRes));
      if (o.isPresent()) {
         try (InputStream model = o.get().open()) {
            JsonObject json = deserializeJson(model);
            JsonArray overrides;
            if (json.has("overrides")) {
               overrides = json.getAsJsonArray("overrides");
            } else {
               overrides = new JsonArray();
            }

            modelConsumer.accept(ov -> overrides.add(serializeModelOverride(ov)));
            json.add("overrides", overrides);
            pack.addItemModel(modelRes, json);
         } catch (Exception var10) {
         }
      }
   }

   @Deprecated(
      forRemoval = true
   )
   public static void appendModelOverride(
      ResourceManager manager, ResourceSink pack, ResourceLocation modelRes, Consumer<RPUtils.OverrideAppender> modelConsumer
   ) {
      JsonElement json = makeModelOverride(manager, modelRes, modelConsumer);
      pack.addItemModel(modelRes, json);
   }

   public static JsonElement makeModelOverride(ResourceManager manager, ResourceLocation modelRes, Consumer<RPUtils.OverrideAppender> modelConsumer) {
      try {
         JsonObject var6;
         try (InputStream model = manager.getResourceOrThrow(ResType.ITEM_MODELS.getPath(modelRes)).open()) {
            JsonObject json = deserializeJson(model);
            JsonArray overrides;
            if (json.has("overrides")) {
               overrides = json.getAsJsonArray("overrides");
            } else {
               overrides = new JsonArray();
            }

            modelConsumer.accept(ov -> overrides.add(serializeModelOverride(ov)));
            json.add("overrides", overrides);
            var6 = json;
         }

         return var6;
      } catch (Exception var9) {
         throw new RuntimeException(var9);
      }
   }

   private static JsonObject serializeModelOverride(ItemOverride override) {
      JsonObject json = new JsonObject();
      json.addProperty("model", override.getModel().toString());
      JsonObject predicates = new JsonObject();
      override.getPredicates().forEach(p -> predicates.addProperty(p.getProperty().toString(), p.getValue()));
      json.add("predicate", predicates);
      return json;
   }

   @FunctionalInterface
   public interface OverrideAppender {
      void add(ItemOverride var1);
   }
}
