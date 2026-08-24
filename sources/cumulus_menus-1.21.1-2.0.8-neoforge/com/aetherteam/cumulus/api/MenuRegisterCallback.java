package com.aetherteam.cumulus.api;

import net.minecraft.resources.ResourceLocation;

@FunctionalInterface
public interface MenuRegisterCallback {
   void registerMenu(ResourceLocation var1, Menu var2);
}
