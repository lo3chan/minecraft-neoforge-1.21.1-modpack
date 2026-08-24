package com.alonie.brbe.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.PrefixText;

@me.shedaniel.autoconfig.annotation.Config(
   name = "instantCraft"
)
public class InstantCraft implements ConfigData {
   @PrefixText
   public boolean showButton = true;
   public boolean enabled = false;
}
