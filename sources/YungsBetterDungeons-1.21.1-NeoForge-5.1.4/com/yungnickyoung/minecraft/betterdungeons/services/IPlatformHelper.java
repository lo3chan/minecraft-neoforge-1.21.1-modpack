package com.yungnickyoung.minecraft.betterdungeons.services;

public interface IPlatformHelper {
   String getPlatformName();

   boolean isModLoaded(String var1);

   boolean isDevelopmentEnvironment();
}
