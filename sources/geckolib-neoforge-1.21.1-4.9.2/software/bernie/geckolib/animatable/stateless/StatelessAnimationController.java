package software.bernie.geckolib.animatable.stateless;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

public class StatelessAnimationController extends AnimationController<GeoAnimatable> {
   @Nullable
   protected RawAnimation currentAnim = null;

   public StatelessAnimationController(GeoAnimatable animatable, String name) {
      super(animatable, name, state -> PlayState.STOP);
   }

   public void setCurrentAnimation(@Nullable RawAnimation animation) {
      this.currentAnim = animation;
   }

   @Nullable
   public RawAnimation getCurrentAnim() {
      return this.currentAnim;
   }

   @Override
   public AnimationController.AnimationStateHandler<GeoAnimatable> getStateHandler() {
      return this::overrideStateHandler;
   }

   @Internal
   protected PlayState overrideStateHandler(AnimationState<GeoAnimatable> test) {
      return this.getCurrentAnim() == null ? PlayState.STOP : test.setAndContinue(this.getCurrentAnim());
   }
}
