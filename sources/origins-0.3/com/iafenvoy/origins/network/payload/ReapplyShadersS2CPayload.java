package com.iafenvoy.origins.network.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public enum ReapplyShadersS2CPayload implements CustomPacketPayload {
   INSTANCE;

   public static final Type<ReapplyShadersS2CPayload> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("origins", "reapply_shaders_s2c"));
   public static final StreamCodec<RegistryFriendlyByteBuf, ReapplyShadersS2CPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

   @NotNull
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
