package net.mehvahdjukaar.moonlight.api.resources.pack;

import com.google.common.base.Stopwatch;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import net.mehvahdjukaar.moonlight.api.misc.ResourceLocationSearchTrie;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.minecraft.FileUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PackResources.ResourceOutput;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class FastSearchPathPackResources extends AbstractPackResources {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Path root;
   private final ResourceLocationSearchTrie searchTrie = new ResourceLocationSearchTrie();
   private final PackType packType;

   public FastSearchPathPackResources(PackLocationInfo location, Path root, PackType packType) {
      super(location);
      this.root = root;
      this.packType = packType;
      this.buildIndex();
   }

   private void buildIndex() {
      Stopwatch watch = Stopwatch.createStarted();
      Path base = this.root.resolve(this.packType.getDirectory());
      if (!Files.exists(base)) {
         Moonlight.LOGGER.info("Pack at {} does not contain {}, skipping index", this.root, this.packType.getDirectory());
      } else {
         try (Stream<Path> stream = Files.find(base, 2147483647, (p, attrs) -> attrs.isRegularFile())) {
            stream.forEach(file -> {
               String rel = base.relativize(file).toString().replace('\\', '/');
               int slash = rel.indexOf(47);
               if (slash > 0 && slash != rel.length() - 1) {
                  String namespace = rel.substring(0, slash);
                  String pathWithinNs = rel.substring(slash + 1);
                  if (!ResourceLocation.isValidNamespace(namespace)) {
                     LOGGER.warn("Non [a-z0-9_.-] character in namespace {} in pack {}, ignoring", namespace, this.root);
                  } else {
                     ResourceLocation rl = ResourceLocation.tryBuild(namespace, pathWithinNs);
                     if (rl != null) {
                        this.searchTrie.insert(rl);
                     }
                  }
               }
            });
         } catch (IOException var13) {
            LOGGER.error("Failed to build index for {}", base, var13);
         } finally {
            Moonlight.LOGGER.info("Populated search tree for pack at {} in {}", this.root, watch);
         }
      }
   }

   @Nullable
   public IoSupplier<InputStream> getRootResource(String... elements) {
      FileUtil.validatePath(elements);
      Path path = FileUtil.resolvePath(this.root, List.of(elements));
      return Files.exists(path) ? IoSupplier.create(path) : null;
   }

   @Nullable
   public IoSupplier<InputStream> getResource(PackType packType, ResourceLocation location) {
      if (this.packType != packType) {
         return null;
      } else if (this.searchTrie.search(ResourceLocationSearchTrie.getResPath(location)).isEmpty()) {
         return null;
      } else {
         Path path = this.root.resolve(packType.getDirectory()).resolve(location.getNamespace());
         return getResource(location, path);
      }
   }

   @Nullable
   private static IoSupplier<InputStream> getResource(ResourceLocation location, Path path) {
      return (IoSupplier<InputStream>)FileUtil.decomposePath(location.getPath()).mapOrElse(list -> {
         Path path2 = FileUtil.resolvePath(path, list);
         return returnFileIfExists(path2);
      }, error -> {
         LOGGER.error("Invalid path {}: {}", location, error.message());
         return null;
      });
   }

   @Nullable
   private static IoSupplier<InputStream> returnFileIfExists(Path path) {
      return Files.exists(path) ? IoSupplier.create(path) : null;
   }

   public void listResources(PackType packType, String namespace, String path, ResourceOutput output) {
      if (packType == this.packType) {
         this.searchTrie.search(namespace + "/" + path).forEach(resourceLocation -> {
            Path file = this.root.resolve(packType.getDirectory()).resolve(resourceLocation.getNamespace()).resolve(resourceLocation.getPath());
            if (Files.isRegularFile(file)) {
               output.accept(resourceLocation, IoSupplier.create(file));
            } else {
               LOGGER.warn("File not found or not regular: {} -> {}", resourceLocation, file);
            }
         });
      }
   }

   public Set<String> getNamespaces(PackType packType) {
      return (Set<String>)(packType != this.packType ? Set.of() : new HashSet<>(this.searchTrie.listFolders("")));
   }

   public void close() {
   }
}
