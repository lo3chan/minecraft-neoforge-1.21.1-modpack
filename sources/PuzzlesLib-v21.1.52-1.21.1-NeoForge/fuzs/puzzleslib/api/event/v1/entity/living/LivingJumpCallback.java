package fuzs.puzzleslib.api.event.v1.entity.living;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedDouble;
import net.minecraft.world.entity.LivingEntity;

@FunctionalInterface
public interface LivingJumpCallback {
   EventInvoker<LivingJumpCallback> EVENT = EventInvoker.lookup(LivingJumpCallback.class);

   EventResult onLivingJump(LivingEntity var1, DefaultedDouble var2);
}
