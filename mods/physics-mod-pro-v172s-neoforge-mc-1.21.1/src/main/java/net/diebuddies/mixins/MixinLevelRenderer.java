/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Camera
 *  net.minecraft.client.DeltaTracker
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.LightTexture
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.world.level.Level
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.diebuddies.mixins;

import net.diebuddies.minecraft.LevelRendererAccessor;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.render.MainRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LevelRenderer.class})
public class MixinLevelRenderer
implements LevelRendererAccessor {
    @Shadow
    private ClientLevel level;
    @Unique
    private MainRenderer mainRenderer = new MainRenderer();
    @Unique
    private Matrix4f prjSnow = new Matrix4f();
    @Unique
    private Matrix4f viewSnow = new Matrix4f();

    @Inject(at={@At(value="TAIL")}, method={"renderSectionLayer"})
    private void renderClothOptifine(RenderType blockLayerIn, double xIn, double yIn, double zInm, Matrix4f viewMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
        if (blockLayerIn == RenderType.translucent()) {
            // empty if block
        }
    }

    @Inject(at={@At(value="TAIL")}, method={"renderSectionLayer"})
    private void renderMain(RenderType blockLayerIn, double xIn, double yIn, double zInm, Matrix4f viewMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
        if (blockLayerIn == RenderType.cutout()) {
            this.mainRenderer.renderAll(this.level, blockLayerIn, xIn, yIn, zInm, viewMatrix, projectionMatrix);
        }
    }

    @Inject(at={@At(value="HEAD")}, method={"renderSectionLayer"})
    private void renderLiquid(RenderType blockLayerIn, double xIn, double yIn, double zInm, Matrix4f viewMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
        if (blockLayerIn == RenderType.translucent()) {
            this.mainRenderer.renderLiquid(this.level, blockLayerIn, xIn, yIn, zInm, viewMatrix, projectionMatrix);
            this.mainRenderer.renderCloth(this.level, blockLayerIn, xIn, yIn, zInm, viewMatrix, projectionMatrix);
        }
    }

    @Inject(at={@At(value="HEAD")}, method={"renderLevel"})
    public void getSnowWorldViewProjection(DeltaTracker deltaTracker, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f viewMatrix, Matrix4f projectionMatrix, CallbackInfo info) {
        if (this.level != null) {
            PhysicsMod mod = PhysicsMod.getInstance((Level)this.level);
            this.prjSnow.set((Matrix4fc)projectionMatrix);
            this.viewSnow.set((Matrix4fc)viewMatrix);
            this.prjSnow.mul((Matrix4fc)this.viewSnow, mod.getPhysicsWorld().getSnowWorld().viewProjection);
        }
    }

    @Override
    public MainRenderer getMainRenderer() {
        return this.mainRenderer;
    }
}

