/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Position
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.sonicether.soundphysics.utils;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RaycastUtils {
    public static BlockHitResult rayCast(@Nullable BlockGetter blockGetter, Vec3 from, Vec3 to, @Nullable BlockPos ignore) {
        if (blockGetter == null) {
            return BlockHitResult.miss((Vec3)to, (Direction)Direction.getNearest((Vec3)from.subtract(to)), (BlockPos)BlockPos.containing((Position)to));
        }
        return (BlockHitResult)BlockGetter.traverseBlocks((Vec3)from, (Vec3)to, (Object)blockGetter, (g, pos) -> {
            double fluidDistance;
            if (pos.equals((Object)ignore)) {
                return null;
            }
            BlockState blockState = blockGetter.getBlockState(pos);
            FluidState fluidState = blockGetter.getFluidState(pos);
            VoxelShape shape = ClipContext.Block.COLLIDER.get(blockState, blockGetter, pos, CollisionContext.empty());
            BlockHitResult blockHitResult = blockGetter.clipWithInteractionOverride(from, to, pos, shape, blockState);
            VoxelShape fluidShape = fluidState.getShape(blockGetter, pos);
            BlockHitResult fluidHitResult = fluidShape.clip(from, to, pos);
            if (fluidHitResult == null) {
                return blockHitResult;
            }
            if (blockHitResult == null) {
                return fluidHitResult;
            }
            double blockDistance = from.distanceToSqr(blockHitResult.getLocation());
            return blockDistance <= (fluidDistance = from.distanceToSqr(fluidHitResult.getLocation())) ? blockHitResult : fluidHitResult;
        }, g -> BlockHitResult.miss((Vec3)to, (Direction)Direction.getNearest((Vec3)from.subtract(to)), (BlockPos)BlockPos.containing((Position)to)));
    }
}

