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
import net.diebuddies.physics.DummyVertexConsumer;
import net.diebuddies.physics.StarterClient;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

public class DummyMultiBufferSource
implements MultiBufferSource {
    public Map<RenderType, DummyVertexConsumer> dummy = new Object2ObjectOpenHashMap();
    public RenderType lastLayer;

    public VertexConsumer getBuffer(RenderType layer) {
        if (this.lastLayer != null) {
            this.lastLayer.clearRenderState();
        }
        this.lastLayer = layer;
        layer.setupRenderState();
        return this.dummy.computeIfAbsent(layer, key -> StarterClient.sodium ? Sodium.getNewDummyConsumer() : new DummyVertexConsumer());
    }

    public void trackVertices(boolean value) {
        for (DummyVertexConsumer consumer : this.dummy.values()) {
            consumer.trackVertices = value;
        }
    }
}

