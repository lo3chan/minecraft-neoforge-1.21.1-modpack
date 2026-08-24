package net.mcreator.undeadrevamp.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class UndeadRevamp2ModPotions {
   public static final DeferredRegister<Potion> REGISTRY = DeferredRegister.create(Registries.POTION, "undead_revamp2");
   public static final DeferredHolder<Potion, Potion> SCENTOFMINOS = REGISTRY.register(
      "scentofminos", () -> new Potion(new MobEffectInstance[]{new MobEffectInstance(UndeadRevamp2ModMobEffects.MOONFLOWERSSCENT, 3500, 0, false, true)})
   );
   public static final DeferredHolder<Potion, Potion> SCENTOFQUEENBEE = REGISTRY.register(
      "scentofqueenbee", () -> new Potion(new MobEffectInstance[]{new MobEffectInstance(UndeadRevamp2ModMobEffects.HONEYSPLAT, 3500, 0, false, true)})
   );
   public static final DeferredHolder<Potion, Potion> DROWSINESS = REGISTRY.register(
      "drowsiness", () -> new Potion(new MobEffectInstance[]{new MobEffectInstance(UndeadRevamp2ModMobEffects.SLEEPWALKING, 800, 0, false, true)})
   );
}
