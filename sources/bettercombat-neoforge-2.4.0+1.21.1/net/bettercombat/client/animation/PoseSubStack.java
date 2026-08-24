package net.bettercombat.client.animation;

import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractModifier;
import dev.kosmx.playerAnim.api.layered.modifier.MirrorModifier;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation.AnimationBuilder;
import dev.kosmx.playerAnim.core.util.Ease;
import java.util.function.Consumer;
import org.jetbrains.annotations.Nullable;

public class PoseSubStack {
   public final MirrorModifier mirror = new MirrorModifier();
   public final ModifierLayer base = new ModifierLayer(null, new AbstractModifier[0]);
   public boolean lastAnimationUsesBodyChannel = false;
   private final boolean isMainHand;
   private final boolean isBodyChannel;
   private PoseData lastPose;
   public Consumer<AnimationBuilder> configure;

   public PoseSubStack(AbstractModifier adjustmentModifier, boolean isBodyChannel, boolean isMainHand) {
      this.isMainHand = isMainHand;
      this.isBodyChannel = isBodyChannel;
      if (adjustmentModifier != null) {
         this.base.addModifier(adjustmentModifier, 0);
      }

      this.base.addModifier(this.mirror, 0);
   }

   public void setPose(@Nullable KeyframeAnimation pose, boolean isLeftHanded) {
      boolean mirror = isLeftHanded;
      if (!this.isMainHand) {
         mirror = !isLeftHanded;
      }

      PoseData newPoseData = PoseData.from(pose, mirror);
      if (this.lastPose == null || !newPoseData.equals(this.lastPose)) {
         if (pose == null) {
            this.base.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(5, Ease.INOUTSINE), null);
            this.lastAnimationUsesBodyChannel = false;
         } else {
            AnimationBuilder copy = pose.mutableCopy();
            if (this.configure != null) {
               this.configure.accept(copy);
            }

            if (this.isBodyChannel) {
               StateCollectionHelper.configure(copy.rightItem, false, false);
               StateCollectionHelper.configure(copy.leftItem, false, false);
            } else {
               StateCollectionHelper.configure(copy.head, false, false);
               StateCollectionHelper.configure(copy.torso, false, false);
               StateCollectionHelper.configure(copy.body, false, false);
               StateCollectionHelper.configure(copy.rightArm, false, false);
               StateCollectionHelper.configure(copy.leftArm, false, false);
               StateCollectionHelper.configure(copy.rightLeg, false, false);
               StateCollectionHelper.configure(copy.leftLeg, false, false);
            }

            KeyframeAnimation animation = copy.build();
            this.mirror.setEnabled(mirror);
            KeyframeAnimationPlayer player = new KeyframeAnimationPlayer(animation, 0);
            player.setFirstPersonMode(FirstPersonMode.NONE);
            this.base.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(5, Ease.INOUTSINE), player);
            this.lastAnimationUsesBodyChannel = copy.body.isEnabled();
         }

         this.lastPose = newPoseData;
      }
   }
}
