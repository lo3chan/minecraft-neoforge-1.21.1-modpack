package software.bernie.geckolib.animation.keyframe;

import software.bernie.geckolib.loading.math.MathValue;

public record BoneAnimation(
   String boneName,
   KeyframeStack<Keyframe<MathValue>> rotationKeyFrames,
   KeyframeStack<Keyframe<MathValue>> positionKeyFrames,
   KeyframeStack<Keyframe<MathValue>> scaleKeyFrames
) {
}
