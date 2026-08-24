package com.teamresourceful.resourcefulconfig.api.patching;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;

public interface ConfigPatchEvent {
   void register(int var1, UnaryOperator<JsonObject> var2);

   default void move(int version, String from, String to) {
      this.register(version, json -> {
         JsonObject object = getParent(json, from, false);
         if (object == null) {
            return json;
         } else {
            JsonObject parent = getParent(json, to, true);
            if (parent == null) {
               return json;
            } else {
               JsonElement element = object.remove(from.substring(from.lastIndexOf(46) + 1));
               parent.add(from.substring(from.lastIndexOf(46) + 1), element);
               return json;
            }
         }
      });
   }

   private static JsonObject getParent(JsonObject json, String path, boolean create) {
      List<String> parts = Arrays.asList(path.split("\\."));
      if (parts.size() == 1) {
         return json;
      } else {
         parts.removeLast();
         JsonElement element = json;

         for (String part : parts) {
            if (!(element instanceof JsonObject object)) {
               return null;
            }

            if (object.has(part)) {
               element = object.get(part);
            } else {
               if (!create) {
                  return null;
               }

               JsonObject newObject = new JsonObject();
               object.add(part, newObject);
               element = newObject;
            }
         }

         return element instanceof JsonObject object ? object : null;
      }
   }
}
