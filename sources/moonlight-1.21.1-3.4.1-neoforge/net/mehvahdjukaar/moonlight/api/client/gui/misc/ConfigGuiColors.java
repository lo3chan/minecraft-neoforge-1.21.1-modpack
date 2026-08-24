package net.mehvahdjukaar.moonlight.api.client.gui.misc;

import java.util.Map;
import java.util.Objects;
import net.minecraft.ChatFormatting;

public final class ConfigGuiColors {
   public static final int HEADER_SEPARATOR = -15724526;
   public static final int LABEL = chat(ChatFormatting.WHITE);
   public static final int TEXT = chat(ChatFormatting.WHITE);
   public static final int TEXT_SECONDARY = chat(ChatFormatting.GRAY);
   public static final int DESCRIPTION = chat(ChatFormatting.GRAY);
   public static final int ERROR = chat(ChatFormatting.RED);
   public static final int TITLE = chat(ChatFormatting.GOLD);
   public static final int MODIFIED = chat(ChatFormatting.YELLOW);
   public static final int CATEGORY = chat(ChatFormatting.GREEN);
   public static final int SELECTED = chat(ChatFormatting.LIGHT_PURPLE);
   public static final int TILE_BG = -15000800;
   public static final int TILE_BG_HOVER = -13882316;
   public static final int TILE_OUTLINE = -16777216;
   public static final int TILE_OUTLINE_HOVER = 0xFF000000 | CATEGORY;
   public static final int TILE_OUTLINE_HOVER_FOREIGN = 0xFF000000 | chat(ChatFormatting.AQUA);
   public static final int TILE_ICON_BG = -13619144;
   private static final ChatFormatting[] INITIAL_COLORS = new ChatFormatting[]{
      ChatFormatting.RED,
      ChatFormatting.GOLD,
      ChatFormatting.YELLOW,
      ChatFormatting.GREEN,
      ChatFormatting.AQUA,
      ChatFormatting.BLUE,
      ChatFormatting.LIGHT_PURPLE,
      ChatFormatting.WHITE
   };
   private static final Map<String, Integer> BRAND_COLORS = Map.of("quark", -12001860, "zeta", -12001860);
   public static final int CRUMB = chat(ChatFormatting.GRAY);
   public static final int CRUMB_HOVER = chat(ChatFormatting.WHITE);
   public static final int CRUMB_CURRENT = chat(ChatFormatting.YELLOW);
   public static final int CRUMB_SEPARATOR = chat(ChatFormatting.DARK_GRAY);
   public static final int SYNTAX_DEFAULT = chat(ChatFormatting.WHITE);
   public static final int SYNTAX_KEY = chat(ChatFormatting.AQUA);
   public static final int SYNTAX_STRING = chat(ChatFormatting.GREEN);
   public static final int SYNTAX_NUMBER = chat(ChatFormatting.GOLD);
   public static final int SYNTAX_KEYWORD = chat(ChatFormatting.LIGHT_PURPLE);
   public static final int SYNTAX_TYPE = chat(ChatFormatting.YELLOW);
   public static final int SYNTAX_PUNCTUATION = chat(ChatFormatting.GRAY);
   public static final int SYNTAX_ESCAPE = chat(ChatFormatting.GOLD);
   public static final int SYNTAX_CHAR_CLASS = chat(ChatFormatting.GREEN);
   public static final int SYNTAX_GROUP = chat(ChatFormatting.AQUA);
   public static final int SYNTAX_QUANTIFIER = chat(ChatFormatting.LIGHT_PURPLE);
   public static final int SYNTAX_ANCHOR = chat(ChatFormatting.YELLOW);

   public static int chat(ChatFormatting color) {
      return Objects.requireNonNull(color.getColor());
   }

   public static int initialLetter(String modId) {
      Integer brand = BRAND_COLORS.get(modId);
      return brand != null ? brand : chat(INITIAL_COLORS[Math.floorMod(modId.hashCode(), INITIAL_COLORS.length)]);
   }
}
