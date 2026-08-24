package net.mcreator.borninchaosv.procedures;

import javax.annotation.Nullable;
import net.mcreator.borninchaosv.entity.LifestealerEntity;
import net.mcreator.borninchaosv.init.BornInChaosV1ModEntities;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber
public class LifestealerAtackProcedure {
   @SubscribeEvent
   public static void onEntityAttacked(LivingIncomingDamageEvent event) {
      if (event.getEntity() != null) {
         execute(
            event,
            event.getEntity().level(),
            event.getEntity().getX(),
            event.getEntity().getY(),
            event.getEntity().getZ(),
            event.getEntity(),
            event.getSource().getEntity()
         );
      }
   }

   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
      execute(null, world, x, y, z, entity, sourceentity);
   }

   private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
      if (entity != null && sourceentity != null) {
         if (sourceentity instanceof LifestealerEntity) {
            if (!sourceentity.level().isClientSide()) {
               sourceentity.discard();
            }

            if (world instanceof ServerLevel _level) {
               Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.LIFESTEALER_TRUE_FORM.get())
                  .spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
               if (entityToSpawn != null) {
                  entityToSpawn.setYRot(sourceentity.getYRot());
                  entityToSpawn.setYBodyRot(sourceentity.getYRot());
                  entityToSpawn.setYHeadRot(sourceentity.getYRot());
                  entityToSpawn.setXRot(sourceentity.getXRot());
               }
            }

            if (world instanceof Level _levelx) {
               if (!_levelx.isClientSide()) {
                  _levelx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:lifestealer_scream_ap")),
                     SoundSource.NEUTRAL,
                     3.0F,
                     1.0F
                  );
               } else {
                  _levelx.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:lifestealer_scream_ap")),
                     SoundSource.NEUTRAL,
                     3.0F,
                     1.0F,
                     false
                  );
               }
            }

            if (world instanceof ServerLevel _levelxx) {
               _levelxx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.DIM_LONG.get(), x, y, z, 10, 0.4, 0.7, 0.4, 0.4);
            }

            if (!(entity instanceof LivingEntity _livEnt7 && _livEnt7.isBlocking())
               && entity instanceof LivingEntity _entity
               && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.LIFESTEAL, 260, 0));
            }
         }
      }
   }
}
