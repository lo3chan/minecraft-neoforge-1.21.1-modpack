package com.yungnickyoung.minecraft.betterwitchhuts.module;

public class ConfigModule {
   public ConfigModule.General general = new ConfigModule.General();

   public static class General {
      public boolean disableVanillaWitchHuts = true;
   }
}
