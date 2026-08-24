package net.mehvahdjukaar.moonlight.api.resources.pack;

import com.google.common.base.Stopwatch;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.mehvahdjukaar.moonlight.api.resources.RPUtils;
import net.mehvahdjukaar.moonlight.api.util.FastCachedWriter;
import net.mehvahdjukaar.moonlight.api.util.FilesHelper;
import net.mehvahdjukaar.moonlight.core.CommonConfigs;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources.PathResourcesSupplier;
import org.apache.commons.io.FileUtils;

public class CachePathPackResources extends AbstractCachedEditableResources {
   private final FastCachedWriter writer = new FastCachedWriter();

   public CachePathPackResources(PackLocationInfo location, PackType type, Path path) {
      super(path, location, type, Component.translatable("message.moonlight.cached"));
   }

   @Override
   public void addResource(ResourceLocation id, byte[] bytes) {
      try {
         Path resPath = RPUtils.getResourcePath(this.path, id, this.packType);
         this.writer.writeFast(resPath, bytes);
      } catch (IOException var4) {
         throw new RuntimeException("An error occurred while adding a resource to the dynamic pack: ", var4);
      }
   }

   @Override
   public void removeResource(ResourceLocation id) {
      Path resPath = RPUtils.getResourcePath(this.path, id, this.packType);

      try {
         deleteRecursively(resPath);
      } catch (Exception var4) {
         Moonlight.LOGGER.warn("Failed to delete resource {}", id, var4);
      }
   }

   @Override
   public void removeRootResource(String name) {
   }

   @Override
   public boolean clearAllResources() {
      Stopwatch stopwatch = Stopwatch.createStarted();

      try {
         this.writer.clear();
         FilesHelper.fastRemove(this.path);
      } catch (Exception var3) {
         Moonlight.LOGGER.warn("Failed to clear cache pack resources at {}", this.path, var3);
         return false;
      }

      Moonlight.LOGGER.info("Cleared cache pack resources at {} in {}", this.path, stopwatch);
      return true;
   }

   @Override
   public boolean initializeIfValid() {
      if (Files.exists(this.path) && !Files.isDirectory(this.path)) {
         FilesHelper.fastRemove(this.path);
      }

      boolean dirExists = Files.isDirectory(this.path);
      if (dirExists) {
         if (CommonConfigs.FASTER_CACHE_SEARCH.get()) {
            this.cachedResources = new FastSearchPathPackResources(this.locationInfo, this.path, this.packType);
         } else {
            this.cachedResources = new PathResourcesSupplier(this.path).openPrimary(this.locationInfo);
         }
      }

      return dirExists;
   }

   @Override
   public void commitChanges() {
      this.initializeIfValid();
   }

   @Override
   public PackType getPackType() {
      return this.packType;
   }

   @Override
   public boolean isEmpty() {
      return false;
   }

   private static void deleteRecursively(Path p) throws IOException {
      if (Files.isDirectory(p)) {
         FileUtils.deleteDirectory(p.toFile());
      } else {
         Files.deleteIfExists(p);
      }
   }
}
