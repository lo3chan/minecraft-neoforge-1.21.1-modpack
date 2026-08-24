package dev.latvian.mods.kubejs.web;

import dev.latvian.apps.tinyserver.error.BindFailedException;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.rhino.util.HideFromJS;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.util.thread.BlockableEventLoop;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record LocalWebServer(KJSHTTPServer server, String url, List<LocalWebServer.Endpoint> endpoints, String explorerCode) {
   private static LocalWebServer instance;

   @HideFromJS
   @Nullable
   public static LocalWebServer instance() {
      return instance;
   }

   @HideFromJS
   public static void start(BlockableEventLoop<?> eventLoop, boolean localClient) {
      if (instance == null) {
         try {
            WebServerProperties properties = WebServerProperties.get();
            KJSHTTPServer server = new KJSHTTPServer(new KJSHTTPServer.RequestFactory(eventLoop), properties.auth);
            HashSet<LocalWebServer.Endpoint> endpoints0 = new HashSet<>();
            LocalWebServerRegistry registry = new LocalWebServerRegistry(server, endpoints0, false);
            LocalWebServerRegistry registryWithAuth = server.auth.isEmpty() ? registry : new LocalWebServerRegistry(server, endpoints0, true);
            KubeJSPlugins.forEachPlugin(registry, KubeJSPlugin::registerLocalWebServer);
            KubeJSPlugins.forEachPlugin(registryWithAuth, KubeJSPlugin::registerLocalWebServerWithAuth);
            String publicAddress = localClient ? "" : properties.publicAddress;
            if (publicAddress.startsWith("https://")) {
               publicAddress = publicAddress.substring(8);
            } else if (publicAddress.startsWith("http://")) {
               publicAddress = publicAddress.substring(7);
            }

            server.setDaemon(true);
            server.setServerName(KubeJS.DISPLAY_NAME);
            server.setAddress(publicAddress.isEmpty() ? "127.0.0.1" : "0.0.0.0");
            server.setPort(IntStream.range(properties.port, properties.port + 10));
            server.setMaxKeepAliveConnections(3);
            server.setKeepAliveTimeout(Duration.ofMinutes(5L));
            int port = server.start();
            String url = "http://localhost:" + port;
            ArrayList<LocalWebServer.Endpoint> endpoints = new ArrayList<>(endpoints0);
            endpoints.sort(null);
            instance = new LocalWebServer(
               server,
               publicAddress.isEmpty() ? url : "https://" + publicAddress,
               List.copyOf(endpoints),
               (publicAddress.isEmpty() ? "p=" + port : "a=" + URLEncoder.encode(publicAddress, StandardCharsets.UTF_8))
                  + (server.auth.isEmpty() ? "" : "&c=" + server.encodedAuth)
            );
            KubeJSPlugins.forEachPlugin(instance, KubeJSPlugin::localWebServerStarted);
            KubeJS.LOGGER.info("Started the local web server at {}", url);
         } catch (BindFailedException var11) {
            KubeJS.LOGGER.warn("Failed to start the local web server - all ports occupied");
         } catch (Exception var12) {
            KubeJS.LOGGER.warn("Failed to start the local web server - unexpected error");
            var12.printStackTrace();
         }
      }
   }

   public record Endpoint(String method, String path, boolean auth) implements Comparable<LocalWebServer.Endpoint> {
      public int compareTo(@NotNull LocalWebServer.Endpoint o) {
         return this.path.compareToIgnoreCase(o.path);
      }
   }
}
