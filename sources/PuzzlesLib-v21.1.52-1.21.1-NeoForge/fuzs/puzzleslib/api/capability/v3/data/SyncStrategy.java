package fuzs.puzzleslib.api.capability.v3.data;

import fuzs.puzzleslib.api.network.v3.PlayerSet;
import net.minecraft.world.entity.Entity;

public enum SyncStrategy {
   MANUAL {
      @Override
      public PlayerSet getPlayerSet(Entity entity) {
         return PlayerSet.ofNone();
      }
   },
   TRACKING {
      @Override
      public PlayerSet getPlayerSet(Entity entity) {
         return PlayerSet.nearEntity(entity);
      }
   },
   PLAYER {
      @Override
      public PlayerSet getPlayerSet(Entity entity) {
         return PlayerSet.ofEntity(entity);
      }
   };

   public abstract PlayerSet getPlayerSet(Entity var1);
}
