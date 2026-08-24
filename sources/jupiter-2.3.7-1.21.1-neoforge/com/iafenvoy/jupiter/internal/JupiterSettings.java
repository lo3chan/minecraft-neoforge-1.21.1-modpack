package com.iafenvoy.jupiter.internal;

import com.iafenvoy.jupiter.config.container.AutoInitConfigContainer;
import com.iafenvoy.jupiter.config.entry.BooleanEntry;
import com.iafenvoy.jupiter.config.entry.EnumEntry;
import com.iafenvoy.jupiter.util.RLUtil;
import java.util.Locale;

public class JupiterSettings extends AutoInitConfigContainer {
   public static final JupiterSettings INSTANCE = new JupiterSettings();
   public final JupiterSettings.General general = new JupiterSettings.General();

   private JupiterSettings() {
      super(RLUtil.id("jupiter"), "jupiter.screen.config.title", "./config/jupiter.json");
   }

   private static String name(String category, String name) {
      return String.format(Locale.ROOT, "config.%s.%s.%s", "jupiter", category, name);
   }

   private static String tooltip(String category, String name) {
      return String.format(Locale.ROOT, "config.%s.%s.%s.tooltip", "jupiter", category, name);
   }

   public static class General extends AutoInitConfigContainer.AutoInitConfigCategoryBase {
      public final BooleanEntry loadForgeConfigs = BooleanEntry.builder(JupiterSettings.name("general", "loadForgeConfigs"), true)
         .key("loadForgeConfigs")
         .tooltip(JupiterSettings.tooltip("general", "loadForgeConfigs"))
         .build();
      public final BooleanEntry loadClothConfigs = BooleanEntry.builder(JupiterSettings.name("general", "loadClothConfigs"), true)
         .key("loadClothConfigs")
         .tooltip(JupiterSettings.tooltip("general", "loadClothConfigs"))
         .build();
      public final EnumEntry<ConfigButtonReplaceStrategy> configButtonReplacement = EnumEntry.builder(
            JupiterSettings.name("general", "configButtonReplacement"), ConfigButtonReplaceStrategy.UNAVAILABLE_ONLY
         )
         .key("configButtonReplacement")
         .restartRequired()
         .build();
      public final BooleanEntry redirectAutoConfigScreen = BooleanEntry.builder(JupiterSettings.name("general", "redirectAutoConfigScreen"), false)
         .key("redirectAutoConfigScreen")
         .tooltip(JupiterSettings.tooltip("general", "redirectAutoConfigScreen"))
         .build();

      private General() {
         super("general", "jupiter.screen.config.general");
      }
   }
}
