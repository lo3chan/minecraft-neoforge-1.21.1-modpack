package software.bernie.geckolib.animatable.instance;

import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimatableManager;

public class InstancedAnimatableInstanceCache extends AnimatableInstanceCache {
   protected AnimatableManager<?> manager;

   public InstancedAnimatableInstanceCache(GeoAnimatable animatable) {
      super(animatable);
   }

   @Override
   public AnimatableManager<?> getManagerForId(long uniqueId) {
      if (this.manager == null) {
         this.manager = new AnimatableManager(this.animatable);
      }

      return this.manager;
   }
}
