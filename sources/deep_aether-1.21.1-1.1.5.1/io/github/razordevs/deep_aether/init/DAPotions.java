package io.github.razordevs.deep_aether.init;

import com.aetherteam.aether.effect.AetherEffects;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DAPotions {
   public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(BuiltInRegistries.POTION, "deep_aether");
   public static final DeferredHolder<Potion, Potion> REMEDY_POTION = POTIONS.register(
      "remedy_potion", () -> new Potion(new MobEffectInstance[]{new MobEffectInstance(AetherEffects.REMEDY, 200, 0)})
   );
}
