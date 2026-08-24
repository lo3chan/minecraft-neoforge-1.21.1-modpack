package com.seibel.distanthorizons.core.network.messages.base;

import com.google.common.base.MoreObjects.ToStringHelper;
import com.seibel.distanthorizons.core.network.messages.AbstractNetworkMessage;
import io.netty.buffer.ByteBuf;

public class CodecCrashMessage extends AbstractNetworkMessage {
   public CodecCrashMessage.ECrashPhase crashPhase;

   public CodecCrashMessage() {
   }

   public CodecCrashMessage(CodecCrashMessage.ECrashPhase crashPhase) {
      this.crashPhase = crashPhase;
   }

   @Override
   public void encode(ByteBuf out) {
      if (this.crashPhase == CodecCrashMessage.ECrashPhase.ENCODE) {
         throw new RuntimeException("encode force crash");
      }
   }

   @Override
   public void decode(ByteBuf in) {
      throw new RuntimeException("decode force crash");
   }

   @Override
   public ToStringHelper toStringHelper() {
      return super.toStringHelper().add("crashPhase", this.crashPhase);
   }

   public static enum ECrashPhase {
      ENCODE,
      DECODE;
   }
}
