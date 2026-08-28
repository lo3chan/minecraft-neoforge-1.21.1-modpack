/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.fml.loading.FMLLoader
 *  net.neoforged.fml.loading.FMLPaths
 *  net.neoforged.fml.loading.LoadingModList
 *  org.apache.maven.artifact.versioning.ArtifactVersion
 *  org.apache.maven.artifact.versioning.DefaultArtifactVersion
 */
package net.irisshaders.iris.platform;

import java.nio.file.Path;
import net.irisshaders.iris.platform.IrisForgeMod;
import net.irisshaders.iris.platform.IrisPlatformHelpers;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.loading.LoadingModList;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

public class IrisForgeHelpers
implements IrisPlatformHelpers {
    boolean HAS_CAMO = this.isModLoaded("cable_facades");

    @Override
    public boolean isModLoaded(String modId) {
        return LoadingModList.get().getModFileById(modId) != null;
    }

    @Override
    public String getVersion() {
        return LoadingModList.get().getModFileById("iris").versionString();
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public Path getGameDir() {
        return FMLPaths.GAMEDIR.get();
    }

    @Override
    public Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public int compareVersions(String currentVersion, String semanticVersion) throws Exception {
        return new DefaultArtifactVersion(currentVersion).compareTo((ArtifactVersion)new DefaultArtifactVersion(semanticVersion));
    }

    @Override
    public KeyMapping registerKeyBinding(KeyMapping keyMapping) {
        IrisForgeMod.KEYLIST.add(keyMapping);
        return keyMapping;
    }

    @Override
    public boolean useELS() {
        return true;
    }

    @Override
    public BlockState getBlockAppearance(BlockAndTintGetter level, BlockState state, Direction cullFace, BlockPos pos) {
        return state;
    }
}

