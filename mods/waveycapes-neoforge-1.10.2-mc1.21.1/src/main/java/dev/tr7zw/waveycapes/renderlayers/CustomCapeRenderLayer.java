/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.tr7zw.transition.mc.entitywrapper.PlayerWrapper
 *  net.minecraft.client.model.PlayerModel
 *  net.minecraft.client.player.AbstractClientPlayer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.entity.RenderLayerParent
 *  net.minecraft.client.renderer.entity.layers.RenderLayer
 */
package dev.tr7zw.waveycapes.renderlayers;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.tr7zw.transition.mc.entitywrapper.PlayerWrapper;
import dev.tr7zw.waveycapes.WaveyCapesBase;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

public class CustomCapeRenderLayer
extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    public CustomCapeRenderLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderLayerParent) {
        super(renderLayerParent);
    }

    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, AbstractClientPlayer renderState, float f, float g, float delta, float j, float k, float l) {
        PlayerWrapper capeRenderInfo = new PlayerWrapper(renderState);
        if (capeRenderInfo.isPlayerInvisible()) {
            return;
        }
        if (capeRenderInfo.hasElytraEquipped()) {
            return;
        }
        if (!capeRenderInfo.isCapeVisible()) {
            return;
        }
        poseStack.pushPose();
        ((PlayerModel)this.getParentModel()).body.translateAndRotate(poseStack);
        if (capeRenderInfo.hasChestplateEquipped()) {
            poseStack.translate(0.0f, -0.053125f, 0.06875f);
        }
        WaveyCapesBase.INSTANCE.getCapeNodeCollector().submitCape(multiBufferSource, capeRenderInfo, poseStack, packedLight, delta);
        poseStack.popPose();
    }
}

