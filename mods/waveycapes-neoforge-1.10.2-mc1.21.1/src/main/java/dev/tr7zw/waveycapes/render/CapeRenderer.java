/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  dev.tr7zw.transition.mc.entitywrapper.PlayerWrapper
 *  net.minecraft.client.model.geom.ModelPart
 *  net.minecraft.client.renderer.texture.OverlayTexture
 */
package dev.tr7zw.waveycapes.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.tr7zw.transition.mc.entitywrapper.PlayerWrapper;
import dev.tr7zw.waveycapes.render.CapeInfos;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.texture.OverlayTexture;

public interface CapeRenderer {
    default public void render(PlayerWrapper capeRenderInfo, int part, ModelPart model, PoseStack poseStack, VertexConsumer vertexConsumer, int light, int overlay) {
        model.render(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
    }

    public CapeInfos getCapeInfo(PlayerWrapper var1);

    default public boolean vanillaUvValues() {
        return true;
    }
}

