/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  it.unimi.dsi.fastutil.objects.Object2IntFunction
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.Entity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.mixin.entity_render_context;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2IntFunction;
import net.irisshaders.iris.shaderpack.materialmap.NamespacedId;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={EntityRenderer.class})
public class MixinEntityRenderer<T extends Entity> {
    @Unique
    private static final NamespacedId NAME_TAG_ID = new NamespacedId("minecraft", "name_tag");
    @Unique
    private int lastId = -100;

    @Inject(method={"renderNameTag"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/entity/Entity;getViewYRot(F)F")})
    private void setNameTagId(T entity, Component component, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, float f, CallbackInfo ci) {
        Object2IntFunction<NamespacedId> entityIds = WorldRenderingSettings.INSTANCE.getEntityIds();
        if (entityIds == null) {
            return;
        }
        this.lastId = CapturedRenderingState.INSTANCE.getCurrentRenderedEntity();
        int intId = entityIds.applyAsInt((Object)NAME_TAG_ID);
        CapturedRenderingState.INSTANCE.setCurrentEntity(intId);
    }

    @Inject(method={"renderNameTag"}, at={@At(value="RETURN")})
    private void resetId(T entity, Component component, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, float f, CallbackInfo ci) {
        if (this.lastId != -100) {
            CapturedRenderingState.INSTANCE.setCurrentEntity(this.lastId);
            this.lastId = -100;
        }
    }
}

