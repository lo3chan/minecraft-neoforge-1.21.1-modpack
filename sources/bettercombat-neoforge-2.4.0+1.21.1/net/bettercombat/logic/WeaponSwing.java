package net.bettercombat.logic;

import net.bettercombat.api.AttackHand;

public record WeaponSwing(AttackHand attackHand, int startedAt, int upswingTicks, float duration) {
   public int ticksLeft(int time) {
      return this.startedAt + this.durationTicks() - time;
   }

   public int upswingTicksLeft(int time) {
      return this.startedAt + this.upswingTicks - time;
   }

   public int durationTicks() {
      return Math.round(this.duration);
   }

   public boolean isValid(int time) {
      return time >= this.startedAt && time <= this.startedAt + this.durationTicks() + 1;
   }
}
