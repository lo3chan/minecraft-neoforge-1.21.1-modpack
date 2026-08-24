package net.mehvahdjukaar.moonlight.api.resources.pack;

import com.google.common.base.Stopwatch;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.mehvahdjukaar.moonlight.api.misc.ResourceLocationSearchTrie;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PackResources.ResourceOutput;
import net.minecraft.server.packs.resources.IoSupplier;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class FastSearchFilePackResources extends AbstractPackResources {
   static final Logger LOGGER = LogUtils.getLogger();
   private final FastSearchFilePackResources.SharedZipFileAccess zipFileAccess;
   private final ResourceLocationSearchTrie searchTrie = new ResourceLocationSearchTrie();
   private final PackType packType;

   FastSearchFilePackResources(PackLocationInfo location, File file, PackType packType) {
      super(location);
      this.zipFileAccess = new FastSearchFilePackResources.SharedZipFileAccess(file);
      this.packType = packType;
      this.buildIndex();
   }

   private void buildIndex() {
      Stopwatch watch = Stopwatch.createStarted();

      try {
         ZipFile zip = this.zipFileAccess.getOrCreateZipFile();
         String pathName = this.packType.getDirectory() + "/";

         assert zip != null;

         Enumeration<? extends ZipEntry> e = zip.entries();

         while (e.hasMoreElements()) {
            ZipEntry ze = e.nextElement();
            if (!ze.isDirectory()) {
               String name = ze.getName();
               if (name.startsWith(pathName)) {
                  name = name.substring(pathName.length());
                  this.searchTrie.insertPath(name);
               }
            }
         }
      } catch (Exception var10) {
         LOGGER.error("Failed to index zip file {}", this.zipFileAccess, var10);
      } finally {
         Moonlight.LOGGER.info("Populated search tree for pack at {} in {}", this.zipFileAccess, watch);
      }
   }

   private static String getPathFromLocation(PackType packType, ResourceLocation location) {
      return String.format(Locale.ROOT, "%s/%s/%s", packType.getDirectory(), location.getNamespace(), location.getPath());
   }

   @Nullable
   public IoSupplier<InputStream> getRootResource(String... elements) {
      return this.getResource(String.join("/", elements));
   }

   public IoSupplier<InputStream> getResource(PackType packType, ResourceLocation location) {
      return packType != this.packType ? null : this.getResource(getPathFromLocation(packType, location));
   }

   @Nullable
   private IoSupplier<InputStream> getResource(String resourcePath) {
      ZipFile zipFile = this.zipFileAccess.getOrCreateZipFile();
      if (zipFile == null) {
         return null;
      } else {
         ZipEntry zipEntry = zipFile.getEntry(resourcePath);
         return zipEntry == null ? null : IoSupplier.create(zipFile, zipEntry);
      }
   }

   public Set<String> getNamespaces(PackType packType) {
      return (Set<String>)(packType != this.packType ? Set.of() : new HashSet<>(this.searchTrie.listFolders("")));
   }

   public void close() {
      this.zipFileAccess.close();
   }

   public void listResources(PackType packType, String namespace, String path, ResourceOutput output) {
      if (packType == this.packType) {
         String prefix = packType.getDirectory() + "/";
         ZipFile zipFile = this.zipFileAccess.getOrCreateZipFile();
         if (zipFile != null) {
            this.searchTrie.search(namespace + "/" + path).forEach(r -> {
               ZipEntry zipEntry = zipFile.getEntry(prefix + r.getNamespace() + "/" + r.getPath());
               if (zipEntry == null) {
                  throw new RuntimeException("Zip file entry was null");
               } else {
                  output.accept(r, IoSupplier.create(zipFile, zipEntry));
               }
            });
         }
      }
   }

   static class SharedZipFileAccess implements AutoCloseable {
      final File file;
      @Nullable
      private ZipFile zipFile;
      private boolean failedToLoad;

      SharedZipFileAccess(File file) {
         this.file = file;
      }

      @Nullable
      ZipFile getOrCreateZipFile() {
         if (this.failedToLoad) {
            return null;
         } else {
            if (this.zipFile == null) {
               try {
                  this.zipFile = new ZipFile(this.file);
               } catch (IOException var2) {
                  FastSearchFilePackResources.LOGGER.error("Failed to open pack {}", this.file, var2);
                  this.failedToLoad = true;
                  return null;
               }
            }

            return this.zipFile;
         }
      }

      @Override
      public String toString() {
         return this.file.toString();
      }

      @Override
      public void close() {
         if (this.zipFile != null) {
            IOUtils.closeQuietly(this.zipFile);
            this.zipFile = null;
         }
      }
   }
}
