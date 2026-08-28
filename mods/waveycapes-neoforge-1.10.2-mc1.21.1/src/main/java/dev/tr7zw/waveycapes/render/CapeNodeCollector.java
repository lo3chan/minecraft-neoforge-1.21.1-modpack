/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  dev.tr7zw.transition.mc.entitywrapper.PlayerWrapper
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.entity.ItemRenderer
 */
package dev.tr7zw.waveycapes.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.tr7zw.transition.mc.entitywrapper.PlayerWrapper;
import dev.tr7zw.waveycapes.render.CapeInfos;
import dev.tr7zw.waveycapes.render.CapeRenderer;
import dev.tr7zw.waveycapes.render.CustomCapeRenderer;
import dev.tr7zw.waveycapes.render.VanillaCapeRenderer;
import dev.tr7zw.waveycapes.support.ModSupport;
import dev.tr7zw.waveycapes.support.SupportManager;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;

public class CapeNodeCollector {
    private final CustomCapeRenderer customCapeRenderer = new CustomCapeRenderer();
    private final VanillaCapeRenderer vanillaCape = new VanillaCapeRenderer();

    public void submitCape(MultiBufferSource multiBufferSource, PlayerWrapper playerWrapper, PoseStack stack, int packedLight, float delta) {
        CapeRenderer renderer = this.getCapeRenderer(playerWrapper);
        if (renderer == null) {
            return;
        }
        CapeInfos capeInfo = renderer.getCapeInfo(playerWrapper);
        if (capeInfo == null) {
            return;
        }
        VertexConsumer vertexConsumer = capeInfo.isGlint() ? ItemRenderer.getFoilBuffer((MultiBufferSource)multiBufferSource, (RenderType)capeInfo.renderType(), (boolean)false, (boolean)true) : multiBufferSource.getBuffer(capeInfo.renderType());
        this.customCapeRenderer.render(playerWrapper, renderer, vertexConsumer, stack, packedLight, delta);
    }

    private CapeRenderer getCapeRenderer(PlayerWrapper capeRenderInfo) {
        for (ModSupport support : SupportManager.getSupportedMods()) {
            if (!support.shouldBeUsed(capeRenderInfo)) continue;
            return support.getRenderer();
        }
        if (capeRenderInfo.getCapeTexture() == null || !capeRenderInfo.isCapeVisible()) {
            return null;
        }
        return this.vanillaCape;
    }
}

