package com.yungnickyoung.minecraft.betteroceanmonuments.services;

public interface IPlatformHelper {
   String getPlatformName();

   boolean isModLoaded(String var1);

   boolean isDevelopmentEnvironment();
}
