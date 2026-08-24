package com.iafenvoy.jupiter.network.payload;

import com.iafenvoy.jupiter.util.RLUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import org.jetbrains.annotations.NotNull;

public record ConfigErrorPayload() implements CustomPacketPayload {
   public static final Type<ConfigErrorPayload> TYPE = new Type(RLUtil.id("config_error"));
   public static final StreamCodec<FriendlyByteBuf, ConfigErrorPayload> CODEC = StreamCodec.unit(new ConfigErrorPayload());

   @NotNull
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
