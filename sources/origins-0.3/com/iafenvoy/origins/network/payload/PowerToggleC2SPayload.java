package com.iafenvoy.origins.network.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record PowerToggleC2SPayload(String key) implements CustomPacketPayload {
   public static final Type<PowerToggleC2SPayload> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("origins", "power_toggle_c2s"));
   public static final StreamCodec<RegistryFriendlyByteBuf, PowerToggleC2SPayload> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.STRING_UTF8, PowerToggleC2SPayload::key, PowerToggleC2SPayload::new
   );

   @NotNull
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
