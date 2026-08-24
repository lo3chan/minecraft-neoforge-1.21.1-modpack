package com.aetherteam.aether.entity.ai.goal;

import com.aetherteam.aether.entity.NpcDialogue;
import java.util.EnumSet;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.player.Player;

public class NpcDialogueGoal<T extends Mob & NpcDialogue> extends LookAtPlayerGoal {
   private final T npc;

   public NpcDialogueGoal(T npc) {
      super(npc, Player.class, 8.0F);
      this.npc = npc;
      this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
   }

   public boolean canUse() {
      if (this.npc.getConversingPlayer() != null
         && this.npc.getConversingPlayer().isAlive()
         && !this.npc.hurtMarked
         && this.npc.distanceToSqr(this.npc.getConversingPlayer()) <= 64.0) {
         this.lookAt = this.npc.getConversingPlayer();
         return true;
      } else {
         return false;
      }
   }

   public boolean canContinueToUse() {
      return this.canUse();
   }

   public void start() {
      super.start();
      this.npc.getNavigation().stop();
   }

   public void stop() {
      super.stop();
      this.npc.setConversingPlayer(null);
   }
}
