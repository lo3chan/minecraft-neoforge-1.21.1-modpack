package net.bettercombat.client.animation.modifier;

import dev.kosmx.playerAnim.api.layered.modifier.SpeedModifier;
import java.util.List;

public class TransmissionSpeedModifier extends SpeedModifier {
   private float elapsed = 0.0F;
   public List<TransmissionSpeedModifier.Gear> gears = List.of();

   public void set(float speed, List<TransmissionSpeedModifier.Gear> gears) {
      this.speed = speed;
      this.gears = gears;
      this.elapsed = 0.0F;
   }

   private float elapsed(float delta) {
      return this.elapsed + delta;
   }

   public void tick() {
      super.tick();
      this.elapsed++;
   }

   public void setupAnim(float tickDelta) {
      float time = this.elapsed(tickDelta);

      for (TransmissionSpeedModifier.Gear gear : this.gears) {
         if (time > gear.time) {
            this.speed = gear.speed();
         }
      }

      super.setupAnim(tickDelta);
   }

   public record Gear(float time, float speed) {
   }
}
