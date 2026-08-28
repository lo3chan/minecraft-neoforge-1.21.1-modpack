/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.leonardoinc22.shortgrass.client.render.iris;

import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class IrisCompat {
    private static final String[] API_CLASS_NAMES = new String[]{"net.irisshaders.iris.api.v0.IrisApi", "net.coderbot.iris.api.v0.IrisApi"};
    private static boolean initialized;
    private static Object apiInstance;
    private static Method isShaderPackInUseMethod;
    private static Method isRenderingShadowPassMethod;
    private static boolean terrainMetadataInitialized;
    private static Class<?> blockSensitiveBufferBuilderClass;
    private static Method beginBlockMethod;
    private static Method endBlockMethod;
    private static Object worldRenderingSettingsInstance;
    private static Method getBlockStateIdsMethod;

    private IrisCompat() {
    }

    public static boolean isShaderPackInUse() {
        return IrisCompat.invokeBool(isShaderPackInUseMethod);
    }

    public static boolean isRenderingShadowPass() {
        return IrisCompat.invokeBool(isRenderingShadowPassMethod);
    }

    public static void beginTerrainBlock(VertexConsumer consumer, BlockState state, int localX, int localY, int localZ) {
        IrisCompat.initializeTerrainMetadata();
        if (blockSensitiveBufferBuilderClass == null || !blockSensitiveBufferBuilderClass.isInstance(consumer)) {
            return;
        }
        try {
            beginBlockMethod.invoke(consumer, IrisCompat.terrainBlockId(state), (byte)0, (byte)state.getLightEmission(), localX, localY, localZ);
        }
        catch (Throwable throwable) {
            IrisCompat.disableTerrainMetadata();
        }
    }

    public static void endTerrainBlock(VertexConsumer consumer) {
        IrisCompat.initializeTerrainMetadata();
        if (blockSensitiveBufferBuilderClass == null || !blockSensitiveBufferBuilderClass.isInstance(consumer)) {
            return;
        }
        try {
            endBlockMethod.invoke(consumer, new Object[0]);
        }
        catch (Throwable throwable) {
            IrisCompat.disableTerrainMetadata();
        }
    }

    private static boolean invokeBool(Method method) {
        if (!initialized) {
            IrisCompat.initialize();
        }
        if (apiInstance == null || method == null) {
            return false;
        }
        try {
            return (Boolean)method.invoke(apiInstance, new Object[0]);
        }
        catch (Throwable throwable) {
            return false;
        }
    }

    private static void initialize() {
        initialized = true;
        for (String className : API_CLASS_NAMES) {
            try {
                Class<?> apiClass = Class.forName(className);
                apiInstance = apiClass.getMethod("getInstance", new Class[0]).invoke(null, new Object[0]);
                isShaderPackInUseMethod = apiClass.getMethod("isShaderPackInUse", new Class[0]);
                try {
                    isRenderingShadowPassMethod = apiClass.getMethod("isRenderingShadowPass", new Class[0]);
                }
                catch (Throwable ignored) {
                    isRenderingShadowPassMethod = null;
                }
                return;
            }
            catch (Throwable throwable) {
            }
        }
    }

    private static void initializeTerrainMetadata() {
        if (terrainMetadataInitialized) {
            return;
        }
        terrainMetadataInitialized = true;
        try {
            blockSensitiveBufferBuilderClass = Class.forName("net.irisshaders.iris.vertices.BlockSensitiveBufferBuilder");
            beginBlockMethod = blockSensitiveBufferBuilderClass.getMethod("beginBlock", Integer.TYPE, Byte.TYPE, Byte.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE);
            endBlockMethod = blockSensitiveBufferBuilderClass.getMethod("endBlock", new Class[0]);
        }
        catch (Throwable throwable) {
            IrisCompat.disableTerrainMetadata();
            return;
        }
        try {
            Class<?> settingsClass = Class.forName("net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings");
            Field instanceField = settingsClass.getField("INSTANCE");
            worldRenderingSettingsInstance = instanceField.get(null);
            getBlockStateIdsMethod = settingsClass.getMethod("getBlockStateIds", new Class[0]);
        }
        catch (Throwable throwable) {
            worldRenderingSettingsInstance = null;
            getBlockStateIdsMethod = null;
        }
    }

    private static int terrainBlockId(BlockState state) {
        if (worldRenderingSettingsInstance != null && getBlockStateIdsMethod != null) {
            try {
                Object2IntMap idMap;
                int id;
                Object ids = getBlockStateIdsMethod.invoke(worldRenderingSettingsInstance, new Object[0]);
                if (ids instanceof Object2IntMap && (id = (idMap = (Object2IntMap)ids).getOrDefault((Object)state, -1)) != -1) {
                    return id;
                }
            }
            catch (Throwable throwable) {
                worldRenderingSettingsInstance = null;
                getBlockStateIdsMethod = null;
            }
        }
        return IrisCompat.legacyFoliageBlockId(state);
    }

    private static int legacyFoliageBlockId(BlockState state) {
        if (state.is(Blocks.SHORT_GRASS) || state.is(Blocks.FERN)) {
            return 31;
        }
        if (state.is(Blocks.TALL_GRASS) || state.is(Blocks.LARGE_FERN)) {
            return 175;
        }
        return -1;
    }

    private static void disableTerrainMetadata() {
        blockSensitiveBufferBuilderClass = null;
        beginBlockMethod = null;
        endBlockMethod = null;
        worldRenderingSettingsInstance = null;
        getBlockStateIdsMethod = null;
    }
}

