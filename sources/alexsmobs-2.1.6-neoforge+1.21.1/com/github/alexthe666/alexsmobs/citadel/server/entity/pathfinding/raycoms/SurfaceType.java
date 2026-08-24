package com.github.alexthe666.alexsmobs.citadel.server.entity.pathfinding.raycoms;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BambooSaplingBlock;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.MagmaBlock;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.VoxelShape;

public enum SurfaceType {
   WALKABLE,
   DROPABLE,
   NOT_PASSABLE,
   FLYABLE;

   public static SurfaceType getSurfaceType(BlockGetter world, BlockState blockState, BlockPos pos) {
      Block block = blockState.getBlock();
      if (block instanceof FenceBlock
         || block instanceof FenceGateBlock
         || block instanceof WallBlock
         || block instanceof FireBlock
         || block instanceof CampfireBlock
         || block instanceof BambooStalkBlock
         || block instanceof BambooSaplingBlock
         || block instanceof DoorBlock
         || block instanceof MagmaBlock
         || block instanceof PowderSnowBlock) {
         return NOT_PASSABLE;
      } else if (block instanceof TrapDoorBlock && !(Boolean)blockState.getValue(TrapDoorBlock.OPEN)) {
         return WALKABLE;
      } else {
         VoxelShape shape = blockState.getShape(world, pos);
         if (shape.max(Axis.Y) > 1.0) {
            return NOT_PASSABLE;
         } else {
            FluidState fluid = world.getFluidState(pos);
            if (blockState.getBlock() != Blocks.LAVA && (fluid.isEmpty() || fluid.getType() != Fluids.LAVA && fluid.getType() != Fluids.FLOWING_LAVA)) {
               if (isWater(world, pos, blockState, fluid)) {
                  return WALKABLE;
               } else if (block instanceof SignBlock || block instanceof VineBlock) {
                  return DROPABLE;
               } else {
                  return (!blockState.isSolid() || !(shape.max(Axis.X) - shape.min(Axis.X) > 0.75) || !(shape.max(Axis.Z) - shape.min(Axis.Z) > 0.75))
                        && (blockState.getBlock() != Blocks.SNOW || blockState.getValue(SnowLayerBlock.LAYERS) <= 1)
                        && !(block instanceof CarpetBlock)
                     ? DROPABLE
                     : WALKABLE;
               }
            } else {
               return NOT_PASSABLE;
            }
         }
      }
   }

   public static boolean isWater(LevelReader world, BlockPos pos) {
      return isWater(world, pos, null, null);
   }

   public static boolean isWater(BlockGetter world, BlockPos pos, @Nullable BlockState pState, @Nullable FluidState pFluidState) {
      BlockState state = pState;
      if (pState == null) {
         state = world.getBlockState(pos);
      }

      if (state.canOcclude()) {
         return false;
      } else if (state.getBlock() == Blocks.WATER) {
         return true;
      } else {
         FluidState fluidState = pFluidState;
         if (pFluidState == null) {
            fluidState = world.getFluidState(pos);
         }

         if (fluidState.isEmpty()) {
            return false;
         } else if ((state.getBlock() instanceof TrapDoorBlock || state.getBlock() instanceof HorizontalDirectionalBlock)
            && state.hasProperty(TrapDoorBlock.OPEN)
            && !(Boolean)state.getValue(TrapDoorBlock.OPEN)
            && state.hasProperty(TrapDoorBlock.HALF)
            && state.getValue(TrapDoorBlock.HALF) == Half.TOP) {
            return false;
         } else {
            Fluid fluid = fluidState.getType();
            return fluid == Fluids.WATER || fluid == Fluids.FLOWING_WATER;
         }
      }
   }
}
