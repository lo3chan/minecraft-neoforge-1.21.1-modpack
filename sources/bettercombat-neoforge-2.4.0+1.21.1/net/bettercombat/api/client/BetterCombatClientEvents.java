package net.bettercombat.api.client;

import java.util.List;
import net.bettercombat.api.AttackHand;
import net.bettercombat.api.event.Publisher;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public class BetterCombatClientEvents {
   public static final Publisher<BetterCombatClientEvents.PlayerAttackStart> ATTACK_START = new Publisher<>();
   public static final Publisher<BetterCombatClientEvents.PlayerAttackHit> ATTACK_HIT = new Publisher<>();

   @FunctionalInterface
   public interface PlayerAttackHit {
      void onPlayerAttackStart(LocalPlayer var1, AttackHand var2, List<Entity> var3, @Nullable Entity var4);
   }

   @FunctionalInterface
   public interface PlayerAttackStart {
      void onPlayerAttackStart(LocalPlayer var1, AttackHand var2);
   }
}
