package com.alonie.brbe.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip;

@me.shedaniel.autoconfig.annotation.Config(
   name = "alternativeRecipes"
)
public class AlternativeRecipes implements ConfigData {
   @Tooltip
   public boolean onHover = true;
   public boolean noGrouped = false;
}
