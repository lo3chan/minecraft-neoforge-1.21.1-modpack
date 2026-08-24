package fuzs.puzzleslib.api.event.v1.entity;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.data.MutableBoolean;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

@FunctionalInterface
public interface EntityDamageImmunityCallback {
   EventInvoker<EntityDamageImmunityCallback> EVENT = EventInvoker.lookup(EntityDamageImmunityCallback.class);

   void onEntityDamageImmunity(Entity var1, DamageSource var2, MutableBoolean var3);
}
