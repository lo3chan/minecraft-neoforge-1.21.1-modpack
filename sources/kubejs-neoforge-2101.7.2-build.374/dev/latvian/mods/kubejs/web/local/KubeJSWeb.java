package dev.latvian.mods.kubejs.web.local;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import dev.latvian.apps.tinyserver.ServerRegistry;
import dev.latvian.apps.tinyserver.http.response.HTTPPayload;
import dev.latvian.apps.tinyserver.http.response.HTTPResponse;
import dev.latvian.apps.tinyserver.http.response.HTTPStatus;
import dev.latvian.apps.tinyserver.http.response.error.client.NotFoundError;
import dev.latvian.apps.tinyserver.http.response.error.server.InternalError;
import dev.latvian.apps.tinyserver.ws.Frame;
import dev.latvian.apps.tinyserver.ws.WSHandler;
import dev.latvian.apps.tinyserver.ws.WSKeepAliveThread;
import dev.latvian.apps.tinyserver.ws.WSSession;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.data.GeneratedData;
import dev.latvian.mods.kubejs.util.RegExpKJS;
import dev.latvian.mods.kubejs.web.JsonContent;
import dev.latvian.mods.kubejs.web.KJSHTTPRequest;
import dev.latvian.mods.kubejs.web.KJSWSSession;
import dev.latvian.mods.kubejs.web.LocalWebServer;
import dev.latvian.mods.kubejs.web.LocalWebServerRegistry;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.RegistryAccess.RegistryEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.Pack.ResourcesSupplier;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.tags.TagKey;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.resource.ResourcePackLoader;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.neoforged.neoforgespi.language.IModInfo;
import org.jetbrains.annotations.Nullable;

public class KubeJSWeb {
   public static WSHandler<KJSHTTPRequest, KJSWSSession> UPDATES = WSHandler.empty();
   private static final Map<String, Path> BROWSE = Map.of(
      "assets",
      KubeJSPaths.ASSETS,
      "data",
      KubeJSPaths.DATA,
      "startup_scripts",
      KubeJSPaths.STARTUP_SCRIPTS,
      "client_scripts",
      KubeJSPaths.CLIENT_SCRIPTS,
      "server_scripts",
      KubeJSPaths.SERVER_SCRIPTS,
      "logs",
      FMLPaths.GAMEDIR.get().resolve("logs")
   );

   public static int broadcastEvent(@Nullable WSHandler<?, ?> handler, String event, String requiredTag, @Nullable Supplier<JsonElement> payload) {
      if (handler != null && !handler.sessions().isEmpty()) {
         Frame frame = null;
         int count = 0;

         for (WSSession<?> s : handler.sessions().values()) {
            if (!(!requiredTag.isEmpty() && s instanceof KJSWSSession ks) || ks.info.tags().contains(requiredTag)) {
               if (frame == null) {
                  JsonObject json = new JsonObject();
                  json.addProperty("type", event);
                  JsonElement p = payload == null ? null : payload.get();
                  if (p != null && !(p instanceof JsonNull)) {
                     json.add("payload", p);
                  }

                  frame = Frame.text(json.toString());
               }

               s.send(frame);
               count++;
            }
         }

         return count;
      } else {
         return 0;
      }
   }

   public static int broadcastUpdate(String type, String requiredTag, Supplier<JsonElement> payload) {
      return broadcastEvent(UPDATES, type, requiredTag, payload);
   }

   public static void addScriptTypeEndpoints(ServerRegistry<KJSHTTPRequest> registry, ScriptType s, Runnable reload) {
      String path = "/api/console/" + s.name;
      s.console.wsBroadcaster = registry.ws(path + "/stream", () -> new ConsoleWSSession(s.console));
      registry.acceptPostString(path + "/info", s.console::info);
      registry.acceptPostString(path + "/warn", s.console::warn);
      registry.acceptPostString(path + "/error", s.console::error);
      registry.get(path + "/errors", s.console::getErrorsResponse);
      registry.get(path + "/warnings", s.console::getWarningsResponse);
      registry.acceptPostTask("/api/reload/" + s.name, reload);
   }

   public static void register(LocalWebServerRegistry registry) {
      UPDATES = registry.ws("/api/updates", KJSWSSession::new);
      registry.get("/", KubeJSWeb::getHomepage);
      registry.get("/api", KubeJSWeb::getAPIs);
      registry.get("/api/mods", KubeJSWeb::getMods);
      registry.get("/api/mods/{id}/icon", KubeJSWeb::getModIcon);
      registry.get("/api/assets.zip", KubeJSWeb::getAssetsZip);
      registry.get("/api/registries", KubeJSWeb::getRegistriesResponse);
      registry.get("/api/registries/{namespace}/{path}/keys", KubeJSWeb::getRegistryKeysResponse);
      registry.get("/api/registries/{namespace}/{path}/match/{regex}", KubeJSWeb::getRegistryMatchResponse);
      registry.get("/api/tags/{namespace}/{path}", KubeJSWeb::getTagsResponse);
      registry.get("/api/tags/{namespace}/{path}/values/{tag-namespace}/{tag-path}", KubeJSWeb::getTagValuesResponse);
      registry.get("/api/tags/{namespace}/{path}/keys/{value-namespace}/{value-path}", KubeJSWeb::getTagKeysResponse);
   }

   public static void registerWithAuth(LocalWebServerRegistry registry) {
      addScriptTypeEndpoints(registry, ScriptType.STARTUP, KubeJSWeb::reloadStartupScripts);
      addScriptTypeEndpoints(registry, ScriptType.SERVER, KubeJSWeb::reloadInternalServer);
      registry.get("/api/browse", KubeJSWeb::getBrowse);
      registry.get("/api/browse/{directory}", KubeJSWeb::getBrowseDir);
      registry.get("/api/browse/{directory}/<file>", KubeJSWeb::getBrowseFile);
   }

   public static void serverStarted(LocalWebServer instance) {
      new WSKeepAliveThread(instance.server(), UPDATES, "/api/updates").start();
   }

   private static void reloadStartupScripts() {
      KubeJS.getStartupScriptManager().reload();
   }

   private static void reloadInternalServer() {
      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
      if (server != null) {
         server.kjs$runCommand("/reload");
      }
   }

   private static HTTPResponse getHomepage(KJSHTTPRequest req) {
      ArrayList<String> list = new ArrayList<>();
      list.add("KubeJS Local Web Server [" + KubeJS.PROXY.getWebServerWindowTitle() + "]");
      list.add(HTTPPayload.DATE_TIME_FORMATTER.format(req.startTime()));
      list.add("");
      list.add("Loaded Plugins:");

      for (KubeJSPlugin plugin : KubeJSPlugins.getAll()) {
         list.add("- " + plugin.getClass().getName());
      }

      list.add("");
      list.add("Loaded Mods:");

      for (ModContainer mod : ModList.get().getSortedMods()) {
         list.add("- " + mod.getModInfo().getDisplayName() + " (" + mod.getModId() + " - " + mod.getModInfo().getVersion() + ")");
      }

      list.add("");
      list.add("Available Endpoints:");

      for (LocalWebServer.Endpoint endpoint : LocalWebServer.instance().endpoints()) {
         list.add("- " + endpoint.method() + "\t" + endpoint.path() + (endpoint.auth() ? " [Requires Auth]" : ""));
      }

      list.add("");
      list.add("APIs:");
      KubeJSPlugins.forEachPlugin((id, version) -> list.add("- " + id + " v" + Math.max(version, 1)), KubeJSPlugin::registerLocalWebServerAPIs);
      return HTTPResponse.ok().text(list);
   }

   private static HTTPResponse getAPIs(KJSHTTPRequest req) {
      return HTTPResponse.ok()
         .content(
            JsonContent.object(
               json -> KubeJSPlugins.forEachPlugin(
                  (id, version) -> json.addProperty(id.toString(), Math.max(version, 1)), KubeJSPlugin::registerLocalWebServerAPIs
               )
            )
         );
   }

   private static HTTPResponse getMods(KJSHTTPRequest req) {
      return HTTPResponse.ok().content(JsonContent.array(json -> {
         for (ModContainer mod : ModList.get().getSortedMods()) {
            JsonObject o = new JsonObject();
            o.addProperty("id", mod.getModId());
            o.addProperty("name", mod.getModInfo().getDisplayName());
            o.addProperty("version", mod.getModInfo().getVersion().toString());
            json.add(o);
         }
      }));
   }

   private static HTTPResponse getModIcon(KJSHTTPRequest req) throws Exception {
      IModInfo mod = ModList.get().getModContainerById(req.variable("id").asString()).<IModInfo>map(ModContainer::getModInfo).orElse(null);
      if (mod == null) {
         throw new NotFoundError("Mod not found");
      } else {
         String logo = mod.getLogoFile().orElse("");
         BufferedImage img = new BufferedImage(128, 128, 1);

         for (int i = 0; i < 128; i++) {
            for (int j = 0; j < 128; j++) {
               img.setRGB(i, j, -16777216);
            }
         }

         if (!logo.isEmpty()) {
            ResourcesSupplier resourcePack = (ResourcesSupplier)ResourcePackLoader.getPackFor(mod.getModId())
               .orElse((ResourcesSupplier)ResourcePackLoader.getPackFor("neoforge").orElseThrow(() -> new InternalError("Can't find neoforge, WHAT!")));
            PackResources res = resourcePack.openPrimary(
               new PackLocationInfo("mod/" + mod.getModId(), Component.empty(), PackSource.BUILT_IN, Optional.empty())
            );

            try {
               IoSupplier<InputStream> logoResource = res.getRootResource(logo.split("[/\\\\]"));
               if (logoResource != null) {
                  BufferedImage l = ImageIO.read((InputStream)logoResource.get());
                  Graphics2D g = img.createGraphics();
                  g.setRenderingHint(
                     RenderingHints.KEY_INTERPOLATION,
                     mod.getLogoBlur() ? RenderingHints.VALUE_INTERPOLATION_BILINEAR : RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
                  );
                  float r = (float)l.getWidth() / l.getHeight();
                  int w;
                  int h;
                  if (r > 1.0F) {
                     w = 128;
                     h = (int)(128.0F / r);
                  } else {
                     w = (int)(128.0F * r);
                     h = 128;
                  }

                  g.drawImage(l, (128 - w) / 2, (128 - h) / 2, w, h, null);
                  g.dispose();
               }
            } catch (Throwable var13) {
               if (res != null) {
                  try {
                     res.close();
                  } catch (Throwable var12) {
                     var13.addSuppressed(var12);
                  }
               }

               throw var13;
            }

            if (res != null) {
               res.close();
            }
         }

         return HTTPResponse.ok().png(img);
      }
   }

   private static HTTPResponse getAssetsZip(KJSHTTPRequest req) throws IOException {
      if (Files.notExists(KubeJSPaths.ASSETS)) {
         throw new NotFoundError("kubejs/assets directory is not found!");
      } else {
         HashMap<String, byte[]> allFiles = new HashMap<>();
         HashMap<String, byte[]> allZipFiles = new HashMap<>();

         for (Path rpath : Files.list(KubeJSPaths.ASSETS).sorted((a, b) -> a.getFileName().toString().compareToIgnoreCase(b.getFileName().toString())).toList()) {
            String fn = rpath.getFileName().toString();
            if (fn.endsWith(".zip")) {
               try (FileSystem fs = FileSystems.newFileSystem(rpath)) {
                  Path root = fs.getPath(".");

                  for (Path cpath : Files.walk(root).toList()) {
                     if (Files.isRegularFile(cpath)) {
                        String zpath = root.relativize(cpath).toString().replace('\\', '/');
                        allZipFiles.put(zpath, Files.readAllBytes(cpath));
                     }
                  }
               }
            } else if (Files.isDirectory(rpath)) {
               for (Path path : Files.walk(rpath).toList()) {
                  String zpath = KubeJSPaths.DIRECTORY.relativize(path).toString().replace('\\', '/');
                  if (Files.isRegularFile(path)) {
                     allFiles.put(zpath, Files.readAllBytes(path));
                  }
               }
            }
         }

         for (Entry<String, byte[]> entry : allZipFiles.entrySet()) {
            allFiles.putIfAbsent(entry.getKey(), entry.getValue());
         }

         allFiles.remove("LICENSE");
         allFiles.remove("pack.mcmeta");
         allFiles.remove("pack.png");
         List<Entry<String, byte[]>> list = allFiles.entrySet().stream().sorted((a, b) -> a.getKey().compareToIgnoreCase(b.getKey())).toList();
         ByteArrayOutputStream zipBytes = new ByteArrayOutputStream();

         try (ZipOutputStream out = new ZipOutputStream(zipBytes)) {
            for (Entry<String, byte[]> pathx : list) {
               out.putNextEntry(new ZipEntry(pathx.getKey()));
               out.write(pathx.getValue());
               out.closeEntry();
            }

            out.putNextEntry(new ZipEntry("pack.mcmeta"));
            out.write(GeneratedData.PACK_META.data().get());
            out.closeEntry();
            out.putNextEntry(new ZipEntry("pack.png"));
            out.write(GeneratedData.PACK_ICON.data().get());
            out.closeEntry();
         }

         return HTTPResponse.ok().content(zipBytes.toByteArray(), "application/zip").publicCache(Duration.ofSeconds(15L));
      }
   }

   private static HTTPResponse getBrowse(KJSHTTPRequest req) {
      return HTTPResponse.ok().content(JsonContent.array(json -> BROWSE.keySet().forEach(json::add)));
   }

   private static HTTPResponse getBrowseDir(KJSHTTPRequest req) {
      String dirName = req.variable("directory").asString();
      Path dir = BROWSE.get(dirName);
      return (HTTPResponse)(dir == null ? HTTPStatus.NOT_FOUND : HTTPResponse.ok().content(JsonContent.array(json -> {
         try {
            if (Files.exists(dir)) {
               for (Path file : Files.walk(dir).filter(x$0 -> Files.isRegularFile(x$0)).filter(Files::isReadable).toList()) {
                  String fileName = file.getFileName().toString();
                  if (!fileName.endsWith(".gz") || !dirName.equals("logs")) {
                     JsonObject o = new JsonObject();
                     o.addProperty("path", dir.relativize(file).toString().replace('\\', '/'));
                     o.addProperty("name", fileName);
                     o.addProperty("modified", Files.getLastModifiedTime(file).toMillis());
                     json.add(o);
                  }
               }
            }
         } catch (Exception var7) {
            var7.printStackTrace();
         }
      })));
   }

   private static HTTPResponse getBrowseFile(KJSHTTPRequest req) {
      Path dir = BROWSE.get(req.variable("directory").asString());
      if (dir == null) {
         return HTTPStatus.NOT_FOUND;
      } else {
         Path file = dir.resolve(req.variable("file").asString());
         if (Files.notExists(file)) {
            return HTTPStatus.NOT_FOUND;
         } else if (!Files.isRegularFile(file)) {
            return HTTPStatus.BAD_REQUEST;
         } else {
            return (HTTPResponse)(Files.isReadable(file) && file.startsWith(dir) ? HTTPResponse.ok().content(file) : HTTPStatus.FORBIDDEN);
         }
      }
   }

   private static HTTPResponse getRegistriesResponse(KJSHTTPRequest req) {
      return HTTPResponse.ok().content(JsonContent.array(json -> {
         for (RegistryEntry<?> registry : req.registries().access().registries().toList()) {
            json.add(registry.key().location().toString());
         }
      }));
   }

   private static HTTPResponse getRegistryKeysResponse(KJSHTTPRequest req) {
      Optional<Registry<Object>> registry = req.registries().access().registry(ResourceKey.createRegistryKey(req.id()));
      return (HTTPResponse)(registry.isEmpty() ? HTTPStatus.NOT_FOUND : HTTPResponse.ok().content(JsonContent.array(json -> {
         for (ResourceLocation key : registry.get().keySet()) {
            json.add(key.toString());
         }
      })));
   }

   private static HTTPResponse getRegistryMatchResponse(KJSHTTPRequest req) {
      Optional<Registry<Object>> registry = req.registries().access().registry(ResourceKey.createRegistryKey(req.id()));
      if (registry.isEmpty()) {
         return HTTPStatus.NOT_FOUND;
      } else {
         Pattern regex = RegExpKJS.ofString(req.variable("regex").asString());
         return (HTTPResponse)(regex == null ? HTTPStatus.BAD_REQUEST : HTTPResponse.ok().content(JsonContent.array(json -> {
            for (ResourceLocation key : registry.get().keySet()) {
               String k = key.toString();
               if (regex.matcher(k).find()) {
                  json.add(k);
               }
            }
         })));
      }
   }

   private static HTTPResponse getTagsResponse(KJSHTTPRequest req) {
      Optional<Registry<Object>> registry = req.registries().access().registry(ResourceKey.createRegistryKey(req.id()));
      return (HTTPResponse)(registry.isEmpty() ? HTTPStatus.NOT_FOUND : HTTPResponse.ok().content(JsonContent.array(json -> {
         for (ResourceLocation tag : registry.get().getTagNames().map(TagKey::location).toList()) {
            json.add(tag.toString());
         }
      })));
   }

   private static HTTPResponse getTagValuesResponse(KJSHTTPRequest req) {
      Optional<Registry<Object>> registry = req.registries().access().registry(ResourceKey.createRegistryKey(req.id()));
      if (registry.isEmpty()) {
         return HTTPStatus.NOT_FOUND;
      } else {
         Optional<Named<Object>> tagKey = registry.get().getTag(TagKey.create(registry.get().key(), req.id("tag-namespace", "tag-path")));
         return (HTTPResponse)(tagKey.isEmpty()
            ? HTTPStatus.NOT_FOUND
            : HTTPResponse.ok()
               .content(
                  JsonContent.array(
                     json -> {
                        for (ResourceLocation key : tagKey.get()
                           .stream()
                           .<Optional>map(Holder::unwrapKey)
                           .filter(Optional::isPresent)
                           .map(Optional::get)
                           .map(ResourceKey::location)
                           .toList()) {
                           json.add(key.toString());
                        }
                     }
                  )
               ));
      }
   }

   private static HTTPResponse getTagKeysResponse(KJSHTTPRequest req) {
      Optional<Registry<Object>> registry = req.registries().access().registry(ResourceKey.createRegistryKey(req.id()));
      if (registry.isEmpty()) {
         return HTTPStatus.NOT_FOUND;
      } else {
         Optional<Reference<Object>> value = registry.get().getHolder(req.id("value-namespace", "value-path"));
         return (HTTPResponse)(value.isEmpty() ? HTTPStatus.NOT_FOUND : HTTPResponse.ok().content(JsonContent.array(json -> {
            for (ResourceLocation key : value.get().tags().map(TagKey::location).toList()) {
               json.add(key.toString());
            }
         })));
      }
   }
}
