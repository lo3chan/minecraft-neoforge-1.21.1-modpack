package com.yungnickyoung.minecraft.betterfortresses.module;

public class ConfigModule {
   public ConfigModule.General general = new ConfigModule.General();

   public static class General {
      public boolean disableVanillaFortresses = true;
   }
}
