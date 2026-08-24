package com.yungnickyoung.minecraft.betterendisland.world;

public interface IBetterDragonFight {
   DragonRespawnStage getDragonRespawnStage();

   void setDragonRespawnStage(DragonRespawnStage var1);

   boolean isFirstExitPortalSpawn();

   void setIsFirstExitPortalSpawn(boolean var1);

   boolean hasDragonEverSpawned();

   void setHasDragonEverSpawned(boolean var1);

   int getNumTimesDragonKilled();

   void setNumTimesDragonKilled(int var1);

   void advanceRespawnStage(DragonRespawnStage var1);

   void doInitialDragonSpawn();

   void tickBellSound();

   void reset(boolean var1);
}
