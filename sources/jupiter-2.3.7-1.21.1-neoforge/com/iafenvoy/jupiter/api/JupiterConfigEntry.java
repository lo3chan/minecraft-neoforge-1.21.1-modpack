package com.iafenvoy.jupiter.api;

import com.iafenvoy.jupiter.ConfigManager;
import net.minecraft.resources.ResourceLocation;

public interface JupiterConfigEntry {
   ResourceLocation getId();

   default void initializeCommonConfig(ConfigManager manager) {
   }

   default void initializeClientConfig(ConfigManager manager) {
   }
}
