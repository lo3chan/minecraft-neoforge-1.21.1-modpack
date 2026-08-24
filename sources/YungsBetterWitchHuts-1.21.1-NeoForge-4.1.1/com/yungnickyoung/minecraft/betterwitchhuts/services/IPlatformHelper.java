package com.yungnickyoung.minecraft.betterwitchhuts.services;

public interface IPlatformHelper {
   String getPlatformName();

   boolean isModLoaded(String var1);

   boolean isDevelopmentEnvironment();
}
