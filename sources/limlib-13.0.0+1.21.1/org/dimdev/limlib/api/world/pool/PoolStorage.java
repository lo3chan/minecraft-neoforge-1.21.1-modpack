package org.dimdev.limlib.api.world.pool;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Map.Entry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.dimdev.limlib.impl.Limlib;

public class PoolStorage {
   private static Map<ResourceLocation, PiecePool> POOLS = new HashMap<>();

   public static PiecePool getPool(ResourceLocation id) throws NoSuchElementException {
      if (!POOLS.containsKey(id)) {
         Limlib.LOGGER.error("This pool does not exist: {}", id, new NoSuchElementException());
      }

      return POOLS.get(id);
   }

   public static void initializePoolStorage() {
      Limlib.getSided().registerServerLoader("pool_listener", (provider, resourceManager) -> PoolStorage.PoolListener.load(resourceManager));
   }

   private static class PoolListener {
      public static void load(ResourceManager manager) {
         HashMap<ResourceLocation, PiecePool> map = new HashMap<>();

         for (Entry<ResourceLocation, Resource> resource : manager.listResources("worldgen/piece_pools", idx -> idx.getPath().endsWith(".json")).entrySet()) {
            try (InputStream inputStream = resource.getValue().open()) {
               JsonObject json = (JsonObject)Limlib.GSON.fromJson(new InputStreamReader(inputStream), JsonObject.class);
               PiecePool pool = (PiecePool)PiecePool.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow();
               if (map.containsKey(pool.getPool()) && !pool.shouldOverride) {
                  ResourceLocation id = pool.getPool();

                  for (String subPool : pool.getSubPools().keySet()) {
                     if (map.get(id).hasSubPool(subPool)) {
                        map.get(id).addPiecesToSubPool(subPool, pool.getSubPools().get(subPool).toArray(String[]::new));
                     } else {
                        map.get(id).addSubPool(subPool, pool.getSubPools().get(subPool).toArray(String[]::new));
                     }
                  }
               } else {
                  map.put(pool.getPool(), pool);
               }
            } catch (IOException var12) {
               var12.printStackTrace();
            }
         }

         PoolStorage.POOLS = Map.copyOf(map);
      }
   }
}
