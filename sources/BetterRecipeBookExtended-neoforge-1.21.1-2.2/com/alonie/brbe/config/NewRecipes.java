package com.alonie.brbe.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip;

@me.shedaniel.autoconfig.annotation.Config(
   name = "newRecipes"
)
public class NewRecipes implements ConfigData {
   @Tooltip
   public boolean unlockAll = true;
   @Tooltip
   public boolean enableBounce = false;
}
