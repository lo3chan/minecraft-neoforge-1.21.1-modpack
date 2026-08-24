package snownee.jade.track;

import snownee.jade.util.SmoothChasingValue;

public class ProgressTrackInfo extends TrackInfo {
   private final boolean canDecrease;
   private float width;
   private int ticksSinceWidthChanged;
   private float ticksSinceValueChanged;
   private final SmoothChasingValue smoothProgress = new SmoothChasingValue();
   private float progress;
   private float expectedWidth;

   public ProgressTrackInfo(boolean canDecrease, float progress, float width) {
      this.canDecrease = canDecrease;
      this.progress = progress;
      this.width = width;
      this.smoothProgress.start(progress);
   }

   public float getWidth() {
      return this.width;
   }

   @Override
   public void update(float pTicks) {
      if (this.progress != this.smoothProgress.getTarget() && this.ticksSinceValueChanged > 0.0F) {
         if (this.ticksSinceValueChanged > 10.0F) {
            this.smoothProgress.withSpeed(0.4F);
         } else if (this.canDecrease || this.progress > this.smoothProgress.getTarget()) {
            float spd = Math.abs(this.progress - this.smoothProgress.getTarget()) / this.ticksSinceValueChanged;
            spd = Math.max(0.1F, 4.0F * spd);
            this.smoothProgress.withSpeed(spd);
         }

         this.ticksSinceValueChanged = pTicks;
      } else {
         this.ticksSinceValueChanged += pTicks;
      }

      if (this.canDecrease || !(this.progress < this.smoothProgress.getTarget())) {
         this.smoothProgress.target(this.progress);
      } else if (this.smoothProgress.isMoving()) {
         this.smoothProgress.withSpeed(Math.max(0.5F, this.smoothProgress.getSpeed()));
         if (this.smoothProgress.getTarget() > 0.9F) {
            this.smoothProgress.target(1.0F);
         }
      } else {
         this.smoothProgress.start(this.progress);
      }

      this.smoothProgress.tick(pTicks);
   }

   public void setExpectedWidth(float expectedWidth) {
      this.expectedWidth = expectedWidth;
      if (expectedWidth > this.width) {
         this.width = expectedWidth;
         this.ticksSinceWidthChanged = 0;
      }
   }

   public void setProgress(float progress) {
      this.progress = progress;
   }

   public float getSmoothProgress() {
      return this.smoothProgress.value;
   }

   @Override
   public void tick() {
      if (this.expectedWidth < this.width && ++this.ticksSinceWidthChanged > 10) {
         this.width = this.expectedWidth;
         this.ticksSinceWidthChanged = 0;
      }
   }
}
