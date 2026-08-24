package me.flashyreese.mods.reeses_sodium_options.client.gui.theme;

import net.caffeinemc.mods.sodium.client.gui.ColorTheme;

public final class GuiThemes {
   public static final GuiTheme DEFAULT_BUTTON = new GuiTheme(-1, -1, -5592406, -536870912, -1879048192, 1073741824);
   public static final int FLAT_BUTTON_FOCUS_BORDER = -2147418130;
   public static final int OPTION_FOCUS_BORDER = -1;
   public static final int SELECTED_UNDERLINE = -7019309;

   public static GuiTheme fromSodium(ColorTheme theme) {
      return new GuiTheme(theme.theme, theme.themeLighter, theme.themeDarker, DEFAULT_BUTTON.bgHighlight, DEFAULT_BUTTON.bgDefault, DEFAULT_BUTTON.bgInactive);
   }
}
