package fuzs.puzzleslib.api.event.v1.entity.living;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

@FunctionalInterface
public interface PickProjectileCallback {
   EventInvoker<PickProjectileCallback> EVENT = EventInvoker.lookup(PickProjectileCallback.class);

   void onPickProjectile(LivingEntity var1, ItemStack var2, MutableValue<ItemStack> var3);
}
