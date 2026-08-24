package dev.latvian.mods.kubejs.web;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import dev.latvian.apps.tinyserver.ws.WSSession;
import dev.latvian.mods.kubejs.util.JsonUtils;

public class KJSWSSession extends WSSession<KJSHTTPRequest> {
   public SessionInfo info = SessionInfo.NONE;

   public void onTextMessage(String message) {
      if (message.startsWith("{") && message.endsWith("}")) {
         JsonObject json = JsonUtils.fromString(message).getAsJsonObject();
         if (json.has("type")) {
            String type = json.get("type").getAsString();
            JsonElement payload = (JsonElement)(json.has("payload") ? json.get("payload") : JsonNull.INSTANCE);
            if (type.equals("$")) {
               if (payload instanceof JsonObject o) {
                  this.info = SessionInfo.fromJson(this.info, o);
               }
            } else if (!type.isBlank()) {
               this.onEvent(type, payload);
            }
         }
      }
   }

   public void onEvent(String type, JsonElement payload) {
   }
}
