package net.mcreator.borninchaosv.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BornInChaosV1ModPotions {
   public static final DeferredRegister<Potion> REGISTRY = DeferredRegister.create(Registries.POTION, "born_in_chaos_v1");
   public static final DeferredHolder<Potion, Potion> POTION_OF_MAGICAL_DEPLETION = REGISTRY.register(
      "potion_of_magical_depletion",
      () -> new Potion(new MobEffectInstance[]{new MobEffectInstance(BornInChaosV1ModMobEffects.MAGIC_DEPLETION, 4200, 1, false, true)})
   );
   public static final DeferredHolder<Potion, Potion> INTOXICATION_POTION = REGISTRY.register(
      "intoxication_potion", () -> new Potion(new MobEffectInstance[]{new MobEffectInstance(BornInChaosV1ModMobEffects.INTOXICATION, 800, 0, false, true)})
   );
   public static final DeferredHolder<Potion, Potion> STIMULATING_POTION = REGISTRY.register(
      "stimulating_potion", () -> new Potion(new MobEffectInstance[]{new MobEffectInstance(BornInChaosV1ModMobEffects.STIMULATION, 2400, 0, false, true)})
   );
   public static final DeferredHolder<Potion, Potion> POTION_OF_LIVING_COCOON = REGISTRY.register(
      "potion_of_living_cocoon",
      () -> new Potion(new MobEffectInstance[]{new MobEffectInstance(BornInChaosV1ModMobEffects.LIVING_COCOON_PLAYER_SIDE, 800, 0, false, true)})
   );
}
