package net.bettercombat.client.animation;

import dev.kosmx.playerAnim.core.data.KeyframeAnimation.StateCollection;

public class StateCollectionHelper {
   public static void configure(StateCollection bodyPart, boolean isRotationEnabled, boolean isOffsetEnabled) {
      bodyPart.pitch.setEnabled(isRotationEnabled);
      bodyPart.roll.setEnabled(isRotationEnabled);
      bodyPart.yaw.setEnabled(isRotationEnabled);
      bodyPart.x.setEnabled(isOffsetEnabled);
      bodyPart.y.setEnabled(isOffsetEnabled);
      bodyPart.z.setEnabled(isOffsetEnabled);
   }
}
