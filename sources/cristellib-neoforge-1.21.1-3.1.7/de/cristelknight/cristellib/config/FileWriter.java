package de.cristelknight.cristellib.config;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonArray;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonGrammar;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonGrammar.Builder;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.DataResult.Error;
import de.cristelknight.cristellib.Constants;
import de.cristelknight.cristellib.PlatformHelper;
import de.cristelknight.cristellib.util.jankson.CommentArray;
import de.cristelknight.cristellib.util.jankson.JanksonOps;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.function.Supplier;

public class FileWriter {
   public static final Jankson JANKSON = Jankson.builder().build();
   public static final Supplier<Builder> JSON_GRAMMAR_BUILDER = () -> new Builder().withComments(true).bareSpecialNumerics(true).printCommas(true);
   public static final JsonGrammar JSON_GRAMMAR = JSON_GRAMMAR_BUILDER.get().build();

   public static <T> void writeToFile(Path path, Codec<T> codec, Map<String, String> comments, T from, String rawHeader, boolean isSorted) {
      JsonElement jsonElement = writeToElement(
         String.format("Jankson file creation for \"%s\" failed due to the following error(s):", path.toString()), codec, JanksonOps.INSTANCE, from
      );
      if (jsonElement instanceof JsonObject jsonObject) {
         jsonElement = addCommentsAndAlphabeticallySortRecursively(comments, jsonObject, "", isSorted);
      }

      try {
         Files.createDirectories(path.getParent());
         String output = rawHeader + jsonElement.toJson(JSON_GRAMMAR);
         Files.write(path, output.getBytes());
      } catch (IOException var8) {
         Constants.LOG.error("Failed to write file to \"%s\" due to the following error(s):", var8);
      }
   }

   public static <T, E> E writeToElement(String errorMsg, Codec<T> codec, DynamicOps<E> ops, T from) {
      DataResult<E> dataResult = codec.encodeStart(ops, from);
      Optional<Error<E>> error = dataResult.error();
      if (error.isPresent()) {
         throw new IllegalArgumentException(Constants.getWithPrefix(errorMsg + "\n" + error.get().message()));
      } else {
         return (E)dataResult.result().orElseThrow();
      }
   }

   public static <T> T readFromJanksonPath(Path path, Codec<T> codec) {
      JsonElement load;
      try {
         load = JANKSON.load(path.toFile());
      } catch (Exception var4) {
         throw new IllegalArgumentException(
            Constants.getWithPrefix(String.format("Couldn't load %s, crashing instead. Maybe try to delete the config files!", path))
         );
      }

      return loadFromElement(String.format("Couldn't read %s, crashing instead. Maybe try to delete the config files!", path), codec, JanksonOps.INSTANCE, load);
   }

   public static <T> T readFromModContainer(String modId, String subPath, Codec<T> codec, String errorMsg) {
      InputStream stream = PlatformHelper.getResourceStream(modId, subPath);
      if (stream == null) {
         throw new IllegalArgumentException(
            Constants.getWithPrefix("Couldn't create InputStream for subPath: " + subPath + " in ModContainer with id: " + modId)
         );
      } else {
         com.google.gson.JsonElement load = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
         return loadFromElement(errorMsg, codec, JsonOps.INSTANCE, load);
      }
   }

   public static <T, E> T loadFromElement(String errorMsg, Codec<T> codec, DynamicOps<E> ops, E load) {
      DataResult<Pair<T, E>> decode = codec.decode(ops, load);
      Optional<Error<Pair<T, E>>> error = decode.error();
      if (error.isPresent()) {
         throw new IllegalArgumentException(Constants.getWithPrefix(errorMsg) + "\n" + error.get().message());
      } else {
         return (T)((Pair)decode.result().orElseThrow()).getFirst();
      }
   }

   public static JsonObject addCommentsAndAlphabeticallySortRecursively(
      Map<String, String> comments, JsonObject object, String parentKey, boolean alphabeticallySorted
   ) {
      if (comments.isEmpty() && !alphabeticallySorted) {
         return object;
      } else {
         for (Entry<String, JsonElement> entry : object.entrySet()) {
            String objectKey = entry.getKey();
            String commentsKey = parentKey + objectKey;
            String comment = object.getComment(entry.getKey());
            if (comments.containsKey(commentsKey) && comment == null) {
               String commentToAdd = comments.get(commentsKey);
               object.setComment(objectKey, commentToAdd);
               comment = commentToAdd;
            }

            JsonElement value = entry.getValue();
            if (value instanceof JsonArray array) {
               JsonArray sortedJsonElements = new JsonArray();

               for (JsonElement element : array) {
                  if (element instanceof JsonObject nestedObject) {
                     sortedJsonElements.add(addCommentsAndAlphabeticallySortRecursively(comments, nestedObject, entry.getKey() + ".", alphabeticallySorted));
                  } else if (element instanceof JsonArray array1) {
                     CommentArray commentArray = new CommentArray();
                     commentArray.addAll(array1);
                     sortedJsonElements.add(commentArray);
                  }
               }

               if (!sortedJsonElements.isEmpty()) {
                  object.put(objectKey, sortedJsonElements, comment);
               }
            }

            if (value instanceof JsonObject nestedObject) {
               object.put(objectKey, addCommentsAndAlphabeticallySortRecursively(comments, nestedObject, entry.getKey() + ".", alphabeticallySorted), comment);
            }
         }

         if (alphabeticallySorted) {
            JsonObject alphabeticallySortedJsonObject = new JsonObject();
            TreeMap<String, JsonElement> map = new TreeMap<>(String::compareTo);
            map.putAll(object);
            alphabeticallySortedJsonObject.putAll(map);
            alphabeticallySortedJsonObject.forEach((key, entry) -> alphabeticallySortedJsonObject.setComment(key, object.getComment(key)));
            return alphabeticallySortedJsonObject;
         } else {
            return object;
         }
      }
   }
}
