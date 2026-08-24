package com.github.alexthe666.citadel.config;

import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;

public class ServerConfig {
   public final BooleanValue citadelEntityTracker;
   public final BooleanValue skipDatapackWarnings;
   public final DoubleValue chunkGenSpawnModifier;
   public final BooleanValue aprilFoolsContent;
   public static boolean citadelEntityTrack;
   public static boolean skipWarnings;
   public static double chunkGenSpawnModifierVal = 1.0;
   public static boolean aprilFools;

   public ServerConfig(Builder builder) {
      builder.push("general");
      this.citadelEntityTracker = buildBoolean(
         builder,
         "Track Entities",
         "all",
         true,
         "True if citadel tracks entity properties(freezing, stone mobs, etc) on server. Turn this to false to solve some server lag, may break some stuff."
      );
      this.skipDatapackWarnings = buildBoolean(builder, "Skip Datapack Warnings", "all", true, "True to skip warnings about using datapacks.");
      this.chunkGenSpawnModifier = builder.comment(
            "Multiplies the count of entities spawned by this number. 0 = no entites added on chunk gen, 2 = twice as many entities added on chunk gen. Useful for many mods that add a lot of creatures, namely animals, to the spawn lists."
         )
         .translation("chunkGenSpawnModifier")
         .defineInRange("chunkGenSpawnModifier", 1.0, 0.0, 100000.0);
      this.aprilFoolsContent = buildBoolean(builder, "April Fools Content", "all", true, "True to if april fools content can display on april fools.");
   }

   private static BooleanValue buildBoolean(Builder builder, String name, String catagory, boolean defaultValue, String comment) {
      return builder.comment(comment).translation(name).define(name, defaultValue);
   }
}
