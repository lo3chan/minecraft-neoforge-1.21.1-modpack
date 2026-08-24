package software.bernie.geckolib.loading.object;

import java.util.Map;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.Animation;

public record BakedAnimations(Map<String, Animation> animations) {
   @Nullable
   public Animation getAnimation(String name) {
      return this.animations.get(name);
   }
}
