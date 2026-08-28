/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.PoseStack$Pose
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  org.spongepowered.asm.mixin.Mixin
 */
package net.irisshaders.iris.mixin.vertices.block_rendering;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Arrays;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.minecraft.client.renderer.block.model.BakedQuad;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={BufferBuilder.class}, priority=999)
public abstract class MixinBufferBuilder_SeparateAo
implements VertexConsumer {
    public void putBulkData(PoseStack.Pose matrixEntry, BakedQuad quad, float[] brightnesses, float red, float green, float blue, float alpha, int[] lights, int overlay, boolean useQuadColorData) {
        if (WorldRenderingSettings.INSTANCE.shouldUseSeparateAo()) {
            float[] brightnesses1 = brightnesses;
            boolean brightnessIndex = false;
            brightnesses = new float[brightnesses.length];
            Arrays.fill(brightnesses, 1.0f);
        }
        super.putBulkData(matrixEntry, quad, brightnesses, red, green, blue, alpha, lights, overlay, useQuadColorData);
    }
}

