/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 */
package net.diebuddies.physics;

import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.diebuddies.compat.Sodium;
import net.diebuddies.physics.BlockEntityVertexConsumer;
import net.diebuddies.physics.StarterClient;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

public class BlockEntityVertexConsumerProvider
implements MultiBufferSource {
    public static BlockEntityVertexConsumer currentConsumer;
    private Map<RenderType, BlockEntityVertexConsumer> renderTypes = new Object2ObjectOpenHashMap();
    private RenderType lastLayer;
    private boolean destruction;

    public BlockEntityVertexConsumerProvider(boolean destruction) {
        this.destruction = destruction;
    }

    public VertexConsumer getBuffer(RenderType layer) {
        if (this.lastLayer != null) {
            this.lastLayer.clearRenderState();
        }
        this.lastLayer = layer;
        layer.setupRenderState();
        currentConsumer = this.renderTypes.computeIfAbsent(layer, key -> StarterClient.sodium ? Sodium.getNewBlockConsumer() : new BlockEntityVertexConsumer());
        return currentConsumer;
    }

    public RenderType getLastLayer() {
        return this.lastLayer;
    }

    public Map<RenderType, BlockEntityVertexConsumer> getBakedRenderTypeModels() {
        return this.renderTypes;
    }

    public boolean isDestruction() {
        return this.destruction;
    }
}

