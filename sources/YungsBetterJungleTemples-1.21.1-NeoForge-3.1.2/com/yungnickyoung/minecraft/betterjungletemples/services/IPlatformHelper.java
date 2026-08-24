package com.yungnickyoung.minecraft.betterjungletemples.services;

public interface IPlatformHelper {
   String getPlatformName();

   boolean isModLoaded(String var1);

   boolean isDevelopmentEnvironment();
}
