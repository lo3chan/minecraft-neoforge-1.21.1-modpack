package com.aetherteam.aether.network.packet.clientbound;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ToolDebuffPacket(boolean debuffTools) implements CustomPacketPayload {
   public static final Type<ToolDebuffPacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("aether", "apply_tool_debuff"));
   public static final StreamCodec<RegistryFriendlyByteBuf, ToolDebuffPacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.BOOL, ToolDebuffPacket::debuffTools, ToolDebuffPacket::new
   );

   public Type<ToolDebuffPacket> type() {
      return TYPE;
   }

   public static void execute(ToolDebuffPacket payload, IPayloadContext context) {
      if (Minecraft.getInstance().player != null && Minecraft.getInstance().level != null) {
         debuffTools = payload.debuffTools();
      }
   }
}
