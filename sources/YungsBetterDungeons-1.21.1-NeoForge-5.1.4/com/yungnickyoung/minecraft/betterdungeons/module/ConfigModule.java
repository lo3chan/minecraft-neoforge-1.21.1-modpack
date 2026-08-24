package com.yungnickyoung.minecraft.betterdungeons.module;

public class ConfigModule {
   public final ConfigModule.General general = new ConfigModule.General();
   public final ConfigModule.ZombieDungeon zombieDungeons = new ConfigModule.ZombieDungeon();
   public final ConfigModule.SmallDungeon smallDungeons = new ConfigModule.SmallDungeon();
   public final ConfigModule.SmallNetherDungeon smallNetherDungeons = new ConfigModule.SmallNetherDungeon();

   public static class General {
      public boolean enableHeads = true;
      public boolean enableNetherBlocks = true;
      public boolean removeVanillaDungeons = true;
   }

   public static class SmallDungeon {
      public int bannerMaxCount = 2;
      public int chestMinCount = 1;
      public int chestMaxCount = 2;
      public boolean enableOreProps = true;
   }

   public static class SmallNetherDungeon {
      public boolean enabled = false;
      public boolean witherSkeletonsDropWitherSkulls = true;
      public boolean blazesDropBlazeRods = true;
      public int bannerMaxCount = 2;
   }

   public static class ZombieDungeon {
      public int zombieDungeonMaxSurfaceStaircaseLength = 20;
   }
}
