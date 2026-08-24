package dev.latvian.mods.kubejs.plugin.builtin.wrapper;

import dev.latvian.mods.kubejs.color.KubeColor;
import dev.latvian.mods.kubejs.color.NoColor;
import dev.latvian.mods.kubejs.color.SimpleColor;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.item.DyeColor;

public interface ColorWrapper {
   Map<String, KubeColor> MAP = new HashMap<>();
   Map<String, ChatFormatting> TEXT = (Map<String, ChatFormatting>)Util.make(new HashMap(), map -> {
      for (ChatFormatting c : ChatFormatting.values()) {
         map.put(c.getName(), c);
      }
   });
   Map<String, DyeColor> DYE = (Map<String, DyeColor>)Util.make(new HashMap(), map -> {
      for (DyeColor c : DyeColor.values()) {
         map.put(c.getName(), c);
      }
   });
   KubeColor NONE = createMapped(new NoColor(), "NONE", "none", "", "-", "transparent");
   KubeColor BLACK = createMapped(ChatFormatting.BLACK, "BLACK", "black");
   KubeColor DARK_BLUE = createMapped(ChatFormatting.DARK_BLUE, "DARK_BLUE", "dark_blue", "darkBlue");
   KubeColor DARK_GREEN = createMapped(ChatFormatting.DARK_GREEN, "DARK_GREEN", "dark_green", "darkGreen");
   KubeColor DARK_AQUA = createMapped(ChatFormatting.DARK_AQUA, "DARK_AQUA", "dark_aqua", "darkAqua");
   KubeColor DARK_RED = createMapped(ChatFormatting.DARK_RED, "DARK_RED", "dark_red", "darkRed");
   KubeColor DARK_PURPLE = createMapped(ChatFormatting.DARK_PURPLE, "DARK_PURPLE", "dark_purple", "darkPurple");
   KubeColor GOLD = createMapped(ChatFormatting.GOLD, "GOLD", "gold");
   KubeColor GRAY = createMapped(ChatFormatting.GRAY, "GRAY", "gray");
   KubeColor DARK_GRAY = createMapped(ChatFormatting.DARK_GRAY, "DARK_GRAY", "dark_gray", "darkGray");
   KubeColor BLUE = createMapped(ChatFormatting.BLUE, "BLUE", "blue");
   KubeColor GREEN = createMapped(ChatFormatting.GREEN, "GREEN", "green");
   KubeColor AQUA = createMapped(ChatFormatting.AQUA, "AQUA", "aqua");
   KubeColor RED = createMapped(ChatFormatting.RED, "RED", "red");
   KubeColor LIGHT_PURPLE = createMapped(ChatFormatting.LIGHT_PURPLE, "LIGHT_PURPLE", "light_purple", "lightPurple");
   KubeColor YELLOW = createMapped(ChatFormatting.YELLOW, "YELLOW", "yellow");
   KubeColor WHITE = createMapped(ChatFormatting.WHITE, "WHITE", "white");
   KubeColor WHITE_DYE = createMapped(DyeColor.WHITE, "WHITE_DYE", "white_dye", "whiteDye");
   KubeColor ORANGE_DYE = createMapped(DyeColor.ORANGE, "ORANGE_DYE", "orange_dye", "orangeDye");
   KubeColor MAGENTA_DYE = createMapped(DyeColor.MAGENTA, "MAGENTA_DYE", "magenta_dye", "magentaDye");
   KubeColor LIGHT_BLUE_DYE = createMapped(DyeColor.LIGHT_BLUE, "LIGHT_BLUE_DYE", "light_blue_dye", "lightBlueDye");
   KubeColor YELLOW_DYE = createMapped(DyeColor.YELLOW, "YELLOW_DYE", "yellow_dye", "yellowDye");
   KubeColor LIME_DYE = createMapped(DyeColor.LIME, "LIME_DYE", "lime_dye", "limeDye");
   KubeColor PINK_DYE = createMapped(DyeColor.PINK, "PINK_DYE", "pink_dye", "pinkDye");
   KubeColor GRAY_DYE = createMapped(DyeColor.GRAY, "GRAY_DYE", "gray_dye", "grayDye");
   KubeColor LIGHT_GRAY_DYE = createMapped(DyeColor.LIGHT_GRAY, "LIGHT_GRAY_DYE", "light_gray_dye", "lightGrayDye");
   KubeColor CYAN_DYE = createMapped(DyeColor.CYAN, "CYAN_DYE", "cyan_dye", "cyanDye");
   KubeColor PURPLE_DYE = createMapped(DyeColor.PURPLE, "PURPLE_DYE", "purple_dye", "purpleDye");
   KubeColor BLUE_DYE = createMapped(DyeColor.BLUE, "BLUE_DYE", "blue_dye", "blueDye");
   KubeColor BROWN_DYE = createMapped(DyeColor.BROWN, "BROWN_DYE", "brown_dye", "brownDye");
   KubeColor GREEN_DYE = createMapped(DyeColor.GREEN, "GREEN_DYE", "green_dye", "greenDye");
   KubeColor RED_DYE = createMapped(DyeColor.RED, "RED_DYE", "red_dye", "redDye");
   KubeColor BLACK_DYE = createMapped(DyeColor.BLACK, "BLACK_DYE", "black_dye", "blackDye");

   static KubeColor wrap(Object o) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.NullPointerException: Cannot invoke "org.jetbrains.java.decompiler.struct.consts.PrimitiveConstant.getString()" because "this.superClass" is null
      //   at org.jetbrains.java.decompiler.struct.StructClass.getRecordComponents(StructClass.java:216)
      //   at org.jetbrains.java.decompiler.modules.decompiler.IfPatternMatchProcessor.identifyRecordPatternMatch(IfPatternMatchProcessor.java:294)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.identifySwitchRecordPatternMatch(SwitchPatternMatchProcessor.java:526)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processStatement(SwitchPatternMatchProcessor.java:167)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:42)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:37)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:37)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:37)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatching(SwitchPatternMatchProcessor.java:23)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:330)
      //
      // Bytecode:
      // 00: aload 0
      // 01: astore 1
      // 02: bipush 0
      // 03: istore 2
      // 04: aload 1
      // 05: iload 2
      // 06: invokedynamic typeSwitch (Ljava/lang/Object;I)I bsm=java/lang/runtime/SwitchBootstraps.typeSwitch (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ dev/latvian/mods/kubejs/color/KubeColor, java/lang/CharSequence, java/lang/Number ]
      // 0b: tableswitch 172 -1 2 172 29 38 138
      // 28: aload 1
      // 29: checkcast dev/latvian/mods/kubejs/color/KubeColor
      // 2c: astore 3
      // 2d: aload 3
      // 2e: goto bd
      // 31: aload 1
      // 32: checkcast java/lang/CharSequence
      // 35: astore 4
      // 37: aload 4
      // 39: invokeinterface java/lang/CharSequence.toString ()Ljava/lang/String; 1
      // 3e: astore 5
      // 40: getstatic dev/latvian/mods/kubejs/plugin/builtin/wrapper/ColorWrapper.MAP Ljava/util/Map;
      // 43: aload 5
      // 45: invokeinterface java/util/Map.get (Ljava/lang/Object;)Ljava/lang/Object; 2
      // 4a: checkcast dev/latvian/mods/kubejs/color/KubeColor
      // 4d: astore 6
      // 4f: aload 6
      // 51: ifnull 59
      // 54: aload 6
      // 56: goto bd
      // 59: aload 5
      // 5b: ldc "#"
      // 5d: invokevirtual java/lang/String.startsWith (Ljava/lang/String;)Z
      // 60: ifeq 8f
      // 63: aload 5
      // 65: invokestatic java/lang/Long.decode (Ljava/lang/String;)Ljava/lang/Long;
      // 68: invokevirtual java/lang/Long.intValue ()I
      // 6b: istore 7
      // 6d: aload 5
      // 6f: invokevirtual java/lang/String.length ()I
      // 72: bipush 7
      // 74: if_icmpne 83
      // 77: new dev/latvian/mods/kubejs/color/SimpleColor
      // 7a: dup
      // 7b: iload 7
      // 7d: invokespecial dev/latvian/mods/kubejs/color/SimpleColor.<init> (I)V
      // 80: goto bd
      // 83: new dev/latvian/mods/kubejs/color/SimpleColorWithAlpha
      // 86: dup
      // 87: iload 7
      // 89: invokespecial dev/latvian/mods/kubejs/color/SimpleColorWithAlpha.<init> (I)V
      // 8c: goto bd
      // 8f: getstatic dev/latvian/mods/kubejs/plugin/builtin/wrapper/ColorWrapper.NONE Ldev/latvian/mods/kubejs/color/KubeColor;
      // 92: goto bd
      // 95: aload 1
      // 96: checkcast java/lang/Number
      // 99: astore 5
      // 9b: aload 5
      // 9d: invokevirtual java/lang/Number.intValue ()I
      // a0: ifne a8
      // a3: bipush 3
      // a4: istore 2
      // a5: goto 04
      // a8: new dev/latvian/mods/kubejs/color/SimpleColor
      // ab: dup
      // ac: aload 5
      // ae: invokevirtual java/lang/Number.intValue ()I
      // b1: invokespecial dev/latvian/mods/kubejs/color/SimpleColor.<init> (I)V
      // b4: goto bd
      // b7: getstatic dev/latvian/mods/kubejs/plugin/builtin/wrapper/ColorWrapper.NONE Ldev/latvian/mods/kubejs/color/KubeColor;
      // ba: goto bd
      // bd: areturn
   }

   static TextColor wrapTextColor(Object o) {
      return wrap(o).kjs$createTextColor();
   }

   static ColorRGBA wrapColorRGBA(Object o) {
      return new ColorRGBA(wrap(o).kjs$getARGB());
   }

   static KubeColor createMapped(Object o, String... names) {
      KubeColor c = wrap(o);

      for (String s : names) {
         MAP.put(s, c);
      }

      return c;
   }

   static KubeColor rgba(int r, int g, int b, int a) {
      return new SimpleColor(r << 16 | g << 8 | b | a << 24);
   }
}
