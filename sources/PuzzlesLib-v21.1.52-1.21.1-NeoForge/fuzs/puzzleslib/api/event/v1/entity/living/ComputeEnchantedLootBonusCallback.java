package fuzs.puzzleslib.api.event.v1.entity.living;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ComputeEnchantedLootBonusCallback {
   EventInvoker<ComputeEnchantedLootBonusCallback> EVENT = EventInvoker.lookup(ComputeEnchantedLootBonusCallback.class);

   void onComputeEnchantedLootBonus(LivingEntity var1, @Nullable DamageSource var2, Holder<Enchantment> var3, MutableInt var4);
}
