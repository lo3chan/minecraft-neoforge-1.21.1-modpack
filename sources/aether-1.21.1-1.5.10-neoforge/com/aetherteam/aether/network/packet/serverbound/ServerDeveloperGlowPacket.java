package com.aetherteam.aether.network.packet.serverbound;

import com.aetherteam.aether.perk.data.ServerPerkData;
import com.aetherteam.aether.perk.types.DeveloperGlow;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerDeveloperGlowPacket {
   public record Apply(UUID playerUUID, DeveloperGlow developerGlow) implements CustomPacketPayload {
      public static final Type<ServerDeveloperGlowPacket.Apply> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("aether", "apply_developer_glow_server"));
      public static final StreamCodec<RegistryFriendlyByteBuf, ServerDeveloperGlowPacket.Apply> STREAM_CODEC = StreamCodec.composite(
         UUIDUtil.STREAM_CODEC,
         ServerDeveloperGlowPacket.Apply::playerUUID,
         DeveloperGlow.STREAM_CODEC,
         ServerDeveloperGlowPacket.Apply::developerGlow,
         ServerDeveloperGlowPacket.Apply::new
      );

      public Type<ServerDeveloperGlowPacket.Apply> type() {
         return TYPE;
      }

      public static void execute(ServerDeveloperGlowPacket.Apply payload, IPayloadContext context) {
         Player playerEntity = context.player();
         if (playerEntity.getServer() != null && payload.playerUUID() != null && payload.developerGlow() != null) {
            ServerPerkData.DEVELOPER_GLOW_INSTANCE.applyPerkWithVerification(playerEntity.getServer(), payload.playerUUID(), payload.developerGlow());
         }
      }
   }

   public record Remove(UUID playerUUID) implements CustomPacketPayload {
      public static final Type<ServerDeveloperGlowPacket.Remove> TYPE = new Type(
         ResourceLocation.fromNamespaceAndPath("aether", "remove_developer_glow_server")
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, ServerDeveloperGlowPacket.Remove> STREAM_CODEC = StreamCodec.composite(
         UUIDUtil.STREAM_CODEC, ServerDeveloperGlowPacket.Remove::playerUUID, ServerDeveloperGlowPacket.Remove::new
      );

      public Type<ServerDeveloperGlowPacket.Remove> type() {
         return TYPE;
      }

      public static void execute(ServerDeveloperGlowPacket.Remove payload, IPayloadContext context) {
         Player playerEntity = context.player();
         if (playerEntity.getServer() != null && payload.playerUUID() != null) {
            ServerPerkData.DEVELOPER_GLOW_INSTANCE.removePerk(playerEntity.getServer(), payload.playerUUID());
         }
      }
   }
}
