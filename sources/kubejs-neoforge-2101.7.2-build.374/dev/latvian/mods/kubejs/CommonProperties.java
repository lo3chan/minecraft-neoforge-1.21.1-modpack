package dev.latvian.mods.kubejs;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Pair;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.TextIcons;
import dev.latvian.mods.kubejs.util.BaseProperties;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;

public class CommonProperties extends BaseProperties {
   private static CommonProperties instance;
   public boolean hideServerScriptErrors;
   public boolean serverOnly;
   public boolean announceReload;
   public String packMode;
   public boolean saveDevPropertiesInConfig;
   public boolean allowAsyncStreams;
   public boolean matchJsonRecipes;
   public boolean ignoreCustomUniqueRecipeIds;
   public boolean startupErrorGUI;
   public String startupErrorReportUrl;
   public boolean removeSlotLimit;
   public int defaultMaxStackSize;
   public JsonElement creativeModeTabIcon;
   public JsonElement creativeModeTabName;

   public static CommonProperties get() {
      if (instance == null) {
         instance = new CommonProperties();
      }

      return instance;
   }

   public static void reload() {
      instance = new CommonProperties();
   }

   private CommonProperties() {
      super(KubeJSPaths.COMMON_PROPERTIES, "KubeJS Common Properties");
   }

   @Override
   protected void load() {
      this.hideServerScriptErrors = this.get("hide_server_script_errors", false);
      this.serverOnly = this.get("server_only", false);
      this.announceReload = this.get("announce_reload", true);
      this.packMode = this.get("packmode", "");
      this.saveDevPropertiesInConfig = this.get("save_dev_properties_in_config", false);
      this.allowAsyncStreams = this.get("allow_async_streams", true);
      this.matchJsonRecipes = this.get("match_json_recipes", true);
      this.ignoreCustomUniqueRecipeIds = this.get("ignore_custom_unique_recipe_ids", false);
      this.startupErrorGUI = this.get("startup_error_gui", true);
      this.startupErrorReportUrl = this.get("startup_error_report_url", "");
      this.removeSlotLimit = this.get("remove_slot_limit", false);
      this.defaultMaxStackSize = Math.max(0, Math.min(1000000000, this.get("default_max_stack_size", 0)));
      this.creativeModeTabIcon = this.get("creative_mode_tab_icon", new JsonObject());
      this.creativeModeTabName = this.get("creative_mode_tab_name", JsonNull.INSTANCE);
   }

   public void setPackMode(String s) {
      this.packMode = s;
      this.set("packmode", new JsonPrimitive(s));
      this.save();
   }

   public Component getCreativeModeTabName() {
      return !(this.creativeModeTabName instanceof JsonNull)
         ? ComponentSerialization.CODEC
            .decode(RegistryAccessContainer.BUILTIN.json(), this.creativeModeTabName)
            .result()
            .<Component>map(Pair::getFirst)
            .orElse(TextIcons.NAME)
         : TextIcons.NAME;
   }

   public int getMaxSlotSize(int original) {
      return this.removeSlotLimit ? 1000000000 : original;
   }

   public int getMaxStackSize(int original) {
      return this.defaultMaxStackSize == 0 ? original : this.defaultMaxStackSize;
   }
}
