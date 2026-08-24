package fuzs.puzzleslib.api.client.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.HitResult;

public final class InteractionInputEvents {
   public static final EventInvoker<InteractionInputEvents.Attack> ATTACK = EventInvoker.lookup(InteractionInputEvents.Attack.class);
   public static final EventInvoker<InteractionInputEvents.Use> USE = EventInvoker.lookup(InteractionInputEvents.Use.class);
   public static final EventInvoker<InteractionInputEvents.Pick> PICK = EventInvoker.lookup(InteractionInputEvents.Pick.class);

   private InteractionInputEvents() {
   }

   @FunctionalInterface
   public interface Attack {
      EventResult onAttackInteraction(Minecraft var1, LocalPlayer var2, HitResult var3);
   }

   @FunctionalInterface
   public interface Pick {
      EventResult onPickInteraction(Minecraft var1, LocalPlayer var2, HitResult var3);
   }

   @FunctionalInterface
   public interface Use {
      EventResult onUseInteraction(Minecraft var1, LocalPlayer var2, InteractionHand var3, HitResult var4);
   }
}
