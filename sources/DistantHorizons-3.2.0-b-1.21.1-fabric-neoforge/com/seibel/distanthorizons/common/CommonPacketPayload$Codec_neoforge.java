package com.seibel.distanthorizons.common;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class CommonPacketPayload$Codec_neoforge implements StreamCodec<FriendlyByteBuf, CommonPacketPayload_neoforge> {
   @NotNull
   public CommonPacketPayload_neoforge decode(@NotNull FriendlyByteBuf in) {
      return new CommonPacketPayload_neoforge(CommonPacketPayload_neoforge.PACKET_SENDER.decodeMessage(in));
   }

   public void encode(@NotNull FriendlyByteBuf out, CommonPacketPayload_neoforge payload) {
      CommonPacketPayload_neoforge.PACKET_SENDER.encodeMessage(out, payload.message());
   }
}
