package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityCaiman;
import java.util.EnumSet;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.phys.Vec3;

public class CaimanAIMelee extends Goal {
   private final EntityCaiman caiman;
   private int grabTime = 0;

   public CaimanAIMelee(EntityCaiman caiman) {
      this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      this.caiman = caiman;
   }

   public boolean canUse() {
      LivingEntity target = this.caiman.getTarget();
      return target != null && target.isAlive();
   }

   public void stop() {
      this.caiman.setHeldMobId(-1);
      this.grabTime = 0;
   }

   public void tick() {
      if (this.grabTime < 0) {
         this.grabTime++;
      }

      LivingEntity target = this.caiman.getTarget();
      if (target != null) {
         double bbWidth = (this.caiman.getBbWidth() + target.getBbWidth()) / 2.0;
         double dist = this.caiman.distanceTo(target);
         boolean flag = false;
         if (dist < bbWidth + 2.0 && this.grabTime >= 0) {
            if (this.grabTime % 25 == 0) {
               target.hurt(
                  this.caiman.isTame() ? this.caiman.damageSources().drown() : this.caiman.damageSources().mobAttack(this.caiman),
                  (float)this.caiman.getAttributeValue(Attributes.ATTACK_DAMAGE)
               );
            }

            this.grabTime++;
            Vec3 shakePreyPos = this.caiman.getShakePreyPos();
            Vec3 minus = new Vec3(shakePreyPos.x - target.getX(), 0.0, shakePreyPos.z - target.getZ()).normalize();
            target.setDeltaMovement(
               target.getDeltaMovement().multiply(0.6000000238418579, 0.6000000238418579, 0.6000000238418579).add(minus.scale(0.3499999940395355))
            );
            flag = true;
            if (this.grabTime > this.getGrabDuration()) {
               this.grabTime = -10;
            }
         }

         this.caiman.setHeldMobId(flag ? target.getId() : -1);
         if (dist > bbWidth && !flag) {
            this.caiman.lookAt(Anchor.EYES, target.getEyePosition());
            this.caiman.getNavigation().moveTo(target, 1.2000000476837158);
         }
      }
   }

   private int getGrabDuration() {
      return this.caiman.isTame() && this.caiman.tameAttackFlag ? 300 : 2;
   }
}
