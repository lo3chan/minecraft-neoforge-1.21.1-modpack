package net.mcreator.borninchaosv.procedures;

import java.util.Comparator;
import net.mcreator.borninchaosv.entity.FelsteedEntity;
import net.mcreator.borninchaosv.entity.LordPumpkinheadEntity;
import net.mcreator.borninchaosv.entity.LordPumpkinheadHeadEntity;
import net.mcreator.borninchaosv.entity.LordPumpkinheadWithoutaHorseEntity;
import net.mcreator.borninchaosv.entity.LordsFelsteedEntity;
import net.mcreator.borninchaosv.entity.MrPumpkinEntity;
import net.mcreator.borninchaosv.entity.MrsPumpkinEntity;
import net.mcreator.borninchaosv.entity.PumpkinBruiserEntity;
import net.mcreator.borninchaosv.entity.PumpkinDunceEntity;
import net.mcreator.borninchaosv.entity.PumpkinheadEntity;
import net.mcreator.borninchaosv.entity.SearedSpiritEntity;
import net.mcreator.borninchaosv.entity.SenorPumpkinEntity;
import net.mcreator.borninchaosv.entity.SirPumpkinheadEntity;
import net.mcreator.borninchaosv.entity.SirPumpkinheadWithoutHorseEntity;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class MrsPumpkinPriObnovlieniiTikaSushchnostiProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (entity.getPersistentData().getDouble("healing") == 0.0) {
            entity.getPersistentData().putDouble("healing", 160.0);
         } else {
            entity.getPersistentData().putDouble("healing", entity.getPersistentData().getDouble("healing") - 1.0);
         }

         if (entity.getPersistentData().getDouble("healing") == 0.0) {
            if (world instanceof Level _level) {
               if (!_level.isClientSide()) {
                  _level.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.glow_squid.squirt")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F
                  );
               } else {
                  _level.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.glow_squid.squirt")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F,
                     false
                  );
               }
            }

            Vec3 _center = new Vec3(entity.getX(), entity.getY(), entity.getZ());

            for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(10.0), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if (entityiterator instanceof FelsteedEntity
                  || entityiterator instanceof LordPumpkinheadEntity
                  || entityiterator instanceof LordPumpkinheadHeadEntity
                  || entityiterator instanceof LordPumpkinheadWithoutaHorseEntity
                  || entityiterator instanceof MrPumpkinEntity
                  || entityiterator instanceof MrsPumpkinEntity
                  || entityiterator instanceof PumpkinDunceEntity
                  || entityiterator instanceof PumpkinheadEntity
                  || entityiterator instanceof SenorPumpkinEntity
                  || entityiterator instanceof SirPumpkinheadEntity
                  || entityiterator instanceof LordsFelsteedEntity
                  || entityiterator instanceof SirPumpkinheadWithoutHorseEntity
                  || entityiterator instanceof SearedSpiritEntity
                  || entityiterator instanceof PumpkinBruiserEntity) {
                  if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, 4));
                  }

                  if (world instanceof ServerLevel _levelx) {
                     _levelx.sendParticles(
                        (SimpleParticleType)BornInChaosV1ModParticleTypes.PUMPKIN_TRAIL.get(),
                        entity.getX(),
                        entity.getY() + 1.0,
                        entity.getZ(),
                        5,
                        0.3,
                        0.3,
                        0.3,
                        0.2
                     );
                  }

                  if (world instanceof ServerLevel _levelx) {
                     _levelx.sendParticles(ParticleTypes.COMPOSTER, entity.getX(), entity.getY() + 1.0, entity.getZ(), 5, 0.3, 0.3, 0.3, 0.2);
                  }
               }
            }
         }
      }
   }
}
