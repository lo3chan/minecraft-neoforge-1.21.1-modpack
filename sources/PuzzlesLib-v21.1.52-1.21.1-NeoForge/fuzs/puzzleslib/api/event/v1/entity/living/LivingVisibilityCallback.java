package fuzs.puzzleslib.api.event.v1.entity.living;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.data.MutableDouble;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

@Deprecated
@FunctionalInterface
public interface LivingVisibilityCallback {
   EventInvoker<LivingVisibilityCallback> EVENT = EventInvoker.lookup(LivingVisibilityCallback.class);

   void onLivingVisibility(LivingEntity var1, @Nullable Entity var2, MutableDouble var3);
}
