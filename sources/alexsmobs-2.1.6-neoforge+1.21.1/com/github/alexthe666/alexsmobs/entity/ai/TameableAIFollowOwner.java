package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.IFollower;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;

public class TameableAIFollowOwner extends FollowOwnerGoal {
   private final IFollower follower;
   private final TamableAnimal tameable;

   public TameableAIFollowOwner(TamableAnimal tameable, double speed, float minDist, float maxDist, boolean teleportToLeaves) {
      super(tameable, speed, minDist, maxDist);
      this.follower = (IFollower)tameable;
      this.tameable = tameable;
   }

   public boolean canUse() {
      return super.canUse() && this.follower.shouldFollow() && !this.isInCombat();
   }

   public boolean canContinueToUse() {
      return super.canContinueToUse() && this.follower.shouldFollow() && !this.isInCombat();
   }

   private boolean isInCombat() {
      Entity owner = this.tameable.getOwner();
      return owner == null ? false : this.tameable.distanceTo(owner) < 30.0F && this.tameable.getTarget() != null && this.tameable.getTarget().isAlive();
   }
}
