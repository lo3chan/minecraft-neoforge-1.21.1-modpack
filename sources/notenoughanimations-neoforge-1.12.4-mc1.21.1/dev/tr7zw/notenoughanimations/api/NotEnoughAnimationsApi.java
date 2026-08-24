package dev.tr7zw.notenoughanimations.api;

import dev.tr7zw.notenoughanimations.NEAnimationsMod;

public class NotEnoughAnimationsApi {
   public static void registerAnimation(BasicAnimation animation) {
      NEAnimationsMod.INSTANCE.animationProvider.addAnimation(animation);
   }

   public static void refreshEnabledAnimations() {
      NEAnimationsMod.INSTANCE.animationProvider.refreshEnabledAnimations();
   }
}
