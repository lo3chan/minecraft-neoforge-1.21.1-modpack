package com.iafenvoy.jupiter.network.payload;

import com.iafenvoy.jupiter.util.RLUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record ConfigRequestPayload(ResourceLocation id) implements CustomPacketPayload {
   public static final Type<ConfigRequestPayload> TYPE = new Type(RLUtil.id("config_request"));
   public static final StreamCodec<FriendlyByteBuf, ConfigRequestPayload> CODEC = StreamCodec.of(
      (buf, value) -> buf.writeResourceLocation(value.id), buf -> new ConfigRequestPayload(buf.readResourceLocation())
   );

   @NotNull
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
