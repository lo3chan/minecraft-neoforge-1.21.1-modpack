package fuzs.puzzleslib.impl.network;

import fuzs.puzzleslib.impl.network.codec.CustomPacketPayloadAdapter;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;

public record CustomPacketPayloadAdapterImpl<T>(Type<CustomPacketPayloadAdapter<T>> type, T message) implements CustomPacketPayloadAdapter<T> {
   @Override
   public T unwrap() {
      return this.message;
   }
}
