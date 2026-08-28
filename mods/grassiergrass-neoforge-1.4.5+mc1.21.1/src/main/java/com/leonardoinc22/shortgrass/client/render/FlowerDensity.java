/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.FlowerBlock
 *  net.minecraft.world.phys.Vec3
 */
package com.leonardoinc22.shortgrass.client.render;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.phys.Vec3;

final class FlowerDensity {
    private static final Direction[] HORIZONTAL = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
    private static final double SPREAD = 0.42;
    private static final double MIN_SPACING = 0.42;
    private static final double DIAGONAL_MIN_RATIO = 0.8;
    private static final int MAX_COMPANIONS = 2;
    private static final int PLACEMENT_ATTEMPTS = 3;
    private static final long SEED_SALT = -7046029254386353131L;

    private FlowerDensity() {
    }

    static void collectExtras(BlockGetter world, BlockPos pos, Vec3 base, List<Vec3> out) {
        int neighbours = 0;
        for (Direction dir : HORIZONTAL) {
            if (!(world.getBlockState(pos.relative(dir)).getBlock() instanceof FlowerBlock)) continue;
            ++neighbours;
        }
        if (neighbours == 0) {
            return;
        }
        int companions = Math.min(neighbours, 2);
        RandomSource random = RandomSource.create((long)(pos.asLong() ^ 0x9E3779B97F4A7C15L));
        int firstExtra = out.size();
        block1: for (int i = 0; i < companions; ++i) {
            for (int attempt = 0; attempt < 3; ++attempt) {
                double z;
                double x = (random.nextDouble() - 0.5) * 2.0 * 0.42;
                if (!FlowerDensity.isClear(x, z = (random.nextDouble() - 0.5) * 2.0 * 0.42, base, out, firstExtra)) continue;
                out.add(new Vec3(x, 0.0, z));
                continue block1;
            }
        }
    }

    private static boolean isClear(double x, double z, Vec3 base, List<Vec3> placed, int firstExtra) {
        if (!FlowerDensity.clearOf(x, z, base.x, base.z)) {
            return false;
        }
        for (int i = firstExtra; i < placed.size(); ++i) {
            Vec3 v = placed.get(i);
            if (FlowerDensity.clearOf(x, z, v.x, v.z)) continue;
            return false;
        }
        return true;
    }

    private static boolean clearOf(double x, double z, double ox, double oz) {
        double az;
        double dx = x - ox;
        double dz = z - oz;
        if (dx * dx + dz * dz < 0.17639999999999997) {
            return false;
        }
        double ax = Math.abs(dx);
        return Math.min(ax, az = Math.abs(dz)) <= 0.8 * Math.max(ax, az);
    }
}

