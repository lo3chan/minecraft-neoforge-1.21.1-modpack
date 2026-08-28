/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.MeshData
 *  net.minecraft.client.renderer.RenderType
 */
package net.irisshaders.batchedentityrendering.impl;

import com.mojang.blaze3d.vertex.MeshData;
import net.minecraft.client.renderer.RenderType;

public record BufferSegment(MeshData meshData, RenderType type) {
}

