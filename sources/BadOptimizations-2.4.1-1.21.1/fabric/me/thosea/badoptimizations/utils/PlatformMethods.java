package fabric.me.thosea.badoptimizations.utils;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.injectables.annotations.ExpectPlatform.Transformed;
import fabric.me.thosea.badoptimizations.hook.CacheHooks;
import fabric.me.thosea.badoptimizations.utils.fabric.PlatformMethodsImpl;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public final class PlatformMethods {
   private PlatformMethods() {
   }

   @ExpectPlatform
   @Transformed
   public static String getVersion() {
      return PlatformMethodsImpl.getVersion();
   }

   @ExpectPlatform
   @Transformed
   public static Path getConfigFolder() {
      return PlatformMethodsImpl.getConfigFolder();
   }

   @ExpectPlatform
   @Transformed
   public static boolean isModLoaded(String id) {
      return PlatformMethodsImpl.isModLoaded(id);
   }

   @ExpectPlatform
   @Transformed
   public static boolean isOnServer() {
      return PlatformMethodsImpl.isOnServer();
   }

   @ExpectPlatform
   @Transformed
   public static InputStream streamConfigTemplate() {
      return PlatformMethodsImpl.streamConfigTemplate();
   }

   @ExpectPlatform
   @Transformed
   public static Map<String, List<String>> getModIncompatibilities() {
      return PlatformMethodsImpl.getModIncompatibilities();
   }

   @ExpectPlatform
   @Transformed
   public static List<CacheHooks.CacheHookEntry> getModCacheHooks() {
      return PlatformMethodsImpl.getModCacheHooks();
   }
}
