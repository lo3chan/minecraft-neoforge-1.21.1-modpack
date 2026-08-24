package com.yungnickyoung.minecraft.betterstrongholds.module;

public class ConfigModule {
   public ConfigModule.General general = new ConfigModule.General();

   public static class General {
      public boolean enableStructureRuin = false;
      public float filledPortalFrameChance = 0.1F;
   }
}
