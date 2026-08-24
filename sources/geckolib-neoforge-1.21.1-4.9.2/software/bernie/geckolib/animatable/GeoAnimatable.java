package software.bernie.geckolib.animatable;

import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;

public interface GeoAnimatable {
   void registerControllers(AnimatableManager.ControllerRegistrar var1);

   AnimatableInstanceCache getAnimatableInstanceCache();

   default double getBoneResetTime() {
      return 5.0;
   }

   default boolean shouldPlayAnimsWhileGamePaused() {
      return false;
   }

   double getTick(Object var1);

   default AnimatableInstanceCache animatableCacheOverride() {
      return null;
   }
}
