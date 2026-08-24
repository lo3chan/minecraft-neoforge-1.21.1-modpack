package software.bernie.geckolib.animation.keyframe.event;

import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.keyframe.event.data.CustomInstructionKeyframeData;

public class CustomInstructionKeyframeEvent<T extends GeoAnimatable> extends KeyFrameEvent<T, CustomInstructionKeyframeData> {
   public CustomInstructionKeyframeEvent(
      T entity, double animationTick, AnimationController<T> controller, CustomInstructionKeyframeData customInstructionKeyframeData
   ) {
      super(entity, animationTick, controller, customInstructionKeyframeData);
   }

   public CustomInstructionKeyframeData getKeyframeData() {
      return (CustomInstructionKeyframeData)super.getKeyframeData();
   }
}
