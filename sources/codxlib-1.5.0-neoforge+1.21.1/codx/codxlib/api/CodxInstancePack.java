package codx.codxlib.api;

import codx.codxlib.platform.Services;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

public final class CodxInstancePack {
   private static final HttpClient HTTP = HttpClient.newHttpClient();
   private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(20L);
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private static final DateTimeFormatter FILE_STAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
   private static final int HASH_BATCH = 100;
   private static final String MODRINTH_CDN = "https://cdn.modrinth.com/";

   private CodxInstancePack() {
   }

   public static CompletableFuture<CodxInstancePack.Result> writeAsync(MinecraftServer server) {
      CodxInstancePack.Snapshot snapshot = snapshot(server);
      LocalDateTime now = LocalDateTime.now();
      return CompletableFuture.supplyAsync(() -> {
         try {
            return write(snapshot, now);
         } catch (IOException var3) {
            throw new UncheckedIOException(var3);
         }
      });
   }

   private static CodxInstancePack.Snapshot snapshot(MinecraftServer server) {
      if (server == null) {
         return new CodxInstancePack.Snapshot(null, null, null);
      } else {
         String levelName;
         try {
            levelName = server.getWorldData().getLevelName();
         } catch (RuntimeException var7) {
            levelName = "world";
         }

         Path levelDat;
         try {
            levelDat = server.getWorldPath(LevelResource.LEVEL_DATA_FILE);
         } catch (RuntimeException var6) {
            levelDat = null;
         }

         Long seed;
         try {
            seed = server.overworld().getSeed();
         } catch (RuntimeException var5) {
            seed = null;
         }

         return new CodxInstancePack.Snapshot(levelName, levelDat, seed);
      }
   }

   private static CodxInstancePack.Result write(CodxInstancePack.Snapshot snapshot, LocalDateTime now) throws IOException {
      Path dir = CodxLib.configDir().resolve("codxlib-debug");
      Files.createDirectories(dir);
      Path out = dir.resolve("codxlib-instance-" + now.format(FILE_STAMP) + ".mrpack");
      List<Path> jars = modJars();
      Map<String, Path> bySha1 = new LinkedHashMap<>();

      for (Path jar : jars) {
         String sha1 = sha1(jar);
         if (sha1 != null) {
            bySha1.put(sha1, jar);
         }
      }

      Map<String, JsonObject> versions = lookUpOnModrinth(bySha1.keySet());
      JsonArray files = new JsonArray();
      List<String> unresolved = new ArrayList<>();
      Set<String> projectIds = new HashSet<>();
      Map<String, List<JsonObject>> byProject = new LinkedHashMap<>();

      for (Entry<String, Path> entry : bySha1.entrySet()) {
         JsonObject version = versions.get(entry.getKey());
         JsonObject file = version == null ? null : matchingFile(version, entry.getKey());
         if (file == null) {
            unresolved.add(entry.getValue().getFileName().toString());
         } else {
            JsonObject packFile = packFile(file);
            if (packFile == null) {
               unresolved.add(entry.getValue().getFileName().toString());
            } else {
               files.add(packFile);
               String projectId = asString(version, "project_id");
               if (projectId != null) {
                  projectIds.add(projectId);
                  byProject.computeIfAbsent(projectId, k -> new ArrayList<>()).add(packFile);
               }
            }
         }
      }

      applyEnvironments(projectIds, byProject);
      JsonObject index = index(files, snapshot, now);

      CodxInstancePack.Result var25;
      try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(out), StandardCharsets.UTF_8)) {
         put(zip, "modrinth.index.json", GSON.toJson(index).getBytes(StandardCharsets.UTF_8));
         boolean world = putWorld(zip, snapshot);
         putConfigs(zip);
         put(zip, "overrides/codxlib-debug/README.txt", readme(snapshot, files.size(), unresolved, world).getBytes(StandardCharsets.UTF_8));
         var25 = new CodxInstancePack.Result(out, files.size(), List.copyOf(unresolved), snapshot.seed(), world);
      }

      return var25;
   }

   private static List<Path> modJars() {
      List<Path> jars = new ArrayList<>();

      Path mods;
      try {
         mods = CodxLib.gameDir().resolve("mods");
      } catch (RuntimeException var6) {
         return jars;
      }

      if (!Files.isDirectory(mods)) {
         return jars;
      } else {
         try (DirectoryStream<Path> stream = Files.newDirectoryStream(mods, "*.jar")) {
            for (Path jar : stream) {
               if (Files.isRegularFile(jar)) {
                  jars.add(jar);
               }
            }
         } catch (IOException var8) {
            return jars;
         }

         jars.sort((a, b) -> a.getFileName().toString().compareToIgnoreCase(b.getFileName().toString()));
         return jars;
      }
   }

   private static String sha1(Path file) {
      try {
         MessageDigest digest = MessageDigest.getInstance("SHA-1");
         byte[] buffer = new byte[65536];

         int read;
         try (InputStream in = Files.newInputStream(file)) {
            while ((read = in.read(buffer)) > 0) {
               digest.update(buffer, 0, read);
            }
         }

         StringBuilder hex = new StringBuilder(40);

         for (byte b : digest.digest()) {
            hex.append(Character.forDigit(b >> 4 & 15, 16));
            hex.append(Character.forDigit(b & 15, 16));
         }

         return hex.toString();
      } catch (NoSuchAlgorithmException | IOException var10) {
         return null;
      }
   }

   private static Map<String, JsonObject> lookUpOnModrinth(Set<String> hashes) {
      Map<String, JsonObject> found = new HashMap<>();
      List<String> batch = new ArrayList<>(100);

      for (String hash : hashes) {
         batch.add(hash);
         if (batch.size() == 100) {
            found.putAll(lookUpBatch(batch));
            batch.clear();
         }
      }

      if (!batch.isEmpty()) {
         found.putAll(lookUpBatch(batch));
      }

      return found;
   }

   private static Map<String, JsonObject> lookUpBatch(List<String> hashes) {
      JsonArray array = new JsonArray();
      hashes.forEach(array::add);
      JsonObject body = new JsonObject();
      body.add("hashes", array);
      body.addProperty("algorithm", "sha1");
      HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.modrinth.com/v2/version_files"))
         .header("Accept", "application/json")
         .header("Content-Type", "application/json")
         .header("User-Agent", userAgent())
         .timeout(REQUEST_TIMEOUT)
         .POST(BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
         .build();
      Map<String, JsonObject> result = new HashMap<>();

      try {
         HttpResponse<String> response = HTTP.send(request, BodyHandlers.ofString());
         if (response.statusCode() >= 200 && response.statusCode() < 300) {
            JsonElement json = JsonParser.parseString(response.body());
            if (!json.isJsonObject()) {
               return result;
            } else {
               for (Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
                  if (entry.getValue().isJsonObject()) {
                     result.put(entry.getKey().toLowerCase(Locale.ROOT), entry.getValue().getAsJsonObject());
                  }
               }

               return result;
            }
         } else {
            return result;
         }
      } catch (RuntimeException | IOException var9) {
         return result;
      } catch (InterruptedException var10) {
         Thread.currentThread().interrupt();
         return result;
      }
   }

   private static JsonObject matchingFile(JsonObject version, String sha1) {
      if (version.has("files") && version.get("files").isJsonArray()) {
         for (JsonElement element : version.get("files").getAsJsonArray()) {
            if (element.isJsonObject()) {
               JsonObject file = element.getAsJsonObject();
               if (file.has("hashes") && file.get("hashes").isJsonObject()) {
                  String candidate = asString(file.getAsJsonObject("hashes"), "sha1");
                  if (candidate != null && candidate.equalsIgnoreCase(sha1)) {
                     return file;
                  }
               }
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private static JsonObject packFile(JsonObject file) {
      String url = asString(file, "url");
      String filename = asString(file, "filename");
      JsonObject hashes = file.has("hashes") && file.get("hashes").isJsonObject() ? file.getAsJsonObject("hashes") : null;
      String sha1 = hashes == null ? null : asString(hashes, "sha1");
      String sha512 = hashes == null ? null : asString(hashes, "sha512");
      if (url == null || filename == null || sha1 == null || sha512 == null) {
         return null;
      } else if (!url.startsWith("https://cdn.modrinth.com/")) {
         return null;
      } else {
         JsonObject packHashes = new JsonObject();
         packHashes.addProperty("sha1", sha1);
         packHashes.addProperty("sha512", sha512);
         JsonArray downloads = new JsonArray();
         downloads.add(url);
         JsonObject entry = new JsonObject();
         entry.addProperty("path", "mods/" + filename);
         entry.add("hashes", packHashes);
         entry.add("downloads", downloads);
         entry.addProperty("fileSize", file.has("size") ? file.get("size").getAsLong() : 0L);
         return entry;
      }
   }

   private static void applyEnvironments(Set<String> projectIds, Map<String, List<JsonObject>> byProject) {
      if (!projectIds.isEmpty()) {
         StringBuilder ids = new StringBuilder("[");
         boolean first = true;

         for (String id : projectIds) {
            if (!first) {
               ids.append(',');
            }

            ids.append('"').append(id).append('"');
            first = false;
         }

         ids.append(']');
         String url = "https://api.modrinth.com/v2/projects?ids=" + URLEncoder.encode(ids.toString(), StandardCharsets.UTF_8);
         HttpRequest request = HttpRequest.newBuilder(URI.create(url))
            .header("Accept", "application/json")
            .header("User-Agent", userAgent())
            .timeout(REQUEST_TIMEOUT)
            .GET()
            .build();

         try {
            HttpResponse<String> response = HTTP.send(request, BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
               return;
            }

            JsonElement json = JsonParser.parseString(response.body());
            if (!json.isJsonArray()) {
               return;
            }

            for (JsonElement element : json.getAsJsonArray()) {
               if (element.isJsonObject()) {
                  JsonObject project = element.getAsJsonObject();
                  List<JsonObject> entries = byProject.get(asString(project, "id"));
                  if (entries != null) {
                     for (JsonObject entry : entries) {
                        JsonObject env = new JsonObject();
                        env.addProperty("client", side(asString(project, "client_side")));
                        env.addProperty("server", side(asString(project, "server_side")));
                        entry.add("env", env);
                     }
                  }
               }
            }
         } catch (RuntimeException | IOException var15) {
         } catch (InterruptedException var16) {
            Thread.currentThread().interrupt();
         }
      }
   }

   private static String side(String value) {
      if (value == null) {
         return "optional";
      } else {
         String var1 = value.toLowerCase(Locale.ROOT);

         return switch (var1) {
            case "required" -> "required";
            case "unsupported" -> "unsupported";
            default -> "optional";
         };
      }
   }

   private static JsonObject index(JsonArray files, CodxInstancePack.Snapshot snapshot, LocalDateTime now) {
      JsonObject dependencies = new JsonObject();
      dependencies.addProperty("minecraft", CodxLib.minecraftVersion());
      String loaderKey = loaderKey();
      if (loaderKey != null) {
         dependencies.addProperty(loaderKey, loaderVersion());
      }

      String level = snapshot.levelName() == null ? "unknown world" : snapshot.levelName();
      JsonObject index = new JsonObject();
      index.addProperty("formatVersion", 1);
      index.addProperty("game", "minecraft");
      index.addProperty("versionId", now.format(FILE_STAMP));
      index.addProperty("name", "Debug instance — " + level);
      index.addProperty(
         "summary",
         "Exported by /codxlib help on "
            + CodxLib.loaderName()
            + " "
            + CodxLib.minecraftVersion()
            + (snapshot.seed() == null ? "" : " (world seed " + snapshot.seed() + ")")
      );
      index.add("files", files);
      index.add("dependencies", dependencies);
      return index;
   }

   private static String loaderKey() {
      String var0 = normalizedLoader();

      return switch (var0) {
         case "fabric" -> "fabric-loader";
         case "quilt" -> "quilt-loader";
         case "forge" -> "forge";
         case "neoforge" -> "neoforge";
         default -> null;
      };
   }

   private static String loaderVersion() {
      String var0 = normalizedLoader();

      return switch (var0) {
         case "fabric" -> CodxLib.version("fabricloader");
         case "quilt" -> CodxLib.version("quilt_loader");
         case "forge" -> CodxLib.version("forge");
         case "neoforge" -> CodxLib.version("neoforge");
         default -> "unknown";
      };
   }

   private static String normalizedLoader() {
      String loader = Services.PLATFORM.getLoaderName();
      return loader == null ? "" : loader.trim().toLowerCase(Locale.ROOT);
   }

   private static String userAgent() {
      return "codxlib/" + CodxLib.version("codxlib") + " (instance-pack)";
   }

   private static boolean putWorld(ZipOutputStream zip, CodxInstancePack.Snapshot snapshot) throws IOException {
      Path levelDat = snapshot.levelDat();
      if (levelDat != null && Files.isRegularFile(levelDat)) {
         byte[] bytes;
         try {
            bytes = Files.readAllBytes(levelDat);
         } catch (IOException var5) {
            return false;
         }

         put(zip, "overrides/saves/" + saveFolder(snapshot.levelName()) + "/level.dat", bytes);
         return true;
      } else {
         return false;
      }
   }

   private static void putConfigs(ZipOutputStream zip) throws IOException {
      Path configDir;
      try {
         configDir = CodxLib.configDir();
      } catch (RuntimeException var6) {
         return;
      }

      try (DirectoryStream<Path> stream = Files.newDirectoryStream(configDir, "codxlib*.json")) {
         for (Path config : stream) {
            if (Files.isRegularFile(config)) {
               put(zip, "overrides/config/" + config.getFileName(), Files.readAllBytes(config));
            }
         }
      } catch (IOException var8) {
      }
   }

   private static String saveFolder(String levelName) {
      if (levelName != null && !levelName.isBlank()) {
         String cleaned = levelName.trim().replaceAll("[^A-Za-z0-9 ._-]", "_");
         return cleaned.isBlank() ? "world" : cleaned;
      } else {
         return "world";
      }
   }

   private static String readme(CodxInstancePack.Snapshot snapshot, int resolved, List<String> unresolved, boolean world) {
      StringBuilder sb = new StringBuilder();
      sb.append("CodxLib instance export\n");
      sb.append("=======================\n\n");
      sb.append("Import this .mrpack with the Modrinth App (Add instance -> From file) to\n");
      sb.append("recreate the setup this report came from.\n\n");
      sb.append("Minecraft:  ").append(CodxLib.minecraftVersion()).append('\n');
      sb.append("Loader:     ").append(CodxLib.loaderName()).append(' ').append(loaderVersion()).append('\n');
      sb.append("Mods:       ").append(resolved).append(" downloaded from Modrinth\n");
      if (snapshot.levelName() != null) {
         sb.append("World:      ").append(snapshot.levelName());
         sb.append(world ? " (level.dat included)" : " (not included)").append('\n');
      }

      if (snapshot.seed() != null) {
         sb.append("Seed:       ").append(snapshot.seed()).append('\n');
      }

      sb.append('\n');
      sb.append("The world folder holds only level.dat, so Minecraft regenerates the terrain\n");
      sb.append("from the same seed, world type and generator settings. Nothing that was built\n");
      sb.append("or explored comes with it.\n");
      if (!unresolved.isEmpty()) {
         sb.append('\n');
         sb.append("NOT INCLUDED (").append(unresolved.size()).append(")\n");
         sb.append("Modrinth has no download matching these jars byte-for-byte — they are\n");
         sb.append("CurseForge-only, private, or locally modified builds. Add them by hand:\n");

         for (String name : unresolved) {
            sb.append("  - ").append(name).append('\n');
         }
      }

      return sb.toString();
   }

   private static void put(ZipOutputStream zip, String path, byte[] bytes) throws IOException {
      ZipEntry entry = new ZipEntry(path);
      entry.setTime(0L);
      zip.putNextEntry(entry);
      zip.write(bytes);
      zip.closeEntry();
   }

   private static String asString(JsonObject object, String key) {
      if (object != null && object.has(key) && !object.get(key).isJsonNull()) {
         try {
            return object.get(key).getAsString();
         } catch (RuntimeException var3) {
            return null;
         }
      } else {
         return null;
      }
   }

   public record Result(Path file, int resolved, List<String> unresolved, Long seed, boolean world) {
      public int total() {
         return this.resolved + this.unresolved.size();
      }
   }

   private record Snapshot(String levelName, Path levelDat, Long seed) {
   }
}
