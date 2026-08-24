package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModBlocks;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class LordPumpkinheadHeadPriObnovlieniiTikaSushchnostiProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (world.getBlockState(BlockPos.containing(x, y - 1.0, z)).getBlock() == Blocks.WATER
            || world.getBlockState(BlockPos.containing(x, y - 1.0, z)).getBlock() == Blocks.LAVA
            || world.getBlockState(BlockPos.containing(x, y - 1.0, z)).getBlock() == Blocks.KELP_PLANT
            || world.getBlockState(BlockPos.containing(x, y - 1.0, z)).getBlock() == Blocks.SEAGRASS
            || world.getBlockState(BlockPos.containing(x, y - 1.0, z)).getBlock() == Blocks.TALL_SEAGRASS) {
            world.setBlock(BlockPos.containing(x, y - 1.0, z), ((Block)BornInChaosV1ModBlocks.FEL_SOIL.get()).defaultBlockState(), 3);
         }

         if (world instanceof ServerLevel _level) {
            _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.ANIM_FIRE.get(), x, y + 2.0, z, 1, 0.2, 0.2, 0.2, 0.1);
         }

         if (entity.isInWall() || entity.isInLava()) {
            entity.teleportTo(x + 1.0, y + 2.0, z);
            if (entity instanceof ServerPlayer _serverPlayer) {
               _serverPlayer.connection.teleport(x + 1.0, y + 2.0, z, entity.getYRot(), entity.getXRot());
            }
         }
      }
   }
}
