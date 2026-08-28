/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.ShaderInstance
 */
package net.diebuddies.physics.ocean;

import net.minecraft.client.renderer.ShaderInstance;

public interface PhysicsExtendedPipeline {
    public ShaderInstance physicsmod$getOceanShader();

    public ShaderInstance physicsmod$getOceanShadowShader();

    public ShaderInstance physicsmod$getLiquidShader();

    public ShaderInstance physicsmod$getLiquidShadowShader();

    public boolean physicsmod$renderOceanShadow();

    public boolean physicsmod$renderLiquidShadow();
}

