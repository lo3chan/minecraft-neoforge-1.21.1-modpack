package dev.latvian.mods.kubejs.script.data;

import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Stream;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.FilePackResources.SharedZipFileAccess;
import net.minecraft.server.packs.PackResources.ResourceOutput;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class KubeFileResourcePack implements PackResources {
   public static final PackLocationInfo PACK_LOCATION_INFO = new PackLocationInfo("kubejs", Component.empty(), PackSource.BUILT_IN, Optional.empty());
   private final PackType packType;
   private Map<ResourceLocation, GeneratedData> generated;
   private Set<String> generatedNamespaces;

   private static Stream<Path> tryWalk(Path path) {
      try {
         return Files.walk(path);
      } catch (Exception var2) {
         return Stream.empty();
      }
   }

   public static void scanForInvalidFiles(String pathName, Path path) throws IOException {
      long start = System.currentTimeMillis();
      int files = 0;

      for (Path p : Files.list(path)
         .filter(x$0 -> Files.isDirectory(x$0))
         .flatMap(KubeFileResourcePack::tryWalk)
         .filter(x$0 -> Files.isRegularFile(x$0))
         .filter(Files::isReadable)
         .toList()) {
         files++;

         try {
            String fileName = p.getFileName().toString();
            String fileNameLC = fileName.toLowerCase(Locale.ROOT);
            if (!fileNameLC.endsWith(".zip") && !fileNameLC.equals(".ds_store") && !fileNameLC.equals("thumbs.db") && !fileNameLC.equals("desktop.ini")) {
               if (Files.isHidden(path)) {
                  ConsoleJS.STARTUP.error("Invisible file found: " + pathName + path.relativize(p).toString().replace('\\', '/')).withExternalFile(p);
               } else {
                  char[] chars = fileName.toCharArray();

                  for (char c : chars) {
                     if (c >= 'A' && c <= 'Z') {
                        ConsoleJS.STARTUP
                           .error("Invalid file name: Uppercase '" + c + "' in " + pathName + path.relativize(p).toString().replace('\\', '/'))
                           .withExternalFile(p);
                        break;
                     }

                     if (c != '_' && c != '-' && (c < 'a' || c > 'z') && (c < '0' || c > '9') && c != '/' && c != '.') {
                        ConsoleJS.STARTUP
                           .error("Invalid file name: Invalid character '" + c + "' in " + pathName + path.relativize(p).toString().replace('\\', '/'))
                           .withExternalFile(p);
                        break;
                     }
                  }
               }
            }
         } catch (Exception var14) {
            ConsoleJS.STARTUP.error("Invalid file name: " + pathName + path.relativize(p).toString().replace('\\', '/'), var14).withExternalFile(p);
         }
      }

      ConsoleJS.STARTUP.info("Validated " + files + " files in " + pathName + " in " + (System.currentTimeMillis() - start) + "ms");
   }

   public static int findBeforeModsIndex(List<PackResources> packs) {
      for (int i = 0; i < packs.size(); i++) {
         PackResources pack = packs.get(i);
         if (pack instanceof VanillaPackResources) {
            return i + 1;
         }
      }

      return 1;
   }

   public static int findAfterModsIndex(List<PackResources> packs) {
      for (int i = packs.size() - 1; i >= 0; i--) {
         PackResources pack = packs.get(i);
         if (pack instanceof FilePackResources) {
            return i + 1;
         }
      }

      return packs.size();
   }

   public static void scanAndLoad(Path path, List<PackResources> packs) {
      for (File file : Objects.requireNonNull(path.toFile().listFiles())) {
         String fileName = file.getName();
         if (file.isFile() && fileName.endsWith(".zip")) {
            StringBuilder packName = new StringBuilder();

            for (char c : fileName.toCharArray()) {
               if (c == '_' || c == '.' || c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z') {
                  packName.append(c);
               }
            }

            long lastModified = 0L;
            if (file.exists()) {
               lastModified = file.lastModified();
            }

            packs.add(
               new FilePackResources(
                  new PackLocationInfo(
                     fileName,
                     Component.literal(fileName),
                     PackSource.BUILT_IN,
                     Optional.of(
                        new KnownPack(
                           "kubejs",
                           "kubejs_file_" + packName.toString().toLowerCase(Locale.ROOT),
                           lastModified <= 0L ? "1" : Long.toUnsignedString(lastModified)
                        )
                     )
                  ),
                  new SharedZipFileAccess(file),
                  ""
               )
            );
         }
      }
   }

   public KubeFileResourcePack(PackType t) {
      this.packType = t;
   }

   @Nullable
   public GeneratedData getRootResource(String... path) {
      String var2 = path.length == 1 ? path[0] : "";

      return switch (var2) {
         case "pack.mcmeta" -> GeneratedData.PACK_META;
         case "pack.png" -> GeneratedData.PACK_ICON;
         default -> null;
      };
   }

   public Map<ResourceLocation, GeneratedData> getGenerated() {
      if (this.generated == null) {
         this.generated = new HashMap<>();
         this.generate(this.generated);
         boolean debug = DevProperties.get().logGeneratedData;

         try {
            Path root = KubeJSPaths.get(this.packType);

            for (Path dir : Files.list(root).filter(x$0 -> Files.isDirectory(x$0)).toList()) {
               String ns = dir.getFileName().toString();
               if (debug) {
                  KubeJS.LOGGER.info("# Walking namespace '{}'", ns);
               }

               for (Path path : Files.walk(dir).filter(x$0 -> Files.isRegularFile(x$0)).filter(Files::isReadable).toList()) {
                  String pathStr = dir.relativize(path).toString().replace('\\', '/').toLowerCase(Locale.ROOT);
                  int sindex = pathStr.lastIndexOf(47);
                  String fileNameLC = sindex == -1 ? pathStr : pathStr.substring(sindex + 1);
                  if (!fileNameLC.endsWith(".zip")
                     && !fileNameLC.equals(".ds_store")
                     && !fileNameLC.equals("thumbs.db")
                     && !fileNameLC.equals("desktop.ini")
                     && !Files.isHidden(path)) {
                     GeneratedData data = new GeneratedData(ResourceLocation.fromNamespaceAndPath(ns, pathStr), () -> {
                        try {
                           return Files.readAllBytes(path);
                        } catch (Exception var2x) {
                           var2x.printStackTrace();
                           return new byte[0];
                        }
                     });
                     if (debug) {
                        KubeJS.LOGGER.info("- File found: '{}' ({} bytes)", data.id(), data.data().get().length);
                     }

                     if (this.skipFile(data)) {
                        if (debug) {
                           KubeJS.LOGGER.info("- Skipping '{}'", data.id());
                        }
                     } else {
                        this.generated.put(data.id(), data);
                     }
                  }
               }
            }
         } catch (Exception var12) {
            KubeJS.LOGGER.error("Failed to load files from kubejs/{}", this.packType.getDirectory(), var12);
         }

         this.generated.put(GeneratedData.INTERNAL_RELOAD.id(), GeneratedData.INTERNAL_RELOAD);
         this.generated = Map.copyOf(this.generated);
         if (debug) {
            KubeJS.LOGGER.info("Generated {} data ({} files)", this.packType, this.generated.size());
         }
      }

      return this.generated;
   }

   protected boolean skipFile(GeneratedData data) {
      return this.packType == PackType.CLIENT_RESOURCES ? data.id().getPath().startsWith("lang/") : false;
   }

   @Nullable
   public IoSupplier<InputStream> getResource(PackType type, ResourceLocation location) {
      GeneratedData r = type == this.packType ? this.getGenerated().get(location) : null;
      if (r == GeneratedData.INTERNAL_RELOAD) {
         this.close();
      }

      return r;
   }

   public void generate(Map<ResourceLocation, GeneratedData> map) {
   }

   public void listResources(PackType type, String namespace, String path, ResourceOutput visitor) {
      if (type == this.packType) {
         if (!path.endsWith("/")) {
            path = path + "/";
         }

         for (Entry<ResourceLocation, GeneratedData> r : this.getGenerated().entrySet()) {
            if (r.getKey().getNamespace().equals(namespace) && r.getKey().getPath().startsWith(path)) {
               visitor.accept(r.getKey(), r.getValue());
            }
         }
      }
   }

   @NotNull
   public Set<String> getNamespaces(PackType type) {
      if (type != this.packType) {
         return Collections.emptySet();
      } else {
         if (this.generatedNamespaces == null) {
            this.generatedNamespaces = new HashSet<>();

            for (Entry<ResourceLocation, GeneratedData> s : this.getGenerated().entrySet()) {
               this.generatedNamespaces.add(s.getKey().getNamespace());
            }
         }

         return this.generatedNamespaces;
      }
   }

   @Nullable
   public <T> T getMetadataSection(MetadataSectionSerializer<T> serializer) throws IOException {
      GeneratedData inputSupplier = this.getRootResource("pack.mcmeta");
      if (inputSupplier != null) {
         Object var4;
         try (InputStream input = inputSupplier.get()) {
            var4 = AbstractPackResources.getMetadataFromStream(serializer, input);
         }

         return (T)var4;
      } else {
         return null;
      }
   }

   @NotNull
   public String packId() {
      return "KubeJS File Resource Pack [" + this.packType.getDirectory() + "]";
   }

   public void close() {
      this.generated = null;
      this.generatedNamespaces = null;
   }

   public PackLocationInfo location() {
      return PACK_LOCATION_INFO;
   }

   @Override
   public String toString() {
      return this.packId();
   }
}
