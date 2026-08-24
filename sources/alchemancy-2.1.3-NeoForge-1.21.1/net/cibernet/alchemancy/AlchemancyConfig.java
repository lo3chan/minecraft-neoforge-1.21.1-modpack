package net.cibernet.alchemancy;

import net.cibernet.alchemancy.data.save.InfusionCodexSaveData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.EnumValue;

public class AlchemancyConfig {
   private static boolean validateItemName(Object obj) {
      return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
   }

   private static String translationKey(String key) {
      return "alchemancy.configuration." + key;
   }

   public static class Client {
      private static final Builder BUILDER = new Builder();
      private static final EnumValue<InfusionCodexSaveData.DisplayMode> CODEX_DISPLAY_MODE = BUILDER.comment(
            "Determines how entries are displayed in the Infusion Codex\nSPOILER: Shows all undiscovered infusions, but does not enable their entries\nNERD: Unlocks all infusion entries and property sources"
         )
         .translation(AlchemancyConfig.translationKey("codex_display_mode"))
         .defineEnum("codex_display_mode", InfusionCodexSaveData.DisplayMode.DEFAULT);
      static final ModConfigSpec SPEC = BUILDER.build();

      public static InfusionCodexSaveData.DisplayMode codexDisplayMode() {
         return (InfusionCodexSaveData.DisplayMode)CODEX_DISPLAY_MODE.get();
      }
   }

   public static class Common {
      private static final Builder BUILDER = new Builder();
      static final ModConfigSpec SPEC = BUILDER.build();
   }

   public static class Server {
      private static final Builder BUILDER = new Builder();
      private static final BooleanValue INFUSED_LOOT = BUILDER.comment("Gives a chance for mob equipment and chest loot to have Infused Properties")
         .translation(AlchemancyConfig.translationKey("infused_loot"))
         .define("gameplay.infusedLoot", false);
      private static final BooleanValue RANDOMIZED_INFUSIONS = BUILDER.comment(
            "Gives random Infusions to every item type. Overrides Infused Loot and requires server restart"
         )
         .translation(AlchemancyConfig.translationKey("randomized_infusions"))
         .define("gameplay.randomizedInfusions", false);
      static final ModConfigSpec SPEC = BUILDER.build();

      public static boolean infusedLoot() {
         return (Boolean)INFUSED_LOOT.get() && !(Boolean)RANDOMIZED_INFUSIONS.get();
      }

      public static boolean randomizedInfusions() {
         return (Boolean)RANDOMIZED_INFUSIONS.get();
      }
   }
}
