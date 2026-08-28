/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager$BlendState
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.client.renderer.texture.AbstractTexture
 *  net.minecraft.client.renderer.texture.TextureAtlas
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.HumanoidArm
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.LightLayer
 *  net.minecraft.world.level.material.FogType
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Math
 *  org.joml.Vector2f
 *  org.joml.Vector2i
 *  org.joml.Vector3d
 *  org.joml.Vector4f
 *  org.joml.Vector4i
 */
package net.irisshaders.iris.uniforms;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.irisshaders.iris.compat.dh.DHCompat;
import net.irisshaders.iris.gl.state.FogMode;
import net.irisshaders.iris.gl.state.StateUpdateNotifiers;
import net.irisshaders.iris.gl.uniform.DynamicUniformHolder;
import net.irisshaders.iris.gl.uniform.UniformHolder;
import net.irisshaders.iris.gl.uniform.UniformUpdateFrequency;
import net.irisshaders.iris.helpers.JomlConversions;
import net.irisshaders.iris.layer.GbufferPrograms;
import net.irisshaders.iris.mixin.GlStateManagerAccessor;
import net.irisshaders.iris.mixin.statelisteners.BooleanStateAccessor;
import net.irisshaders.iris.mixin.texture.TextureAtlasAccessor;
import net.irisshaders.iris.mixinterface.LocalPlayerInterface;
import net.irisshaders.iris.pbr.TextureInfoCache;
import net.irisshaders.iris.pbr.TextureTracker;
import net.irisshaders.iris.shaderpack.IdMap;
import net.irisshaders.iris.shaderpack.properties.PackDirectives;
import net.irisshaders.iris.uniforms.BiomeUniforms;
import net.irisshaders.iris.uniforms.CameraUniforms;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.irisshaders.iris.uniforms.CelestialUniforms;
import net.irisshaders.iris.uniforms.ExternallyManagedUniforms;
import net.irisshaders.iris.uniforms.FogUniforms;
import net.irisshaders.iris.uniforms.FrameUpdateNotifier;
import net.irisshaders.iris.uniforms.IdMapUniforms;
import net.irisshaders.iris.uniforms.IrisExclusiveUniforms;
import net.irisshaders.iris.uniforms.IrisInternalUniforms;
import net.irisshaders.iris.uniforms.IrisTimeUniforms;
import net.irisshaders.iris.uniforms.MatrixUniforms;
import net.irisshaders.iris.uniforms.SystemTimeUniforms;
import net.irisshaders.iris.uniforms.ViewportUniforms;
import net.irisshaders.iris.uniforms.WorldTimeUniforms;
import net.irisshaders.iris.uniforms.transforms.SmoothedFloat;
import net.irisshaders.iris.uniforms.transforms.SmoothedVec2f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.joml.Vector4f;
import org.joml.Vector4i;

public final class CommonUniforms {
    private static final Minecraft client = Minecraft.getInstance();
    private static final Vector2i ZERO_VECTOR_2i = new Vector2i();
    private static final Vector4i ZERO_VECTOR_4i = new Vector4i(0, 0, 0, 0);
    private static final Vector3d ZERO_VECTOR_3d = new Vector3d();

    private CommonUniforms() {
    }

    public static void addDynamicUniforms(DynamicUniformHolder uniforms, FogMode fogMode) {
        ExternallyManagedUniforms.addExternallyManagedUniforms117(uniforms);
        FogUniforms.addFogUniforms(uniforms, fogMode);
        IrisInternalUniforms.addFogUniforms(uniforms, fogMode);
        uniforms.uniform1i("entityId", CapturedRenderingState.INSTANCE::getCurrentRenderedEntity, StateUpdateNotifiers.fallbackEntityNotifier);
        uniforms.uniform2i("atlasSize", () -> {
            int glId = RenderSystem.getShaderTexture((int)0);
            AbstractTexture texture = TextureTracker.INSTANCE.getTexture(glId);
            if (texture instanceof TextureAtlas) {
                TextureAtlas atlas = (TextureAtlas)texture;
                TextureAtlasAccessor atlasAccessor = (TextureAtlasAccessor)atlas;
                return new Vector2i(atlasAccessor.callGetWidth(), atlasAccessor.callGetHeight());
            }
            return ZERO_VECTOR_2i;
        }, listener -> {});
        uniforms.uniform2i("gtextureSize", () -> {
            int glId = GlStateManagerAccessor.getTEXTURES()[0].binding;
            TextureInfoCache.TextureInfo info = TextureInfoCache.INSTANCE.getInfo(glId);
            return new Vector2i(info.getWidth(), info.getHeight());
        }, StateUpdateNotifiers.bindTextureNotifier);
        uniforms.uniform4i("blendFunc", () -> {
            GlStateManager.BlendState blend = GlStateManagerAccessor.getBLEND();
            if (((BooleanStateAccessor)blend.mode).isEnabled()) {
                return new Vector4i(blend.srcRgb, blend.dstRgb, blend.srcAlpha, blend.dstAlpha);
            }
            return ZERO_VECTOR_4i;
        }, StateUpdateNotifiers.blendFuncNotifier);
        uniforms.uniform1i("renderStage", () -> GbufferPrograms.getCurrentPhase().ordinal(), StateUpdateNotifiers.phaseChangeNotifier);
    }

    public static void addCommonUniforms(DynamicUniformHolder uniforms, IdMap idMap, PackDirectives directives, FrameUpdateNotifier updateNotifier, FogMode fogMode) {
        CommonUniforms.addNonDynamicUniforms(uniforms, idMap, directives, updateNotifier);
        CommonUniforms.addDynamicUniforms(uniforms, fogMode);
    }

    public static void addNonDynamicUniforms(UniformHolder uniforms, IdMap idMap, PackDirectives directives, FrameUpdateNotifier updateNotifier) {
        CameraUniforms.addCameraUniforms(uniforms, updateNotifier);
        ViewportUniforms.addViewportUniforms(uniforms);
        WorldTimeUniforms.addWorldTimeUniforms(uniforms);
        SystemTimeUniforms.addSystemTimeUniforms(uniforms);
        BiomeUniforms.addBiomeUniforms(uniforms);
        new CelestialUniforms(directives.getSunPathRotation()).addCelestialUniforms(uniforms);
        IrisExclusiveUniforms.addIrisExclusiveUniforms(uniforms);
        IrisTimeUniforms.addTimeUniforms(uniforms);
        MatrixUniforms.addMatrixUniforms(uniforms, directives);
        IdMapUniforms.addIdMapUniforms(updateNotifier, uniforms, idMap, directives.isOldHandLight());
        CommonUniforms.generalCommonUniforms(uniforms, updateNotifier, directives);
    }

    public static void generalCommonUniforms(UniformHolder uniforms, FrameUpdateNotifier updateNotifier, PackDirectives directives) {
        ExternallyManagedUniforms.addExternallyManagedUniforms117(uniforms);
        SmoothedVec2f eyeBrightnessSmooth = new SmoothedVec2f(directives.getEyeBrightnessHalfLife(), directives.getEyeBrightnessHalfLife(), CommonUniforms::getEyeBrightness, updateNotifier);
        uniforms.uniform1b(UniformUpdateFrequency.PER_FRAME, "hideGUI", () -> CommonUniforms.client.options.hideGui).uniform1b(UniformUpdateFrequency.PER_FRAME, "isRightHanded", () -> CommonUniforms.client.options.mainHand().get() == HumanoidArm.RIGHT).uniform1i(UniformUpdateFrequency.PER_FRAME, "isEyeInWater", CommonUniforms::isEyeInWater).uniform1f(UniformUpdateFrequency.PER_FRAME, "blindness", CommonUniforms::getBlindness).uniform1f(UniformUpdateFrequency.PER_FRAME, "darknessFactor", CommonUniforms::getDarknessFactor).uniform1f(UniformUpdateFrequency.PER_FRAME, "darknessLightFactor", CapturedRenderingState.INSTANCE::getDarknessLightFactor).uniform1f(UniformUpdateFrequency.PER_FRAME, "nightVision", CommonUniforms::getNightVision).uniform1b(UniformUpdateFrequency.PER_FRAME, "is_sneaking", CommonUniforms::isSneaking).uniform1b(UniformUpdateFrequency.PER_FRAME, "is_sprinting", CommonUniforms::isSprinting).uniform1b(UniformUpdateFrequency.PER_FRAME, "is_hurt", CommonUniforms::isHurt).uniform1b(UniformUpdateFrequency.PER_FRAME, "is_invisible", CommonUniforms::isInvisible).uniform1b(UniformUpdateFrequency.PER_FRAME, "is_burning", CommonUniforms::isBurning).uniform1b(UniformUpdateFrequency.PER_FRAME, "is_on_ground", CommonUniforms::isOnGround).uniform1f(UniformUpdateFrequency.PER_FRAME, "screenBrightness", () -> (Double)CommonUniforms.client.options.gamma().get()).uniform4f(UniformUpdateFrequency.ONCE, "entityColor", () -> new Vector4f(0.0f, 0.0f, 0.0f, 0.0f)).uniform1i(UniformUpdateFrequency.ONCE, "blockEntityId", () -> -1).uniform1i(UniformUpdateFrequency.ONCE, "currentRenderedItemId", () -> -1).uniform1f(UniformUpdateFrequency.ONCE, "pi", () -> Math.PI).uniform1f(UniformUpdateFrequency.PER_TICK, "playerMood", CommonUniforms::getPlayerMood).uniform1f(UniformUpdateFrequency.PER_TICK, "constantMood", CommonUniforms::getConstantMood).uniform2i(UniformUpdateFrequency.PER_FRAME, "eyeBrightness", CommonUniforms::getEyeBrightness).uniform2i(UniformUpdateFrequency.PER_FRAME, "eyeBrightnessSmooth", () -> {
            Vector2f smoothed = eyeBrightnessSmooth.get();
            return new Vector2i((int)smoothed.x(), (int)smoothed.y());
        }).uniform1f(UniformUpdateFrequency.PER_TICK, "rainStrength", CommonUniforms::getRainStrength).uniform1f(UniformUpdateFrequency.PER_TICK, "wetness", new SmoothedFloat(directives.getWetnessHalfLife(), directives.getDrynessHalfLife(), CommonUniforms::getRainStrength, updateNotifier)).uniform3d(UniformUpdateFrequency.PER_FRAME, "skyColor", CommonUniforms::getSkyColor).uniform1f(UniformUpdateFrequency.PER_FRAME, "dhFarPlane", DHCompat::getFarPlane).uniform1f(UniformUpdateFrequency.PER_FRAME, "dhNearPlane", DHCompat::getNearPlane).uniform1i(UniformUpdateFrequency.PER_FRAME, "dhRenderDistance", DHCompat::getRenderDistance);
    }

    private static boolean isOnGround() {
        return CommonUniforms.client.player != null && CommonUniforms.client.player.onGround();
    }

    private static boolean isHurt() {
        if (CommonUniforms.client.player != null) {
            return CommonUniforms.client.player.hurtTime > 0;
        }
        return false;
    }

    private static boolean isInvisible() {
        if (CommonUniforms.client.player != null) {
            return CommonUniforms.client.player.isInvisible();
        }
        return false;
    }

    private static boolean isBurning() {
        if (CommonUniforms.client.player != null) {
            return CommonUniforms.client.player.isOnFire();
        }
        return false;
    }

    private static boolean isSneaking() {
        if (CommonUniforms.client.player != null) {
            return CommonUniforms.client.player.isCrouching();
        }
        return false;
    }

    private static boolean isSprinting() {
        if (CommonUniforms.client.player != null) {
            return CommonUniforms.client.player.isSprinting();
        }
        return false;
    }

    private static Vector3d getSkyColor() {
        if (CommonUniforms.client.level == null || CommonUniforms.client.cameraEntity == null) {
            return ZERO_VECTOR_3d;
        }
        return JomlConversions.fromVec3(CommonUniforms.client.level.getSkyColor(CommonUniforms.client.cameraEntity.position(), CapturedRenderingState.INSTANCE.getTickDelta()));
    }

    static float getBlindness() {
        MobEffectInstance blindness;
        Entity cameraEntity = client.getCameraEntity();
        if (cameraEntity instanceof LivingEntity && (blindness = ((LivingEntity)cameraEntity).getEffect(MobEffects.BLINDNESS)) != null) {
            if (blindness.isInfiniteDuration()) {
                return 1.0f;
            }
            return org.joml.Math.clamp((float)0.0f, (float)1.0f, (float)((float)blindness.getDuration() / 20.0f));
        }
        return 0.0f;
    }

    static float getDarknessFactor() {
        MobEffectInstance darkness;
        Entity cameraEntity = client.getCameraEntity();
        if (cameraEntity instanceof LivingEntity && (darkness = ((LivingEntity)cameraEntity).getEffect(MobEffects.DARKNESS)) != null) {
            return darkness.getBlendFactor((LivingEntity)cameraEntity, CapturedRenderingState.INSTANCE.getTickDelta());
        }
        return 0.0f;
    }

    private static float getPlayerMood() {
        if (!(CommonUniforms.client.cameraEntity instanceof LocalPlayer)) {
            return 0.0f;
        }
        return org.joml.Math.clamp((float)0.0f, (float)1.0f, (float)((LocalPlayer)CommonUniforms.client.cameraEntity).getCurrentMood());
    }

    private static float getConstantMood() {
        if (!(CommonUniforms.client.cameraEntity instanceof LocalPlayer)) {
            return 0.0f;
        }
        return org.joml.Math.clamp((float)0.0f, (float)1.0f, (float)((LocalPlayerInterface)CommonUniforms.client.cameraEntity).getCurrentConstantMood());
    }

    static float getRainStrength() {
        if (CommonUniforms.client.level == null) {
            return 0.0f;
        }
        return org.joml.Math.clamp((float)0.0f, (float)1.0f, (float)CommonUniforms.client.level.getRainLevel(CapturedRenderingState.INSTANCE.getTickDelta()));
    }

    private static Vector2i getEyeBrightness() {
        if (CommonUniforms.client.cameraEntity == null || CommonUniforms.client.level == null) {
            return ZERO_VECTOR_2i;
        }
        Vec3 feet = CommonUniforms.client.cameraEntity.position();
        Vec3 eyes = new Vec3(feet.x, CommonUniforms.client.cameraEntity.getEyeY(), feet.z);
        BlockPos eyeBlockPos = BlockPos.containing((Position)eyes);
        int blockLight = CommonUniforms.client.level.getBrightness(LightLayer.BLOCK, eyeBlockPos);
        int skyLight = CommonUniforms.client.level.getBrightness(LightLayer.SKY, eyeBlockPos);
        return new Vector2i(blockLight * 16, skyLight * 16);
    }

    private static float getNightVision() {
        float underwaterVisibility;
        Entity cameraEntity = client.getCameraEntity();
        if (cameraEntity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)cameraEntity;
            try {
                float nightVisionStrength = GameRenderer.getNightVisionScale((LivingEntity)livingEntity, (float)CapturedRenderingState.INSTANCE.getTickDelta());
                if (nightVisionStrength > 0.0f) {
                    return org.joml.Math.clamp((float)0.0f, (float)1.0f, (float)nightVisionStrength);
                }
            }
            catch (NullPointerException e) {
                return 0.0f;
            }
        }
        if (CommonUniforms.client.player != null && CommonUniforms.client.player.hasEffect(MobEffects.CONDUIT_POWER) && (underwaterVisibility = CommonUniforms.client.player.getWaterVision()) > 0.0f) {
            return org.joml.Math.clamp((float)0.0f, (float)1.0f, (float)underwaterVisibility);
        }
        return 0.0f;
    }

    static int isEyeInWater() {
        FogType submersionType = CommonUniforms.client.gameRenderer.getMainCamera().getFluidInCamera();
        if (submersionType == FogType.WATER) {
            return 1;
        }
        if (submersionType == FogType.LAVA) {
            return 2;
        }
        if (submersionType == FogType.POWDER_SNOW) {
            return 3;
        }
        return 0;
    }

    static {
        GbufferPrograms.init();
    }
}

