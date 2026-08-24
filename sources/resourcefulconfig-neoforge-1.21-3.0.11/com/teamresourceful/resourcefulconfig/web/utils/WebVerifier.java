package com.teamresourceful.resourcefulconfig.web.utils;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.teamresourceful.resourcefulconfig.web.config.WebServerConfig;
import com.teamresourceful.resourcefulconfig.web.info.UserJwtPayload;
import com.teamresourceful.resourcefulconfig.web.server.WebServer;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class WebVerifier {
   private static final HttpClient CLIENT = HttpClient.newBuilder()
      .version(Version.HTTP_2)
      .followRedirects(Redirect.NORMAL)
      .connectTimeout(Duration.ofSeconds(10L))
      .build();
   private final String origin;
   private final String authUrl;

   public WebVerifier(WebServerConfig config) {
      this.origin = config.getSite();
      this.authUrl = this.origin + "/api/v1/private/verify";
   }

   private UserJwtPayload verify(UserJwtPayload info) {
      return WebServer.verify(info) ? info : null;
   }

   @Nullable
   public UserJwtPayload getInfo(HttpExchange exchange) {
      return this.getInfo(exchange.getRequestHeaders().getFirst("Authorization"));
   }

   @Nullable
   public UserJwtPayload getInfo(String jwt) {
      if (jwt != null) {
         try {
            HttpRequest request = HttpRequest.newBuilder(new URI(this.authUrl))
               .GET()
               .version(Version.HTTP_2)
               .header("Authorization", jwt)
               .header("User-Agent", "ResourcefulConfig")
               .build();
            HttpResponse<String> response = CLIENT.send(request, BodyHandlers.ofString());
            if (response.statusCode() == 200) {
               JsonObject body = (JsonObject)WebServerUtils.GSON.fromJson(response.body(), JsonObject.class);
               if (WebServerUtils.getBool(body, "valid") && body.has("user")) {
                  return this.verify(UserJwtPayload.fromJson(body.get("user")));
               }
            }

            return null;
         } catch (Exception var5) {
            return null;
         }
      } else {
         return null;
      }
   }

   public String origin() {
      return this.origin;
   }
}
