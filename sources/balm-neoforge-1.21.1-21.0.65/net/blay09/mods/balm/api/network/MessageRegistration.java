package net.blay09.mods.balm.api.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;

public class MessageRegistration<TBuffer extends FriendlyByteBuf, TPayload extends CustomPacketPayload> {
   private final Type<TPayload> type;
   private final StreamCodec<TBuffer, TPayload> codec;

   public MessageRegistration(Type<TPayload> type, StreamCodec<TBuffer, TPayload> codec) {
      this.type = type;
      this.codec = codec;
   }

   public Type<TPayload> getType() {
      return this.type;
   }

   public StreamCodec<TBuffer, TPayload> getCodec() {
      return this.codec;
   }
}
