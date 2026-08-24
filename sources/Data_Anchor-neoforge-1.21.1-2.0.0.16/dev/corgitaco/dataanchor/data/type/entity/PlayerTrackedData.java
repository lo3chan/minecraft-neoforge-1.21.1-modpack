package dev.corgitaco.dataanchor.data.type.entity;

import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.registry.TrackedDataRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public abstract sealed class PlayerTrackedData extends EntityTrackedData permits ServerPlayerTrackedData, SyncedPlayerTrackedData {
   protected final TrackedDataKey<? extends PlayerTrackedData> trackedDataKey;
   protected final Player player;
   private final boolean persistent;

   public PlayerTrackedData(TrackedDataKey<? extends PlayerTrackedData> trackedDataKey, Player player) {
      this(trackedDataKey, player, false);
   }

   public PlayerTrackedData(TrackedDataKey<? extends PlayerTrackedData> trackedDataKey, Player player, boolean persistent) {
      super(trackedDataKey, player);
      this.trackedDataKey = trackedDataKey;
      this.player = player;
      this.persistent = persistent;
   }

   public void respawn(ServerPlayer oldPlayer, boolean keepEverything) {
      if (this.persistent) {
         TrackedDataRegistries.ENTITY.get(this.trackedDataKey, oldPlayer).ifPresent(data -> this.load(data.save()));
      }
   }

   public void playerJoin() {
   }

   public void playerAddedToWorld() {
   }

   public void addRespawnedPlayer() {
   }

   public Player get() {
      return this.player;
   }
}
