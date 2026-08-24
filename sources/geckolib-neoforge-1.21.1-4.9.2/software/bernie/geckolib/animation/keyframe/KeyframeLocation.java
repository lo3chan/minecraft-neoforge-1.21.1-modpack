package software.bernie.geckolib.animation.keyframe;

public record KeyframeLocation<T extends Keyframe<?>>(T keyframe, double startTick) {
}
