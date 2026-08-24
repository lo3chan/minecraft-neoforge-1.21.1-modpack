package net.bettercombat.client.animation;

import java.util.List;
import net.bettercombat.api.fx.ParticlePlacement;
import net.bettercombat.api.fx.TrailAppearance;
import net.bettercombat.logic.AnimatedHand;

public interface PlayerAttackAnimatable {
   void updateAnimationsOnTick();

   void playAttackAnimation(String var1, AnimatedHand var2, float var3, float var4);

   void playAttackParticles(boolean var1, float var2, int var3, List<ParticlePlacement> var4, TrailAppearance var5);

   void stopAttackAnimation(float var1);
}
