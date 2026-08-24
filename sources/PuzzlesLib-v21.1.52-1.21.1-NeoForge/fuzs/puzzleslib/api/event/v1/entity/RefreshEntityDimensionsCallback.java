package fuzs.puzzleslib.api.event.v1.entity;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;

@FunctionalInterface
public interface RefreshEntityDimensionsCallback {
   EventInvoker<RefreshEntityDimensionsCallback> EVENT = EventInvoker.lookup(RefreshEntityDimensionsCallback.class);

   EventResultHolder<EntityDimensions> onRefreshEntityDimensions(Entity var1, Pose var2, EntityDimensions var3);
}
