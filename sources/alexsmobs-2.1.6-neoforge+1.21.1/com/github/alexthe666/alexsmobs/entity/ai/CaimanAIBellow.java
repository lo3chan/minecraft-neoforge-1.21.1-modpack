package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityCaiman;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import java.util.EnumSet;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.phys.Vec3;

public class CaimanAIBellow extends Goal {
   private final EntityCaiman caiman;
   private int bellowTime = 0;

   public CaimanAIBellow(EntityCaiman caiman) {
      this.caiman = caiman;
      this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
   }

   public boolean canUse() {
      return this.caiman.getTarget() == null && this.caiman.bellowCooldown <= 0 && this.caiman.isInWaterOrBubble() && !this.caiman.shouldFollow();
   }

   public boolean canContinueToUse() {
      return super.canContinueToUse() && this.bellowTime < 60;
   }

   public void stop() {
      this.bellowTime = 0;
      this.caiman.bellowCooldown = 1000 + this.caiman.getRandom().nextInt(1000);
      this.caiman.setBellowing(false);
   }

   public void tick() {
      if (this.caiman.isInWaterOrBubble()) {
         double d1 = AMPlatform.fluidHeightWater(this.caiman);
         this.caiman.getNavigation().stop();
         if (d1 > 0.30000001192092896) {
            double d2 = Math.pow(d1 - 0.30000001192092896, 2.0);
            this.caiman
               .setDeltaMovement(
                  new Vec3(this.caiman.getDeltaMovement().x, Math.min(d2 * 0.07999999821186066, 0.03999999910593033), this.caiman.getDeltaMovement().z)
               );
         } else {
            this.caiman.setDeltaMovement(new Vec3(this.caiman.getDeltaMovement().x, -0.019999999552965164, this.caiman.getDeltaMovement().z));
         }

         if (d1 > 0.1899999976158142 && d1 < 0.5) {
            this.bellowTime++;
            this.caiman.playSound(AMSoundRegistry.CAIMAN_SPLASH.get(), 1.0F, this.caiman.getVoicePitch());
            this.caiman.setBellowing(true);
         }
      }
   }
}
