package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityGrizzlyBear;
import com.github.alexthe666.alexsmobs.message.MessageSendVisualFlagFromServer;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMDamageTypes;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import java.util.EnumSet;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.player.Player;

public class GrizzlyBearAIAprilFools extends Goal {
   private final EntityGrizzlyBear bear;
   private Player target;
   private int runDelay = 0;
   private final double maxDistance = 13.0;
   private int powerOutTimer = 0;
   private int musicBoxTimer = 0;
   private int maxMusicBoxTime = 0;
   private int leapTimer = 0;

   public GrizzlyBearAIAprilFools(EntityGrizzlyBear bear) {
      this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      this.bear = bear;
   }

   public boolean canUse() {
      if (!this.bear.isBaby() && AlexsMobs.isAprilFools() && this.runDelay-- <= 0 && this.bear.getRandom().nextInt(30) == 0) {
         this.runDelay = 400 + this.bear.getRandom().nextInt(350);
         Player nearestPlayer = this.bear
            .level()
            .getNearestPlayer(
               this.bear.getX(),
               this.bear.getY(),
               this.bear.getZ(),
               13.0,
               entity -> this.bear.hasLineOfSight(entity)
                  && (!(entity instanceof Player) || !((Player)entity).hasEffect(AMCompat.effect(AMEffectRegistry.POWER_DOWN.get())))
            );
         if (nearestPlayer != null) {
            this.target = nearestPlayer;
            return true;
         }
      }

      return false;
   }

   public boolean canContinueToUse() {
      return this.target != null && this.bear.distanceTo(this.target) < 26.0;
   }

   public void start() {
      this.maxMusicBoxTime = 100 + this.bear.getRandom().nextInt(130);
   }

   public void tick() {
      super.tick();
      double dist = this.bear.distanceTo(this.target);
      this.bear.getLookControl().setLookAt(this.target.getX(), this.target.getEyeY(), this.target.getZ());
      if (dist <= 6.0 && this.bear.hasLineOfSight(this.target)) {
         this.bear.getNavigation().stop();
         if (this.bear.getAprilFoolsFlag() == 5) {
            this.leapTimer++;
            if (this.leapTimer == 7) {
               AlexsMobs.sendMSGToAll(new MessageSendVisualFlagFromServer(this.target.getId(), 87));
            }

            if (this.leapTimer >= 10) {
               this.bear.setAprilFoolsFlag(0);
               if (this.bear.level().getLevelData().isHardcore()) {
                  this.target.hurt(AMDamageTypes.causeFreddyBearDamage(this.bear), this.target.getMaxHealth() - 1.0F);
                  this.target.setHealth(1.0F);
               } else {
                  this.target.hurt(AMDamageTypes.causeFreddyBearDamage(this.bear), this.target.getMaxHealth() + 1000.0F);
               }

               this.stop();
               return;
            }
         } else if (this.bear.getAprilFoolsFlag() < 4) {
            if (this.powerOutTimer == 0) {
               this.target
                  .addEffect(new MobEffectInstance(AMCompat.effect(AMEffectRegistry.POWER_DOWN.get()), 2 * (this.maxMusicBoxTime + 100), 0, false, false, true));
            }

            this.powerOutTimer++;
            if (this.powerOutTimer >= 60) {
               this.bear.setAprilFoolsFlag(4);
               this.powerOutTimer = 0;
            } else {
               this.bear.setAprilFoolsFlag(3);
            }
         } else {
            if (this.musicBoxTimer == 0) {
               this.bear.level().broadcastEntityEvent(this.bear, (byte)67);
            }

            this.musicBoxTimer++;
            if (this.musicBoxTimer >= this.maxMusicBoxTime && this.bear.getAprilFoolsFlag() != 5) {
               this.bear.level().broadcastEntityEvent(this.bear, (byte)68);
               this.bear.setAprilFoolsFlag(5);
               this.bear.gameEvent(AMPlatform.ENTITY_ACTION);
               this.bear.playSound(AMSoundRegistry.APRIL_FOOLS_SCREAM.get(), 3.0F, 1.0F);
               this.musicBoxTimer = 0;
            }
         }

         if (this.bear.getAprilFoolsFlag() < 2) {
            this.bear.setAprilFoolsFlag(2);
         }
      } else {
         this.bear.getNavigation().moveTo(this.target, 1.2000000476837158);
         if (this.bear.getAprilFoolsFlag() < 1) {
            this.bear.setAprilFoolsFlag(1);
         }
      }
   }

   public void stop() {
      this.target = null;
      this.runDelay = 100 + this.bear.getRandom().nextInt(100);
      this.bear.setAprilFoolsFlag(0);
      this.powerOutTimer = 0;
      this.musicBoxTimer = 0;
      this.leapTimer = 0;
   }
}
