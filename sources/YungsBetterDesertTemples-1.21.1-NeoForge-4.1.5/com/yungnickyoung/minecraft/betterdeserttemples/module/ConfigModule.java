package com.yungnickyoung.minecraft.betterdeserttemples.module;

public class ConfigModule {
   public ConfigModule.General general = new ConfigModule.General();

   public static class General {
      public boolean disableVanillaPyramids = true;
      public boolean applyMiningFatigue = true;
   }
}
