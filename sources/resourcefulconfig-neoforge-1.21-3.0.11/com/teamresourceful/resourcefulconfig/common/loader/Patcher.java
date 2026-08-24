package com.teamresourceful.resourcefulconfig.common.loader;

import com.google.gson.JsonObject;
import com.teamresourceful.resourcefulconfig.api.patching.ConfigPatchEvent;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import net.minecraft.util.GsonHelper;

public class Patcher {
   public static JsonObject patch(JsonObject json, int currentVersion, Consumer<ConfigPatchEvent> handler) {
      if (currentVersion < 0) {
         return json;
      } else {
         int fileVersion = GsonHelper.getAsInt(json, "rconfig:version", 0);
         Int2ObjectFunction<UnaryOperator<JsonObject>> patches = getPatches(currentVersion, handler);

         for (int i = fileVersion; i < currentVersion; i++) {
            UnaryOperator<JsonObject> patch = (UnaryOperator<JsonObject>)patches.get(i);
            if (patch != null) {
               json = patch.apply(json);
            }
         }

         return json;
      }
   }

   private static Int2ObjectFunction<UnaryOperator<JsonObject>> getPatches(int currentVersion, Consumer<ConfigPatchEvent> handler) {
      Int2ObjectMap<UnaryOperator<JsonObject>> patches = new Int2ObjectOpenHashMap();
      handler.accept((version, patcher) -> {
         if (version > currentVersion) {
            throw new IllegalStateException("Patch version is greater than current version");
         } else {
            patches.compute(version, (key, value) -> value == null ? patcher : andThen(value, patcher));
         }
      });
      return version -> (UnaryOperator)patches.getOrDefault(version, UnaryOperator.identity());
   }

   private static <T> UnaryOperator<T> andThen(UnaryOperator<T> first, UnaryOperator<T> second) {
      return t -> second.apply(first.apply((T)t));
   }
}
