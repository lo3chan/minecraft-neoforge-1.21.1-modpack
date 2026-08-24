package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntitySeagull;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.phys.Vec3;

public class SeagullAIRevealTreasure extends Goal {
   private final EntitySeagull seagull;
   private BlockPos sitPos;

   public SeagullAIRevealTreasure(EntitySeagull entitySeagull) {
      this.seagull = entitySeagull;
      this.setFlags(EnumSet.of(Flag.MOVE, Flag.TARGET));
   }

   public boolean canUse() {
      return this.seagull.getTreasurePos() != null && this.seagull.treasureSitTime > 0;
   }

   public void start() {
      this.seagull.aiItemFlag = true;
      this.sitPos = this.seagull.getSeagullGround(this.seagull.getTreasurePos());
   }

   public void stop() {
      this.sitPos = null;
      this.seagull.setSitting(false);
      this.seagull.aiItemFlag = false;
   }

   public void tick() {
      if (this.sitPos != null) {
         if (this.seagull.distanceToSqr(new Vec3(this.sitPos.getX() + 0.5F, this.seagull.getY(), this.sitPos.getZ() + 0.5F)) > 2.5) {
            this.seagull.getMoveControl().setWantedPosition(this.sitPos.getX() + 0.5F, this.sitPos.getY() + 2, this.sitPos.getZ() + 0.5F, 1.0);
            if (!this.seagull.onGround()) {
               this.seagull.setFlying(true);
            }
         } else {
            Vec3 vec = Vec3.upFromBottomCenterOf(this.sitPos, 1.0);
            if (vec.subtract(this.seagull.position()).length() > 0.03999999910593033) {
               this.seagull.setDeltaMovement(vec.subtract(this.seagull.position()).scale(0.20000000298023224));
            }

            this.seagull.eatItem();
            this.seagull.treasureSitTime = Math.min(this.seagull.treasureSitTime, 100);
            this.seagull.setFlying(false);
            this.seagull.setSitting(true);
         }
      }
   }
}
