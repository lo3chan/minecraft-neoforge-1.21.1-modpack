package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.entity.LifestealerTrueFormEntity;
import net.mcreator.borninchaosv.init.BornInChaosV1ModBlocks;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class LifestealerTicProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (entity instanceof LivingEntity _livEnt0
            && _livEnt0.hasEffect(MobEffects.DAMAGE_RESISTANCE)
            && entity instanceof LivingEntity _livEnt1
            && _livEnt1.hasEffect(BornInChaosV1ModMobEffects.BLOCK_BREAK)
            && entity.getPersistentData().getDouble("appearancem") == 1.0) {
            entity.getPersistentData().putDouble("appearancem", entity.getPersistentData().getDouble("appearancem") - 1.0);
            if (entity.getPersistentData().getDouble("appearancem") == 0.0) {
               if (entity instanceof LifestealerTrueFormEntity) {
                  ((LifestealerTrueFormEntity)entity).setAnimation("appearance");
               }

               if (entity instanceof LivingEntity _entity) {
                  _entity.removeEffect(BornInChaosV1ModMobEffects.BLOCK_BREAK);
               }

               if (entity instanceof LivingEntity _entity) {
                  _entity.removeEffect(MobEffects.DAMAGE_RESISTANCE);
               }
            }
         }

         if (entity instanceof LivingEntity _livEnt9
            && _livEnt9.hasEffect(BornInChaosV1ModMobEffects.DARK_SPLASH)
            && !(entity instanceof LivingEntity _livEnt10 && _livEnt10.hasEffect(BornInChaosV1ModMobEffects.BLOCK_BREAK))
            && entity.getPersistentData().getDouble("splashpreparation") == 1.0) {
            if (entity instanceof LifestealerTrueFormEntity) {
               ((LifestealerTrueFormEntity)entity).setAnimation("splash");
            }

            entity.getPersistentData().putDouble("splashpreparation", 0.0);
         }

         if (world.getBlockState(BlockPos.containing(x, y, z)).getBlock() == Blocks.AIR
            && (
               world.getBlockState(BlockPos.containing(x, y - 1.0, z)).getBlock() == Blocks.WATER
                  || world.getBlockState(BlockPos.containing(x, y - 1.0, z)).getBlock() == Blocks.KELP_PLANT
                  || world.getBlockState(BlockPos.containing(x, y - 1.0, z)).getBlock() == Blocks.SEAGRASS
                  || world.getBlockState(BlockPos.containing(x, y - 1.0, z)).getBlock() == Blocks.TALL_SEAGRASS
            )) {
            world.setBlock(BlockPos.containing(x, y - 1.0, z), ((Block)BornInChaosV1ModBlocks.DARK_ICE.get()).defaultBlockState(), 3);
         }

         if (world instanceof ServerLevel _level) {
            _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.DARKMATTER.get(), x, y, z, 1, 0.3, 0.2, 0.3, 0.1);
         }
      }
   }
}
