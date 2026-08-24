package tallestegg.guardvillagers.common.entities.ai.goals;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Spider;

public class AttackEntityDaytimeGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
   public AttackEntityDaytimeGoal(Spider spider, Class<T> classTarget) {
      super(spider, classTarget, true);
   }

   public boolean canUse() {
      float f = this.mob.getLightLevelDependentMagicValue();
      return f >= 0.5F ? false : super.canUse();
   }
}
