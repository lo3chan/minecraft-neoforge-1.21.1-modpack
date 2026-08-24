package de.cristelknight.cristellib.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.cristelknight.cristellib.Constants;
import de.cristelknight.cristellib.PlatformHelper;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class JsonHelper {
   private static final String ANY = "[*]";

   @Nullable
   public static JsonElement getSetElement(ResourceLocation location, String getDataFromModId) {
      return getElement(getDataFromModId, "data/" + location.getNamespace() + "/worldgen/structure_set/" + location.getPath() + ".json");
   }

   @Nullable
   public static JsonElement getElement(String getDataFromModId, String location) {
      InputStream in = PlatformHelper.getResourceStream(getDataFromModId, location);
      if (in == null) {
         Constants.LOG.warn("Couldn't create Input Stream for sub path {} in modId {}", location, getDataFromModId);
         return null;
      } else {
         try {
            JsonElement var4;
            try (InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
               var4 = JsonParser.parseReader(reader);
            }

            return var4;
         } catch (IOException var8) {
            Constants.LOG.warn("Couldn't read {} from mod: {}", location, getDataFromModId, var8);
            return null;
         }
      }
   }

   public static List<JsonElement> findAll(String searchedKey, JsonObject object, String parentKey) {
      if (searchedKey.isEmpty() && parentKey.isEmpty()) {
         return List.of(object);
      } else {
         List<JsonElement> results = new ArrayList<>();

         for (Entry<String, JsonElement> entry : object.entrySet()) {
            String fullKey = parentKey + entry.getKey();
            JsonElement value = entry.getValue();
            boolean shouldBreak = collectMatches(results, searchedKey, fullKey, value);
            if (shouldBreak) {
               break;
            }
         }

         return results;
      }
   }

   public static void findAllInArray(List<JsonElement> results, String searchedKey, JsonArray array, String parentKey) {
      if (searchedKey.split("\\.").length > parentKey.split("\\.").length) {
         for (int i = 0; i < array.size(); i++) {
            JsonElement value = array.get(i);
            String fullKey = parentKey + "." + getIndex(i);
            boolean shouldBreak = collectMatches(results, searchedKey, fullKey, value);
            if (shouldBreak) {
               break;
            }
         }
      }
   }

   private static boolean collectMatches(List<JsonElement> results, String searchedKey, String fullKey, JsonElement value) {
      JsonHelper.Match match = matches(searchedKey, fullKey);
      switch (match) {
         case MATCH:
            results.add(value);
            return false;
         case MATCH_AND_BREAK:
            results.add(value);
            return true;
         case NO_MATCH:
         default:
            if (value instanceof JsonArray array) {
               findAllInArray(results, searchedKey, array, fullKey);
            } else if (value instanceof JsonObject nested) {
               results.addAll(findAll(searchedKey, nested, fullKey + "."));
            }

            return false;
         case BREAK:
            return false;
      }
   }

   private static JsonHelper.Match matches(String searchedKey, String fullKey) {
      String[] searched = searchedKey.split("\\.");
      String[] current = fullKey.split("\\.");
      if (searched.length < current.length) {
         return JsonHelper.Match.BREAK;
      } else {
         for (int i = 0; i < current.length; i++) {
            String s = searched[i];
            if (!s.equals("[*]")) {
               String c = current[i];
               if (!s.equals(c)) {
                  return JsonHelper.Match.BREAK;
               }
            }
         }

         if (searched.length > current.length) {
            return JsonHelper.Match.NO_MATCH;
         } else {
            return searched[searched.length - 1].equals("[*]") ? JsonHelper.Match.MATCH : JsonHelper.Match.MATCH_AND_BREAK;
         }
      }
   }

   private static String getIndex(int i) {
      return "[" + i + "]";
   }

   private static enum Match {
      MATCH,
      MATCH_AND_BREAK,
      NO_MATCH,
      BREAK;
   }
}
