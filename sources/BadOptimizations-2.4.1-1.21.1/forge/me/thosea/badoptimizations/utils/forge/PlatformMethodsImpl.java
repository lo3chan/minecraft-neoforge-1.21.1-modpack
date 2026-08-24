package forge.me.thosea.badoptimizations.utils.forge;

import forge.me.thosea.badoptimizations.config.Config;
import forge.me.thosea.badoptimizations.hook.CacheHooks;
import forge.me.thosea.badoptimizations.hook.HookCreator;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModInfo;

public final class PlatformMethodsImpl {
   private PlatformMethodsImpl() {
   }

   public static String getVersion() {
      return ModList.get().getModContainerById("badoptimizations").map(mod -> mod.getModInfo().getVersion().toString()).orElse("[unknown version]");
   }

   public static Path getConfigFolder() {
      return FMLPaths.CONFIGDIR.get();
   }

   public static boolean isModLoaded(String id) {
      return LoadingModList.get().getModFileById(id) != null;
   }

   public static boolean isOnServer() {
      return FMLLoader.getDist() == Dist.DEDICATED_SERVER;
   }

   public static InputStream streamConfigTemplate() throws IOException {
      return Files.newInputStream(LoadingModList.get().getModFileById("badoptimizations").getFile().findResource(new String[]{"bo-config-template.txt"}));
   }

   public static Map<String, List<String>> getModIncompatibilities() {
      Map<String, List<String>> result = new HashMap<>(1);

      for (ModInfo mod : LoadingModList.get().getMods()) {
         String id = mod.getModId();
         Optional<Object> object = mod.getOwningFile().getConfigElement(new String[]{"badoptimizations:incompatibilities"});
         if (!object.isEmpty()) {
            if (object.get() instanceof Map<?, ?> map && castMap(map).get("options") instanceof List<?> list) {
               ArrayList var12 = new ArrayList();

               for (Object element : list) {
                  if (element instanceof String entry) {
                     var12.add(entry);
                  } else {
                     Config.LOGGER.warn("Mod {} specifies invalid BadOptimizations incompatibilities", id);
                     Config.LOGGER.warn("TOML options contains non-string value in array");
                  }
               }

               result.put(id, var12);
            } else {
               Config.LOGGER.warn("Mod {} specifies invalid BadOptimizations incompatibilities, ignoring", id);
               Config.LOGGER.warn("TOML is not a map containing a string list named options");
            }
         }
      }

      return result;
   }

   public static List<CacheHooks.CacheHookEntry> getModCacheHooks() {
      List<CacheHooks.CacheHookEntry> result = new ArrayList<>();

      for (ModInfo mod : LoadingModList.get().getMods()) {
         String id = mod.getModId();
         Optional<Object> object = mod.getOwningFile().getConfigElement(new String[]{"badoptimizations:cache_hooks"});
         if (!object.isEmpty()) {
            if (object.get() instanceof Map<?, ?> map) {
               BooleanSupplier var10 = getEntry(id, map, "common");
               BooleanSupplier lightmap = getEntry(id, map, "lightmap");
               BooleanSupplier skyColor = getEntry(id, map, "skycolor");
               if (var10 != null || lightmap != null || skyColor != null) {
                  CacheHooks.CacheHookEntry entry = new CacheHooks.CacheHookEntry(var10, lightmap, skyColor);
                  result.add(entry);
               }
            } else {
               Config.LOGGER.warn("Mod {} specifies invalid BadOptimizations caching hooks, ignoring", id);
               Config.LOGGER.warn("TOML is not a map");
            }
         }
      }

      return result;
   }

   private static BooleanSupplier getEntry(String modId, Map<?, ?> map, String key) {
      Object value = map.get(key);
      if (value == null) {
         return null;
      } else if (value instanceof String string) {
         BooleanSupplier hook = HookCreator.tryCreateHook(modId, string);
         if (hook != null) {
            Config.LOGGER.info("Mod {} added a {} caching hook: {}", new Object[]{modId, key, string});
         }

         return hook;
      } else {
         Config.LOGGER.warn("Mod {} specifies an invalid BadOptimizations caching hook", modId);
         Config.LOGGER.warn("TOML key {} is not a string", key);
         return null;
      }
   }

   private static <T extends Map<?, ?>> T castMap(Map<?, ?> map) {
      return (T)map;
   }
}
