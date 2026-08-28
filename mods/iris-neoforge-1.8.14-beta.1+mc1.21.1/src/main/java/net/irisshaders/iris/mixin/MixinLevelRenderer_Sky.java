/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.material.FogType
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix4f
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.mixin;

import net.irisshaders.iris.Iris;
import net.irisshaders.iris.mixin.LevelRendererAccessor;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LevelRenderer.class})
public class MixinLevelRenderer_Sky {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method={"renderSky"}, at={@At(value="HEAD")}, cancellable=true)
    private void preRenderSky(Matrix4f matrix4f, Matrix4f matrix4f2, float f, Camera camera, boolean bl, Runnable runnable, CallbackInfo ci) {
        if (Iris.getCurrentPack().isEmpty()) {
            boolean useThickFog;
            Vec3 cameraPosition = camera.getPosition();
            Entity cameraEntity = camera.getEntity();
            boolean isSubmersed = camera.getFluidInCamera() != FogType.NONE;
            boolean blockSky = ((LevelRendererAccessor)Minecraft.getInstance().levelRenderer).invokeDoesMobEffectBlockSky(camera);
            boolean bl2 = useThickFog = this.minecraft.level.effects().isFoggyAt(Mth.floor((double)cameraPosition.x()), Mth.floor((double)cameraPosition.y())) || this.minecraft.gui.getBossOverlay().shouldCreateWorldFog();
            if (isSubmersed || blockSky || useThickFog) {
                ci.cancel();
            }
        }
    }
}

