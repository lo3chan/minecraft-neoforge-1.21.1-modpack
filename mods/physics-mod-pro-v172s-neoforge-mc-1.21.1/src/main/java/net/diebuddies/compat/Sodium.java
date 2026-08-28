/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormatElement
 *  net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter
 *  net.caffeinemc.mods.sodium.api.vertex.format.common.ParticleVertex
 *  net.caffeinemc.mods.sodium.client.render.texture.SpriteUtil
 *  net.caffeinemc.mods.sodium.client.world.LevelRendererExtension
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  org.joml.Vector3f
 *  org.lwjgl.system.MemoryStack
 */
package net.diebuddies.compat;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
import net.caffeinemc.mods.sodium.api.vertex.format.common.ParticleVertex;
import net.caffeinemc.mods.sodium.client.render.texture.SpriteUtil;
import net.caffeinemc.mods.sodium.client.world.LevelRendererExtension;
import net.diebuddies.compat.BlockEntityVertexConsumerSodium;
import net.diebuddies.compat.BoundingBoxGetterSodium;
import net.diebuddies.compat.DummyVertexConsumerSodium;
import net.diebuddies.physics.BlockEntityVertexConsumer;
import net.diebuddies.physics.DummyVertexConsumer;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.settings.mobs.BoundingBoxGetter;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

public class Sodium {
    public static void markSpriteActive(TextureAtlasSprite sprite) {
        if (StarterClient.sodium) {
            try {
                SpriteUtil.markSpriteActive((TextureAtlasSprite)sprite);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void scheduleChunkRebuild(LevelRenderer renderer, int x, int y, int z, boolean important) {
        if (StarterClient.sodium) {
            try {
                ((LevelRendererExtension)renderer).sodium$getWorldRenderer().scheduleRebuildForChunk(x, y, z, important);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static BlockEntityVertexConsumer getNewBlockConsumer() {
        return new BlockEntityVertexConsumerSodium();
    }

    public static DummyVertexConsumer getNewDummyConsumer() {
        return new DummyVertexConsumerSodium();
    }

    public static BoundingBoxGetter getNewBoundingBoxConsumer() {
        return new BoundingBoxGetterSodium();
    }

    public static long getTextureElementOffset(Object format) {
        return ((VertexFormat)format).getOffset(VertexFormatElement.UV0);
    }

    public static long getStride(Object format) {
        return ((VertexFormat)format).getVertexSize();
    }

    public static void renderParticle(VertexConsumer vertexConsumer, Vector3f tmp0, Vector3f tmp1, Vector3f tmp2, Vector3f tmp3, float currentX, float currentY, float currentZ, float u0, float v0, float u1, float v1, int color, int light) {
        VertexBufferWriter writer = VertexBufferWriter.of((VertexConsumer)vertexConsumer);
        try (MemoryStack stack = MemoryStack.stackPush();){
            long buffer;
            long ptr = buffer = stack.nmalloc(112);
            ParticleVertex.put((long)ptr, (float)(tmp0.x + currentX), (float)(tmp0.y + currentY), (float)(tmp0.z + currentZ), (float)u1, (float)v1, (int)color, (int)light);
            ParticleVertex.put((long)(ptr += 28L), (float)(tmp1.x + currentX), (float)(tmp1.y + currentY), (float)(tmp1.z + currentZ), (float)u1, (float)v0, (int)color, (int)light);
            ParticleVertex.put((long)(ptr += 28L), (float)(tmp2.x + currentX), (float)(tmp2.y + currentY), (float)(tmp2.z + currentZ), (float)u0, (float)v0, (int)color, (int)light);
            ParticleVertex.put((long)(ptr += 28L), (float)(tmp3.x + currentX), (float)(tmp3.y + currentY), (float)(tmp3.z + currentZ), (float)u0, (float)v1, (int)color, (int)light);
            ptr += 28L;
            writer.push(stack, buffer, 4, ParticleVertex.FORMAT);
        }
    }
}

