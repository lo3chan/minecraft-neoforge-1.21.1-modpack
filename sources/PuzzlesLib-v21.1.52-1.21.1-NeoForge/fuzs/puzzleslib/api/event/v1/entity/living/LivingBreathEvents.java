package fuzs.puzzleslib.api.event.v1.entity.living;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedInt;
import net.minecraft.world.entity.LivingEntity;

@Deprecated(
   forRemoval = true
)
public final class LivingBreathEvents {
   public static final EventInvoker<LivingBreathEvents.Breathe> BREATHE = EventInvoker.lookup(LivingBreathEvents.Breathe.class);
   public static final EventInvoker<LivingBreathEvents.Drown> DROWN = EventInvoker.lookup(LivingBreathEvents.Drown.class);

   private LivingBreathEvents() {
   }

   @FunctionalInterface
   public interface Breathe {
      EventResult onLivingBreathe(LivingEntity var1, DefaultedInt var2, boolean var3, boolean var4);
   }

   @FunctionalInterface
   public interface Drown {
      EventResult onLivingDrown(LivingEntity var1, int var2, boolean var3);
   }
}
