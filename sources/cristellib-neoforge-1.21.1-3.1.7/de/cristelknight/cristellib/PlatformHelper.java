package de.cristelknight.cristellib;

import com.mojang.datafixers.util.Pair;
import de.cristelknight.cristellib.api.CristelLibAPI;
import de.cristelknight.cristellib.neoforge.PlatformHelperImpl;
import de.cristelknight.cristellib.util.Platform;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.injectables.annotations.ExpectPlatform.Transformed;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;

public class PlatformHelper {
   @ExpectPlatform
   @Transformed
   public static Path getConfigDirectory() {
      return PlatformHelperImpl.getConfigDirectory();
   }

   @ExpectPlatform
   @Transformed
   public static InputStream getResourceStream(String modId, String subPath) {
      return PlatformHelperImpl.getResourceStream(modId, subPath);
   }

   @ExpectPlatform
   @Transformed
   public static Pair<PackResources, PackResources> registerBuiltinResourcePack(ResourceLocation id, Component displayName) {
      return PlatformHelperImpl.registerBuiltinResourcePack(id, displayName);
   }

   @ExpectPlatform
   @Transformed
   public static PackResources createOverlay(PackResources pack, String overlay) {
      return PlatformHelperImpl.createOverlay(pack, overlay);
   }

   @ExpectPlatform
   @Transformed
   public static String getModDisplayName(String modId) {
      return PlatformHelperImpl.getModDisplayName(modId);
   }

   @ExpectPlatform
   @Transformed
   public static Platform getPlatform() {
      return PlatformHelperImpl.getPlatform();
   }

   @ExpectPlatform
   @Transformed
   public static boolean isClient() {
      return PlatformHelperImpl.isClient();
   }

   @ExpectPlatform
   @Transformed
   public static void findInModFiles(String modId, String startingFolder, Predicate<Path> fileFilter, Consumer<String> consumer) {
      PlatformHelperImpl.findInModFiles(modId, startingFolder, fileFilter, consumer);
   }

   @ExpectPlatform
   @Transformed
   public static Map<String, CristelLibAPI> getApis() {
      return PlatformHelperImpl.getApis();
   }

   @ExpectPlatform
   @Transformed
   public static boolean isDevelopmentEnvironment() {
      return PlatformHelperImpl.isDevelopmentEnvironment();
   }
}
