package io.wispforest.owo.network.neoforge;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;

public record SidedPacketCodec<T>(StreamCodec<FriendlyByteBuf, T> serverCodec, StreamCodec<FriendlyByteBuf, T> clientCodec)
   implements StreamCodec<FriendlyByteBuf, T> {
   public StreamCodec<FriendlyByteBuf, T> getCodec(PacketFlow side) {
      return side == PacketFlow.CLIENTBOUND ? this.clientCodec : this.serverCodec;
   }

   public T decode(FriendlyByteBuf buf) {
      throw new IllegalStateException("[owo] Sided Packet Codec has not been unpacked, issue has occured!");
   }

   public void encode(FriendlyByteBuf buf, T value) {
      throw new IllegalStateException("[owo] Sided Packet Codec has not been unpacked, issue has occured!");
   }
}
