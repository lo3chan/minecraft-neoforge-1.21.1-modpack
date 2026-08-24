package dev.architectury.platform;

import architectury_inject_architectury_common_d9fb27da44a148dfa43566d20897d6de_f4ed98e062c0e85c5cc998c6fbbab16b146fa1f727cd0f469b44c896e896070earchitectury13011devjar.PlatformMethods;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.injectables.annotations.ExpectPlatform.Transformed;
import dev.architectury.platform.forge.PlatformImpl;
import dev.architectury.utils.Env;
import java.nio.file.Path;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;
import net.minecraft.SharedConstants;
import net.neoforged.api.distmarker.Dist;

public final class Platform {
   private static int simpleLoaderCache = -1;

   private Platform() {
   }

   public static boolean isFabric() {
      updateLoaderCache();
      return simpleLoaderCache == 0;
   }

   @Deprecated(
      forRemoval = true
   )
   public static boolean isForge() {
      return isForgeLike();
   }

   public static boolean isForgeLike() {
      return isMinecraftForge() || isNeoForge();
   }

   public static boolean isMinecraftForge() {
      updateLoaderCache();
      return simpleLoaderCache == 1;
   }

   public static boolean isNeoForge() {
      updateLoaderCache();
      return simpleLoaderCache == 2;
   }

   private static void updateLoaderCache() {
      if (simpleLoaderCache == -1) {
         String var0 = PlatformMethods.getCurrentTarget();
         switch (var0) {
            case "fabric":
               simpleLoaderCache = 0;
               break;
            case "forge":
               simpleLoaderCache = 1;
               break;
            case "neoforge":
               simpleLoaderCache = 2;
         }
      }
   }

   public static String getMinecraftVersion() {
      return SharedConstants.getCurrentVersion().getId();
   }

   @ExpectPlatform
   @Transformed
   public static Path getGameFolder() {
      return PlatformImpl.getGameFolder();
   }

   @ExpectPlatform
   @Transformed
   public static Path getConfigFolder() {
      return PlatformImpl.getConfigFolder();
   }

   @ExpectPlatform
   @Transformed
   public static Path getModsFolder() {
      return PlatformImpl.getModsFolder();
   }

   @ExpectPlatform
   @Transformed
   public static Env getEnvironment() {
      return PlatformImpl.getEnvironment();
   }

   @ExpectPlatform
   @Transformed
   public static Dist getEnv() {
      return PlatformImpl.getEnv();
   }

   @ExpectPlatform
   @Transformed
   public static boolean isModLoaded(String id) {
      return PlatformImpl.isModLoaded(id);
   }

   @ExpectPlatform
   @Transformed
   public static Mod getMod(String id) {
      return PlatformImpl.getMod(id);
   }

   public static Optional<Mod> getOptionalMod(String id) {
      try {
         return Optional.of(getMod(id));
      } catch (NoSuchElementException var2) {
         return Optional.empty();
      }
   }

   @ExpectPlatform
   @Transformed
   public static Collection<Mod> getMods() {
      return PlatformImpl.getMods();
   }

   @ExpectPlatform
   @Transformed
   public static Collection<String> getModIds() {
      return PlatformImpl.getModIds();
   }

   @ExpectPlatform
   @Transformed
   public static boolean isDevelopmentEnvironment() {
      return PlatformImpl.isDevelopmentEnvironment();
   }
}
