package com.finndog.moogs_structures.config;

import com.finndog.moogs_structures.MoogsStructuresCommon;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.PreparableReloadListener.PreparationBarrier;
import net.minecraft.util.profiling.ProfilerFiller;

public class StructureManifestReloadListener implements PreparableReloadListener {
   private static final String DIR = "moogs_structures";
   private static final String FILE = "replace_vanilla.json";
   private static final String SET_DIR = "worldgen/structure_set";

   public CompletableFuture<Void> reload(
      PreparationBarrier barrier,
      ResourceManager manager,
      ProfilerFiller prepProfiler,
      ProfilerFiller applyProfiler,
      Executor prepExecutor,
      Executor applyExecutor
   ) {
      return CompletableFuture.<StructureManifestReloadListener.Prepared>supplyAsync(() -> {
         Map<String, String> manifests = readManifests(manager);
         Map<String, Map<String, String>> setJsons = readStructureSets(manager);
         return new StructureManifestReloadListener.Prepared(manifests, setJsons);
      }, prepExecutor).<StructureManifestReloadListener.Prepared>thenCompose(barrier::wait).thenAcceptAsync(prepared -> {
         StructureListManager.reload(prepared.manifests(), prepared.setJsons());
         ReplaceVanillaManager.reloadConfig();
      }, applyExecutor);
   }

   private static Map<String, String> readManifests(ResourceManager manager) {
      Map<String, String> out = new HashMap<>();

      for (Entry<ResourceLocation, Resource> e : manager.listResources("moogs_structures", loc -> loc.getPath().endsWith("replace_vanilla.json")).entrySet()) {
         try (InputStream is = e.getValue().open()) {
            out.put(e.getKey().getNamespace(), new String(is.readAllBytes(), StandardCharsets.UTF_8));
         } catch (IOException var9) {
            MoogsStructuresCommon.LOGGER.warn("Moogs Structures: could not read {} ({})", e.getKey(), var9.getMessage());
         }
      }

      return out;
   }

   private static Map<String, Map<String, String>> readStructureSets(ResourceManager manager) {
      Map<String, Map<String, String>> out = new HashMap<>();

      for (Entry<ResourceLocation, Resource> e : manager.listResources("worldgen/structure_set", locx -> locx.getPath().endsWith(".json")).entrySet()) {
         ResourceLocation loc = e.getKey();
         String ns = loc.getNamespace();
         String path = loc.getPath();
         String name = path.substring("worldgen/structure_set".length() + 1, path.length() - ".json".length());

         try (InputStream is = e.getValue().open()) {
            String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            if (json.contains("moogs_structures:") && StructureListManager.isMslStructureSet(json)) {
               out.computeIfAbsent(ns, k -> new HashMap<>()).put(ns + ":" + name, json);
            }
         } catch (IOException var13) {
            MoogsStructuresCommon.LOGGER.warn("Moogs Structures: could not read structure_set {} ({})", loc, var13.getMessage());
         }
      }

      return out;
   }

   private record Prepared(Map<String, String> manifests, Map<String, Map<String, String>> setJsons) {
   }
}
