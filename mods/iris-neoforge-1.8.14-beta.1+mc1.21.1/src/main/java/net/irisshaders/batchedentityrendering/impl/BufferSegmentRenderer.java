/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.BufferUploader
 *  com.mojang.blaze3d.vertex.MeshData
 */
package net.irisshaders.batchedentityrendering.impl;

import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.MeshData;
import net.irisshaders.batchedentityrendering.impl.BufferSegment;

public class BufferSegmentRenderer {
    public void draw(BufferSegment segment) {
        if (segment.meshData() != null) {
            segment.type().draw(segment.meshData());
        }
    }

    public void drawInner(BufferSegment segment) {
        BufferUploader.drawWithShader((MeshData)segment.meshData());
    }
}

