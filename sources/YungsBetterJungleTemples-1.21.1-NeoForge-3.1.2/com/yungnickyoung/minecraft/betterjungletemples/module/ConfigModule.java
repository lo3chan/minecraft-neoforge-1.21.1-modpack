package com.yungnickyoung.minecraft.betterjungletemples.module;

public class ConfigModule {
   public ConfigModule.General general = new ConfigModule.General();
   public ConfigModule.Compat compat = new ConfigModule.Compat();

   public static class Compat {
   }

   public static class General {
      public boolean disableVanillaJungleTemples = true;
   }
}
