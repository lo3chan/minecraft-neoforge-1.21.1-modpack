package software.bernie.geckolib.animatable.stateless;

import org.jetbrains.annotations.ApiStatus.Internal;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;

public sealed interface StatelessAnimatable permits StatelessGeoEntity, StatelessGeoBlockEntity, StatelessGeoSingletonAnimatable, StatelessGeoObject {
   default void playAnimation(String animation) {
      this.playAnimation(RawAnimation.begin().thenPlay(animation));
   }

   default void playLoopingAnimation(String animation) {
      this.playAnimation(RawAnimation.begin().thenLoop(animation));
   }

   default void playAndHoldAnimation(String animation) {
      this.playAnimation(RawAnimation.begin().thenPlayAndHold(animation));
   }

   default void stopAnimation(RawAnimation animation) {
      this.stopAnimation(
         animation.getStageCount() == 1 ? ((RawAnimation.Stage)animation.getAnimationStages().getFirst()).animationName() : animation.toString()
      );
   }

   void playAnimation(RawAnimation var1);

   void stopAnimation(String var1);

   @Internal
   default void handleClientAnimationPlay(GeoAnimatable animatable, long animatableId, RawAnimation animation) {
      AnimatableManager<GeoAnimatable> animatableManager = animatable.getAnimatableInstanceCache().getManagerForId(animatableId);
      if (animatableManager != null) {
         String animKey = animation.getStageCount() == 1
            ? ((RawAnimation.Stage)animation.getAnimationStages().getFirst()).animationName()
            : animation.toString();
         AnimationController<?> controller = animatableManager.getAnimationControllers()
            .computeIfAbsent(animKey, anim -> new StatelessAnimationController(animatable, anim));
         if (controller instanceof StatelessAnimationController statelessController) {
            statelessController.setCurrentAnimation(animation);
         }
      }
   }

   @Internal
   default void handleClientAnimationStop(GeoAnimatable animatable, long animatableId, String animName) {
      AnimatableManager<GeoAnimatable> animatableManager = animatable.getAnimatableInstanceCache().getManagerForId(animatableId);
      if (animatableManager != null) {
         if (animatableManager.getAnimationControllers().get(animName) instanceof StatelessAnimationController statelessController) {
            statelessController.setCurrentAnimation(null);
         }
      }
   }
}
