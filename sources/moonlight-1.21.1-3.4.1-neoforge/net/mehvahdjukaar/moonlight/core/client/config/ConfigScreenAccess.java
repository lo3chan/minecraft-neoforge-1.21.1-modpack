package net.mehvahdjukaar.moonlight.core.client.config;

import net.mehvahdjukaar.moonlight.api.client.gui.ConfigEditSession;
import net.mehvahdjukaar.moonlight.api.platform.configs.options.ConfigCategory;
import net.mehvahdjukaar.moonlight.api.platform.configs.options.ConfigOption;
import net.minecraft.client.gui.Font;

interface ConfigScreenAccess {
   Font font();

   ConfigEditSession session();

   void openCategory(ConfigCategory var1);

   void toggleExpanded(ConfigOption<?> var1);

   void onValueEdited();

   boolean isCategoryEnabled(ConfigCategory var1);

   default boolean areAncestorsEnabled(ConfigCategory category) {
      ConfigCategory parent = category.parent();
      return parent == null || this.isCategoryEnabled(parent);
   }
}
