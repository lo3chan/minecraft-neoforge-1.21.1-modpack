/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter
 *  org.lwjgl.system.MemoryStack
 */
package net.diebuddies.compat;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
import net.diebuddies.physics.DummyVertexConsumer;
import org.lwjgl.system.MemoryStack;

public class DummyVertexConsumerSodium
extends DummyVertexConsumer
implements VertexBufferWriter {
    public void push(MemoryStack stack, long ptr, int count, VertexFormat format) {
    }
}

