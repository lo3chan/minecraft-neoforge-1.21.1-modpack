package zank.mods.open_in_inventory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.client.resources.language.I18n;

public record OpenInInventoryConfig(
   @SerializedName("screen_blacklist") Set<String> screenBlacklist,
   @SerializedName("require_empty_main_hand") boolean requireEmptyMainHand,
   @SerializedName("require_single_stack") boolean requireSingleStack,
   @SerializedName("open_delay") int openDelay,
   @SerializedName("debug") boolean debug,
   @SerializedName("enabled_items") JsonArray enabledItems
) {
   public static final String LANG_PREFIX = "open_in_inventory.config.";

   public OpenInInventoryConfig() {
      this(new HashSet<>(), false, true, 3, false, new JsonArray());
   }

   public void write(Path configFile) throws IOException {
      JsonObject original = (JsonObject)OpenInInventory.GSON.toJsonTree(this);
      JsonObject json = new JsonObject();
      json.addProperty("//", I18n.get("open_in_inventory.config.refresh", new Object[0]));

      for (Entry<String, JsonElement> entry : original.entrySet()) {
         String name = entry.getKey();
         String commentStr = I18n.get("open_in_inventory.config." + name, new Object[0]);
         if (commentStr.indexOf(10) < 0) {
            json.addProperty("//" + name, commentStr);
         } else {
            json.add("//" + name, OpenInInventory.GSON.toJsonTree(commentStr.split("\n")));
         }

         json.add(name, entry.getValue());
      }

      try (BufferedWriter writer = Files.newBufferedWriter(configFile)) {
         OpenInInventory.GSON.toJson(json, writer);
      }
   }
}
