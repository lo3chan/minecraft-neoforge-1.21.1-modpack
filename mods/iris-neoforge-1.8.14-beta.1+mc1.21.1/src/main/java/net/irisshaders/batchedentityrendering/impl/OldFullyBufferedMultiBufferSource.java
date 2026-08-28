/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.ByteBufferBuilder
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectSortedMaps
 *  java.util.SequencedMap
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.RenderType
 */
package net.irisshaders.batchedentityrendering.impl;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectSortedMaps;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.SequencedMap;
import java.util.Set;
import net.irisshaders.batchedentityrendering.impl.BlendingStateHolder;
import net.irisshaders.batchedentityrendering.impl.TransparencyType;
import net.irisshaders.batchedentityrendering.impl.WrappableRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

public class OldFullyBufferedMultiBufferSource
extends MultiBufferSource.BufferSource {
    private final Map<RenderType, BufferBuilder> bufferBuilders = new HashMap<RenderType, BufferBuilder>();
    private final Object2IntMap<RenderType> unused = new Object2IntOpenHashMap();
    private final Set<BufferBuilder> activeBuffers = new HashSet<BufferBuilder>();
    private final Set<RenderType> typesThisFrame = new HashSet<RenderType>();
    private final List<RenderType> typesInOrder = new ArrayList<RenderType>();
    private boolean flushed = false;

    public OldFullyBufferedMultiBufferSource() {
        super(new ByteBufferBuilder(0), (SequencedMap)Object2ObjectSortedMaps.emptyMap());
    }

    private TransparencyType getTransparencyType(RenderType type) {
        while (type instanceof WrappableRenderType) {
            type = ((WrappableRenderType)type).unwrap();
        }
        if (type instanceof BlendingStateHolder) {
            return ((BlendingStateHolder)type).getTransparencyType();
        }
        return TransparencyType.GENERAL_TRANSPARENT;
    }

    public VertexConsumer getBuffer(RenderType renderType) {
        this.flushed = false;
        BufferBuilder buffer = this.bufferBuilders.computeIfAbsent(renderType, type -> new BufferBuilder(new ByteBufferBuilder(type.bufferSize()), renderType.mode(), renderType.format()));
        if (this.activeBuffers.add(buffer)) {
            // empty if block
        }
        if (this.typesThisFrame.add(renderType)) {
            this.typesInOrder.add(renderType);
        }
        this.unused.removeInt((Object)renderType);
        return buffer;
    }

    public void endBatch() {
        if (this.flushed) {
            return;
        }
        ArrayList removedTypes = new ArrayList();
        this.unused.forEach((unusedType, unusedCount) -> {
            if (unusedCount < 10) {
                return;
            }
            BufferBuilder buffer = this.bufferBuilders.remove(unusedType);
            removedTypes.add(unusedType);
            if (this.activeBuffers.contains(buffer)) {
                throw new IllegalStateException("A buffer was simultaneously marked as inactive and as active, something is very wrong...");
            }
        });
        for (RenderType removed : removedTypes) {
            this.unused.removeInt((Object)removed);
        }
        this.typesInOrder.sort(Comparator.comparing(this::getTransparencyType));
        for (RenderType type : this.typesInOrder) {
            this.drawInternal(type);
        }
        this.typesInOrder.clear();
        this.typesThisFrame.clear();
        this.flushed = true;
    }

    public void endBatch(RenderType type) {
    }

    private void drawInternal(RenderType type) {
        BufferBuilder buffer = this.bufferBuilders.get(type);
        if (buffer == null) {
            return;
        }
        if (this.activeBuffers.remove(buffer)) {
            type.draw(buffer.build());
        } else {
            int unusedCount = this.unused.getOrDefault((Object)type, 0);
            this.unused.put((Object)type, ++unusedCount);
        }
    }
}

