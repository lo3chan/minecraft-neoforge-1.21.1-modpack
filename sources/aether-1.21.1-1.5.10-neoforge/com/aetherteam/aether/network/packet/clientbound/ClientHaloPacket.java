package com.aetherteam.aether.network.packet.clientbound;

import com.aetherteam.aether.perk.data.ClientHaloPerkData;
import com.aetherteam.aether.perk.types.Halo;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientHaloPacket {
   public record Apply(UUID playerUUID, Halo halo) implements CustomPacketPayload {
      public static final Type<ClientHaloPacket.Apply> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("aether", "apply_halo"));
      public static final StreamCodec<RegistryFriendlyByteBuf, ClientHaloPacket.Apply> STREAM_CODEC = StreamCodec.composite(
         UUIDUtil.STREAM_CODEC, ClientHaloPacket.Apply::playerUUID, Halo.STREAM_CODEC, ClientHaloPacket.Apply::halo, ClientHaloPacket.Apply::new
      );

      public Type<ClientHaloPacket.Apply> type() {
         return TYPE;
      }

      public static void execute(ClientHaloPacket.Apply payload, IPayloadContext context) {
         if (Minecraft.getInstance().player != null && Minecraft.getInstance().level != null && payload.playerUUID() != null && payload.halo() != null) {
            ClientHaloPerkData.INSTANCE.applyPerk(payload.playerUUID(), payload.halo());
         }
      }
   }

   public record Remove(UUID playerUUID) implements CustomPacketPayload {
      public static final Type<ClientHaloPacket.Remove> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("aether", "remove_halo"));
      public static final StreamCodec<RegistryFriendlyByteBuf, ClientHaloPacket.Remove> STREAM_CODEC = StreamCodec.composite(
         UUIDUtil.STREAM_CODEC, ClientHaloPacket.Remove::playerUUID, ClientHaloPacket.Remove::new
      );

      public Type<ClientHaloPacket.Remove> type() {
         return TYPE;
      }

      public static void execute(ClientHaloPacket.Remove payload, IPayloadContext context) {
         if (Minecraft.getInstance().player != null && Minecraft.getInstance().level != null && payload.playerUUID() != null) {
            ClientHaloPerkData.INSTANCE.removePerk(payload.playerUUID());
         }
      }
   }

   public record Sync(Map<UUID, Halo> halos) implements CustomPacketPayload {
      public static final Type<ClientHaloPacket.Sync> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("aether", "sync_halo"));
      public static final StreamCodec<RegistryFriendlyByteBuf, ClientHaloPacket.Sync> STREAM_CODEC = StreamCodec.composite(
         ByteBufCodecs.map(Maps::newHashMapWithExpectedSize, UUIDUtil.STREAM_CODEC, Halo.STREAM_CODEC),
         ClientHaloPacket.Sync::halos,
         ClientHaloPacket.Sync::new
      );

      public Type<?> type() {
         return TYPE;
      }

      public static void execute(ClientHaloPacket.Sync payload, IPayloadContext context) {
         if (Minecraft.getInstance().player != null && Minecraft.getInstance().level != null && payload.halos() != null && !payload.halos().isEmpty()) {
            for (Entry<UUID, Halo> haloEntry : payload.halos().entrySet()) {
               ClientHaloPerkData.INSTANCE.applyPerk(haloEntry.getKey(), haloEntry.getValue());
            }
         }
      }
   }
}
