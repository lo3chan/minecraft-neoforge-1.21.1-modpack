package net.mcreator.borninchaosv.procedures;

import javax.annotation.Nullable;
import net.mcreator.borninchaosv.entity.BabySpiderControlledEntity;
import net.mcreator.borninchaosv.entity.ControlledBabySkeletonEntity;
import net.mcreator.borninchaosv.entity.ControlledSpiritualAssistantEntity;
import net.mcreator.borninchaosv.entity.MrPumpkinControlledEntity;
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
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber
public class LivingcocoonplayersideDeadProcedure {
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
         if ((entity instanceof Mob || entity instanceof Monster || entity instanceof Animal || entity instanceof Player)
            && !(entity instanceof BabySpiderControlledEntity)
            && !(entity instanceof ControlledBabySkeletonEntity)
            && !(entity instanceof ControlledSpiritualAssistantEntity)
            && !(entity instanceof MrPumpkinControlledEntity)
            && entity instanceof LivingEntity _livEnt8
            && _livEnt8.hasEffect(BornInChaosV1ModMobEffects.LIVING_COCOON_PLAYER_SIDE)) {
            if (world instanceof Level _level) {
               if (!_level.isClientSide()) {
                  _level.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:stomach_open")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F
                  );
               } else {
                  _level.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:stomach_open")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F,
                     false
                  );
               }
            }

            if (world instanceof ServerLevel _levelx) {
               _levelx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.WEB_SPLASH.get(), x, y + 1.0, z, 8, 0.3, 0.3, 0.3, 0.2);
            }

            if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1.0F) > 50.0F) {
               for (int index0 = 0; index0 < (int)Mth.nextDouble(RandomSource.create(), 2.0, 3.0); index0++) {
                  if (world instanceof ServerLevel _levelx) {
                     Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.BABY_SPIDER_CONTROLLED.get())
                        .spawn(_levelx, BlockPos.containing(x, y + 1.0, z), MobSpawnType.MOB_SUMMONED);
                     if (entityToSpawn != null) {
                     }
                  }
               }
            } else if ((entity instanceof LivingEntity _livEntx ? _livEntx.getMaxHealth() : -1.0F) > 30.0F
               && (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1.0F) <= 50.0F) {
               for (int index1 = 0; index1 < (int)Mth.nextDouble(RandomSource.create(), 1.0, 2.0); index1++) {
                  if (world instanceof ServerLevel _levelxx) {
                     Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.BABY_SPIDER_CONTROLLED.get())
                        .spawn(_levelxx, BlockPos.containing(x, y + 1.0, z), MobSpawnType.MOB_SUMMONED);
                     if (entityToSpawn != null) {
                     }
                  }
               }

               return;
            } else if ((entity instanceof LivingEntity _livEntx ? _livEntx.getMaxHealth() : -1.0F) > 10.0F
               && (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1.0F) <= 30.0F) {
               if (Math.random() < 0.8 && world instanceof ServerLevel _levelxxx) {
                  Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.BABY_SPIDER_CONTROLLED.get())
                     .spawn(_levelxxx, BlockPos.containing(x, y + 1.0, z), MobSpawnType.MOB_SUMMONED);
                  if (entityToSpawn != null) {
                  }
               }
            } else if ((entity instanceof LivingEntity _livEntxx ? _livEntxx.getMaxHealth() : -1.0F) <= 10.0F
               && Math.random() < 0.3
               && world instanceof ServerLevel _levelxxxx) {
               Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.BABY_SPIDER_CONTROLLED.get())
                  .spawn(_levelxxxx, BlockPos.containing(x, y + 1.0, z), MobSpawnType.MOB_SUMMONED);
               if (entityToSpawn != null) {
               }
            }
         }
      }
   }
}
