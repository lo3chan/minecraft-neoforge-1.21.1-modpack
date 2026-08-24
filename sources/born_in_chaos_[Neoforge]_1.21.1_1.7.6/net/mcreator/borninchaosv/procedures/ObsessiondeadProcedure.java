package net.mcreator.borninchaosv.procedures;

import javax.annotation.Nullable;
import net.mcreator.borninchaosv.init.BornInChaosV1ModEntities;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber
public class ObsessiondeadProcedure {
   @SubscribeEvent
   public static void onEntityDeath(LivingDeathEvent event) {
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
         if (entity instanceof Monster && entity instanceof LivingEntity _livEnt1 && _livEnt1.hasEffect(BornInChaosV1ModMobEffects.OBSESSION)) {
            if (world instanceof ServerLevel _level) {
               Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.SPIRITOF_CHAOS.get())
                  .spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
               if (entityToSpawn != null) {
                  entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
               }
            }

            if (world instanceof ServerLevel _levelx) {
               _levelx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.DIMLARG.get(), x, y, z, 14, 0.2, 0.8, 0.2, 0.1);
            }

            if (world instanceof Level _levelx) {
               if (!_levelx.isClientSide()) {
                  _levelx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:persecutor_scream")),
                     SoundSource.NEUTRAL,
                     0.5F,
                     1.0F
                  );
               } else {
                  _levelx.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:persecutor_scream")),
                     SoundSource.NEUTRAL,
                     0.5F,
                     1.0F,
                     false
                  );
               }
            }

            for (int index0 = 0; index0 < (int)Math.floor((entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1.0F) / 10.0F + 3.0F); index0++) {
               if (world instanceof ServerLevel _levelxx) {
                  _levelxx.addFreshEntity(
                     new ExperienceOrb(_levelxx, x, y, z, (int)((entity instanceof LivingEntity _livEntx ? _livEntx.getMaxHealth() : -1.0F) * 0.15 + 5.0))
                  );
               }
            }

            if (sourceentity instanceof Player && sourceentity instanceof ServerPlayer _player) {
               AdvancementHolder _adv = _player.server.getAdvancements().get(ResourceLocation.parse("born_in_chaos_v1:exorcism"));
               if (_adv != null) {
                  AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
                  if (!_ap.isDone()) {
                     for (String criteria : _ap.getRemainingCriteria()) {
                        _player.getAdvancements().award(_adv, criteria);
                     }
                  }
               }
            }
         }
      }
   }
}
