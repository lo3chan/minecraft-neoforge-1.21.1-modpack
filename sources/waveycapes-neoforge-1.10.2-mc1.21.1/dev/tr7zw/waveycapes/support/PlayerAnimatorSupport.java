package dev.tr7zw.waveycapes.support;

import dev.tr7zw.waveycapes.versionless.util.Vector3;
import net.minecraft.world.entity.LivingEntity;

public class PlayerAnimatorSupport implements AnimationSupport {
   @Override
   public Vector3 applyAnimationChanges(LivingEntity entity, float delta, Vector3 cur) {
      return cur;
   }
}
