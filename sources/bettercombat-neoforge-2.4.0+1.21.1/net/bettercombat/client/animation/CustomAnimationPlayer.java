package net.bettercombat.client.animation;

import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import org.jetbrains.annotations.NotNull;

public class CustomAnimationPlayer extends KeyframeAnimationPlayer {
   public CustomAnimationPlayer(KeyframeAnimation emote, int t, boolean mutable) {
      super(emote, t, mutable);
   }

   public CustomAnimationPlayer(KeyframeAnimation emote, int t) {
      super(emote, t, false);
   }

   public boolean isWindingDown(float tickDelta) {
      int windDownStart = this.getData().endTick + (this.getData().stopTick - this.getData().endTick) / 4;
      return this.getTick() + tickDelta > windDownStart + 0.5F;
   }

   @NotNull
   public FirstPersonMode getFirstPersonMode(float tickDelta) {
      return this.isWindingDown(tickDelta) ? FirstPersonMode.NONE : super.getFirstPersonMode(tickDelta);
   }
}
