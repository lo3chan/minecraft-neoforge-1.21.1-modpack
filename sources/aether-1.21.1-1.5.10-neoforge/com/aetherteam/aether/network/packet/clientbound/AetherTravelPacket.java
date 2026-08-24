package com.aetherteam.aether.network.packet.clientbound;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record AetherTravelPacket(boolean displayAetherTravel) implements CustomPacketPayload {
   public static final Type<AetherTravelPacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("aether", "travel_across_dimensions"));
   public static final StreamCodec<RegistryFriendlyByteBuf, AetherTravelPacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.BOOL, AetherTravelPacket::displayAetherTravel, AetherTravelPacket::new
   );

   public Type<AetherTravelPacket> type() {
      return TYPE;
   }

   public static void execute(AetherTravelPacket payload, IPayloadContext context) {
      if (Minecraft.getInstance().player != null && Minecraft.getInstance().level != null) {
         displayAetherTravel = payload.displayAetherTravel();
      }
   }
}
