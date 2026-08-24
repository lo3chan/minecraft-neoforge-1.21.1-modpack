package net.mcreator.borninchaosv.procedures;

import java.util.Calendar;
import net.mcreator.borninchaosv.entity.NightmareStalkerEntity;
import net.mcreator.borninchaosv.init.BornInChaosV1ModBlocks;
import net.mcreator.borninchaosv.init.BornInChaosV1ModGameRules;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class NightmareStalkerGonProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if ((entity instanceof LivingEntity _livEntx ? _livEntx.getHealth() : -1.0F)
               >= (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1.0F)
            && entity instanceof LivingEntity _entity
            && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.UNITY_WITH_DARKNESS, 10, 0, false, false));
         }

         if (!(
               (entity instanceof LivingEntity _livEntx ? _livEntx.getHealth() : -1.0F)
                     >= (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1.0F)
                  && entity instanceof LivingEntity _livEnt5
            )
            || !_livEnt5.hasEffect(BornInChaosV1ModMobEffects.UNITY_WITH_DARKNESS)) {
            if (entity instanceof LivingEntity _livEnt7 && _livEnt7.hasEffect(BornInChaosV1ModMobEffects.UNITY_WITH_DARKNESS)
               || !(
                  (entity instanceof LivingEntity _livEntxxx ? _livEntxxx.getHealth() : -1.0F)
                     < (entity instanceof LivingEntity _livEntxx ? _livEntxx.getMaxHealth() : -1.0F)
               )
               || (Calendar.getInstance().get(2) != 1 || Calendar.getInstance().get(5) < 13 || Calendar.getInstance().get(5) > 20)
                  && !entity.getDisplayName().getString().equals("Pookie")
                  && !entity.getDisplayName().getString().equals("pookie")
                  && !entity.getDisplayName().getString().equals("Lover")
                  && !entity.getDisplayName().getString().equals("lover")) {
               if (!(entity instanceof LivingEntity _livEnt15 && _livEnt15.hasEffect(BornInChaosV1ModMobEffects.UNITY_WITH_DARKNESS))
                  && (entity instanceof LivingEntity _livEntxxxxx ? _livEntxxxxx.getHealth() : -1.0F)
                     < (entity instanceof LivingEntity _livEntxxxx ? _livEntxxxx.getMaxHealth() : -1.0F)
                  && entity instanceof NightmareStalkerEntity animatable) {
                  animatable.setTexture("nightmarestalker");
               }
            } else if (entity instanceof NightmareStalkerEntity animatable) {
               animatable.setTexture("nightmarestalker_lover");
            }
         } else if (entity instanceof NightmareStalkerEntity animatable) {
            animatable.setTexture("nightmarestalker_e");
         }

         if (world.dayTime() < 2400000L || entity instanceof LivingEntity _livEnt20 && _livEnt20.hasEffect(BornInChaosV1ModMobEffects.TERRIFYING_PRESENCE)) {
            if (world.dayTime() >= 1200000L
               && world.dayTime() < 2400000L
               && !(entity instanceof LivingEntity _livEnt25 && _livEnt25.hasEffect(BornInChaosV1ModMobEffects.TERRIFYING_PRESENCE))) {
               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20, 1, false, false));
               }

               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20, 0, false, false));
               }
            }
         } else {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20, 2, false, false));
            }

            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20, 2, false, false));
            }
         }

         if (world.dayTime() >= 360000L
            && world.getBlockState(BlockPos.containing(x, y, z)).getBlock() == Blocks.AIR
            && (
               world.getBlockState(BlockPos.containing(x, y - 1.0, z)).getBlock() == Blocks.WATER
                  || world.getBlockState(BlockPos.containing(x, y - 1.0, z)).getBlock() == Blocks.KELP_PLANT
                  || world.getBlockState(BlockPos.containing(x, y - 1.0, z)).getBlock() == Blocks.SEAGRASS
                  || world.getBlockState(BlockPos.containing(x, y - 1.0, z)).getBlock() == Blocks.TALL_SEAGRASS
            )) {
            world.setBlock(BlockPos.containing(x, y - 1.0, z), ((Block)BornInChaosV1ModBlocks.DARK_ICE.get()).defaultBlockState(), 3);
         }

         if (world.getLevelData().getGameRules().getBoolean(BornInChaosV1ModGameRules.DISAPPEARANCEOFSPIRITSUNDERTHESUN)
            && world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1.0, z))
            && world instanceof Level _lvl42
            && _lvl42.isDay()
            && !world.getLevelData().isRaining()
            && !world.getLevelData().isThundering()
            && !entity.getDisplayName().getString().equals("Pookie")
            && !entity.getDisplayName().getString().equals("pookie")
            && !entity.getDisplayName().getString().equals("Lover")
            && !entity.getDisplayName().getString().equals("lover")
            && !world.isClientSide()) {
            if (!entity.level().isClientSide()) {
               entity.discard();
            }

            if (world instanceof ServerLevel _level) {
               _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.DIMLARG.get(), x, y, z, 10, 0.5, 0.5, 0.5, 0.1);
            }
         }
      }
   }
}
