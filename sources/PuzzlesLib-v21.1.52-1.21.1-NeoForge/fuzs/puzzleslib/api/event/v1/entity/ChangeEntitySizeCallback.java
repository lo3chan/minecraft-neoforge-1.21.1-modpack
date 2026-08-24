package fuzs.puzzleslib.api.event.v1.entity;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;

@Deprecated
@FunctionalInterface
public interface ChangeEntitySizeCallback {
   EventInvoker<ChangeEntitySizeCallback> EVENT = EventInvoker.lookup(ChangeEntitySizeCallback.class);

   EventResultHolder<EntityDimensions> onChangeEntitySize(Entity var1, Pose var2, EntityDimensions var3);
}
