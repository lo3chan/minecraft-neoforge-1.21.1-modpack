package net.bettercombat.client.animation;

import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractModifier;
import dev.kosmx.playerAnim.api.layered.modifier.AdjustmentModifier;
import dev.kosmx.playerAnim.api.layered.modifier.MirrorModifier;
import net.bettercombat.client.animation.modifier.TransmissionSpeedModifier;

public class AttackAnimationSubStack {
   public final TransmissionSpeedModifier speed = new TransmissionSpeedModifier();
   public final MirrorModifier mirror = new MirrorModifier();
   public final ModifierLayer base = new ModifierLayer(null, new AbstractModifier[0]);
   public final AdjustmentModifier adjustmentModifier;

   public AttackAnimationSubStack(AdjustmentModifier adjustmentModifier) {
      this.adjustmentModifier = adjustmentModifier;
      this.mirror.setEnabled(false);
      this.base.addModifier(adjustmentModifier, 0);
      this.base.addModifier(this.speed, 0);
      this.base.addModifier(this.mirror, 0);
   }
}
