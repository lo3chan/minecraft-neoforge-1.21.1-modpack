package fuzs.puzzleslib.api.event.v1.entity.living;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.world.entity.LivingEntity;

@FunctionalInterface
public interface LivingConversionCallback {
   EventInvoker<LivingConversionCallback> EVENT = EventInvoker.lookup(LivingConversionCallback.class);

   void onLivingConversion(LivingEntity var1, LivingEntity var2);
}
