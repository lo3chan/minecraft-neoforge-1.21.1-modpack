/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 */
package net.irisshaders.iris.layer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Function;
import net.irisshaders.batchedentityrendering.impl.Groupable;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

public class BufferSourceWrapper
implements MultiBufferSource,
Groupable {
    private final MultiBufferSource bufferSource;
    private final Function<RenderType, RenderType> typeChanger;

    public BufferSourceWrapper(MultiBufferSource bufferSource, Function<RenderType, RenderType> typeChanger) {
        this.bufferSource = bufferSource;
        this.typeChanger = typeChanger;
    }

    public MultiBufferSource getOriginal() {
        return this.bufferSource;
    }

    @Override
    public void startGroup() {
        MultiBufferSource multiBufferSource = this.bufferSource;
        if (multiBufferSource instanceof Groupable) {
            Groupable groupable = (Groupable)multiBufferSource;
            groupable.startGroup();
        }
    }

    @Override
    public boolean maybeStartGroup() {
        MultiBufferSource multiBufferSource = this.bufferSource;
        if (multiBufferSource instanceof Groupable) {
            Groupable groupable = (Groupable)multiBufferSource;
            return groupable.maybeStartGroup();
        }
        return false;
    }

    @Override
    public void endGroup() {
        MultiBufferSource multiBufferSource = this.bufferSource;
        if (multiBufferSource instanceof Groupable) {
            Groupable groupable = (Groupable)multiBufferSource;
            groupable.endGroup();
        }
    }

    public VertexConsumer getBuffer(RenderType renderType) {
        return this.bufferSource.getBuffer(this.typeChanger.apply(renderType));
    }
}

