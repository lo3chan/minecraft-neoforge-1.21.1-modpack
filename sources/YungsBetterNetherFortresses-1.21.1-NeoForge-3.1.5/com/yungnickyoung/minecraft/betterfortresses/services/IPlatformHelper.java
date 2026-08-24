package com.yungnickyoung.minecraft.betterfortresses.services;

public interface IPlatformHelper {
   String getPlatformName();

   boolean isModLoaded(String var1);

   boolean isDevelopmentEnvironment();
}
