package software.bernie.geckolib.animation.keyframe;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Objects;
import software.bernie.geckolib.animation.EasingType;
import software.bernie.geckolib.loading.math.MathValue;

public record Keyframe<T extends MathValue>(double length, T startValue, T endValue, EasingType easingType, List<T> easingArgs) {
   public Keyframe(double length, T startValue, T endValue) {
      this(length, startValue, endValue, EasingType.LINEAR);
   }

   public Keyframe(double length, T startValue, T endValue, EasingType easingType) {
      this(length, startValue, endValue, easingType, new ObjectArrayList(0));
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.length, this.startValue, this.endValue, this.easingType, this.easingArgs);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return obj != null && this.getClass() == obj.getClass() ? this.hashCode() == obj.hashCode() : false;
      }
   }
}
