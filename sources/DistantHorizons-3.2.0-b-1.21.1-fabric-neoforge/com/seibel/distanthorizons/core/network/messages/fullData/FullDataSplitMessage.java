package com.seibel.distanthorizons.core.network.messages.fullData;

import com.google.common.base.MoreObjects.ToStringHelper;
import com.seibel.distanthorizons.core.network.messages.AbstractNetworkMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class FullDataSplitMessage extends AbstractNetworkMessage {
   public int bufferId;
   public ByteBuf buffer;
   public boolean isFirst;

   public FullDataSplitMessage() {
   }

   public FullDataSplitMessage(int bufferId, ByteBuf buffer, boolean isFirst) {
      this.bufferId = bufferId;
      this.buffer = buffer;
      this.isFirst = isFirst;
   }

   @Override
   public void encode(ByteBuf out) {
      out.writeInt(this.bufferId);
      out.writeInt(this.buffer.writerIndex());
      out.writeBytes(this.buffer.readerIndex(0));
      out.writeBoolean(this.isFirst);
   }

   @Override
   public void decode(ByteBuf in) {
      this.bufferId = in.readInt();
      int bufferSize = in.readInt();
      this.buffer = Unpooled.copiedBuffer(in.readSlice(bufferSize));
      this.isFirst = in.readBoolean();
   }

   @Override
   public ToStringHelper toStringHelper() {
      return super.toStringHelper().add("bufferId", this.bufferId).add("buffer", this.buffer).add("isFirst", this.isFirst);
   }
}
