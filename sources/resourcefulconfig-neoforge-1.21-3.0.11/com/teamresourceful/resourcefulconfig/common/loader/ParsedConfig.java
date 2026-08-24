package com.teamresourceful.resourcefulconfig.common.loader;

import com.google.gson.JsonObject;
import com.teamresourceful.resourcefulconfig.api.annotations.Config;
import com.teamresourceful.resourcefulconfig.api.patching.ConfigPatchEvent;
import com.teamresourceful.resourcefulconfig.api.types.ResourcefulConfig;
import com.teamresourceful.resourcefulconfig.api.types.ResourcefulConfigButton;
import com.teamresourceful.resourcefulconfig.api.types.entries.ResourcefulConfigEntry;
import com.teamresourceful.resourcefulconfig.api.types.info.ResourcefulConfigInfo;
import com.teamresourceful.resourcefulconfig.common.jsonc.JsoncObject;
import com.teamresourceful.resourcefulconfig.common.utils.ModUtils;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Consumer;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

public record ParsedConfig(
   int version,
   @NotNull String id,
   @NotNull ResourcefulConfigInfo info,
   @NotNull LinkedHashMap<String, ResourcefulConfigEntry> entries,
   @NotNull LinkedHashMap<String, ResourcefulConfig> categories,
   @NotNull List<ResourcefulConfigButton> buttons
) implements ResourcefulConfig {
   public ParsedConfig(Config config, ResourcefulConfigInfo info) {
      this(config.version(), config.value(), info, new LinkedHashMap<>(), new LinkedHashMap<>(), new ArrayList<>());
   }

   private File getConfigFile() {
      Path configDir = ModUtils.getConfigPath();
      File jsonFile = configDir.resolve(this.id + ".json").toFile();
      return jsonFile.exists() ? jsonFile : configDir.resolve(this.id + ".jsonc").toFile();
   }

   @Override
   public void save() {
      try {
         FileUtils.write(this.getConfigFile(), Writer.save(this).toString(), StandardCharsets.UTF_8);
      } catch (Exception var2) {
         ModUtils.log("Failed to save config file " + this.id + ".json", var2);
      }
   }

   @Override
   public void load(Consumer<ConfigPatchEvent> handler) {
      File file = this.getConfigFile();
      if (file.exists()) {
         try {
            String data = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            JsonObject json = JsoncObject.parse(data);
            json = Patcher.patch(json, this.version(), handler);
            Loader.loadConfig(this, json);
         } catch (Exception var5) {
         }

         if (file.getName().endsWith(".json") && !file.delete()) {
            ModUtils.log("Failed to delete old config file " + this.id + ".json");
         }
      }
   }

   @Override
   public boolean hasFile() {
      return true;
   }
}
