package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.util.BaseProperties;

public class ClientProperties extends BaseProperties {
   private static ClientProperties instance;
   public String windowTitle;
   public boolean disableRecipeBook;
   public boolean exportAtlases;
   public boolean blurScaledPackIcon;
   public boolean customStackSizeText;
   public boolean shrinkStackSizeText;

   public static ClientProperties get() {
      if (instance == null) {
         instance = new ClientProperties();
      }

      return instance;
   }

   public static void reload() {
      instance = new ClientProperties();
   }

   private ClientProperties() {
      super(KubeJSPaths.CLIENT_PROPERTIES, "KubeJS Client Properties");
   }

   @Override
   protected void load() {
      this.windowTitle = this.get("window_title", "");
      this.disableRecipeBook = this.get("disable_recipe_book", false);
      this.exportAtlases = this.get("export_atlases", false);
      this.blurScaledPackIcon = this.get("blur_scaled_pack_icon", true);
      this.customStackSizeText = this.get("custom_stack_size_text", true);
      this.shrinkStackSizeText = this.get("shrink_stack_size_text", true);
   }
}
