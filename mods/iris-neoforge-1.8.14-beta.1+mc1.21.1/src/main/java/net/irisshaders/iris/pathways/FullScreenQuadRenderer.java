/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.BufferUploader
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.MeshData
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexBuffer
 *  com.mojang.blaze3d.vertex.VertexBuffer$Usage
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 */
package net.irisshaders.iris.pathways;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.helpers.VertexBufferHelper;

public class FullScreenQuadRenderer {
    public static final FullScreenQuadRenderer INSTANCE = new FullScreenQuadRenderer();
    private final VertexBuffer quad;

    private FullScreenQuadRenderer() {
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.addVertex(0.0f, 0.0f, 0.0f).setUv(0.0f, 0.0f);
        bufferBuilder.addVertex(1.0f, 0.0f, 0.0f).setUv(1.0f, 0.0f);
        bufferBuilder.addVertex(1.0f, 1.0f, 0.0f).setUv(1.0f, 1.0f);
        bufferBuilder.addVertex(0.0f, 1.0f, 0.0f).setUv(0.0f, 1.0f);
        MeshData meshData = bufferBuilder.build();
        this.quad = new VertexBuffer(VertexBuffer.Usage.STATIC);
        this.quad.bind();
        this.quad.upload(meshData);
        Tesselator.getInstance().clear();
        VertexBuffer.unbind();
    }

    public void render() {
        this.begin();
        this.renderQuad();
        this.end();
    }

    public void begin() {
        ((VertexBufferHelper)this.quad).saveBinding();
        RenderSystem.disableDepthTest();
        BufferUploader.reset();
        this.quad.bind();
    }

    public void renderQuad() {
        IrisRenderSystem.overridePolygonMode();
        this.quad.draw();
        IrisRenderSystem.restorePolygonMode();
    }

    public void end() {
        RenderSystem.enableDepthTest();
        ((VertexBufferHelper)this.quad).restoreBinding();
    }
}

