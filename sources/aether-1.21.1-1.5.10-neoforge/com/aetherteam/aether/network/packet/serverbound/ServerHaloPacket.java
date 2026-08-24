package com.aetherteam.aether.network.packet.serverbound;

import com.aetherteam.aether.perk.data.ServerPerkData;
import com.aetherteam.aether.perk.types.Halo;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerHaloPacket {
   public record Apply(UUID playerUUID, Halo halo) implements CustomPacketPayload {
      public static final Type<ServerHaloPacket.Apply> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("aether", "add_halo_server"));
      public static final StreamCodec<RegistryFriendlyByteBuf, ServerHaloPacket.Apply> STREAM_CODEC = StreamCodec.composite(
         UUIDUtil.STREAM_CODEC, ServerHaloPacket.Apply::playerUUID, Halo.STREAM_CODEC, ServerHaloPacket.Apply::halo, ServerHaloPacket.Apply::new
      );

      public Type<ServerHaloPacket.Apply> type() {
         return TYPE;
      }

      public static void execute(ServerHaloPacket.Apply payload, IPayloadContext context) {
         Player playerEntity = context.player();
         if (playerEntity.getServer() != null && payload.playerUUID() != null && payload.halo() != null) {
            ServerPerkData.HALO_INSTANCE.applyPerkWithVerification(playerEntity.getServer(), payload.playerUUID(), payload.halo());
         }
      }
   }

   public record Remove(UUID playerUUID) implements CustomPacketPayload {
      public static final Type<ServerHaloPacket.Remove> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("aether", "remove_halo_server"));
      public static final StreamCodec<RegistryFriendlyByteBuf, ServerHaloPacket.Remove> STREAM_CODEC = StreamCodec.composite(
         UUIDUtil.STREAM_CODEC, ServerHaloPacket.Remove::playerUUID, ServerHaloPacket.Remove::new
      );

      public Type<ServerHaloPacket.Remove> type() {
         return TYPE;
      }

      public static void execute(ServerHaloPacket.Remove payload, IPayloadContext context) {
         Player playerEntity = context.player();
         if (playerEntity.getServer() != null && payload.playerUUID() != null) {
            ServerPerkData.HALO_INSTANCE.removePerk(playerEntity.getServer(), payload.playerUUID());
         }
      }
   }
}
