package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityKomodoDragon;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.BreedGoal;

public class KomodoDragonAIBreed extends BreedGoal {
   boolean withPartner;
   private final EntityKomodoDragon komodo;
   int selfBreedTime = 0;

   public KomodoDragonAIBreed(EntityKomodoDragon entityKomodoDragon, double v) {
      super(entityKomodoDragon, v);
      this.komodo = entityKomodoDragon;
   }

   public boolean canUse() {
      boolean prev = super.canUse();
      this.withPartner = prev;
      return this.withPartner || this.animal.isInLove();
   }

   public boolean canContinueToUse() {
      return this.withPartner ? super.canContinueToUse() : this.selfBreedTime < 60;
   }

   public void stop() {
      super.stop();
      this.selfBreedTime = 0;
   }

   public void tick() {
      if (this.withPartner) {
         super.tick();
      } else {
         this.animal.getNavigation().stop();
         this.selfBreedTime++;
         if (this.selfBreedTime >= 60) {
            this.spawnParthogenicBaby();
         }
      }
   }

   protected void breed() {
      for (int i = 0; i < 2 + this.animal.getRandom().nextInt(2); i++) {
         this.animal.spawnChildFromBreeding((ServerLevel)this.level, this.partner);
      }

      this.komodo.slaughterCooldown = 200;
   }

   private void spawnParthogenicBaby() {
      for (int i = 0; i < 2 + this.animal.getRandom().nextInt(2); i++) {
         this.animal.spawnChildFromBreeding((ServerLevel)this.level, this.animal);
      }

      this.komodo.slaughterCooldown = 200;
   }
}
