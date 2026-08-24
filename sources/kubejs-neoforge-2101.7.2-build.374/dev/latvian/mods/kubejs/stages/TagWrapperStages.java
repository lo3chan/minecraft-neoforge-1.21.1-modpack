package dev.latvian.mods.kubejs.stages;

import java.util.Collection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public record TagWrapperStages(Player player) implements Stages {
   @Override
   public Player getPlayer() {
      return this.player;
   }

   @Override
   public boolean addNoUpdate(String stage) {
      return this.player.addTag(stage);
   }

   @Override
   public boolean removeNoUpdate(String stage) {
      return this.player.removeTag(stage);
   }

   @Override
   public Collection<String> getAll() {
      return this.player.getTags();
   }

   @Override
   public boolean clear() {
      if (!this.player.getTags().isEmpty()) {
         this.player.getTags().clear();
         this.sync();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void replace(Collection<String> stages) {
      if (!(this.getPlayer() instanceof ServerPlayer) || !this.player.getTags().equals(stages)) {
         this.player.getTags().clear();
         this.player.getTags().addAll(stages);
         this.sync();
      }
   }
}
