/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  org.jetbrains.annotations.Nullable
 */
package traben.entity_texture_features.utils;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.texture_handlers.ETFTexture;

public interface ETFVertexConsumer {
    @Nullable
    public ETFTexture etf$getETFTexture();

    @Nullable
    public MultiBufferSource etf$getProvider();

    @Nullable
    public RenderType etf$getRenderLayer();

    public void etf$initETFVertexConsumer(MultiBufferSource var1, RenderType var2);
}

