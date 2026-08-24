package dev.latvian.mods.kubejs.stages;

import dev.latvian.mods.kubejs.net.AddStagePayload;
import dev.latvian.mods.kubejs.net.KubeJSNet;
import dev.latvian.mods.kubejs.net.RemoveStagePayload;
import dev.latvian.mods.kubejs.net.SyncStagesPayload;
import dev.latvian.mods.kubejs.player.StageChangedEvent;
import dev.latvian.mods.kubejs.plugin.builtin.event.PlayerEvents;
import java.util.ArrayList;
import java.util.Collection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public interface Stages {
   Player getPlayer();

   boolean addNoUpdate(String stage);

   boolean removeNoUpdate(String stage);

   Collection<String> getAll();

   default boolean has(String stage) {
      return this.getAll().contains(stage);
   }

   default boolean add(String stage) {
      if (this.addNoUpdate(stage)) {
         if (this.getPlayer() instanceof ServerPlayer player) {
            KubeJSNet.sendToAllPlayers(new AddStagePayload(player.getUUID(), stage));
         }

         if (PlayerEvents.STAGE_ADDED.hasListeners(stage)) {
            PlayerEvents.STAGE_ADDED.post(new StageChangedEvent(this.getPlayer(), this, stage), stage);
         }

         return true;
      } else {
         return false;
      }
   }

   default boolean remove(String stage) {
      if (this.removeNoUpdate(stage)) {
         if (this.getPlayer() instanceof ServerPlayer player) {
            KubeJSNet.sendToAllPlayers(new RemoveStagePayload(player.getUUID(), stage));
         }

         if (PlayerEvents.STAGE_REMOVED.hasListeners(stage)) {
            PlayerEvents.STAGE_REMOVED.post(new StageChangedEvent(this.getPlayer(), this, stage), stage);
         }

         return true;
      } else {
         return false;
      }
   }

   default boolean set(String stage, boolean enabled) {
      return enabled ? this.add(stage) : this.remove(stage);
   }

   default boolean toggle(String stage) {
      return this.set(stage, !this.has(stage));
   }

   default boolean clear() {
      Collection<String> all = this.getAll();
      if (all.isEmpty()) {
         return false;
      } else {
         for (String s : new ArrayList<>(all)) {
            this.remove(s);
         }

         return true;
      }
   }

   default void sync() {
      if (this.getPlayer() instanceof ServerPlayer player) {
         KubeJSNet.safeSendToPlayer(player, new SyncStagesPayload(this.getAll()));
      }
   }

   default void replace(Collection<String> stages) {
      Collection<String> all = this.getAll();

      for (String s : new ArrayList<>(all)) {
         this.removeNoUpdate(s);
      }

      for (String s : stages) {
         this.addNoUpdate(s);
      }

      this.sync();
   }
}
