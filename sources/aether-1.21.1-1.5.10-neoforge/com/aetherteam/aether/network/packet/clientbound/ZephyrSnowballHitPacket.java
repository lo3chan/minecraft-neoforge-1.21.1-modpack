package com.aetherteam.aether.network.packet.clientbound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ZephyrSnowballHitPacket(int entityID, double xSpeed, double zSpeed) implements CustomPacketPayload {
   public static final Type<ZephyrSnowballHitPacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("aether", "zephyr_snowball_knockback_player"));
   public static final StreamCodec<RegistryFriendlyByteBuf, ZephyrSnowballHitPacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.INT,
      ZephyrSnowballHitPacket::entityID,
      ByteBufCodecs.DOUBLE,
      ZephyrSnowballHitPacket::xSpeed,
      ByteBufCodecs.DOUBLE,
      ZephyrSnowballHitPacket::zSpeed,
      ZephyrSnowballHitPacket::new
   );

   public Type<ZephyrSnowballHitPacket> type() {
      return TYPE;
   }

   public static void execute(ZephyrSnowballHitPacket payload, IPayloadContext context) {
      if (Minecraft.getInstance().player != null
         && Minecraft.getInstance().level != null
         && Minecraft.getInstance().player.level().getEntity(payload.entityID()) instanceof LocalPlayer localPlayer) {
         if (!localPlayer.isBlocking()) {
            localPlayer.setDeltaMovement(localPlayer.getDeltaMovement().x(), localPlayer.getDeltaMovement().y() + 0.5, localPlayer.getDeltaMovement().z());
         }

         localPlayer.setDeltaMovement(
            localPlayer.getDeltaMovement().x() + payload.xSpeed() * 1.5,
            localPlayer.getDeltaMovement().y(),
            localPlayer.getDeltaMovement().z() + payload.zSpeed() * 1.5
         );
      }
   }
}
