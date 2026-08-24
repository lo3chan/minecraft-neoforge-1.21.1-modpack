package io.wispforest.owo.mixin;

import io.wispforest.endec.Endec;
import io.wispforest.endec.SerializationContext;
import io.wispforest.endec.format.bytebuf.ByteBufDeserializer;
import io.wispforest.endec.format.bytebuf.ByteBufSerializer;
import io.wispforest.endec.util.EndecBuffer;
import net.minecraft.network.FriendlyByteBuf;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({FriendlyByteBuf.class})
public class PacketByteBufMixin implements EndecBuffer {
   public <T> void write(SerializationContext ctx, Endec<T> endec, T value) {
      endec.encodeFully(ctx, () -> ByteBufSerializer.of((FriendlyByteBuf)this), value);
   }

   public <T> T read(SerializationContext ctx, Endec<T> endec) {
      return (T)endec.decodeFully(ctx, ByteBufDeserializer::of, (FriendlyByteBuf)this);
   }
}
