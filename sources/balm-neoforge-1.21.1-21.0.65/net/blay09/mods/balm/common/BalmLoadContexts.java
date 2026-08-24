package net.blay09.mods.balm.common;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import net.blay09.mods.balm.api.BalmRuntimeLoadContext;

public class BalmLoadContexts {
   private static final Map<String, BalmRuntimeLoadContext> loadContexts = new ConcurrentHashMap<>();

   public static void register(String modId, BalmRuntimeLoadContext context) {
      loadContexts.put(modId, context);
   }

   public static <T extends BalmRuntimeLoadContext> Optional<T> get(String modId) {
      return Optional.ofNullable((T)loadContexts.get(modId));
   }
}
