package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.entity.KrampusEntity;
import net.mcreator.borninchaosv.entity.LifestealerEntity;
import net.mcreator.borninchaosv.entity.LifestealerTrueFormEntity;
import net.mcreator.borninchaosv.entity.NightmareStalkerEntity;
import net.mcreator.borninchaosv.init.BornInChaosV1ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DarkIceKoghdaSushchnostKhoditPoBlokuProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (entity instanceof NightmareStalkerEntity
            || entity instanceof LifestealerEntity
            || entity instanceof LifestealerTrueFormEntity
            || entity instanceof KrampusEntity) {
            if (!world.isClientSide()) {
               BlockPos _bp = BlockPos.containing(x, y, z);
               BlockEntity _blockEntity = world.getBlockEntity(_bp);
               BlockState _bs = world.getBlockState(_bp);
               if (_blockEntity != null) {
                  _blockEntity.getPersistentData().putDouble("splitting", (new Object() {
                     public double getValue(LevelAccessor world, BlockPos pos, String tag) {
                        BlockEntity blockEntity = world.getBlockEntity(pos);
                        return blockEntity != null ? blockEntity.getPersistentData().getDouble(tag) : -1.0;
                     }
                  }).getValue(world, BlockPos.containing(x, y, z), "splitting") + 2.0);
               }

               if (world instanceof Level _level) {
                  _level.sendBlockUpdated(_bp, _bs, _bs, 3);
               }
            }

            if (world.getBlockState(BlockPos.containing(x - 1.0, y, z)).getBlock() == Blocks.WATER
               || world.getBlockState(BlockPos.containing(x - 1.0, y, z)).getBlock() == Blocks.LAVA
               || world.getBlockState(BlockPos.containing(x - 1.0, y, z)).getBlock() == Blocks.KELP_PLANT
               || world.getBlockState(BlockPos.containing(x - 1.0, y, z)).getBlock() == Blocks.SEAGRASS
               || world.getBlockState(BlockPos.containing(x - 1.0, y, z)).getBlock() == Blocks.TALL_SEAGRASS) {
               world.setBlock(BlockPos.containing(x - 1.0, y, z), ((Block)BornInChaosV1ModBlocks.DARK_ICE.get()).defaultBlockState(), 3);
            }

            if (world.getBlockState(BlockPos.containing(x + 1.0, y, z)).getBlock() == Blocks.WATER
               || world.getBlockState(BlockPos.containing(x + 1.0, y, z)).getBlock() == Blocks.LAVA
               || world.getBlockState(BlockPos.containing(x + 1.0, y, z)).getBlock() == Blocks.KELP_PLANT
               || world.getBlockState(BlockPos.containing(x + 1.0, y, z)).getBlock() == Blocks.SEAGRASS
               || world.getBlockState(BlockPos.containing(x + 1.0, y, z)).getBlock() == Blocks.TALL_SEAGRASS) {
               world.setBlock(BlockPos.containing(x + 1.0, y, z), ((Block)BornInChaosV1ModBlocks.DARK_ICE.get()).defaultBlockState(), 3);
            }

            if (world.getBlockState(BlockPos.containing(x, y, z + 1.0)).getBlock() == Blocks.WATER
               || world.getBlockState(BlockPos.containing(x, y, z + 1.0)).getBlock() == Blocks.LAVA
               || world.getBlockState(BlockPos.containing(x, y, z + 1.0)).getBlock() == Blocks.KELP_PLANT
               || world.getBlockState(BlockPos.containing(x, y, z + 1.0)).getBlock() == Blocks.SEAGRASS
               || world.getBlockState(BlockPos.containing(x, y, z + 1.0)).getBlock() == Blocks.TALL_SEAGRASS) {
               world.setBlock(BlockPos.containing(x, y, z + 1.0), ((Block)BornInChaosV1ModBlocks.DARK_ICE.get()).defaultBlockState(), 3);
            }

            if (world.getBlockState(BlockPos.containing(x, y, z - 1.0)).getBlock() == Blocks.WATER
               || world.getBlockState(BlockPos.containing(x, y, z - 1.0)).getBlock() == Blocks.LAVA
               || world.getBlockState(BlockPos.containing(x, y, z - 1.0)).getBlock() == Blocks.KELP_PLANT
               || world.getBlockState(BlockPos.containing(x, y, z - 1.0)).getBlock() == Blocks.SEAGRASS
               || world.getBlockState(BlockPos.containing(x, y, z - 1.0)).getBlock() == Blocks.TALL_SEAGRASS) {
               world.setBlock(BlockPos.containing(x, y, z - 1.0), ((Block)BornInChaosV1ModBlocks.DARK_ICE.get()).defaultBlockState(), 3);
            }

            if (world.getBlockState(BlockPos.containing(x + 1.0, y, z - 1.0)).getBlock() == Blocks.WATER
               || world.getBlockState(BlockPos.containing(x + 1.0, y, z - 1.0)).getBlock() == Blocks.LAVA
               || world.getBlockState(BlockPos.containing(x + 1.0, y, z - 1.0)).getBlock() == Blocks.KELP_PLANT
               || world.getBlockState(BlockPos.containing(x + 1.0, y, z - 1.0)).getBlock() == Blocks.SEAGRASS
               || world.getBlockState(BlockPos.containing(x + 1.0, y, z - 1.0)).getBlock() == Blocks.TALL_SEAGRASS) {
               world.setBlock(BlockPos.containing(x + 1.0, y, z - 1.0), ((Block)BornInChaosV1ModBlocks.DARK_ICE.get()).defaultBlockState(), 3);
            }

            if (world.getBlockState(BlockPos.containing(x - 1.0, y, z - 1.0)).getBlock() == Blocks.WATER
               || world.getBlockState(BlockPos.containing(x - 1.0, y, z - 1.0)).getBlock() == Blocks.LAVA
               || world.getBlockState(BlockPos.containing(x - 1.0, y, z - 1.0)).getBlock() == Blocks.KELP_PLANT
               || world.getBlockState(BlockPos.containing(x - 1.0, y, z - 1.0)).getBlock() == Blocks.SEAGRASS
               || world.getBlockState(BlockPos.containing(x - 1.0, y, z - 1.0)).getBlock() == Blocks.TALL_SEAGRASS) {
               world.setBlock(BlockPos.containing(x - 1.0, y, z - 1.0), ((Block)BornInChaosV1ModBlocks.DARK_ICE.get()).defaultBlockState(), 3);
            }

            if (world.getBlockState(BlockPos.containing(x - 1.0, y, z + 1.0)).getBlock() == Blocks.WATER
               || world.getBlockState(BlockPos.containing(x - 1.0, y, z + 1.0)).getBlock() == Blocks.LAVA
               || world.getBlockState(BlockPos.containing(x - 1.0, y, z + 1.0)).getBlock() == Blocks.KELP_PLANT
               || world.getBlockState(BlockPos.containing(x - 1.0, y, z + 1.0)).getBlock() == Blocks.SEAGRASS
               || world.getBlockState(BlockPos.containing(x - 1.0, y, z + 1.0)).getBlock() == Blocks.TALL_SEAGRASS) {
               world.setBlock(BlockPos.containing(x - 1.0, y, z + 1.0), ((Block)BornInChaosV1ModBlocks.DARK_ICE.get()).defaultBlockState(), 3);
            }

            if (world.getBlockState(BlockPos.containing(x + 1.0, y, z + 1.0)).getBlock() == Blocks.WATER
               || world.getBlockState(BlockPos.containing(x + 1.0, y, z + 1.0)).getBlock() == Blocks.LAVA
               || world.getBlockState(BlockPos.containing(x + 1.0, y, z + 1.0)).getBlock() == Blocks.KELP_PLANT
               || world.getBlockState(BlockPos.containing(x + 1.0, y, z + 1.0)).getBlock() == Blocks.SEAGRASS
               || world.getBlockState(BlockPos.containing(x + 1.0, y, z + 1.0)).getBlock() == Blocks.TALL_SEAGRASS) {
               world.setBlock(BlockPos.containing(x + 1.0, y, z + 1.0), ((Block)BornInChaosV1ModBlocks.DARK_ICE.get()).defaultBlockState(), 3);
            }
         }
      }
   }
}
