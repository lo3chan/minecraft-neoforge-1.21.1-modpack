package software.bernie.geckolib.animation.keyframe.event;

import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.keyframe.event.data.ParticleKeyframeData;

public class ParticleKeyframeEvent<T extends GeoAnimatable> extends KeyFrameEvent<T, ParticleKeyframeData> {
   public ParticleKeyframeEvent(T animatable, double animationTick, AnimationController<T> controller, ParticleKeyframeData particleKeyFrameData) {
      super(animatable, animationTick, controller, particleKeyFrameData);
   }

   public ParticleKeyframeData getKeyframeData() {
      return (ParticleKeyframeData)super.getKeyframeData();
   }
}
