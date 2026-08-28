/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.phys.AABB
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package dev.tr7zw.entityculling.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.tr7zw.entityculling.access.EntityRendererInter;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={EntityRenderer.class})
public abstract class EntityRendererMixin<T extends Entity>
implements EntityRendererInter<T> {
    @Override
    public boolean shadowShouldShowName(T entity) {
        return this.shouldShowName(entity);
    }

    @Override
    public void shadowRenderNameTag(T entity, Component component, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, float delta) {
        this.renderNameTag(entity, component, poseStack, multiBufferSource, light, delta);
    }

    @Shadow
    public abstract boolean shouldShowName(T var1);

    @Shadow
    public abstract void renderNameTag(T var1, Component var2, PoseStack var3, MultiBufferSource var4, int var5, float var6);

    @Override
    public boolean entityCullingIgnoresCulling(T entity) {
        return ((Entity)entity).noCulling;
    }

    @Override
    public AABB entityCullingGetCullingBox(T entity) {
        return entity.getBoundingBoxForCulling();
    }
}

