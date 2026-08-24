package net.mcreator.borninchaosv.procedures;

import javax.annotation.Nullable;
import net.mcreator.borninchaosv.init.BornInChaosV1ModEntities;
import net.mcreator.borninchaosv.init.BornInChaosV1ModGameRules;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber
public class ZombieMaggotsProcedure {
   @SubscribeEvent
   public static void onEntityDeath(LivingDeathEvent event) {
      if (event.getEntity() != null) {
         execute(event, event.getEntity().level(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), event.getEntity());
      }
   }

   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      execute(null, world, x, y, z, entity);
   }

   private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (world.getLevelData().getGameRules().getBoolean(BornInChaosV1ModGameRules.MAGGOTS_APPEARANCE)
            && (
               entity instanceof Zombie
                  || entity instanceof ZombieVillager
                  || entity instanceof ZombieHorse
                  || entity instanceof Husk
                  || entity instanceof Drowned && !entity.isInWater()
            )
            && Math.random() < 0.15) {
            if (world instanceof ServerLevel _level) {
               _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.FLESHSPLASH.get(), x, y + 1.0, z, 5, 0.3, 0.3, 0.3, 0.1);
            }

            if (world instanceof ServerLevel _level) {
               Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.MAGGOT.get())
                  .spawn(_level, BlockPos.containing(x + 0.5, y + 1.0, z + 0.5), MobSpawnType.MOB_SUMMONED);
               if (entityToSpawn != null) {
                  entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
               }
            }

            if (world instanceof ServerLevel _levelx) {
               _levelx.sendParticles(ParticleTypes.POOF, x + 0.5, y + 1.0, z + 0.5, 3, 0.1, 0.1, 0.1, 0.1);
            }

            if (!world.isClientSide()) {
               if (world instanceof Level _levelx) {
                  if (!_levelx.isClientSide()) {
                     _levelx.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.slime_block.break")),
                        SoundSource.NEUTRAL,
                        0.9F,
                        0.9F
                     );
                  } else {
                     _levelx.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.slime_block.break")),
                        SoundSource.NEUTRAL,
                        0.9F,
                        0.9F,
                        false
                     );
                  }
               }

               if (world instanceof Level _levelxx) {
                  if (!_levelxx.isClientSide()) {
                     _levelxx.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:stomach_open")),
                        SoundSource.NEUTRAL,
                        0.8F,
                        1.0F
                     );
                  } else {
                     _levelxx.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:stomach_open")),
                        SoundSource.NEUTRAL,
                        0.8F,
                        1.0F,
                        false
                     );
                  }
               }
            }

            if (Math.random() < 0.6) {
               if (world instanceof ServerLevel _levelxxx) {
                  Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.MAGGOT.get())
                     .spawn(_levelxxx, BlockPos.containing(x + 0.2, y + 1.0, z + 0.3), MobSpawnType.MOB_SUMMONED);
                  if (entityToSpawn != null) {
                     entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
                  }
               }

               if (world instanceof ServerLevel _levelxxxx) {
                  _levelxxxx.sendParticles(ParticleTypes.POOF, x + 0.2, y + 1.0, z + 0.3, 3, 0.1, 0.1, 0.1, 0.1);
               }

               if (Math.random() < 0.3) {
                  if (world instanceof ServerLevel _levelxxxx) {
                     Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.MAGGOT.get())
                        .spawn(_levelxxxx, BlockPos.containing(x + 0.2, y + 1.0, z + 0.8), MobSpawnType.MOB_SUMMONED);
                     if (entityToSpawn != null) {
                        entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
                     }
                  }

                  if (world instanceof ServerLevel _levelxxxxx) {
                     _levelxxxxx.sendParticles(ParticleTypes.POOF, x + 0.2, y + 1.0, z + 0.8, 3, 0.1, 0.1, 0.1, 0.1);
                  }
               }
            }
         }
      }
   }
}
