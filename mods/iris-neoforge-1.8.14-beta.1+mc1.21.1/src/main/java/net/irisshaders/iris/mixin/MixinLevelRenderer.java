/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Camera
 *  net.minecraft.client.DeltaTracker
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.LightTexture
 *  net.minecraft.client.renderer.RenderBuffers
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.culling.Frustum
 *  net.minecraft.network.chat.Component
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyArg
 *  org.spongepowered.asm.mixin.injection.ModifyVariable
 *  org.spongepowered.asm.mixin.injection.Slice
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.compat.dh.DHCompat;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.layer.IsOutlineRenderStateShard;
import net.irisshaders.iris.layer.OuterWrappedRenderType;
import net.irisshaders.iris.mixin.LevelRendererAccessor;
import net.irisshaders.iris.pathways.HandRenderer;
import net.irisshaders.iris.pipeline.WorldRenderingPhase;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;
import net.irisshaders.iris.shadows.frustum.fallback.NonCullingFrustum;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.irisshaders.iris.uniforms.IrisTimeUniforms;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LevelRenderer.class})
public class MixinLevelRenderer {
    @Unique
    private static final String CLEAR = "Lcom/mojang/blaze3d/systems/RenderSystem;clear(IZ)V";
    @Unique
    private static final String RENDER_SKY = "Lnet/minecraft/client/renderer/LevelRenderer;renderSky(Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V";
    @Unique
    private static final String RENDER_CLOUDS = "Lnet/minecraft/client/renderer/LevelRenderer;renderClouds(Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;FDDD)V";
    @Unique
    private static final String RENDER_WEATHER = "Lnet/minecraft/client/renderer/LevelRenderer;renderSnowAndRain(Lnet/minecraft/client/renderer/LightTexture;FDDD)V";
    @Shadow
    @Final
    private Minecraft minecraft;
    @Unique
    private WorldRenderingPipeline pipeline;
    @Shadow
    private RenderBuffers renderBuffers;
    @Shadow
    private int ticks;
    @Shadow
    private Frustum cullingFrustum;
    @Shadow
    @Nullable
    private ClientLevel level;
    @Unique
    private boolean warned;
    @Unique
    private NonCullingFrustum overrideFrustum;

    @Inject(method={"renderLevel"}, at={@At(value="HEAD")})
    private void iris$setupPipeline(DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f modelView, Matrix4f projection, CallbackInfo callback) {
        DHCompat.checkFrame();
        IrisTimeUniforms.updateTime();
        CapturedRenderingState.INSTANCE.setGbufferModelView((Matrix4fc)modelView);
        CapturedRenderingState.INSTANCE.setGbufferProjection(projection);
        float fakeTickDelta = deltaTracker.getGameTimeDeltaPartialTick(false);
        CapturedRenderingState.INSTANCE.setTickDelta(fakeTickDelta);
        CapturedRenderingState.INSTANCE.setCloudTime(((float)this.ticks + fakeTickDelta) * 0.03f);
        this.pipeline = Iris.getPipelineManager().preparePipeline(Iris.getCurrentDimension());
        if (this.pipeline.shouldDisableFrustumCulling()) {
            this.overrideFrustum = new NonCullingFrustum();
            this.overrideFrustum.prepare(camera.getPosition().x, camera.getPosition().y, camera.getPosition().z);
            this.cullingFrustum = this.overrideFrustum;
        } else {
            this.overrideFrustum = null;
        }
        IrisRenderSystem.backupAndDisableCullingState(this.pipeline.shouldDisableOcclusionCulling());
        if (Iris.shouldActivateWireframe() && this.minecraft.isLocalServer()) {
            IrisRenderSystem.setPolygonMode(6913);
        }
    }

    @ModifyArg(method={"renderLevel"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/LevelRenderer;setupRender(Lnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/culling/Frustum;ZZ)V"), index=1)
    private Frustum changeFrustum(Frustum frustum) {
        return this.overrideFrustum != null ? this.overrideFrustum : frustum;
    }

    @Inject(method={"renderLevel"}, at={@At(value="INVOKE", target="Lcom/mojang/blaze3d/systems/RenderSystem;clear(IZ)V", shift=At.Shift.AFTER, remap=false)})
    private void iris$beginLevelRender(DeltaTracker deltaTracker, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
        this.pipeline.beginLevelRendering();
        this.pipeline.setPhase(WorldRenderingPhase.NONE);
    }

    @Inject(method={"renderLevel"}, at={@At(value="RETURN", shift=At.Shift.BEFORE)})
    private void iris$endLevelRender(DeltaTracker deltaTracker, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f modelMatrix, Matrix4f matrix4f2, CallbackInfo ci) {
        HandRenderer.INSTANCE.renderTranslucent((Matrix4fc)modelMatrix, deltaTracker.getGameTimeDeltaPartialTick(true), camera, gameRenderer, this.pipeline);
        Minecraft.getInstance().getProfiler().popPush("iris_final");
        this.pipeline.finalizeLevelRendering();
        this.pipeline = null;
        if (!this.warned) {
            this.warned = true;
            Iris.getUpdateChecker().getBetaInfo().ifPresent(info -> Minecraft.getInstance().gui.getChat().addMessage((Component)Component.literal((String)("A new beta is out for Iris " + info.betaTag + ". Please redownload it.")).withStyle(new ChatFormatting[]{ChatFormatting.BOLD, ChatFormatting.RED})));
        }
        IrisRenderSystem.restoreCullingState();
        if (Iris.shouldActivateWireframe() && this.minecraft.isLocalServer()) {
            IrisRenderSystem.setPolygonMode(6914);
        }
    }

    @Inject(method={"renderLevel"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/LevelRenderer;renderSky(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V")})
    private void iris$renderTerrainShadows(DeltaTracker deltaTracker, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
        this.pipeline.renderShadows((LevelRendererAccessor)((Object)this), camera);
    }

    @ModifyVariable(method={"renderSky"}, at=@At(value="HEAD"), index=5, argsOnly=true)
    private boolean iris$alwaysRenderSky(boolean value) {
        return false;
    }

    @Inject(method={"renderLevel"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/LevelRenderer;renderSky(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V")})
    private void iris$beginSky(DeltaTracker deltaTracker, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
        this.pipeline.setPhase(WorldRenderingPhase.CUSTOM_SKY);
        RenderSystem.setShader(GameRenderer::getPositionShader);
    }

    @Inject(method={"renderSky"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/FogRenderer;levelFogColor()V")})
    private void iris$renderSky$beginNormalSky(Matrix4f matrix4f, Matrix4f matrix4f2, float f, Camera camera, boolean bl, Runnable runnable, CallbackInfo ci) {
        this.pipeline.setPhase(WorldRenderingPhase.SKY);
    }

    @Inject(method={"renderSky"}, at={@At(value="FIELD", target="Lnet/minecraft/client/renderer/LevelRenderer;SUN_LOCATION:Lnet/minecraft/resources/ResourceLocation;")})
    private void iris$setSunRenderStage(Matrix4f matrix4f, Matrix4f matrix4f2, float f, Camera camera, boolean bl, Runnable runnable, CallbackInfo ci) {
        this.pipeline.setPhase(WorldRenderingPhase.SUN);
    }

    @Inject(method={"renderSky"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/DimensionSpecialEffects;getSunriseColor(FF)[F")})
    private void iris$setSunsetRenderStage(Matrix4f matrix4f, Matrix4f matrix4f2, float f, Camera camera, boolean bl, Runnable runnable, CallbackInfo ci) {
        this.pipeline.setPhase(WorldRenderingPhase.SUNSET);
    }

    @Inject(method={"renderSky"}, at={@At(value="FIELD", target="Lnet/minecraft/client/renderer/LevelRenderer;MOON_LOCATION:Lnet/minecraft/resources/ResourceLocation;")})
    private void iris$setMoonRenderStage(Matrix4f matrix4f, Matrix4f matrix4f2, float f, Camera camera, boolean bl, Runnable runnable, CallbackInfo ci) {
        this.pipeline.setPhase(WorldRenderingPhase.MOON);
    }

    @Inject(method={"renderSky"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/multiplayer/ClientLevel;getStarBrightness(F)F")})
    private void iris$setStarRenderStage(Matrix4f matrix4f, Matrix4f matrix4f2, float f, Camera camera, boolean bl, Runnable runnable, CallbackInfo ci) {
        this.pipeline.setPhase(WorldRenderingPhase.STARS);
    }

    @Inject(method={"renderSky"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/player/LocalPlayer;getEyePosition(F)Lnet/minecraft/world/phys/Vec3;")})
    private void iris$setVoidRenderStage(Matrix4f matrix4f, Matrix4f matrix4f2, float f, Camera camera, boolean bl, Runnable runnable, CallbackInfo ci) {
        this.pipeline.setPhase(WorldRenderingPhase.VOID);
    }

    @Inject(method={"renderSky"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/multiplayer/ClientLevel;getTimeOfDay(F)F")}, slice={@Slice(from=@At(value="FIELD", target="Lcom/mojang/math/Axis;YP:Lcom/mojang/math/Axis;"))})
    private void iris$renderSky$tiltSun(Matrix4f matrix4f, Matrix4f matrix4f2, float f, Camera camera, boolean bl, Runnable runnable, CallbackInfo ci, @Local PoseStack poseStack) {
        poseStack.mulPose(Axis.ZP.rotationDegrees(this.pipeline.getSunPathRotation()));
    }

    @Inject(method={"renderLevel"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/LevelRenderer;renderSky(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V", shift=At.Shift.AFTER)})
    private void iris$endSky(DeltaTracker deltaTracker, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
        this.pipeline.setPhase(WorldRenderingPhase.NONE);
    }

    @Inject(method={"renderLevel"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/LevelRenderer;renderClouds(Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;FDDD)V")})
    private void iris$beginClouds(DeltaTracker deltaTracker, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
        this.pipeline.setPhase(WorldRenderingPhase.CLOUDS);
    }

    @Inject(method={"renderLevel"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/LevelRenderer;renderClouds(Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;FDDD)V", shift=At.Shift.AFTER)})
    private void iris$endClouds(DeltaTracker deltaTracker, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
        this.pipeline.setPhase(WorldRenderingPhase.NONE);
    }

    @Inject(method={"renderSectionLayer"}, at={@At(value="HEAD")})
    private void iris$beginTerrainLayer(RenderType renderType, double d, double e, double f, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
        this.pipeline.setPhase(WorldRenderingPhase.fromTerrainRenderType(renderType));
    }

    @Inject(method={"renderSectionLayer"}, at={@At(value="RETURN")})
    private void iris$endTerrainLayer(RenderType renderType, double d, double e, double f, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
        this.pipeline.setPhase(WorldRenderingPhase.NONE);
    }

    @Inject(method={"renderLevel"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/LevelRenderer;renderSnowAndRain(Lnet/minecraft/client/renderer/LightTexture;FDDD)V")})
    private void iris$beginWeather(DeltaTracker deltaTracker, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
        this.pipeline.setPhase(WorldRenderingPhase.RAIN_SNOW);
    }

    @ModifyArg(method={"Lnet/minecraft/client/renderer/LevelRenderer;renderSnowAndRain(Lnet/minecraft/client/renderer/LightTexture;FDDD)V"}, at=@At(value="INVOKE", target="Lcom/mojang/blaze3d/systems/RenderSystem;depthMask(Z)V", ordinal=0, remap=false))
    private boolean iris$writeRainAndSnowToDepthBuffer(boolean depthMaskEnabled) {
        if (this.pipeline.shouldWriteRainAndSnowToDepthBuffer()) {
            return true;
        }
        return depthMaskEnabled;
    }

    @Inject(method={"renderLevel"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/LevelRenderer;renderSnowAndRain(Lnet/minecraft/client/renderer/LightTexture;FDDD)V", shift=At.Shift.AFTER)})
    private void iris$endWeather(DeltaTracker deltaTracker, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
        this.pipeline.setPhase(WorldRenderingPhase.NONE);
    }

    @Inject(method={"renderLevel"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/LevelRenderer;renderWorldBorder(Lnet/minecraft/client/Camera;)V")})
    private void iris$beginWorldBorder(DeltaTracker deltaTracker, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
        this.pipeline.setPhase(WorldRenderingPhase.WORLD_BORDER);
    }

    @Inject(method={"renderLevel"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/LevelRenderer;renderWorldBorder(Lnet/minecraft/client/Camera;)V", shift=At.Shift.AFTER)})
    private void iris$endWorldBorder(DeltaTracker deltaTracker, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
        this.pipeline.setPhase(WorldRenderingPhase.NONE);
    }

    @Inject(method={"renderLevel"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/debug/DebugRenderer;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;DDD)V")})
    private void iris$setDebugRenderStage(DeltaTracker deltaTracker, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
        this.pipeline.setPhase(WorldRenderingPhase.DEBUG);
    }

    @Inject(method={"renderLevel"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/debug/DebugRenderer;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;DDD)V", shift=At.Shift.AFTER)})
    private void iris$resetDebugRenderStage(DeltaTracker deltaTracker, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
        this.pipeline.setPhase(WorldRenderingPhase.NONE);
    }

    @ModifyArg(method={"renderLevel"}, at=@At(value="INVOKE", target="net/minecraft/client/renderer/MultiBufferSource$BufferSource.getBuffer (Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"), slice=@Slice(from=@At(value="CONSTANT", args={"stringValue=outline"}), to=@At(value="INVOKE", target="net/minecraft/client/renderer/LevelRenderer.renderHitOutline (Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V")))
    private RenderType iris$beginBlockOutline(RenderType type) {
        return new OuterWrappedRenderType("iris:is_outline", type, IsOutlineRenderStateShard.INSTANCE);
    }

    @Inject(method={"renderLevel"}, at={@At(value="CONSTANT", args={"stringValue=translucent"})})
    private void iris$beginTranslucents(DeltaTracker deltaTracker, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f modelMatrix, Matrix4f matrix4f2, CallbackInfo ci) {
        this.pipeline.beginHand();
        HandRenderer.INSTANCE.renderSolid((Matrix4fc)modelMatrix, deltaTracker.getGameTimeDeltaPartialTick(true), camera, gameRenderer, this.pipeline);
        Minecraft.getInstance().getProfiler().popPush("iris_pre_translucent");
        this.pipeline.beginTranslucents();
    }
}

