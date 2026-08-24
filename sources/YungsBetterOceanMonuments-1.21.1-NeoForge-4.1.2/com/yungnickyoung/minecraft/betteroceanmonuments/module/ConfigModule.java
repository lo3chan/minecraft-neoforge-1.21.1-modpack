package com.yungnickyoung.minecraft.betteroceanmonuments.module;

public class ConfigModule {
   public ConfigModule.General general = new ConfigModule.General();

   public static class General {
      public boolean disableVanillaMonuments = true;
   }
}
