package com.seibel.distanthorizons.core.network.messages.base;

import com.google.common.base.MoreObjects.ToStringHelper;
import com.seibel.distanthorizons.core.network.messages.AbstractNetworkMessage;
import io.netty.buffer.ByteBuf;

public class RequestLevelInitMessage extends AbstractNetworkMessage {
   public String dimensionResourceLocation;

   public RequestLevelInitMessage() {
   }

   public RequestLevelInitMessage(String dimensionResourceLocation) {
      this.dimensionResourceLocation = dimensionResourceLocation;
   }

   @Override
   public void encode(ByteBuf out) {
      this.writeString(this.dimensionResourceLocation, out);
   }

   @Override
   public void decode(ByteBuf in) {
      this.dimensionResourceLocation = this.readString(in);
   }

   @Override
   public ToStringHelper toStringHelper() {
      return super.toStringHelper().add("dimensionResourceLocation", this.dimensionResourceLocation);
   }
}
