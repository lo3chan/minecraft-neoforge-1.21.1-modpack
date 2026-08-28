/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.neoforged.neoforge.client.event.RenderLevelStageEvent
 *  net.neoforged.neoforge.client.event.RenderLevelStageEvent$Stage
 *  org.joml.Matrix4f
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.mixin.forge;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.mixin.LevelRendererAccessor;
import net.irisshaders.iris.platform.IrisPlatformHelpers;
import net.irisshaders.iris.shadows.ShadowRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ShadowRenderer.class})
public class MixinShadowRenderer {
    @Unique
    private static MethodHandle IEhandle;

    @Inject(method={"<clinit>"}, at={@At(value="TAIL")})
    private static void init(CallbackInfo ci) {
        try {
            IEhandle = MethodHandles.lookup().findStatic(Class.forName("blusunrize.immersiveengineering.client.utils.VertexBufferHolder"), "afterTERRendering", MethodType.methodType(Void.TYPE, RenderLevelStageEvent.class));
        }
        catch (Throwable e) {
            if (IrisPlatformHelpers.getInstance().isModLoaded("immersiveengineering")) {
                Iris.logger.error("Failed to load IE compatibility?", e);
            }
            IEhandle = null;
        }
    }

    @Inject(method={"renderShadows"}, at={@At(value="INVOKE", target="Lnet/irisshaders/batchedentityrendering/impl/FullyBufferedMultiBufferSource;readyUp()V")})
    private void render(LevelRendererAccessor levelRenderer, Camera playerCamera, CallbackInfo ci, @Local PoseStack modelView, @Local Matrix4f shadowProjection) {
        if (IEhandle != null) {
            try {
                IEhandle.invokeExact(new RenderLevelStageEvent(RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES, Minecraft.getInstance().levelRenderer, new PoseStack(), new Matrix4f(), shadowProjection, 0, Minecraft.getInstance().getTimer(), playerCamera, ShadowRenderer.FRUSTUM));
            }
            catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }
}

