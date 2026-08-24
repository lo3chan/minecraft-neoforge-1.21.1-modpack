package net.mcreator.borninchaosv.procedures;

import javax.annotation.Nullable;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber
public class InsectProtectionTrigerProcedure {
   @SubscribeEvent
   public static void onEntityAttacked(LivingIncomingDamageEvent event) {
      if (event.getEntity() != null) {
         execute(event, event.getEntity().level(), event.getEntity(), event.getSource().getEntity());
      }
   }

   public static void execute(LevelAccessor world, Entity entity, Entity sourceentity) {
      execute(null, world, entity, sourceentity);
   }

   private static void execute(@Nullable Event event, LevelAccessor world, Entity entity, Entity sourceentity) {
      if (entity != null && sourceentity != null) {
         if (entity instanceof LivingEntity _livEnt0
            && _livEnt0.hasEffect(BornInChaosV1ModMobEffects.INSECT_PROTECTION)
            && sourceentity.getType().is(EntityTypeTags.ARTHROPOD)) {
            sourceentity.hurt(new DamageSource(world.holderOrThrow(DamageTypes.GENERIC)), 6.0F);
            if (world instanceof ServerLevel _level) {
               _level.sendParticles(
                  (SimpleParticleType)BornInChaosV1ModParticleTypes.DARK_SMOKE.get(),
                  sourceentity.getX(),
                  sourceentity.getY() + 1.0,
                  sourceentity.getZ(),
                  5,
                  0.3,
                  0.2,
                  0.3,
                  0.1
               );
            }

            if (world instanceof Level _level) {
               if (!_level.isClientSide()) {
                  _level.playSound(
                     null,
                     BlockPos.containing(sourceentity.getX(), sourceentity.getY() + 1.0, sourceentity.getZ()),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.fire.extinguish")),
                     SoundSource.NEUTRAL,
                     0.6F,
                     1.2F
                  );
               } else {
                  _level.playLocalSound(
                     sourceentity.getX(),
                     sourceentity.getY() + 1.0,
                     sourceentity.getZ(),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.fire.extinguish")),
                     SoundSource.NEUTRAL,
                     0.6F,
                     1.2F,
                     false
                  );
               }
            }
         }
      }
   }
}
