package codx.codxlib.api;

import codx.codxlib.platform.Services;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public final class UpdateChecker {
   private static final HttpClient HTTP = HttpClient.newHttpClient();
   private static final boolean FORCE_SHOW_UPDATE = false;
   private static final long THROTTLE_SECONDS = 1800L;
   private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10L);
   private static final Pattern FILE_VERSION = Pattern.compile("(\\d+(?:\\.\\d+)+)");
   private static final List<ModInfo> REGISTERED = new CopyOnWriteArrayList<>();
   private static final Map<String, UpdateChecker.State> STATE = new ConcurrentHashMap<>();
   private static volatile boolean enabled = true;

   private UpdateChecker() {
   }

   public static void setEnabled(boolean value) {
      enabled = value;
   }

   public static List<ModInfo> registered() {
      return List.copyOf(REGISTERED);
   }

   public static String currentVersionOf(ModInfo mod) {
      return currentVersion(mod);
   }

   public static void register(ModInfo mod) {
      if (mod != null) {
         for (ModInfo existing : REGISTERED) {
            if (existing.modId().equals(mod.modId())) {
               return;
            }
         }

         REGISTERED.add(mod);
      }
   }

   public static void onServerStarted(MinecraftServer server) {
      if (server != null && enabled) {
         for (ModInfo mod : REGISTERED) {
            refresh(server, mod, (hasUpdate, latest) -> {
               if (hasUpdate && latest != null) {
                  server.sendSystemMessage(updateAvailableConsoleMessage(mod, latest));
               }
            });
         }
      }
   }

   public static void onPlayerJoin(MinecraftServer server, ServerPlayer player) {
      if (server != null && player != null && enabled) {
         if (server.isSingleplayer() || player.hasPermissions(2)) {
            for (ModInfo mod : REGISTERED) {
               UpdateChecker.State state = STATE.computeIfAbsent(mod.modId(), k -> new UpdateChecker.State());
               boolean useCache;
               synchronized (state) {
                  useCache = state.checked && Instant.now().isBefore(state.lastCheck.plusSeconds(1800L));
               }

               if (useCache) {
                  if (state.hasUpdate && state.latestVersion != null) {
                     player.sendSystemMessage(updateAvailableMessage(mod, state.latestVersion));
                  }
               } else {
                  refresh(server, mod, (hasUpdate, latest) -> {
                     if (hasUpdate && latest != null) {
                        player.sendSystemMessage(updateAvailableMessage(mod, latest));
                     }
                  });
               }
            }
         }
      }
   }

   private static void refresh(MinecraftServer server, ModInfo mod, BiConsumer<Boolean, String> sink) {
      UpdateChecker.State state = STATE.computeIfAbsent(mod.modId(), k -> new UpdateChecker.State());
      synchronized (state) {
         state.lastCheck = Instant.now();
      }

      checkVersionAsync(server, mod, (hasUpdate, latest) -> {
         state.checked = true;
         state.hasUpdate = hasUpdate;
         state.latestVersion = latest;
         sink.accept(hasUpdate, latest);
      });
   }

   public static Component updateAvailableMessage(ModInfo mod, String latest) {
      return Component.literal(headline(mod, latest) + " — ")
         .append(CodxNotify.link("Modrinth", mod.modrinthUrl()))
         .append(Component.literal("§7 or "))
         .append(CodxNotify.link("CurseForge", mod.curseforgeUrl()));
   }

   public static Component updateAvailableConsoleMessage(ModInfo mod, String latest) {
      return Component.literal(headline(mod, latest) + "§7 — " + mod.modrinthUrl() + " §7or " + mod.curseforgeUrl());
   }

   private static String headline(ModInfo mod, String latest) {
      return "§7" + mod.chatPrefix() + " §aUpdate available: §6" + displayVersion(latest) + " §7(current " + displayVersion(currentVersion(mod)) + ")";
   }

   private static String displayVersion(String version) {
      if (version == null) {
         return "unknown";
      } else {
         int plus = version.indexOf(43);
         return plus >= 0 ? version.substring(0, plus) : version;
      }
   }

   public static void checkVersionAsync(MinecraftServer server, ModInfo mod, BiConsumer<Boolean, String> callback) {
      if (server != null && mod != null) {
         String currentVersion = currentVersion(mod);
         String minecraftVersion = Services.PLATFORM.getMinecraftVersion();
         latestOnModrinth(mod, currentVersion, minecraftVersion)
            .thenCombine(latestOnCurseForge(mod, currentVersion, minecraftVersion), UpdateChecker::newerOf)
            .exceptionally(ex -> null)
            .thenAccept(latest -> server.execute(() -> {
               boolean hasUpdate = latest != null && isNewerVersion(latest, currentVersion);
               if (callback != null) {
                  callback.accept(hasUpdate, hasUpdate ? latest : null);
               }
            }));
      } else {
         if (callback != null) {
            callback.accept(false, null);
         }
      }
   }

   private static String newerOf(String a, String b) {
      if (a == null) {
         return b;
      } else if (b == null) {
         return a;
      } else {
         return isNewerVersion(b, a) ? b : a;
      }
   }

   private static HttpRequest jsonRequest(String url, ModInfo mod, String currentVersion) {
      return HttpRequest.newBuilder(URI.create(url))
         .header("Accept", "application/json")
         .header("User-Agent", mod.modId() + "/" + currentVersion)
         .timeout(REQUEST_TIMEOUT)
         .GET()
         .build();
   }

   private static CompletableFuture<String> latestOnModrinth(ModInfo mod, String currentVersion, String minecraftVersion) {
      String encodedLoaders = URLEncoder.encode(runningLoadersJson(), StandardCharsets.UTF_8);
      String encodedMc = URLEncoder.encode("[\"" + minecraftVersion + "\"]", StandardCharsets.UTF_8);
      String url = "https://api.modrinth.com/v2/project/" + mod.modrinthSlug() + "/version?loaders=" + encodedLoaders + "&game_versions=" + encodedMc;
      return HTTP.sendAsync(jsonRequest(url, mod, currentVersion), BodyHandlers.ofString()).thenApply(response -> {
         if (response.statusCode() >= 200 && response.statusCode() < 300) {
            JsonElement json = JsonParser.parseString(response.body());
            if (!json.isJsonArray()) {
               return null;
            } else {
               String latest = null;

               for (JsonElement element : json.getAsJsonArray()) {
                  if (element.isJsonObject()) {
                     JsonObject obj = element.getAsJsonObject();
                     if (obj.has("version_number")) {
                        String candidate = obj.get("version_number").getAsString();
                        latest = newerOf(latest, candidate);
                     }
                  }
               }

               return latest;
            }
         } else {
            return null;
         }
      }).exceptionally(ex -> null);
   }

   private static CompletableFuture<String> latestOnCurseForge(ModInfo mod, String currentVersion, String minecraftVersion) {
      if (!mod.canQueryCurseForge()) {
         return CompletableFuture.completedFuture(null);
      } else {
         String loader = normalizedLoader();
         String url = "https://api.cfwidget.com/" + mod.curseforgeProjectId();
         return HTTP.sendAsync(jsonRequest(url, mod, currentVersion), BodyHandlers.ofString()).thenApply(response -> {
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
               JsonElement json = JsonParser.parseString(response.body());
               if (!json.isJsonObject()) {
                  return null;
               } else {
                  JsonObject project = json.getAsJsonObject();
                  if (project.has("files") && project.get("files").isJsonArray()) {
                     String latest = null;

                     for (JsonElement element : project.get("files").getAsJsonArray()) {
                        if (element.isJsonObject()) {
                           JsonObject file = element.getAsJsonObject();
                           if (matchesRunningNode(file, loader, minecraftVersion)) {
                              latest = newerOf(latest, versionFromFileName(file.has("name") ? file.get("name").getAsString() : null, minecraftVersion));
                           }
                        }
                     }

                     return latest;
                  } else {
                     return null;
                  }
               }
            } else {
               return null;
            }
         }).exceptionally(ex -> null);
      }
   }

   private static boolean matchesRunningNode(JsonObject file, String loader, String minecraftVersion) {
      if (file.has("versions") && file.get("versions").isJsonArray()) {
         boolean mcMatch = false;
         boolean loaderMatch = false;

         for (JsonElement tag : file.get("versions").getAsJsonArray()) {
            String value = tag.getAsString();
            if (value.equals(minecraftVersion)) {
               mcMatch = true;
            } else if (value.toLowerCase(Locale.ROOT).equals(loader)) {
               loaderMatch = true;
            }
         }

         return mcMatch && loaderMatch;
      } else {
         return false;
      }
   }

   private static String versionFromFileName(String fileName, String minecraftVersion) {
      if (fileName == null) {
         return null;
      } else {
         String stripped = fileName.replace(minecraftVersion, "");
         Matcher matcher = FILE_VERSION.matcher(stripped);
         return matcher.find() ? matcher.group(1) : null;
      }
   }

   private static String runningLoadersJson() {
      String loader = Services.PLATFORM.getLoaderName();
      String normalized = loader == null ? "" : loader.trim().toLowerCase(Locale.ROOT);

      return switch (normalized) {
         case "fabric" -> "[\"fabric\"]";
         case "quilt" -> "[\"quilt\",\"fabric\"]";
         case "forge" -> "[\"forge\"]";
         case "neoforge" -> "[\"neoforge\"]";
         default -> "[\"fabric\",\"forge\",\"neoforge\"]";
      };
   }

   private static String normalizedLoader() {
      String loader = Services.PLATFORM.getLoaderName();
      String normalized = loader == null ? "" : loader.trim().toLowerCase(Locale.ROOT);
      return "quilt".equals(normalized) ? "fabric" : normalized;
   }

   private static String currentVersion(ModInfo mod) {
      String live = Services.PLATFORM.getModVersion(mod.modId());
      return live != null && !live.isBlank() && !"unknown".equals(live) ? live : mod.version();
   }

   private static boolean isNewerVersion(String newVersion, String currentVersion) {
      String parsedNew = normalizeVersion(newVersion);
      String parsedCurrent = normalizeVersion(currentVersion);

      try {
         String[] newParts = parsedNew.split("\\.");
         String[] currentParts = parsedCurrent.split("\\.");
         int maxLength = Math.max(newParts.length, currentParts.length);

         for (int i = 0; i < maxLength; i++) {
            int newPart = i < newParts.length ? Integer.parseInt(newParts[i]) : 0;
            int currentPart = i < currentParts.length ? Integer.parseInt(currentParts[i]) : 0;
            if (newPart > currentPart) {
               return true;
            }

            if (newPart < currentPart) {
               return false;
            }
         }

         return false;
      } catch (NumberFormatException var10) {
         return !parsedNew.equals(parsedCurrent);
      }
   }

   private static String normalizeVersion(String version) {
      if (version != null && !version.isBlank()) {
         String normalized = version.trim();
         int plusIndex = normalized.indexOf(43);
         if (plusIndex >= 0) {
            normalized = normalized.substring(0, plusIndex);
         }

         int dashIndex = normalized.indexOf(45);
         if (dashIndex >= 0) {
            normalized = normalized.substring(0, dashIndex);
         }

         return normalized.replaceAll("[^0-9.]", "");
      } else {
         return "0.0.0";
      }
   }

   private static final class State {
      Instant lastCheck = Instant.EPOCH;
      volatile boolean checked = false;
      volatile boolean hasUpdate = false;
      volatile String latestVersion = null;
   }
}
