package dev.latvian.mods.kubejs.web.local;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.web.KJSWSSession;

public class ConsoleWSSession extends KJSWSSession {
   public final ConsoleJS console;

   public ConsoleWSSession(ConsoleJS console) {
      this.console = console;
   }

   @Override
   public void onEvent(String type, JsonElement payload) {
      switch (type) {
         case "info":
            this.console.info(payload.getAsString());
            break;
         case "warn":
            this.console.warn(payload.getAsString());
            break;
         case "error":
            this.console.error(payload.getAsString());
      }
   }
}
