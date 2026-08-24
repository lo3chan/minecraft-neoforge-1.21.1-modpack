package dev.latvian.mods.kubejs;

import dev.latvian.mods.kubejs.client.EditorExt;
import dev.latvian.mods.kubejs.util.BaseProperties;
import java.util.Locale;

public class DevProperties extends BaseProperties {
   private static DevProperties instance;
   public boolean virtualPackOutput;
   public boolean logRegistryTypes;
   public boolean logRegistryEventObjects;
   public boolean logAddedRecipes;
   public boolean logRemovedRecipes;
   public boolean logModifiedRecipes;
   public boolean logSkippedRecipes;
   public boolean logRecipeDebug;
   public boolean logSkippedTags;
   public boolean logErroringRecipes;
   public boolean logErroringParsedRecipes;
   public boolean logInvalidRecipeHandlers;
   public boolean logSkippedPlugins;
   public boolean logGeneratedData;
   public boolean logEventErrorStackTrace;
   public boolean logChangesInChat;
   public boolean strictTags;
   public boolean alwaysCaptureErrors;
   public boolean reloadOnFileSave;
   public String openUriFormat;
   public String kubedexSound;

   public static DevProperties get() {
      if (instance == null) {
         instance = new DevProperties();
      }

      return instance;
   }

   public static void reload() {
      instance = new DevProperties();
   }

   private DevProperties() {
      super(KubeJSPaths.getLocalDevProperties(), "KubeJS Dev Properties");
   }

   @Override
   protected void load() {
      this.virtualPackOutput = this.get("virtual_pack_output", false);
      this.logRegistryTypes = this.get("log_registry_types", false);
      this.logRegistryEventObjects = this.get("log_registry_event_objects", false);
      this.logAddedRecipes = this.get("log_added_recipes", false);
      this.logRemovedRecipes = this.get("log_removed_recipes", false);
      this.logModifiedRecipes = this.get("log_modified_recipes", false);
      this.logSkippedRecipes = this.get("log_skipped_recipes", false);
      this.logRecipeDebug = this.get("log_recipe_debug", false);
      this.logSkippedTags = this.get("log_skipped_tags", false);
      this.logErroringRecipes = this.get("log_erroring_recipes", true);
      this.logErroringParsedRecipes = this.get("log_erroring_parsed_recipes", false);
      this.logInvalidRecipeHandlers = this.get("log_invalid_recipe_handlers", true);
      this.logSkippedPlugins = this.get("log_skipped_plugins", true);
      this.logGeneratedData = this.get("log_generated_data", false);
      this.logEventErrorStackTrace = this.get("log_event_error_stack_trace", false);
      this.logChangesInChat = this.get("log_changes_in_chat", false);
      this.strictTags = this.get("strict_tags", false);
      this.alwaysCaptureErrors = this.get("always_capture_errors", false);
      this.reloadOnFileSave = this.get("reload_on_file_save", false);
      this.kubedexSound = this.get("kubedex_sound", "entity.experience_orb.pickup");
      String format = this.get("open_uri_format", "vscode");
      String var2 = format.toLowerCase(Locale.ROOT);

      this.openUriFormat = switch (var2) {
         case "vscode" -> EditorExt.VSCODE;
         case "vscodium" -> EditorExt.VSCODIUM;
         case "vscode-oss" -> EditorExt.VSCODE_OSS;
         default -> format;
      };
   }
}
