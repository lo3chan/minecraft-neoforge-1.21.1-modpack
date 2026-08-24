package net.mehvahdjukaar.moonlight.api.resources.pack;

import com.google.common.base.Stopwatch;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import net.mehvahdjukaar.moonlight.api.util.FilesHelper;
import net.mehvahdjukaar.moonlight.core.CommonConfigs;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.FilePackResources.FileResourcesSupplier;

public class CacheZipPackResources extends AbstractCachedEditableResources {
   private final Map<ResourceLocation, byte[]> tempResources = new ConcurrentHashMap<>();
   private volatile boolean dirty = false;

   public CacheZipPackResources(PackLocationInfo location, PackType type, Path path) {
      super(
         !path.getFileName().toString().endsWith(".zip") ? path.resolveSibling(path.getFileName() + ".zip") : path,
         location,
         type,
         Component.translatable("message.moonlight.cached_zipped")
      );
   }

   @Override
   public void addResource(ResourceLocation id, byte[] bytes) {
      this.tempResources.put(id, bytes);
      this.dirty = true;
   }

   @Override
   public void removeResource(ResourceLocation id) {
      this.tempResources.remove(id);
      this.dirty = true;
   }

   @Override
   public void removeRootResource(String name) {
   }

   @Override
   public boolean clearAllResources() {
      Stopwatch stopwatch = Stopwatch.createStarted();

      try {
         if (this.cachedResources != null) {
            this.cachedResources.close();
         }

         this.cachedResources = null;
         FilesHelper.fastRemove(this.path);
      } catch (Exception var3) {
         Moonlight.LOGGER.warn("Failed to clear zipped cached resource pack at {}", this.path, var3);
      }

      boolean gone = !Files.exists(this.path);
      if (!gone) {
         Moonlight.LOGGER.error("Failed to delete cached resource pack at {}", this.path);
      }

      Moonlight.LOGGER.info("Cleared zipped cached resource pack at {} in {}", this.path, stopwatch);
      return gone;
   }

   @Override
   public boolean initializeIfValid() {
      if (Files.exists(this.path) && Files.isDirectory(this.path)) {
         FilesHelper.fastRemove(this.path);
      }

      boolean cacheExists = Files.isRegularFile(this.path);
      if (cacheExists) {
         if (CommonConfigs.FASTER_CACHE_SEARCH.get()) {
            this.cachedResources = new FastSearchFilePackResources(this.locationInfo, this.path.toFile(), this.packType);
         } else {
            this.cachedResources = new FileResourcesSupplier(this.path.toFile()).openPrimary(this.locationInfo);
         }
      }

      return cacheExists;
   }

   @Override
   public PackType getPackType() {
      return this.packType;
   }

   @Override
   public boolean isEmpty() {
      return false;
   }

   @Override
   public void commitChanges() {
      if (this.dirty) {
         this.dirty = false;
         if (this.cachedResources != null) {
            Moonlight.LOGGER.error("Zip file resources was not cleared before commit. Clearing now.");
            if (!this.clearAllResources()) {
               throw new RuntimeException("Could not clear resources before writing zip");
            }
         }

         try {
            Stopwatch stopwatch = Stopwatch.createStarted();
            this.writeZipPreferStored(this.tempResources, this.path);
            this.tempResources.clear();
            this.initializeIfValid();
            Moonlight.LOGGER.info("Wrote cached resource pack to {} in {}", this.path, stopwatch);
         } catch (Exception var2) {
            throw new RuntimeException(var2);
         }
      }
   }

   public void writeZipPreferStored(Map<ResourceLocation, byte[]> files, Path outputZip) throws IOException {
      try {
         FilesHelper.writeAtomically(outputZip, out -> {
            try (ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(out))) {
               this.writeEntriesStored(zos, files);
            }
         });
      } catch (Exception var4) {
         Moonlight.LOGGER.warn("Could not write zip using STORED; falling back to DEFLATED: {}", String.valueOf(var4));
         FilesHelper.writeAtomically(outputZip, out -> {
            try (ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(out))) {
               this.writeEntriesDeflated(zos, files, 0);
            }
         });
      }
   }

   private void writeEntriesStored(ZipOutputStream zos, Map<ResourceLocation, byte[]> files) throws IOException {
      for (Entry<ResourceLocation, byte[]> e : files.entrySet()) {
         String name = this.packType.getDirectory() + "/" + e.getKey().toString().replace(':', '/').replace('\\', '/');
         byte[] data = e.getValue();
         CRC32 crc = new CRC32();
         crc.update(data);
         ZipEntry ze = new ZipEntry(name);
         ze.setMethod(0);
         ze.setSize(data.length);
         ze.setCompressedSize(data.length);
         ze.setCrc(crc.getValue());
         zos.putNextEntry(ze);
         zos.write(data);
         zos.closeEntry();
      }
   }

   private void writeEntriesDeflated(ZipOutputStream zos, Map<ResourceLocation, byte[]> files, int level) throws IOException {
      zos.setLevel(level);

      for (Entry<ResourceLocation, byte[]> e : files.entrySet()) {
         String name = this.packType.getDirectory() + "/" + e.getKey().toString().replace(':', '/').replace('\\', '/');
         ZipEntry ze = new ZipEntry(name);
         zos.putNextEntry(ze);
         zos.write(e.getValue());
         zos.closeEntry();
      }
   }
}
