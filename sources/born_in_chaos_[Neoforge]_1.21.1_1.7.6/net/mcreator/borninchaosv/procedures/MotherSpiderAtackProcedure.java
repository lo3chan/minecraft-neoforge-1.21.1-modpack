package net.mcreator.borninchaosv.procedures;

import javax.annotation.Nullable;
import net.mcreator.borninchaosv.entity.MotherSpiderEntity;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber
public class MotherSpiderAtackProcedure {
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
         if (sourceentity instanceof MotherSpiderEntity
            && (entity instanceof Mob || entity instanceof Monster || entity instanceof Animal || entity instanceof Player)
            && !(entity instanceof LivingEntity _livEnt5 && _livEnt5.isBlocking())) {
            if (!(entity instanceof LivingEntity _livEnt6 && _livEnt6.hasEffect(BornInChaosV1ModMobEffects.LIVING_COCOON))) {
               if (world instanceof ServerLevel _level) {
                  _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.WEB_SPLASH.get(), x, y + 1.0, z, 6, 0.3, 0.3, 0.3, 0.1);
               }

               if (world.getDifficulty() == Difficulty.HARD) {
                  if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.LIVING_COCOON, 400, 0));
                  }
               } else if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.LIVING_COCOON, 300, 0));
               }
            }

            if (world.getDifficulty() == Difficulty.HARD) {
               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(MobEffects.POISON, 400, 1, false, false));
               }
            } else if (world.getDifficulty() == Difficulty.NORMAL && entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 0, false, false));
            }
         }
      }
   }
}
