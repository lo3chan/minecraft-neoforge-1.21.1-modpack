package software.bernie.geckolib.animation.keyframe.event.data;

import java.util.Objects;

public abstract class KeyFrameData {
   private final double startTick;

   public KeyFrameData(double startTick) {
      this.startTick = startTick;
   }

   public double getStartTick() {
      return this.startTick;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return obj != null && this.getClass() == obj.getClass() ? this.hashCode() == obj.hashCode() : false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(this.startTick);
   }
}
