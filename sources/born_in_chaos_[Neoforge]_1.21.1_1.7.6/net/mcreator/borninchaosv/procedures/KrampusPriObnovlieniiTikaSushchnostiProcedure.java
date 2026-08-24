package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.entity.KrampusEntity;
import net.mcreator.borninchaosv.init.BornInChaosV1ModBlocks;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class KrampusPriObnovlieniiTikaSushchnostiProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (entity instanceof LivingEntity _entity) {
            _entity.removeEffect(BornInChaosV1ModMobEffects.BONE_CHILLING);
         }

         if (entity instanceof LivingEntity _livEnt1
            && _livEnt1.hasEffect(MobEffects.DAMAGE_RESISTANCE)
            && entity instanceof LivingEntity _livEnt2
            && _livEnt2.hasEffect(BornInChaosV1ModMobEffects.BLOCK_BREAK)) {
            if (entity.getPersistentData().getDouble("appearance") == 0.0) {
               entity.getPersistentData().putDouble("appearance", 1.0);
            } else {
               entity.getPersistentData().putDouble("appearance", entity.getPersistentData().getDouble("appearance") - 1.0);
            }

            if (entity.getPersistentData().getDouble("appearance") == 0.0) {
               if (entity instanceof KrampusEntity) {
                  ((KrampusEntity)entity).setAnimation("appearance");
               }

               if (entity instanceof LivingEntity _entity) {
                  _entity.removeEffect(MobEffects.DAMAGE_RESISTANCE);
               }

               if (entity instanceof LivingEntity _entity) {
                  _entity.removeEffect(BornInChaosV1ModMobEffects.BLOCK_BREAK);
               }
            }
         }

         if (world.getBlockState(BlockPos.containing(x, y - 1.0, z)).getBlock() == Blocks.WATER
            || world.getBlockState(BlockPos.containing(x, y - 1.0, z)).getBlock() == Blocks.KELP_PLANT
            || world.getBlockState(BlockPos.containing(x, y - 1.0, z)).getBlock() == Blocks.SEAGRASS
            || world.getBlockState(BlockPos.containing(x, y - 1.0, z)).getBlock() == Blocks.TALL_SEAGRASS) {
            world.setBlock(BlockPos.containing(x, y - 1.0, z), ((Block)BornInChaosV1ModBlocks.DARK_ICE.get()).defaultBlockState(), 3);
         }

         if (world.getBlockState(BlockPos.containing(x, y - 1.0, z)).canOcclude()
            && world.getBlockState(BlockPos.containing(x, y, z)).getBlock() == Blocks.AIR
            && entity instanceof LivingEntity _livEnt23
            && _livEnt23.hasEffect(BornInChaosV1ModMobEffects.SNOW_STORM)) {
            world.setBlock(BlockPos.containing(x, y, z), Blocks.SNOW.defaultBlockState(), 3);
         }
      }
   }
}
