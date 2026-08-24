package pl.skidam.automodpack_core.utils;

import amp_libs.org.tomlj.Toml;
import amp_libs.org.tomlj.TomlArray;
import amp_libs.org.tomlj.TomlParseResult;
import amp_libs.org.tomlj.TomlTable;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import pl.skidam.automodpack_core.GlobalVariables;
import pl.skidam.automodpack_core.loader.LoaderManagerService;

public class FileInspection {
   private static final Gson GSON = new Gson();
   private static final String LOADER = GlobalVariables.LOADER;
   private static final Map<FileInspection.HashPathPair, FileInspection.Mod> modCache = new HashMap<>();
   private static final Set<String> services = Set.of(
      "META-INF/services/net.minecraftforge.forgespi.locating.IModLocator",
      "META-INF/services/net.minecraftforge.forgespi.locating.IDependencyLocator",
      "META-INF/services/net.minecraftforge.forgespi.language.IModLanguageProvider",
      "META-INF/services/net.neoforged.neoforgespi.locating.IModLocator",
      "META-INF/services/net.neoforged.neoforgespi.locating.IDependencyLocator",
      "META-INF/services/net.neoforged.neoforgespi.locating.IModLanguageLoader",
      "META-INF/services/net.neoforged.neoforgespi.locating.IModFileCandidateLocator",
      "META-INF/services/net.neoforged.neoforgespi.earlywindow.GraphicsBootstrapper"
   );
   private static final String forbiddenChars = "\\/:*\"<>|!?&%$;=+";

   public static boolean isMod(Path file) {
      if (file.getFileName().toString().endsWith(".jar") && Files.exists(file)) {
         try {
            boolean var2;
            try (FileSystem fs = FileSystems.newFileSystem(file)) {
               var2 = getModID(fs) != null || hasSpecificServices(fs);
            }

            return var2;
         } catch (IOException var6) {
            return false;
         }
      } else {
         return false;
      }
   }

   public static FileInspection.Mod getMod(Path file) {
      if (!Files.isRegularFile(file)) {
         return null;
      } else if (!file.getFileName().toString().endsWith(".jar")) {
         return null;
      } else {
         String hash = CustomFileUtils.getHash(file);
         if (hash == null) {
            GlobalVariables.LOGGER.error("Failed to get hash for file: {}", file);
            return null;
         } else {
            FileInspection.HashPathPair hashPathPair = new FileInspection.HashPathPair(hash, file);
            if (modCache.containsKey(hashPathPair)) {
               return modCache.get(hashPathPair);
            } else {
               for (FileInspection.Mod mod : GlobalVariables.LOADER_MANAGER.getModList()) {
                  if (hash.equals(mod.hash)) {
                     modCache.put(hashPathPair, mod);
                     return mod;
                  }
               }

               try (FileSystem fs = FileSystems.newFileSystem(file)) {
                  String modId = (String)getModInfo(fs, "modId");
                  if (modId != null) {
                     String modVersion = (String)getModInfo(fs, "version");
                     LoaderManagerService.EnvironmentType environmentType = (LoaderManagerService.EnvironmentType)getModInfo(fs, "environment");
                     Set<String> dependencies = getModDependencies(fs);
                     Set<String> providesIDs = getProvidedIDs(fs);
                     if (modVersion != null && dependencies != null) {
                        FileInspection.Mod modx = new FileInspection.Mod(modId, hash, providesIDs, modVersion, file, environmentType, dependencies);
                        modCache.put(hashPathPair, modx);
                        return modx;
                     } else {
                        GlobalVariables.LOGGER
                           .error("Not enough mod information for file: {} modId: {}, modVersion: {}, dependencies: {}", file, modId, modVersion, dependencies);
                     }
                  }

                  return null;
               } catch (IOException var13) {
                  GlobalVariables.LOGGER.debug("Failed to get mod info for file: {}", file);
                  return null;
               }
            }
         }
      }
   }

   public static boolean isModCompatible(Path file) {
      if (file.getFileName().toString().endsWith(".jar") && Files.exists(file)) {
         try {
            boolean var9;
            try (FileSystem fs = FileSystems.newFileSystem(file)) {
               String var3 = LOADER;

               String entryPathString = switch (var3) {
                  case "neoforge" -> "META-INF/neoforge.mods.toml";
                  case "fabric" -> "fabric.mod.json";
                  case "forge" -> "META-INF/mods.toml";
                  case "quilt" -> "quilt.mod.json";
                  default -> null;
               };
               if (entryPathString == null || !Files.exists(fs.getPath(entryPathString))) {
                  if (("forge".equals(LOADER) || "neoforge".equals(LOADER)) && hasSpecificServices(fs)) {
                     return true;
                  }

                  return false;
               }

               var9 = true;
            }

            return var9;
         } catch (IOException var7) {
            return false;
         }
      } else {
         return false;
      }
   }

   public static boolean hasSpecificServices(Path file) {
      if (file.getFileName().toString().endsWith(".jar") && Files.exists(file)) {
         try {
            boolean var2;
            try (FileSystem fs = FileSystems.newFileSystem(file)) {
               var2 = hasSpecificServices(fs);
            }

            return var2;
         } catch (IOException var6) {
            GlobalVariables.LOGGER.error("Error reading file {}: {}", file, var6.getMessage());
            return false;
         }
      } else {
         return false;
      }
   }

   public static boolean hasSpecificServices(FileSystem fs) {
      for (String service : services) {
         if (Files.exists(fs.getPath(service))) {
            return true;
         }
      }

      Path jarJarDir = fs.getPath("META-INF", "jarjar");
      if (!Files.exists(jarJarDir)) {
         return false;
      } else {
         try (Stream<Path> walk = Files.walk(jarJarDir, 1)) {
            for (Path nestedJarPath : walk.toList()) {
               if (!nestedJarPath.equals(jarJarDir) && nestedJarPath.toString().endsWith(".jar")) {
                  ZipEntry nestedEntry;
                  try (
                     InputStream inputStream = Files.newInputStream(nestedJarPath);
                     ZipInputStream zipInputStream = new ZipInputStream(inputStream);
                  ) {
                     while ((nestedEntry = zipInputStream.getNextEntry()) != null) {
                        if (services.contains(nestedEntry.getName())) {
                           return true;
                        }
                     }
                  } catch (IOException var14) {
                     GlobalVariables.LOGGER.error("Error reading nested JAR in {}: {}", nestedJarPath, var14.getMessage());
                  }
               }
            }
         } catch (IOException var16) {
            GlobalVariables.LOGGER.error("Error examining JarJar in {}", fs, var16);
         }

         return false;
      }
   }

   public static Path getMetadataPath(FileSystem fs) {
      String fallbackEntries = LOADER;

      String preferredEntry = switch (fallbackEntries) {
         case "neoforge" -> "META-INF/neoforge.mods.toml";
         case "fabric" -> "fabric.mod.json";
         case "forge" -> "META-INF/mods.toml";
         case "quilt" -> "quilt.mod.json";
         default -> null;
      };
      if (preferredEntry != null) {
         Path path = fs.getPath(preferredEntry);
         if (Files.exists(path)) {
            return path;
         }
      }

      String[] fallbackEntries = new String[]{"META-INF/neoforge.mods.toml", "fabric.mod.json", "META-INF/mods.toml", "quilt.mod.json"};

      for (String entryName : fallbackEntries) {
         if (!entryName.equals(preferredEntry)) {
            Path path = fs.getPath(entryName);
            if (Files.exists(path)) {
               return path;
            }
         }
      }

      return null;
   }

   public static String getModVersion(Path file) {
      return (String)getModInfo(file, "version");
   }

   public static String getModID(Path file) {
      return (String)getModInfo(file, "modId");
   }

   public static LoaderManagerService.EnvironmentType getModEnvironment(Path file) {
      return (LoaderManagerService.EnvironmentType)getModInfo(file, "environment");
   }

   private static String getModID(FileSystem fs) {
      return (String)getModInfo(fs, "modId");
   }

   private static Set<String> getProvidedIDs(FileSystem fs) {
      return (Set<String>)getModInfo(fs, "provides");
   }

   private static Set<String> getModDependencies(FileSystem fs) {
      return (Set<String>)getModInfo(fs, "dependencies");
   }

   private static boolean isBasicInfo(String infoType) {
      return "version".equals(infoType) || "modId".equals(infoType) || "environment".equals(infoType);
   }

   private static Object getModInfo(Path file, String infoType) {
      if (file.getFileName().toString().endsWith(".jar") && Files.exists(file)) {
         try {
            Object var3;
            try (FileSystem fs = FileSystems.newFileSystem(file)) {
               var3 = getModInfo(fs, infoType);
            }

            return var3;
         } catch (IOException var7) {
            GlobalVariables.LOGGER.error("Error reading mod file {}: {}", file, var7.getMessage());
            return isBasicInfo(infoType) ? null : Set.of();
         }
      } else {
         return isBasicInfo(infoType) ? null : Set.of();
      }
   }

   private static Object getModInfo(FileSystem fs, String infoType) {
      Path metadataPath = getMetadataPath(fs);
      if (metadataPath != null && Files.exists(metadataPath)) {
         try {
            Object var12;
            try (
               InputStream stream = Files.newInputStream(metadataPath);
               BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            ) {
               if (metadataPath.getFileName().toString().endsWith("mods.toml")) {
                  return getModInfoFromToml(reader, infoType);
               }

               var12 = getModInfoFromJson(reader, infoType);
            }

            return var12;
         } catch (IOException var11) {
            GlobalVariables.LOGGER.error("Error reading metadata {}: {}", metadataPath, var11.getMessage());
            return isBasicInfo(infoType) ? null : Set.of();
         }
      } else {
         return isBasicInfo(infoType) ? null : Set.of();
      }
   }

   private static Object getModInfoFromToml(BufferedReader reader, String infoType) {
      try {
         TomlParseResult result = Toml.parse(reader);
         result.errors().forEach(error -> GlobalVariables.LOGGER.error(error.toString()));
         TomlArray modsArray = result.getArray("mods");
         if (modsArray == null) {
            return isBasicInfo(infoType) ? null : Set.of();
         }

         switch (infoType) {
            case "version":
               String modVersion = null;

               for (Object oxxx : modsArray.toList()) {
                  TomlTable mod = (TomlTable)oxxx;
                  if (mod != null) {
                     modVersion = mod.getString("version");
                  }
               }

               return modVersion != null ? modVersion : "1";
            case "modId":
               String modID = null;

               for (Object oxx : modsArray.toList()) {
                  TomlTable mod = (TomlTable)oxx;
                  if (mod != null) {
                     modID = mod.getString("modId");
                  }
               }

               return modID;
            case "provides":
               Set<String> providedIDs = new HashSet<>();

               for (Object oxxxxxx : modsArray.toList()) {
                  TomlTable mod = (TomlTable)oxxxxxx;
                  if (mod != null) {
                     TomlArray providesArray = mod.getArray("provides");
                     if (providesArray != null) {
                        for (int j = 0; j < providesArray.size(); j++) {
                           String id = providesArray.getString(j);
                           if (id != null && !id.isEmpty()) {
                              providedIDs.add(id);
                           }
                        }
                     }
                  }
               }

               return providedIDs;
            case "dependencies":
               Set<String> dependencies = new HashSet<>();
               String modID = null;

               for (Object o : modsArray.toList()) {
                  TomlTable mod = (TomlTable)o;
                  if (mod != null) {
                     modID = mod.getString("modId");
                  }
               }

               if (modID == null) {
                  return dependencies;
               }

               TomlArray dependenciesArray = result.getArray("dependencies.\"" + modID + "\"");
               if (dependenciesArray == null) {
                  return dependencies;
               }

               for (Object ox : dependenciesArray.toList()) {
                  TomlTable mod = (TomlTable)ox;
                  if (mod != null) {
                     String depId = mod.getString("modId");
                     if (depId != null) {
                        dependencies.add(depId);
                     }
                  }
               }

               return dependencies;
            case "environment":
               LoaderManagerService.EnvironmentType environment = LoaderManagerService.EnvironmentType.UNIVERSAL;
               String modID = null;

               for (Object oxxxx : modsArray.toList()) {
                  TomlTable mod = (TomlTable)oxxxx;
                  if (mod != null) {
                     modID = mod.getString("modId");
                  }
               }

               if (modID == null) {
                  return environment;
               }

               TomlArray dependenciesArray = result.getArray("dependencies.\"" + modID + "\"");
               if (dependenciesArray == null) {
                  return environment;
               }

               for (Object oxxxxx : dependenciesArray.toList()) {
                  TomlTable mod = (TomlTable)oxxxxx;
                  if (mod != null) {
                     String depId = mod.getString("modId");
                     if (depId != null && (depId.equals("minecraft") || depId.equals("neoforge") || depId.equals("forge"))) {
                        String depEnv = mod.getString("side");
                        if (depEnv != null) {
                           String var14 = depEnv.toLowerCase();
                           switch (var14) {
                              case "client":
                                 environment = LoaderManagerService.EnvironmentType.CLIENT;
                                 break;
                              case "server":
                                 environment = LoaderManagerService.EnvironmentType.SERVER;
                           }

                           if (environment != LoaderManagerService.EnvironmentType.UNIVERSAL) {
                              return environment;
                           }
                        }
                     }
                  }
               }

               return environment;
         }
      } catch (Exception var16) {
         GlobalVariables.LOGGER.error("Error parsing TOML metadata: {}", var16.getMessage());
      }

      return !infoType.equals("version") && !infoType.equals("modId") && !infoType.equals("environment") ? Set.of() : null;
   }

   private static Object getModInfoFromJson(BufferedReader reader, String infoType) {
      JsonObject json = (JsonObject)GSON.fromJson(reader, JsonObject.class);
      switch (infoType) {
         case "version":
            if (json.has("version")) {
               return json.get("version").getAsString();
            }

            if (json.has("quilt_loader") && json.get("quilt_loader").getAsJsonObject().has("version")) {
               return json.get("quilt_loader").getAsJsonObject().get("version").getAsString();
            }
            break;
         case "modId":
            if (json.has("id")) {
               return json.get("id").getAsString();
            }

            if (json.has("quilt_loader") && json.get("quilt_loader").getAsJsonObject().has("id")) {
               return json.get("quilt_loader").getAsJsonObject().get("id").getAsString();
            }
            break;
         case "provides":
            Set<String> providedIDs = new HashSet<>();
            if (json.has("provides")) {
               for (JsonElement provides : json.get("provides").getAsJsonArray()) {
                  providedIDs.add(provides.getAsString());
               }
            } else if (json.has("quilt_loader") && json.get("quilt_loader").getAsJsonObject().has("provides")) {
               JsonObject quiltLoader = json.get("quilt_loader").getAsJsonObject();

               for (JsonElement provides : quiltLoader.get("provides").getAsJsonArray()) {
                  JsonObject providesObject = provides.getAsJsonObject();
                  String id = providesObject.get("id").getAsString();
                  providedIDs.add(id);
               }
            }

            return providedIDs;
         case "dependencies":
            Set<String> dependencies = new HashSet<>();
            if (json.has("depends")) {
               JsonObject depends = json.get("depends").getAsJsonObject();
               if (depends != null) {
                  dependencies.addAll(depends.entrySet().stream().map(Entry::getKey).toList());
               }
            } else if (json.has("quilt_loader") && json.get("quilt_loader").getAsJsonObject().has("depends")) {
               JsonObject depends = json.get("quilt_loader").getAsJsonObject().get("depends").getAsJsonObject();
               if (depends != null) {
                  dependencies.addAll(depends.entrySet().stream().map(Entry::getKey).toList());
               }
            }

            return dependencies;
         case "environment":
            if (json.has("environment")) {
               String environment = json.get("environment").getAsString();
               if (environment == null) {
                  return LoaderManagerService.EnvironmentType.UNIVERSAL;
               }

               String var14 = environment.toLowerCase();

               return switch (var14) {
                  case "client" -> LoaderManagerService.EnvironmentType.CLIENT;
                  case "server" -> LoaderManagerService.EnvironmentType.SERVER;
                  default -> LoaderManagerService.EnvironmentType.UNIVERSAL;
               };
            }

            if (json.has("quilt_loader") && json.has("minecraft") && json.get("minecraft").getAsJsonObject().has("environment")) {
               String environment = json.get("minecraft").getAsJsonObject().get("environment").getAsString();
               if (environment == null) {
                  return LoaderManagerService.EnvironmentType.UNIVERSAL;
               }

               String depends = environment.toLowerCase();

               return switch (depends) {
                  case "client" -> LoaderManagerService.EnvironmentType.CLIENT;
                  case "server" -> LoaderManagerService.EnvironmentType.SERVER;
                  default -> LoaderManagerService.EnvironmentType.UNIVERSAL;
               };
            }
      }

      return !infoType.equals("version") && !infoType.equals("modId") && !infoType.equals("environment") ? Set.of() : null;
   }

   public static boolean isInValidFileName(String fileName) {
      for (char c : "\\/:*\"<>|!?&%$;=+".toCharArray()) {
         if (fileName.indexOf(c) != -1) {
            return true;
         }
      }

      for (char cx : fileName.toCharArray()) {
         if (cx < ' ' || cx == 127) {
            return true;
         }
      }

      return fileName.trim().isEmpty();
   }

   public static String fixFileName(String fileName) {
      for (char c : fileName.toCharArray()) {
         if (c < ' ' || c == 127 || "\\/:*\"<>|!?&%$;=+".indexOf(c) != -1) {
            fileName = fileName.replace(c, '-');
         }
      }

      return fileName.trim();
   }

   public record HashPathPair(String hash, Path path) {
   }

   public record Mod(
      String modID,
      String hash,
      Collection<String> providesIDs,
      String modVersion,
      Path modPath,
      LoaderManagerService.EnvironmentType environmentType,
      Collection<String> dependencies
   ) {
   }
}
