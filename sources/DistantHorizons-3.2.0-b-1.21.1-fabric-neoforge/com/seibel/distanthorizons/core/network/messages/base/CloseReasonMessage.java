package com.seibel.distanthorizons.core.network.messages.base;

import com.google.common.base.MoreObjects.ToStringHelper;
import com.seibel.distanthorizons.core.network.messages.AbstractNetworkMessage;
import io.netty.buffer.ByteBuf;

public class CloseReasonMessage extends AbstractNetworkMessage {
   public String reason;

   public CloseReasonMessage() {
   }

   public CloseReasonMessage(String reason) {
      this.reason = reason;
   }

   @Override
   public void encode(ByteBuf out) {
      this.writeString(this.reason, out);
   }

   @Override
   public void decode(ByteBuf in) {
      this.reason = this.readString(in);
   }

   @Override
   public ToStringHelper toStringHelper() {
      return super.toStringHelper().add("reason", this.reason);
   }
}
