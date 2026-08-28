/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.texture.AbstractTexture
 *  net.minecraft.client.renderer.texture.TextureManager
 *  net.minecraft.world.level.block.state.BlockState
 *  net.optifine.Config
 *  net.optifine.shaders.BlockAliases
 *  net.optifine.shaders.Program
 *  net.optifine.shaders.Shaders
 *  net.optifine.shaders.ShadersTex
 *  org.joml.Matrix3f
 *  org.joml.Matrix4f
 */
package net.diebuddies.compat;

import java.lang.reflect.Method;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.util.ShaderType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.world.level.block.state.BlockState;
import net.optifine.Config;
import net.optifine.shaders.BlockAliases;
import net.optifine.shaders.Program;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.ShadersTex;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class Optifine {
    public static Program compilingProgram;
    public static Program oceanProgram;
    public static Program oceanShadowProgram;
    public static ShaderType compileStage;
    private static Method getTextureById;
    private static boolean init;

    public static boolean areShadersEnabled() {
        if (!Optifine.initMethods()) {
            return false;
        }
        try {
            return Config.isShaders();
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isShadowPass() {
        if (!Optifine.initMethods()) {
            return false;
        }
        try {
            return Shaders.isShadowPass;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isUsingShadersNoInternal() {
        if (!Optifine.initMethods()) {
            return false;
        }
        try {
            return Shaders.uniform_modelViewMatrix.isDefined();
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void setModelViewMatrix(Matrix4f modelViewMatrix) {
        if (!Optifine.initMethods()) {
            return;
        }
        try {
            Shaders.setModelViewMatrix((Matrix4f)modelViewMatrix);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setProjectionMatrix(Matrix4f projectionMatrix) {
        if (!Optifine.initMethods()) {
            return;
        }
        try {
            Shaders.setProjectionMatrix((Matrix4f)projectionMatrix);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setColorModulator(float[] colors) {
        if (!Optifine.initMethods()) {
            return;
        }
        try {
            Shaders.setColorModulator((float[])colors);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setTextureMatrix(Matrix4f textureMatrix) {
        if (!Optifine.initMethods()) {
            return;
        }
        try {
            Shaders.setTextureMatrix((Matrix4f)textureMatrix);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setNormalMatrix(Matrix3f normal) {
        if (!Optifine.initMethods()) {
            return;
        }
        try {
            if (Shaders.uniform_normalMatrix.isDefined()) {
                Shaders.uniform_normalMatrix.setValue(normal);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean bindPBRTexture(int glID) {
        if (!Optifine.initMethods()) {
            return false;
        }
        boolean isDefaultTexture = false;
        try {
            AbstractTexture texture = (AbstractTexture)getTextureById.invoke(Minecraft.getInstance().getTextureManager(), glID);
            if (texture == null) {
                texture = Shaders.defaultTexture;
                isDefaultTexture = true;
            }
            ShadersTex.bindTexture((AbstractTexture)texture);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return isDefaultTexture;
    }

    public static int getMaterialID(BlockState state) {
        if (!Optifine.initMethods()) {
            return -1;
        }
        try {
            return BlockAliases.getAliasBlockId((BlockState)state);
        }
        catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int getRenderType(BlockState state) {
        if (!Optifine.initMethods()) {
            return -1;
        }
        try {
            return BlockAliases.getRenderType((BlockState)state);
        }
        catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static void useEntityShader() {
        if (!Optifine.initMethods()) {
            return;
        }
        Shaders.useProgram((Program)Shaders.ProgramEntities);
    }

    public static void useOceanShader() {
        if (!Optifine.initMethods()) {
            return;
        }
        Shaders.useProgram((Program)oceanProgram);
    }

    public static void useOceanShadowShader() {
        if (!Optifine.initMethods()) {
            return;
        }
        boolean before = Shaders.isShadowPass;
        Shaders.isShadowPass = false;
        Shaders.useProgram((Program)oceanShadowProgram);
        Shaders.isShadowPass = before;
    }

    public static void useWaterShader() {
        if (!Optifine.initMethods()) {
            return;
        }
        Shaders.useProgram((Program)Shaders.ProgramWater);
    }

    private static boolean initMethods() {
        if (!StarterClient.optifabric) {
            return false;
        }
        if (init) {
            return true;
        }
        init = true;
        try {
            Class<TextureManager> textureManager = TextureManager.class;
            getTextureById = textureManager.getMethod("getTextureById", Integer.TYPE);
        }
        catch (NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
        return true;
    }

    static {
        init = false;
    }
}

