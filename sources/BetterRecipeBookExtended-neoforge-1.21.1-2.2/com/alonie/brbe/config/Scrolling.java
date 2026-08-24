package com.alonie.brbe.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.PrefixText;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip;

@me.shedaniel.autoconfig.annotation.Config(
   name = "scrolling"
)
public class Scrolling implements ConfigData {
   @PrefixText
   public boolean enableScrolling = true;
   @Tooltip
   public boolean scrollAround = false;
}
