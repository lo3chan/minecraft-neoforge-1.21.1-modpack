package de.markusbordihn.modsoptimizer.data;

import com.google.gson.JsonObject;
import de.markusbordihn.modsoptimizer.Constants;
import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.Version;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.Toml;
import de.markusbordihn.modsoptimizer.utils.SemanticVersionUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

public class ModFileParser {
   public static final String MANIFEST_AUTOMATIC_MODULE_NAME = "Automatic-Module-Name";
   public static final String MANIFEST_IMPLEMENTATION_VERSION = "Implementation-Version";
   public static final String MANIFEST_IMPLEMENTATION_TIMESTAMP = "Implementation-Timestamp";
   public static final String MANIFEST_IMPLEMENTATION_TITLE = "Implementation-Title";
   public static final String MANIFEST_SPECIFICATION_TITLE = "Specification-Title";
   public static final String MANIFEST_FML_MOD_TYPE = "FMLModType";
   private static final List<DateTimeFormatter> TIMESTAMP_FORMATTERS = List.of(
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ"),
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.n"),
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.nX"),
      DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss")
   );

   protected ModFileParser() {
   }

   private static ModFileData.ModType getModTypeByFile(Manifest manifest, JarFile jarFile) {
      String fileName = jarFile.getName().toLowerCase(Locale.ROOT);
      if (fileName.endsWith(".jar")) {
         if (fileName.contains("-neoforge-") || fileName.endsWith("-neoforge.jar")) {
            return ModFileData.ModType.NEOFORGE;
         }

         if (fileName.startsWith("-fabric-") || fileName.endsWith("-fabric.jar")) {
            return ModFileData.ModType.FABRIC;
         }

         if (fileName.startsWith("-forge-") || fileName.endsWith("-forge.jar")) {
            return ModFileData.ModType.FORGE;
         }

         if (fileName.startsWith("-quilt-") || fileName.endsWith("-quilt.jar")) {
            return ModFileData.ModType.QUILT;
         }
      }

      if (jarFile.getEntry("META-INF/mods.toml") != null && jarFile.getEntry("fabric.mod.json") != null) {
         return ModFileData.ModType.MIXED;
      } else {
         if (manifest != null && manifest.getMainAttributes() != null) {
            Attributes mainAttributes = manifest.getMainAttributes();
            if (mainAttributes.getValue("Fabric-Gradle-Version") != null && !mainAttributes.getValue("Fabric-Gradle-Version").isEmpty()
               || mainAttributes.getValue("Fabric-Loader-Version") != null && !mainAttributes.getValue("Fabric-Loader-Version").isEmpty()) {
               if (jarFile.getEntry("fabric.mod.json") != null) {
                  return ModFileData.ModType.FABRIC;
               }

               if (jarFile.getEntry("quilt.mod.json") != null) {
                  return ModFileData.ModType.QUILT;
               }

               if (jarFile.getEntry("META-INF/mods.toml") != null
                  || jarFile.getEntry("META-INF/services/cpw.mods.modlauncher.api.ITransformationService") != null) {
                  return ModFileData.ModType.MIXED;
               }
            } else {
               if (hasAttributeValue("Implementation-Title", mainAttributes) && mainAttributes.getValue("Implementation-Title").equals("NeoForge")) {
                  return ModFileData.ModType.NEOFORGE;
               }

               if (hasAttributeValue("FMLModType", mainAttributes)) {
                  return ModFileData.ModType.FORGE;
               }
            }
         }

         if (jarFile.getEntry("META-INF/mods.toml") != null) {
            return ModFileData.ModType.FORGE;
         } else if (jarFile.getEntry("META-INF/neoforge.mods.toml") != null) {
            return ModFileData.ModType.NEOFORGE;
         } else if (jarFile.getEntry("fabric.mod.json") != null) {
            return ModFileData.ModType.FABRIC;
         } else if (jarFile.getEntry("quilt.mod.json") != null) {
            return ModFileData.ModType.QUILT;
         } else {
            Constants.LOG
               .warn("⚠ Unable to detect mod type for {} with manifest {}", jarFile.getName(), manifest != null ? manifest.getMainAttributes() : null);
            return ModFileData.ModType.UNKNOWN;
         }
      }
   }

   public static ModFileData parseModFile(Manifest manifest, Path path, JarFile jarFile) {
      ModFileData.ModType modType = getModTypeByFile(manifest, jarFile);

      return switch (modType) {
         case FORGE -> parseForgeModFile(manifest, path, jarFile);
         case NEOFORGE -> parseNeoForgeModFile(manifest, path, jarFile);
         case FABRIC -> parseFabricModFile(manifest, path, jarFile);
         case QUILT -> parseQuiltModFile(manifest, path, jarFile);
         case MIXED -> parseMixedModFile(manifest, path, jarFile);
         default -> {
            Constants.LOG
               .error(
                  "⚠ Found unknown mod type {} for mod file {} with manifest {}!",
                  new Object[]{modType, jarFile.getName(), manifest != null ? manifest.getMainAttributes() : null}
               );
            yield new ModFileData(
               path, "unknown_id", modType, "Unknown", ModFileData.EMPTY_VERSION, ModFileData.ModEnvironment.BOTH, ModFileData.EMPTY_TIMESTAMP
            );
         }
      };
   }

   public static ModFileData parseMixedModFile(Manifest manifest, Path path, JarFile jarFile) {
      ModFileData forgeModFileData = parseForgeModFile(manifest, path, jarFile);
      ModFileData fabricModFileData = parseFabricModFile(manifest, path, jarFile);
      String modId = forgeModFileData.id();
      String name = forgeModFileData.name();
      Version version = forgeModFileData.version();
      ModFileData.ModEnvironment environment = forgeModFileData.environment();
      LocalDateTime timestamp = forgeModFileData.timestamp();
      if (modId == null || modId.isEmpty() || modId.equals("unknown_id")) {
         modId = fabricModFileData.id();
         name = fabricModFileData.name();
         version = fabricModFileData.version();
         environment = fabricModFileData.environment();
         timestamp = fabricModFileData.timestamp();
      }

      if (modId == null || modId.isEmpty() || modId.equals("unknown_id")) {
         ModFileData quiltModFileData = parseQuiltModFile(manifest, path, jarFile);
         modId = quiltModFileData.id();
         name = quiltModFileData.name();
         version = quiltModFileData.version();
         environment = quiltModFileData.environment();
         timestamp = quiltModFileData.timestamp();
      }

      if (jarFile.getEntry("META-INF/services/cpw.mods.modlauncher.api.ITransformationService") != null) {
         environment = ModFileData.ModEnvironment.SERVICE;
      }

      if (environment == ModFileData.ModEnvironment.UNKNOWN) {
         boolean isDataPack = false;

         for (ZipEntry zipEntry : jarFile.stream().toList()) {
            if (zipEntry.isDirectory()) {
               if (!zipEntry.getName().startsWith("assets/")
                  && !zipEntry.getName().startsWith("data/")
                  && !zipEntry.getName().startsWith("META-INF/")
                  && !zipEntry.getName().startsWith("things/")) {
                  isDataPack = false;
                  break;
               }

               isDataPack = true;
            }
         }

         if (isDataPack) {
            environment = ModFileData.ModEnvironment.DATA_PACK;
         }
      }

      return new ModFileData(path, modId, ModFileData.ModType.MIXED, name, version, environment, timestamp);
   }

   public static ModFileData parseNeoForgeModFile(Manifest manifest, Path path, JarFile jarFile) {
      ModFileData modFileData = parseForgeModFile(manifest, path, jarFile);
      return new ModFileData(
         modFileData.path(),
         modFileData.id(),
         ModFileData.ModType.NEOFORGE,
         modFileData.name(),
         modFileData.version(),
         modFileData.environment(),
         modFileData.timestamp()
      );
   }

   public static ModFileData parseForgeModFile(Manifest manifest, Path path, JarFile jarFile) {
      String modId = "unknown_id";
      String name = "Unknown";
      Version version = ModFileData.EMPTY_VERSION;
      ModFileData.ModType modType = ModFileData.ModType.FORGE;
      ModFileData.ModEnvironment environment = ModFileData.ModEnvironment.UNKNOWN;
      LocalDateTime timestamp = ModFileData.EMPTY_TIMESTAMP;
      Toml modsToml = TomlFileParser.tryReadTomlFile(jarFile, Path.of("META-INF/mods.toml"));
      if (modsToml == null || modsToml.isEmpty()) {
         modsToml = TomlFileParser.tryReadTomlFile(jarFile, Path.of("META-INF/neoforge.mods.toml"));
         if (modsToml != null && !modsToml.isEmpty()) {
            modType = ModFileData.ModType.NEOFORGE;
         }
      }

      if (modsToml != null && !modsToml.isEmpty()) {
         String modsPrefix = "mods[0].";
         String modsVersionId = modsPrefix + "version";
         if (modsToml.getString(modsPrefix + "modId") != null) {
            modId = modsToml.getString(modsPrefix + "modId");
         } else {
            Constants.LOG.warn("⚠ Found no modId tag inside the mods.toml file {}!", path);
         }

         if (modsToml.getString(modsPrefix + "displayName") != null) {
            name = modsToml.getString(modsPrefix + "displayName");
         } else {
            Constants.LOG.warn("⚠ Found no displayName tag inside the mods.toml file {}!", path);
         }

         if (modsToml.getString("modLoader") != null && modsToml.getString("modLoader").equalsIgnoreCase("lowcodefml")) {
            environment = ModFileData.ModEnvironment.DATA_PACK;
         }

         if (modsToml.getString(modsVersionId) != null && !modsToml.getString(modsVersionId).startsWith("${")) {
            version = SemanticVersionUtils.parseVersion(modsToml.getString(modsVersionId));
         } else if (modsToml.getString("version") != null && !modsToml.getString("version").startsWith("${")) {
            Constants.LOG.warn("⚠ The version tag should be placed inside the [[mods]] section of the mods.toml file {}!", path);
            version = SemanticVersionUtils.parseVersion(modsToml.getString("version"));
         }

         if (environment == ModFileData.ModEnvironment.UNKNOWN) {
            for (int i = -1; i < 10; i++) {
               String dependencyId = i == -1 ? "dependencies[0]" : "dependencies." + modId + "[" + i + "]";

               String modIdValue;
               try {
                  if (!modsToml.contains(dependencyId)) {
                     break;
                  }

                  modIdValue = modsToml.getString(dependencyId + ".modId");
               } catch (Exception var19) {
                  if (i != -1) {
                     break;
                  }
                  continue;
               }

               if ("forge".equals(modIdValue) || "neoforge".equals(modIdValue) || "minecraft".equals(modIdValue)) {
                  String sideValue = modsToml.getString(dependencyId + ".side");
                  if (sideValue != null) {
                     String requiredSide = sideValue.toLowerCase(Locale.ROOT);

                     environment = switch (requiredSide) {
                        case "client" -> ModFileData.ModEnvironment.CLIENT;
                        case "server" -> ModFileData.ModEnvironment.SERVER;
                        case "both" -> ModFileData.ModEnvironment.BOTH;
                        default -> environment;
                     };
                  } else {
                     Constants.LOG.warn("⚠ Found no side tag inside the {} section of the mods.toml file {}!", dependencyId, path);
                  }
                  break;
               }
            }
         }

         if (environment == ModFileData.ModEnvironment.UNKNOWN) {
            String displayTest = modsToml.getString("mods[0].displayTest");
            if (displayTest != null) {
               environment = switch (displayTest) {
                  case "IGNORE_SERVER_VERSION" -> ModFileData.ModEnvironment.SERVER;
                  case "IGNORE_ALL_VERSION" -> ModFileData.ModEnvironment.CLIENT;
                  case "MATCH_VERSION" -> ModFileData.ModEnvironment.BOTH;
                  default -> environment;
               };
            }
         }
      } else {
         Constants.LOG.warn("⚠ Found no META-INF/mods.toml or META-INF/neoforge.mods.toml file for {}!", path);
      }

      if (manifest != null && manifest.getMainAttributes() != null) {
         Attributes attributes = manifest.getMainAttributes();
         if (version == null || version.equals(ModFileData.EMPTY_VERSION) && hasAttributeValue("Implementation-Version", attributes)) {
            version = SemanticVersionUtils.parseVersion(attributes.getValue("Implementation-Version"));
         }

         if (name == null || name.equals("${file.jarName}") && hasAttributeValue("Specification-Title", attributes)) {
            name = attributes.getValue("Specification-Title");
         }

         if (modId == null || modId.isEmpty() || modId.equals("unknown_id")) {
            if (hasAttributeValue("Automatic-Module-Name", attributes)) {
               modId = attributes.getValue("Automatic-Module-Name").replace(" ", "-").toLowerCase(Locale.ROOT);
            } else if (hasAttributeValue("Implementation-Title", attributes)) {
               modId = attributes.getValue("Implementation-Title").replace(" ", "-").toLowerCase(Locale.ROOT);
            }
         }

         if (environment == ModFileData.ModEnvironment.UNKNOWN && hasAttributeValue("FMLModType", attributes)) {
            String fmlModType = attributes.getValue("FMLModType");
            switch (fmlModType) {
               case "LIBRARY":
               case "GAMELIBRARY":
                  environment = ModFileData.ModEnvironment.LIBRARY;
                  break;
               case "LANGPROVIDER":
                  environment = ModFileData.ModEnvironment.LANGUAGE_PROVIDER;
               case "MOD":
                  break;
               default:
                  Constants.LOG.warn("⚠ Found unknown fml mod type {} for {}!", fmlModType, path);
            }
         }

         timestamp = parseTimestamp(attributes.getValue("Implementation-Timestamp"));
      }

      if (timestamp == null || timestamp.equals(ModFileData.EMPTY_TIMESTAMP)) {
         timestamp = parseTimestampFromPath(path);
      }

      if (modId == null || modId.isEmpty() || modId.equals("unknown_id")) {
         if (environment == ModFileData.ModEnvironment.LIBRARY) {
            modId = "library-" + UUID.randomUUID();
         } else if (environment == ModFileData.ModEnvironment.LANGUAGE_PROVIDER) {
            modId = "language-provider-" + UUID.randomUUID();
         } else if (environment == ModFileData.ModEnvironment.DATA_PACK) {
            modId = "data-pack-" + UUID.randomUUID();
         } else {
            Constants.LOG.error("⚠ Found no valid modId for {}!", path);
            modId = "unknown-" + UUID.randomUUID();
         }
      }

      return new ModFileData(path, modId, modType, name, version, environment, timestamp);
   }

   public static ModFileData parseQuiltModFile(Manifest manifest, Path path, JarFile jarFile) {
      String id = "unknown_id";
      String name = "Unknown";
      Version version = ModFileData.EMPTY_VERSION;
      ModFileData.ModEnvironment environment = ModFileData.ModEnvironment.UNKNOWN;
      LocalDateTime timestamp = ModFileData.EMPTY_TIMESTAMP;
      JsonObject jsonObject = JsonFileParser.readJsonFile(jarFile, Path.of("quilt.mod.json"));
      if (jsonObject != null && !jsonObject.isJsonNull()) {
         if (jsonObject.get("quilt_loader") != null) {
            JsonObject quiltLoaderObject = jsonObject.get("quilt_loader").getAsJsonObject();
            if (quiltLoaderObject != null) {
               if (quiltLoaderObject.get("id") != null) {
                  id = quiltLoaderObject.get("id").getAsString();
               }

               if (quiltLoaderObject.get("version") != null && !quiltLoaderObject.get("version").getAsString().startsWith("${")) {
                  version = SemanticVersionUtils.parseVersion(quiltLoaderObject.get("version").getAsString());
               }

               if (quiltLoaderObject.get("metadata") != null) {
                  JsonObject metadataObject = quiltLoaderObject.get("metadata").getAsJsonObject();
                  if (metadataObject != null) {
                     name = metadataObject.get("name").getAsString();
                  }
               }
            }
         }

         if (jsonObject.get("minecraft") != null) {
            JsonObject minecraftObject = jsonObject.get("minecraft").getAsJsonObject();
            if (minecraftObject != null && minecraftObject.get("environment") != null) {
               String environmentString = minecraftObject.get("environment").getAsString();

               environment = switch (environmentString) {
                  case "client" -> ModFileData.ModEnvironment.CLIENT;
                  case "dedicated_server" -> ModFileData.ModEnvironment.SERVER;
                  case "*" -> ModFileData.ModEnvironment.BOTH;
                  default -> environment;
               };
            }
         }
      } else {
         Constants.LOG.warn("⚠ Found no quilt.mod.json file for {}!", path);
      }

      if (manifest != null && manifest.getMainAttributes() != null) {
         Attributes attributes = manifest.getMainAttributes();
         if (version == null || version.equals(ModFileData.EMPTY_VERSION) && hasAttributeValue("Implementation-Version", attributes)) {
            version = SemanticVersionUtils.parseVersion(attributes.getValue("Implementation-Version"));
         }

         if (name == null || name.equals("${file.jarName}") && hasAttributeValue("Specification-Title", attributes)) {
            name = attributes.getValue("Specification-Title");
         }

         timestamp = parseTimestamp(attributes.getValue("Implementation-Timestamp"));
      }

      if (timestamp == null || timestamp.equals(ModFileData.EMPTY_TIMESTAMP)) {
         timestamp = parseTimestampFromPath(path);
      }

      return new ModFileData(path, id, ModFileData.ModType.QUILT, name, version, environment, timestamp);
   }

   public static ModFileData parseFabricModFile(Manifest manifest, Path path, JarFile jarFile) {
      String id = "unknown_id";
      String name = "Unknown";
      Version version = ModFileData.EMPTY_VERSION;
      ModFileData.ModEnvironment environment = ModFileData.ModEnvironment.UNKNOWN;
      LocalDateTime timestamp = ModFileData.EMPTY_TIMESTAMP;
      JsonObject jsonObject = JsonFileParser.readJsonFile(jarFile, Path.of("fabric.mod.json"));
      if (jsonObject != null && !jsonObject.isJsonNull()) {
         if (jsonObject.get("id") != null) {
            id = jsonObject.get("id").getAsString();
         } else {
            Constants.LOG.warn("⚠ Found no id tag inside the fabric.mod.json file {}!", path);
         }

         if (jsonObject.get("name") != null) {
            name = jsonObject.get("name").getAsString();
         } else {
            Constants.LOG.warn("⚠ Found no name tag inside the fabric.mod.json file {}!", path);
         }

         if (jsonObject.get("version") != null && !jsonObject.get("version").getAsString().startsWith("${")) {
            version = SemanticVersionUtils.parseVersion(jsonObject.get("version").getAsString());
         } else {
            Constants.LOG.warn("⚠ Found no version tag inside the fabric.mod.json file {}!", path);
         }

         if (jsonObject.get("environment") != null) {
            String environmentString = jsonObject.get("environment").getAsString();

            environment = switch (environmentString) {
               case "client" -> ModFileData.ModEnvironment.CLIENT;
               case "server" -> ModFileData.ModEnvironment.SERVER;
               case "*" -> ModFileData.ModEnvironment.BOTH;
               default -> environment;
            };
         }
      } else {
         Constants.LOG.warn("⚠ Found no valid fabric.mod.json file for {}!", path);
      }

      if (manifest != null && manifest.getMainAttributes() != null) {
         Attributes attributes = manifest.getMainAttributes();
         if (version == null || version.equals(ModFileData.EMPTY_VERSION) && hasAttributeValue("Implementation-Version", attributes)) {
            version = SemanticVersionUtils.parseVersion(attributes.getValue("Implementation-Version"));
         }

         if (name == null || name.equals("${file.jarName}") && hasAttributeValue("Specification-Title", attributes)) {
            name = attributes.getValue("Specification-Title");
         }

         timestamp = parseTimestamp(attributes.getValue("Implementation-Timestamp"));
      }

      if (timestamp == null || timestamp.equals(ModFileData.EMPTY_TIMESTAMP)) {
         timestamp = parseTimestampFromPath(path);
      }

      return new ModFileData(path, id, ModFileData.ModType.FABRIC, name, version, environment, timestamp);
   }

   private static LocalDateTime parseTimestampFromPath(Path path) {
      try {
         BasicFileAttributes fileAttributes = Files.readAttributes(path, BasicFileAttributes.class);
         if (fileAttributes != null) {
            FileTime fileTime = fileAttributes.creationTime();
            return LocalDateTime.ofInstant(fileTime.toInstant(), ZoneId.systemDefault());
         }
      } catch (IOException var3) {
         Constants.LOG.error("Was unable to read file attributes from {}:", path, var3);
      }

      return ModFileData.EMPTY_TIMESTAMP;
   }

   public static LocalDateTime parseTimestamp(String timestamp) {
      if (timestamp != null && !timestamp.isEmpty()) {
         for (DateTimeFormatter formatter : TIMESTAMP_FORMATTERS) {
            try {
               return LocalDateTime.parse(timestamp, formatter);
            } catch (DateTimeParseException var5) {
            }
         }

         try {
            return LocalDateTime.ofInstant(Instant.parse(timestamp), ZoneOffset.UTC);
         } catch (DateTimeParseException var4) {
            Constants.LOG.warn("Unable to parse timestamp: {}", timestamp);
            return ModFileData.EMPTY_TIMESTAMP;
         }
      } else {
         return ModFileData.EMPTY_TIMESTAMP;
      }
   }

   private static boolean hasAttributeValue(String name, Attributes attributes) {
      String value = attributes.getValue(name);
      return value != null && !value.isEmpty();
   }
}
