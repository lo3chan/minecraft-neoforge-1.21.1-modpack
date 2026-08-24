package net.cibernet.alchemancy.mobEffects;

import java.util.Objects;
import net.cibernet.alchemancy.registries.AlchemancyMobEffects;
import net.cibernet.alchemancy.registries.AlchemancyParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.EffectCures;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Remove;

@EventBusSubscriber
public class InfernoMobEffect extends MobEffect {
   public InfernoMobEffect() {
      super(MobEffectCategory.HARMFUL, 16746501);
   }

   public ParticleOptions createParticleOptions(MobEffectInstance effect) {
      return (ParticleOptions)AlchemancyParticles.INFERNO.get();
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      if (!entity.fireImmune()) {
         entity.setRemainingFireTicks(entity.getRemainingFireTicks() + 1);
         if (entity.getRemainingFireTicks() == 0) {
            entity.igniteForSeconds(8.0F);
         }
      }

      entity.hurt(entity.damageSources().inFire(), amplifier + 1);
      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   @SubscribeEvent
   private static void onEffectRemoved(Remove event) {
      if (Objects.equals(event.getCure(), EffectCures.MILK) && event.getEffect().equals(AlchemancyMobEffects.INFERNO)) {
         event.setCanceled(true);
      }
   }
}
