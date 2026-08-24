package com.aetherteam.aether.network.packet.serverbound;

import com.aetherteam.aether.entity.passive.Aerbunny;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record AerbunnyPuffPacket(int entityID) implements CustomPacketPayload {
   public static final Type<AerbunnyPuffPacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("aether", "aerbunny_puff"));
   public static final StreamCodec<RegistryFriendlyByteBuf, AerbunnyPuffPacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.INT, AerbunnyPuffPacket::entityID, AerbunnyPuffPacket::new
   );

   public Type<AerbunnyPuffPacket> type() {
      return TYPE;
   }

   public static void execute(AerbunnyPuffPacket payload, IPayloadContext context) {
      Player playerEntity = context.player();
      if (playerEntity.getServer() != null && playerEntity.level().getEntity(payload.entityID()) instanceof Aerbunny aerbunny) {
         aerbunny.puff();
      }
   }
}
