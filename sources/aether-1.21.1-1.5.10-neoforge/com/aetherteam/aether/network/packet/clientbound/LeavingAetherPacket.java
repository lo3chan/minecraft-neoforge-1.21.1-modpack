package com.aetherteam.aether.network.packet.clientbound;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record LeavingAetherPacket(boolean playerLeavingAether) implements CustomPacketPayload {
   public static final Type<LeavingAetherPacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("aether", "leave_aether"));
   public static final StreamCodec<RegistryFriendlyByteBuf, LeavingAetherPacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.BOOL, LeavingAetherPacket::playerLeavingAether, LeavingAetherPacket::new
   );

   public Type<LeavingAetherPacket> type() {
      return TYPE;
   }

   public static void execute(LeavingAetherPacket payload, IPayloadContext context) {
      if (Minecraft.getInstance().player != null && Minecraft.getInstance().level != null) {
         playerLeavingAether = payload.playerLeavingAether();
      }
   }
}
