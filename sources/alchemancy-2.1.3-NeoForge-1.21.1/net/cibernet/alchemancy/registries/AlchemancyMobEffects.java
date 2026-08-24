package net.cibernet.alchemancy.registries;

import net.cibernet.alchemancy.mobEffects.InfernoMobEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AlchemancyMobEffects {
   public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(Registries.MOB_EFFECT, "alchemancy");
   public static final DeferredHolder<MobEffect, InfernoMobEffect> INFERNO = REGISTRY.register("inferno", InfernoMobEffect::new);
}
