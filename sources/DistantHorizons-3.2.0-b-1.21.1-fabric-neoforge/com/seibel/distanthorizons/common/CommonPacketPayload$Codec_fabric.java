package com.seibel.distanthorizons.common;

import net.minecraft.class_2540;
import net.minecraft.class_9139;
import org.jetbrains.annotations.NotNull;

public class CommonPacketPayload$Codec_fabric implements class_9139<class_2540, CommonPacketPayload_fabric> {
   @NotNull
   public CommonPacketPayload_fabric decode(@NotNull class_2540 in) {
      return new CommonPacketPayload_fabric(CommonPacketPayload_fabric.PACKET_SENDER.decodeMessage(in));
   }

   public void encode(@NotNull class_2540 out, CommonPacketPayload_fabric payload) {
      CommonPacketPayload_fabric.PACKET_SENDER.encodeMessage(out, payload.message());
   }
}
