package com.aetherteam.aether.event;

import com.aetherteam.nitrogen.entity.BossRoomTracker;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.event.entity.EntityEvent;

public abstract class BossFightEvent extends EntityEvent {
   private final BossRoomTracker<?> dungeon;

   public BossFightEvent(Entity entity, BossRoomTracker<?> dungeon) {
      super(entity);
      this.dungeon = dungeon;
   }

   public BossRoomTracker<?> getDungeon() {
      return this.dungeon;
   }

   public static class AddPlayer extends BossFightEvent {
      private final ServerPlayer player;

      public AddPlayer(Entity entity, BossRoomTracker<?> dungeon, ServerPlayer player) {
         super(entity, dungeon);
         this.player = player;
      }

      public ServerPlayer getPlayer() {
         return this.player;
      }
   }

   public static class RemovePlayer extends BossFightEvent {
      private final ServerPlayer player;

      public RemovePlayer(Entity entity, BossRoomTracker<?> dungeon, ServerPlayer player) {
         super(entity, dungeon);
         this.player = player;
      }

      public ServerPlayer getPlayer() {
         return this.player;
      }
   }

   public static class Start extends BossFightEvent {
      public Start(Entity entity, BossRoomTracker<?> dungeon) {
         super(entity, dungeon);
      }
   }

   public static class Stop extends BossFightEvent {
      public Stop(Entity entity, BossRoomTracker<?> dungeon) {
         super(entity, dungeon);
      }
   }
}
