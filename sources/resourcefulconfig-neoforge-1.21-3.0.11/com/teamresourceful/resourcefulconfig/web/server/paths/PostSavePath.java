package com.teamresourceful.resourcefulconfig.web.server.paths;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.teamresourceful.resourcefulconfig.api.types.ResourcefulConfig;
import com.teamresourceful.resourcefulconfig.api.types.entries.ResourcefulConfigEntry;
import com.teamresourceful.resourcefulconfig.api.types.entries.ResourcefulConfigValueEntry;
import com.teamresourceful.resourcefulconfig.common.config.Configurations;
import com.teamresourceful.resourcefulconfig.common.config.ParsingUtils;
import com.teamresourceful.resourcefulconfig.web.info.UserJwtPayload;
import com.teamresourceful.resourcefulconfig.web.utils.WebServerUtils;
import com.teamresourceful.resourcefulconfig.web.utils.WebVerifier;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public record PostSavePath(WebVerifier verifier) implements BasePath {
   @Override
   public void handleCall(HttpExchange exchange, UserJwtPayload payload) throws IOException {
      String query = WebServerUtils.getQueryValue(exchange, "id");
      if (query != null) {
         ResourcefulConfig config = Configurations.INSTANCE.configs().get(query);
         if (config != null && !config.info().isHidden()) {
            try {
               String data = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);
               JsonObject object = (JsonObject)WebServerUtils.GSON.fromJson(data, JsonObject.class);
               saveConfig(config, object);
               WebServerUtils.send(exchange, 200, null, new byte[0]);
            } catch (Exception var7) {
               WebServerUtils.send(exchange, 400, null, new byte[0]);
            }
         } else {
            WebServerUtils.send(exchange, 400, null, new byte[0]);
         }
      } else {
         WebServerUtils.send(exchange, 400, null, new byte[0]);
      }
   }

   @Override
   public String method() {
      return "POST";
   }

   private static void saveConfig(ResourcefulConfig config, JsonObject object) {
      object.asMap().forEach((key, value) -> saveEntry(config, key, value));
      config.save();
   }

   private static void saveEntry(ResourcefulConfig config, String key, JsonElement element) {
      if (key.contains(";")) {
         String[] split = key.split(";", 2);
         if (split.length == 2) {
            String id = split[0];
            ResourcefulConfig category = config.categories().get(id);
            if (category != null) {
               saveEntry(category, split[1], element);
            }
         }
      } else if (key.contains(":")) {
         String[] split = key.split(":", 2);
         if (split.length == 2) {
            String id = split[0];
            ResourcefulConfig category = config.categories().get(id);
            if (category != null) {
               saveEntry(category, split[1], element);
            }
         }
      } else {
         ResourcefulConfigEntry entry = config.entries().get(key);
         if (!(entry instanceof ResourcefulConfigValueEntry valueEntry)) {
            return;
         }

         switch (entry.type()) {
            case BOOLEAN:
               valueEntry.setBoolean(element.getAsBoolean());
               break;
            case BYTE:
               valueEntry.setByte(element.getAsByte());
               break;
            case SHORT:
               valueEntry.setShort(element.getAsShort());
               break;
            case INTEGER:
               valueEntry.setInt(element.getAsInt());
               break;
            case LONG:
               valueEntry.setLong(element.getAsLong());
               break;
            case FLOAT:
               valueEntry.setFloat(element.getAsFloat());
               break;
            case DOUBLE:
               valueEntry.setDouble(element.getAsDouble());
               break;
            case STRING:
               valueEntry.setString(element.getAsString());
               break;
            case ENUM:
               valueEntry.setEnum(ParsingUtils.parseEnum(valueEntry.objectType(), element.getAsString()));
         }
      }
   }
}
