package com.seibel.distanthorizons.fabric.wrappers.config;

import com.seibel.distanthorizons.common.wrappers.gui.GetConfigScreen_fabric;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {
   public ConfigScreenFactory<?> getModConfigScreenFactory() {
      return parent -> GetConfigScreen_fabric.getScreen(parent);
   }
}
