package de.cristelknight.cristellib.neoforge;

import com.mojang.datafixers.util.Pair;
import de.cristelknight.cristellib.Constants;
import de.cristelknight.cristellib.api.CristelLibAPI;
import de.cristelknight.cristellib.api.CristelPlugin;
import de.cristelknight.cristellib.builtinpacks.BuiltinResourcePackSource;
import de.cristelknight.cristellib.data.PathFinder;
import de.cristelknight.cristellib.neoforge.extraapiutil.APIFinder;
import de.cristelknight.cristellib.neoforge.mixin.PathPackResourcesAccessor;
import de.cristelknight.cristellib.util.Platform;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.loading.moddiscovery.ModInfo;
import net.neoforged.neoforgespi.locating.IModFile;
import org.jetbrains.annotations.Nullable;

public class PlatformHelperImpl {
   public static Path getConfigDirectory() {
      return FMLPaths.CONFIGDIR.get();
   }

   public static InputStream getResourceStream(String modId, String subPath) {
      Path pathC = getResourceDirectory(modId, subPath);
      if (pathC == null) {
         return null;
      } else {
         try {
            return Files.newInputStream(pathC);
         } catch (IOException var5) {
            Constants.LOG.warn("Couldn't create Input Stream for Path {}", pathC, var5);
            return null;
         }
      }
   }

   public static Pair<PackResources, PackResources> registerBuiltinResourcePack(ResourceLocation id, Component displayName) {
      String modID = id.getNamespace();
      String path = id.getPath();
      Path totalPath = getResourceDirectory(modID, id.getPath());
      if (totalPath != null) {
         PackLocationInfo metadata = new PackLocationInfo(id.toString(), displayName, new BuiltinResourcePackSource(), Optional.empty());
         PathPackResources server = new PathPackResources(metadata, totalPath);
         PathPackResources client = new PathPackResources(metadata, totalPath);
         return new Pair(
            server.getNamespaces(PackType.SERVER_DATA).isEmpty() ? null : server, client.getNamespaces(PackType.CLIENT_RESOURCES).isEmpty() ? null : client
         );
      } else {
         Constants.LOG.debug("Couldn't find path: {} in container for modID: {} for pack with display name: {}", path, modID, displayName);
         return null;
      }
   }

   public static PackResources createOverlay(PackResources pack, String overlay) {
      if (pack instanceof PathPackResourcesAccessor accessor) {
         return new PathPackResources(pack.location(), accessor.cristellib$getRoot().resolve(overlay));
      } else {
         Constants.LOG.warn("Couldn't create overlay for Pack: {}, because it does not support overlays", pack.packId());
         return null;
      }
   }

   public static String getModDisplayName(String modId) {
      return ModList.get().getModContainerById(modId).map(container -> container.getModInfo().getDisplayName()).orElse(modId);
   }

   public static Platform getPlatform() {
      return Platform.NEO_FORGE;
   }

   public static boolean isClient() {
      return FMLEnvironment.dist.isClient();
   }

   public static void findInModFiles(String modId, String startingFolder, Predicate<Path> fileFilter, Consumer<String> consumer) {
      IModFile file = getModFile(modId);
      if (file != null) {
         try {
            PathFinder.walk(getResourceDirectory(modId, startingFolder), fileFilter, consumer);
         } catch (IOException var6) {
            throw new RuntimeException(Constants.getWithPrefix("Error while trying to walk through mod files"), var6);
         }
      }
   }

   public static Map<String, CristelLibAPI> getApis() {
      Map<String, CristelLibAPI> apiMap = new HashMap<>();

      for (Pair<List<String>, CristelLibAPI> apiPair : APIFinder.scanForAPIs(CristelPlugin.class, CristelLibAPI.class)) {
         String modId = (String)((List)apiPair.getFirst()).getFirst();
         apiMap.put(modId, (CristelLibAPI)apiPair.getSecond());
      }

      return apiMap;
   }

   public static boolean isDevelopmentEnvironment() {
      return !FMLLoader.isProduction();
   }

   private static IModFile getModFile(String modId) {
      ModList modList = ModList.get();
      IModFile file;
      if (modList == null) {
         ModInfo info = ModLoadingUtilImpl.getPreLoadedModInfo(modId);
         if (info == null) {
            Constants.LOG.warn("Mod info for modId: {} is null", modId);
            return null;
         }

         file = info.getOwningFile().getFile();
      } else {
         ModContainer container = (ModContainer)modList.getModContainerById(modId).orElse(null);
         if (container == null) {
            Constants.LOG.warn("Mod container for modId: {} is null", modId);
            return null;
         }

         file = container.getModInfo().getOwningFile().getFile();
      }

      return file;
   }

   @Nullable
   private static Path getResourceDirectory(String modId, String subPath) {
      IModFile file = getModFile(modId);
      if (file == null) {
         return null;
      } else {
         Path path = file.findResource(new String[]{subPath});
         if (path == null) {
            Constants.LOG.warn("Path for subPath: {} in modId: {} is null", subPath, modId);
         }

         return path;
      }
   }
}
