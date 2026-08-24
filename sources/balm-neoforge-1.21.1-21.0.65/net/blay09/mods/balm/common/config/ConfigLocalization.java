package net.blay09.mods.balm.common.config;

import java.util.HashSet;
import java.util.Set;
import net.blay09.mods.balm.api.config.schema.BalmConfigSchema;
import net.blay09.mods.balm.api.config.schema.ConfiguredProperty;
import net.blay09.mods.balm.api.config.schema.builder.ConfigCategory;

public class ConfigLocalization {
   private static final Set<String> modernTranslationKeyMods = new HashSet<>();

   public static void enableModernTranslationKeys(String modId) {
      modernTranslationKeyMods.add(modId);
   }

   private static boolean usesLegacyTranslationKeys(String modId) {
      return !modernTranslationKeyMods.contains(modId);
   }

   private static boolean usesLegacyTranslationKeys(BalmConfigSchema schema) {
      return !modernTranslationKeyMods.contains(schema.identifier().getNamespace());
   }

   public static String forTitle(BalmConfigSchema schema) {
      String modId = schema.identifier().getNamespace();
      return usesLegacyTranslationKeys(schema)
         ? "config." + modId + "." + schema.identifier().getPath() + ".title"
         : modId + ".configuration." + schema.identifier().getPath() + ".title";
   }

   public static String forTitle(String modId) {
      return usesLegacyTranslationKeys(modId) ? "config." + modId + ".title" : modId + ".configuration.title";
   }

   public static String forRootCategory(BalmConfigSchema schema) {
      String modId = schema.identifier().getNamespace();
      return usesLegacyTranslationKeys(modId) ? "config." + modId : modId + ".configuration";
   }

   public static String forCategory(ConfigCategory category) {
      String modId = category.parentSchema().identifier().getNamespace();
      return usesLegacyTranslationKeys(modId) ? "config." + modId + "." + category.name() : modId + ".configuration." + category.name();
   }

   public static String forProperty(ConfiguredProperty<?> property) {
      String modId = property.parentSchema().identifier().getNamespace();
      if (usesLegacyTranslationKeys(modId)) {
         return property.category().isEmpty()
            ? "config." + modId + "." + property.name()
            : "config." + modId + "." + property.category() + "." + property.name();
      } else {
         return property.category().isEmpty()
            ? modId + ".configuration." + property.name()
            : modId + ".configuration." + property.category() + "." + property.name();
      }
   }

   public static String forPropertyTooltip(ConfiguredProperty<?> property) {
      return forProperty(property) + ".tooltip";
   }
}
