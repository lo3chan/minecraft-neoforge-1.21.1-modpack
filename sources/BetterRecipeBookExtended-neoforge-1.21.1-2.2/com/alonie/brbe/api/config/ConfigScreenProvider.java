package com.alonie.brbe.api.config;

import net.minecraft.client.gui.screens.Screen;

@FunctionalInterface
public interface ConfigScreenProvider {
   Screen create(Screen var1);
}
