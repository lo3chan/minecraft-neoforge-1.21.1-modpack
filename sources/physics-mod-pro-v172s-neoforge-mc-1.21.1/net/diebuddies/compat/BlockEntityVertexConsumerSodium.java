package net.diebuddies.compat;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
import net.diebuddies.physics.BlockEntityVertexConsumer;
import org.lwjgl.system.MemoryStack;

public class BlockEntityVertexConsumerSodium extends BlockEntityVertexConsumer implements VertexBufferWriter {
   public void push(MemoryStack stack, long ptr, int count, VertexFormat format) {
   }
}
