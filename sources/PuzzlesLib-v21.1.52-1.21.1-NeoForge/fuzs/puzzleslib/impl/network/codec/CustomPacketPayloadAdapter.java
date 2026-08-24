package fuzs.puzzleslib.impl.network.codec;

import fuzs.puzzleslib.impl.network.CustomPacketPayloadAdapterImpl;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;

public interface CustomPacketPayloadAdapter<T> extends CustomPacketPayload {
   Type<? extends CustomPacketPayloadAdapter<T>> type();

   T unwrap();

   static <B extends ByteBuf, T> StreamCodec<? super B, CustomPacketPayloadAdapter<T>> streamCodec(
      Type<CustomPacketPayloadAdapter<T>> type, StreamCodec<? super ByteBuf, T> streamCodec
   ) {
      return CustomPacketPayload.codec(
         (adapter, buf) -> streamCodec.encode(buf, adapter.unwrap()), buf -> new CustomPacketPayloadAdapterImpl<>(type, streamCodec.decode(buf))
      );
   }
}
