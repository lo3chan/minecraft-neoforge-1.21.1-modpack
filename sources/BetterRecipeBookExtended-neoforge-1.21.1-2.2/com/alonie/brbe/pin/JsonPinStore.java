package com.alonie.brbe.pin;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.resources.ResourceLocation;

public final class JsonPinStore implements PinStore {
   private static final Gson GSON = new Gson();
   private static final Type SET_TYPE = (new TypeToken<HashSet<ResourceLocation>>() {}).getType();
   private final Path path;

   public JsonPinStore(Path gameDir) {
      this.path = gameDir.resolve("brbe.pins");
   }

   @Override
   public Set<ResourceLocation> load() {
      if (!Files.exists(this.path)) {
         return new HashSet<>();
      } else {
         try {
            String json = Files.readString(this.path, StandardCharsets.UTF_8);
            Set<ResourceLocation> result = (Set<ResourceLocation>)GSON.fromJson(json, SET_TYPE);
            return (Set<ResourceLocation>)(result != null ? result : new HashSet<>());
         } catch (IOException var3) {
            System.err.println("[BRBE] Failed to read pins file: " + var3.getMessage());
            return new HashSet<>();
         }
      }
   }

   @Override
   public void save(Set<ResourceLocation> pinned) {
      CompletableFuture.runAsync(() -> {
         try {
            String json = GSON.toJson(pinned);
            Files.createDirectories(this.path.getParent());
            Files.writeString(this.path, json, StandardCharsets.UTF_8);
         } catch (IOException var3) {
            System.err.println("[BRBE] Failed to write pins file: " + var3.getMessage());
         }
      });
   }
}
