/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Camera
 *  net.minecraft.client.DeltaTracker
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.util.Mth
 *  net.minecraft.world.effect.MobEffects
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyArg
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.irisshaders.iris.Iris;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={GameRenderer.class})
public abstract class MixinModelViewBobbing {
    @Shadow
    @Final
    Minecraft minecraft;
    @Shadow
    @Final
    private Camera mainCamera;
    @Shadow
    private int confusionAnimationTick;
    @Unique
    private Matrix4fc bobbingEffectsModel;
    @Unique
    private boolean areShadersOn;

    @Shadow
    protected abstract void bobView(PoseStack var1, float var2);

    @Shadow
    protected abstract void bobHurt(PoseStack var1, float var2);

    @Inject(method={"renderLevel"}, at={@At(value="HEAD")})
    private void iris$saveShadersOn(DeltaTracker deltaTracker, CallbackInfo ci) {
        this.areShadersOn = Iris.isPackInUseQuick();
    }

    @ModifyArg(method={"renderLevel"}, index=0, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/GameRenderer;bobHurt(Lcom/mojang/blaze3d/vertex/PoseStack;F)V"))
    private PoseStack iris$separateViewBobbing(PoseStack stack) {
        if (!this.areShadersOn) {
            return stack;
        }
        stack.pushPose();
        stack.last().pose().identity();
        return stack;
    }

    @Redirect(method={"renderLevel"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/GameRenderer;bobView(Lcom/mojang/blaze3d/vertex/PoseStack;F)V"))
    private void iris$stopBobbing(GameRenderer instance, PoseStack pGameRenderer0, float pFloat1) {
        if (!this.areShadersOn) {
            this.bobView(pGameRenderer0, pFloat1);
        }
    }

    @Redirect(method={"renderLevel"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/GameRenderer;bobHurt(Lcom/mojang/blaze3d/vertex/PoseStack;F)V"))
    private void iris$saveBobbing(GameRenderer instance, PoseStack pGameRenderer0, float pFloat1) {
        if (!this.areShadersOn) {
            this.bobHurt(pGameRenderer0, pFloat1);
        }
    }

    @Redirect(method={"renderLevel"}, at=@At(value="INVOKE", target="Ljava/lang/Double;floatValue()F"))
    private float iris$disableConfusionWithShaders(Double instance) {
        return this.areShadersOn ? 0.0f : instance.floatValue();
    }

    @Redirect(method={"renderLevel"}, at=@At(value="INVOKE", target="Lorg/joml/Matrix4f;rotation(Lorg/joml/Quaternionfc;)Lorg/joml/Matrix4f;", remap=false))
    private Matrix4f iris$applyBobbingToModelView(Matrix4f instance, Quaternionfc quat, DeltaTracker deltaTracker) {
        if (!this.areShadersOn) {
            instance.rotation(quat);
            return instance;
        }
        PoseStack stack = new PoseStack();
        stack.last().pose().set((Matrix4fc)instance);
        float tickDelta = this.mainCamera.getPartialTickTime();
        this.bobHurt(stack, tickDelta);
        if (((Boolean)this.minecraft.options.bobView().get()).booleanValue()) {
            this.bobView(stack, tickDelta);
        }
        instance.set((Matrix4fc)stack.last().pose());
        float f = deltaTracker.getGameTimeDeltaPartialTick(false);
        float h = ((Double)this.minecraft.options.screenEffectScale().get()).floatValue();
        float i = Mth.lerp((float)f, (float)this.minecraft.player.oSpinningEffectIntensity, (float)this.minecraft.player.spinningEffectIntensity) * h * h;
        if (i > 0.0f) {
            int j = this.minecraft.player.hasEffect(MobEffects.CONFUSION) ? 7 : 20;
            float k = 5.0f / (i * i + 5.0f) - i * 0.04f;
            k *= k;
            Vector3f vector3f = new Vector3f(0.0f, Mth.SQRT_OF_TWO / 2.0f, Mth.SQRT_OF_TWO / 2.0f);
            float l = ((float)this.confusionAnimationTick + f) * (float)j * ((float)Math.PI / 180);
            instance.rotate(l, (Vector3fc)vector3f);
            instance.scale(1.0f / k, 1.0f, 1.0f);
            instance.rotate(-l, (Vector3fc)vector3f);
        }
        instance.rotate(quat);
        return instance;
    }
}

