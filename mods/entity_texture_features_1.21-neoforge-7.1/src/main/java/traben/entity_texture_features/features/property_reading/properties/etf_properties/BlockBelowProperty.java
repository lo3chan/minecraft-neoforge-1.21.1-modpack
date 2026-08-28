/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import java.util.Properties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.optifine_properties.BlocksProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class BlockBelowProperty
extends BlocksProperty {
    protected BlockBelowProperty(Properties properties, int propertyNum, String[] ids) throws RandomProperty.RandomPropertyException {
        super(properties, propertyNum, ids);
    }

    public static BlocksProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new BlockBelowProperty(properties, propertyNum, new String[]{"blockBelow"});
        }
        catch (RandomProperty.RandomPropertyException e) {
            return null;
        }
    }

    @Override
    @Nullable
    protected BlockState[] getTestingBlocks(ETFEntityRenderState entity) {
        if (entity.uuid().getLeastSignificantBits() == 0x303900003039L) {
            return new BlockState[]{Blocks.SPAWNER.defaultBlockState()};
        }
        if (entity.world() == null || entity.blockPos() == null) {
            return null;
        }
        Level world = entity.world();
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        mutablePos.set((Vec3i)entity.blockPos());
        int minBuildHeight = world.getMinBuildHeight();
        while (minBuildHeight <= mutablePos.getY() && world.getBlockState((BlockPos)mutablePos).isAir()) {
            mutablePos.move(0, -1, 0);
        }
        if (minBuildHeight > mutablePos.getY()) {
            return null;
        }
        return new BlockState[]{world.getBlockState((BlockPos)mutablePos)};
    }

    @Override
    @NotNull
    public String[] getPropertyIds() {
        return new String[]{"blockBelow"};
    }
}

