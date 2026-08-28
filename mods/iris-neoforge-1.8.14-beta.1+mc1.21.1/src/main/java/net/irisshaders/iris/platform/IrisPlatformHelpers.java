/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.block.state.BlockState
 */
package net.irisshaders.iris.platform;

import java.nio.file.Path;
import java.util.ServiceLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface IrisPlatformHelpers {
    public static final IrisPlatformHelpers INSTANCE = ServiceLoader.load(IrisPlatformHelpers.class).findFirst().get();

    public static IrisPlatformHelpers getInstance() {
        return INSTANCE;
    }

    public boolean isModLoaded(String var1);

    public String getVersion();

    public boolean isDevelopmentEnvironment();

    public Path getGameDir();

    public Path getConfigDir();

    public int compareVersions(String var1, String var2) throws Exception;

    public KeyMapping registerKeyBinding(KeyMapping var1);

    public boolean useELS();

    public BlockState getBlockAppearance(BlockAndTintGetter var1, BlockState var2, Direction var3, BlockPos var4);
}

