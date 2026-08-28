/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.irisshaders.iris.helpers.FakeChainedJsonException
 *  net.irisshaders.iris.pipeline.IrisRenderingPipeline
 *  net.irisshaders.iris.pipeline.programs.ShaderKey
 *  net.irisshaders.iris.shaderpack.programs.ProgramSet
 *  net.irisshaders.iris.shaderpack.programs.ProgramSource
 *  net.irisshaders.iris.shadows.ShadowRenderTargets
 *  net.minecraft.client.renderer.ShaderInstance
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.diebuddies.mixins.ocean;

import java.util.Optional;
import net.diebuddies.compat.Iris;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.ocean.PhysicsExtendedPipeline;
import net.diebuddies.physics.ocean.ProgramSetOcean;
import net.irisshaders.iris.helpers.FakeChainedJsonException;
import net.irisshaders.iris.pipeline.IrisRenderingPipeline;
import net.irisshaders.iris.pipeline.programs.ShaderKey;
import net.irisshaders.iris.shaderpack.programs.ProgramSet;
import net.irisshaders.iris.shaderpack.programs.ProgramSource;
import net.irisshaders.iris.shadows.ShadowRenderTargets;
import net.minecraft.client.renderer.ShaderInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={IrisRenderingPipeline.class}, remap=false)
public class MixinNewWorldRenderingPipeline
implements PhysicsExtendedPipeline {
    @Unique
    private ProgramSet physicsmod$programSet;
    @Unique
    private ShaderInstance physicsmod$oceanShader;
    @Unique
    private ShaderInstance physicsmod$oceanShadowShader;
    @Unique
    private ShaderInstance physicsmod$liquidShader;
    @Unique
    private ShaderInstance physicsmod$liquidShadowShader;
    @Unique
    private boolean physicsmod$renderOceanShadow;
    @Unique
    private boolean physicsmod$renderLiquidShadow;
    @Shadow
    private ShadowRenderTargets shadowRenderTargets;

    @Shadow
    public ShaderInstance createShader(String name, Optional<ProgramSource> source, ShaderKey key) {
        return null;
    }

    @Shadow
    public ShaderInstance createShadowShader(String name, Optional<ProgramSource> source, ShaderKey key) {
        return null;
    }

    @Inject(at={@At(value="TAIL")}, method={"<init>"})
    public void constructor(ProgramSet programSet, CallbackInfo info) {
        FakeChainedJsonException fake;
        this.physicsmod$programSet = programSet;
        try {
            this.physicsmod$oceanShader = this.createShader("physics_ocean", ((ProgramSetOcean)programSet).getOceanSource().requireValid(), ShaderKey.TERRAIN_TRANSLUCENT);
            boolean bl = this.physicsmod$renderOceanShadow = this.shadowRenderTargets != null;
            if (this.physicsmod$renderOceanShadow) {
                this.physicsmod$oceanShadowShader = this.createShadowShader("physics_ocean_shadow", ((ProgramSetOcean)programSet).getOceanShadowSource().requireValid(), ShaderKey.SHADOW_TERRAIN_CUTOUT);
            }
            StarterClient.logger.info("constructed ocean shader successfully");
        }
        catch (Exception e) {
            this.physicsmod$oceanShader = null;
            this.physicsmod$oceanShadowShader = null;
            StarterClient.logger.info("failed constructing ocean shader");
            Iris.oceanError = "This shader (or shaderpack settings) is not supported by ocean physics!";
            if (e instanceof FakeChainedJsonException) {
                fake = (FakeChainedJsonException)e;
                fake.getTrueException().printStackTrace();
            }
            e.printStackTrace();
        }
        try {
            this.physicsmod$liquidShader = this.createShader("physics_liquids", ((ProgramSetOcean)programSet).getLiquidsSource().requireValid(), ShaderKey.TERRAIN_TRANSLUCENT);
            boolean bl = this.physicsmod$renderLiquidShadow = this.shadowRenderTargets != null;
            if (this.physicsmod$renderLiquidShadow) {
                Iris.compilingLiquidShadowShader.set(true);
                this.physicsmod$liquidShadowShader = this.createShadowShader("physics_liquids_shadow", ((ProgramSetOcean)programSet).getLiquidsShadowSource().requireValid(), ShaderKey.SHADOW_TERRAIN_CUTOUT);
                Iris.compilingLiquidShadowShader.set(false);
            }
            StarterClient.logger.info("constructed liquids shader successfully");
        }
        catch (Exception e) {
            this.physicsmod$liquidShader = null;
            this.physicsmod$liquidShadowShader = null;
            Iris.compilingLiquidShadowShader.set(false);
            StarterClient.logger.info("failed constructing liquids shader");
            Iris.liquidsError = "This shader (or shaderpack settings) is not supported by liquid physics!";
            if (e instanceof FakeChainedJsonException) {
                fake = (FakeChainedJsonException)e;
                fake.getTrueException().printStackTrace();
            }
            e.printStackTrace();
        }
    }

    @Override
    public ShaderInstance physicsmod$getOceanShader() {
        return this.physicsmod$oceanShader;
    }

    @Override
    public ShaderInstance physicsmod$getOceanShadowShader() {
        return this.physicsmod$oceanShadowShader;
    }

    @Override
    public ShaderInstance physicsmod$getLiquidShader() {
        return this.physicsmod$liquidShader;
    }

    @Override
    public ShaderInstance physicsmod$getLiquidShadowShader() {
        return this.physicsmod$liquidShadowShader;
    }

    @Override
    public boolean physicsmod$renderOceanShadow() {
        return this.physicsmod$renderOceanShadow;
    }

    @Override
    public boolean physicsmod$renderLiquidShadow() {
        return this.physicsmod$renderLiquidShadow;
    }
}

