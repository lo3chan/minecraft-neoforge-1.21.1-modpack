package jeresources.api;

import net.minecraft.world.level.Level;

public interface IJERAPI {
   IMobRegistry getMobRegistry();

   IWorldGenRegistry getWorldGenRegistry();

   IPlantRegistry getPlantRegistry();

   IDungeonRegistry getDungeonRegistry();

   Level getLevel();
}
