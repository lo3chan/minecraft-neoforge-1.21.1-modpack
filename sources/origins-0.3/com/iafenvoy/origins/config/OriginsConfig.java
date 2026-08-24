package com.iafenvoy.origins.config;

import com.iafenvoy.jupiter.config.container.AutoInitConfigContainer;
import com.iafenvoy.jupiter.config.container.AutoInitConfigContainer.AutoInitConfigCategoryBase;
import com.iafenvoy.jupiter.config.entry.BooleanEntry;
import com.iafenvoy.jupiter.config.entry.IntegerEntry;
import com.iafenvoy.jupiter.config.entry.BooleanEntry.Builder;
import net.minecraft.resources.ResourceLocation;

public class OriginsConfig extends AutoInitConfigContainer {
   public static final OriginsConfig INSTANCE = new OriginsConfig();
   public final OriginsConfig.General general = new OriginsConfig.General();
   public final OriginsConfig.ModifyPlayerSpawnPower modifyPlayerSpawnPower = new OriginsConfig.ModifyPlayerSpawnPower();
   public final OriginsConfig.Debug debug = new OriginsConfig.Debug();

   public OriginsConfig() {
      super(ResourceLocation.fromNamespaceAndPath("origins", "config"), "config.origins.title", "./config/origins.json");
   }

   public static class Debug extends AutoInitConfigCategoryBase {
      public final BooleanEntry builtinRegistries = (BooleanEntry)((Builder)BooleanEntry.builder("config.origins.debug.builtinRegistries", false)
            .key("builtinRegistries"))
         .build();
      public final BooleanEntry dynamicRegistries = (BooleanEntry)((Builder)BooleanEntry.builder("config.origins.debug.dynamicRegistries", false)
            .key("dynamicRegistries"))
         .build();

      public Debug() {
         super("debug", "category.origins.debug.title");
      }
   }

   public static class General extends AutoInitConfigCategoryBase {
      public final IntegerEntry permissionLevel = (IntegerEntry)((com.iafenvoy.jupiter.config.entry.IntegerEntry.Builder)IntegerEntry.builder(
               "config.origins.general.permissionLevel", 2
            )
            .key("permissionLevel"))
         .range(0, 4)
         .build();
      public final BooleanEntry compactUsabilityHints = (BooleanEntry)((Builder)BooleanEntry.builder("config.origins.general.compactUsabilityHints", false)
            .key("compactUsabilityHints"))
         .build();
      public final IntegerEntry hudOffsetX = (IntegerEntry)((com.iafenvoy.jupiter.config.entry.IntegerEntry.Builder)IntegerEntry.builder(
               "config.origins.general.hudOffsetX", 0
            )
            .key("hudOffsetX"))
         .build();
      public final IntegerEntry hudOffsetY = (IntegerEntry)((com.iafenvoy.jupiter.config.entry.IntegerEntry.Builder)IntegerEntry.builder(
               "config.origins.general.hudOffsetY", 0
            )
            .key("hudOffsetY"))
         .build();

      public General() {
         super("general", "category.origins.general.title");
      }
   }

   public static class ModifyPlayerSpawnPower extends AutoInitConfigCategoryBase {
      public final IntegerEntry radius = (IntegerEntry)((com.iafenvoy.jupiter.config.entry.IntegerEntry.Builder)IntegerEntry.builder(
               "config.origins.modifyPlayerSpawnPower.radius", 6400
            )
            .key("radius"))
         .min(1)
         .build();
      public final IntegerEntry horizontalBlockCheckInterval = (IntegerEntry)((com.iafenvoy.jupiter.config.entry.IntegerEntry.Builder)IntegerEntry.builder(
               "config.origins.modifyPlayerSpawnPower.horizontalBlockCheckInterval", 64
            )
            .min(0)
            .key("horizontalBlockCheckInterval"))
         .build();
      public final IntegerEntry verticalBlockCheckInterval = (IntegerEntry)((com.iafenvoy.jupiter.config.entry.IntegerEntry.Builder)IntegerEntry.builder(
               "config.origins.modifyPlayerSpawnPower.verticalBlockCheckInterval", 64
            )
            .min(0)
            .key("verticalBlockCheckInterval"))
         .build();

      public ModifyPlayerSpawnPower() {
         super("modifyPlayerSpawnPower", "category.origins.modifyPlayerSpawnPower.title");
      }
   }
}
