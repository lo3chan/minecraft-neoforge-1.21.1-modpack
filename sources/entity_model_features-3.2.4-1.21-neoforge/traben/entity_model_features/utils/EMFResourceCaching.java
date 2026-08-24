package traben.entity_model_features.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import traben.entity_model_features.EMFException;

public class EMFResourceCaching {
   public static final Map<String, Boolean> RESOURCE_EXISTENCE_CACHE = new ConcurrentHashMap<>();
   private static boolean isPopulated = false;

   public static void clearCache() {
      RESOURCE_EXISTENCE_CACHE.clear();
      isPopulated = false;
   }

   private static void populateCacheIfNeeded(ResourceManager resources) {
      if (!isPopulated) {
         scanPrefix(resources, "emf/cem");
         scanPrefix(resources, "optifine/cem");
         isPopulated = true;
      }
   }

   private static void scanPrefix(ResourceManager resources, String prefix) {
      try {
         String pathPrefix = prefix.contains(":") ? prefix.split(":", 2)[1] : prefix;
         Map<ResourceLocation, Resource> found = resources.listResources(pathPrefix, p -> true);

         for (ResourceLocation loc : found.keySet()) {
            RESOURCE_EXISTENCE_CACHE.put(loc.toString(), Boolean.TRUE);
         }
      } catch (Exception var6) {
         EMFException.recordException(var6);
      }
   }

   public static boolean resourceExists(ResourceManager resources, ResourceLocation loc) {
      String key = loc.toString();
      Boolean cached = RESOURCE_EXISTENCE_CACHE.get(key);
      if (cached != null) {
         return cached;
      } else {
         populateCacheIfNeeded(resources);
         boolean exists = resources.getResource(loc).isPresent();
         RESOURCE_EXISTENCE_CACHE.put(key, exists);
         return exists;
      }
   }
}
