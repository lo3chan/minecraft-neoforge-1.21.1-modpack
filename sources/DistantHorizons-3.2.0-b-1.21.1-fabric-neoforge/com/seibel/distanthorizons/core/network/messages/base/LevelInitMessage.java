package com.seibel.distanthorizons.core.network.messages.base;

import com.google.common.base.MoreObjects.ToStringHelper;
import com.seibel.distanthorizons.core.network.messages.AbstractNetworkMessage;
import io.netty.buffer.ByteBuf;

public class LevelInitMessage extends AbstractNetworkMessage {
   public static final int MAX_LENGTH = 150;
   public static final String ALLOWED_CHARS_REGEX = "a-zA-Z0-9-_";
   public static final String SERVER_KEY_REGEX = String.format("^(?=.{1,%s}$)[%s]+$", 150, "a-zA-Z0-9-_");
   public static final String LEVEL_KEY_REGEX = String.format("^(?=.{1,%s}$)([%s]+@)?[%s]+(:[%s]+)?$", 150, "a-zA-Z0-9-_", "a-zA-Z0-9-_", "a-zA-Z0-9-_");
   public String dimensionResourceLocation;
   public String serverKey;
   public String levelKey;
   public long serverTime;

   public LevelInitMessage() {
   }

   public LevelInitMessage(String dimensionResourceLocation, String serverKey, String levelKey) {
      this.dimensionResourceLocation = dimensionResourceLocation;
      this.serverKey = serverKey;
      this.levelKey = levelKey;
      this.serverTime = System.currentTimeMillis();
   }

   @Override
   public void encode(ByteBuf out) {
      this.writeString(this.dimensionResourceLocation, out);
      this.writeString(this.serverKey, out);
      this.writeString(this.levelKey, out);
      out.writeLong(this.serverTime);
   }

   @Override
   public void decode(ByteBuf in) {
      this.dimensionResourceLocation = this.readString(in);
      this.serverKey = this.readString(in);
      this.levelKey = this.readString(in);
      this.serverTime = in.readLong();
   }

   @Override
   public ToStringHelper toStringHelper() {
      return super.toStringHelper()
         .add("dimensionResourceLocation", this.dimensionResourceLocation)
         .add("serverKey", this.serverKey)
         .add("levelKey", this.levelKey)
         .add("serverTime", this.serverTime);
   }
}
