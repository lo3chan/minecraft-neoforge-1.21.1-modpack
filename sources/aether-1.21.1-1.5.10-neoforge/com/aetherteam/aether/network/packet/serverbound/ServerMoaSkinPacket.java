package com.aetherteam.aether.network.packet.serverbound;

import com.aetherteam.aether.perk.data.ServerPerkData;
import com.aetherteam.aether.perk.types.MoaData;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public abstract class ServerMoaSkinPacket {
   public record Apply(UUID playerUUID, MoaData moaSkinData) implements CustomPacketPayload {
      public static final Type<ServerMoaSkinPacket.Apply> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("aether", "apply_moa_skin_server"));
      public static final StreamCodec<RegistryFriendlyByteBuf, ServerMoaSkinPacket.Apply> STREAM_CODEC = StreamCodec.composite(
         UUIDUtil.STREAM_CODEC,
         ServerMoaSkinPacket.Apply::playerUUID,
         MoaData.STREAM_CODEC,
         ServerMoaSkinPacket.Apply::moaSkinData,
         ServerMoaSkinPacket.Apply::new
      );

      public Type<ServerMoaSkinPacket.Apply> type() {
         return TYPE;
      }

      public static void execute(ServerMoaSkinPacket.Apply payload, IPayloadContext context) {
         Player playerEntity = context.player();
         if (playerEntity.getServer() != null && payload.playerUUID() != null && payload.moaSkinData() != null) {
            ServerPerkData.MOA_SKIN_INSTANCE.applyPerkWithVerification(playerEntity.getServer(), payload.playerUUID(), payload.moaSkinData());
         }
      }
   }

   public record Remove(UUID playerUUID) implements CustomPacketPayload {
      public static final Type<ServerMoaSkinPacket.Remove> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("aether", "remove_moa_skin_server"));
      public static final StreamCodec<RegistryFriendlyByteBuf, ServerMoaSkinPacket.Remove> STREAM_CODEC = StreamCodec.composite(
         UUIDUtil.STREAM_CODEC, ServerMoaSkinPacket.Remove::playerUUID, ServerMoaSkinPacket.Remove::new
      );

      public Type<ServerMoaSkinPacket.Remove> type() {
         return TYPE;
      }

      public static void execute(ServerMoaSkinPacket.Remove payload, IPayloadContext context) {
         Player playerEntity = context.player();
         if (playerEntity.getServer() != null && payload.playerUUID() != null) {
            ServerPerkData.MOA_SKIN_INSTANCE.removePerk(playerEntity.getServer(), payload.playerUUID());
         }
      }
   }
}
