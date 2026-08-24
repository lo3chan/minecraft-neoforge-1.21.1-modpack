package com.yungnickyoung.minecraft.yungsmenutweaks.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

public class YMTConfigNeoForge {
   public static final Builder BUILDER = new Builder();
   public static final ModConfigSpec SPEC = BUILDER.build();
   public static final ConfigValue<Boolean> enableRightClickCycleButton = BUILDER.comment(
         " Allows right clicking to cycle backwards through some buttons.\n Default: true"
      )
      .define("Right Click Reverses Cycle Buttons", true);
   public static final ConfigValue<Boolean> enableMouseScrollOnSliders = BUILDER.comment(
         " Allows scrolling the mouse wheel to modify options using a slider\n when hovering over the slider.\n Default: true"
      )
      .define("Mouse Scroll Wheel Affects Sliders", true);
   public static final ConfigValue<Boolean> enableBackgroundTexture = BUILDER.comment(
         " If enabled, a custom background texture will be used in menus where possible.\n The texture used is specified by the Custom Background Texture option below.\n Default: false"
      )
      .define("Enable Custom Background Texture", false);
   public static final ConfigValue<String> backgroundTexture = BUILDER.comment(
         " A texture to use as a custom background, instead of the typical blurred background.\n Only does anything if the Enable Custom Background Texture option is true.\n Default: minecraft:textures/block/dirt.png"
      )
      .define("Custom Background Texture", "minecraft:textures/block/dirt.png");

   static {
      BUILDER.push("YUNG's Menu Tweaks");
      BUILDER.pop();
   }
}
