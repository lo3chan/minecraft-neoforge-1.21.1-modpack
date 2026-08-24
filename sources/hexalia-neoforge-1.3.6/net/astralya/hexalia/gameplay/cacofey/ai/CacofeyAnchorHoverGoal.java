package net.astralya.hexalia.gameplay.cacofey.ai;

import java.util.EnumSet;
import net.astralya.hexalia.entity.custom.CacofeyEntity;
import net.astralya.hexalia.entity.custom.CacofeyMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;

public class CacofeyAnchorHoverGoal extends Goal {
   private static final int HOVER_INTERVAL = 60;
   private static final int HOVER_RADIUS = 5;
   private static final int HOVER_Y_OFFSET = 3;
   private final CacofeyEntity cacofey;
   private int hoverTimer = 0;

   public CacofeyAnchorHoverGoal(CacofeyEntity cacofey) {
      this.cacofey = cacofey;
      this.setFlags(EnumSet.of(Flag.MOVE));
   }

   public boolean canUse() {
      return this.cacofey.isTame() && this.cacofey.getMode() == CacofeyMode.WANDER && this.cacofey.getAnchorPos() != null;
   }

   public boolean canContinueToUse() {
      return this.canUse();
   }

   public void tick() {
      if (++this.hoverTimer >= 60) {
         this.hoverTimer = 0;
         BlockPos anchor = this.cacofey.getAnchorPos();
         double targetX = anchor.getX() + this.cacofey.getRandom().nextIntBetweenInclusive(-5, 5);
         double targetY = anchor.getY() + 3 + this.cacofey.getRandom().nextInt(2);
         double targetZ = anchor.getZ() + this.cacofey.getRandom().nextIntBetweenInclusive(-5, 5);
         this.cacofey.getNavigation().moveTo(targetX, targetY, targetZ, 0.8);
      }
   }

   public void stop() {
      this.cacofey.getNavigation().stop();
      this.hoverTimer = 0;
   }
}
