package com.aetherteam.aether.network.packet.clientbound;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import com.aetherteam.aether.entity.passive.Aerbunny;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RemountAerbunnyPacket(int vehicleID, int aerbunnyID) implements CustomPacketPayload {
   public static final Type<RemountAerbunnyPacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("aether", "remount_aerbunny"));
   public static final StreamCodec<RegistryFriendlyByteBuf, RemountAerbunnyPacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.INT, RemountAerbunnyPacket::vehicleID, ByteBufCodecs.INT, RemountAerbunnyPacket::aerbunnyID, RemountAerbunnyPacket::new
   );

   public Type<RemountAerbunnyPacket> type() {
      return TYPE;
   }

   public static void execute(RemountAerbunnyPacket payload, IPayloadContext context) {
      if (Minecraft.getInstance().player != null && Minecraft.getInstance().level != null) {
         Level world = Minecraft.getInstance().player.level();
         if (world.getEntity(payload.vehicleID()) instanceof Player player && world.getEntity(payload.aerbunnyID()) instanceof Aerbunny aerbunny) {
            aerbunny.startRiding(player);
            ((AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER)).setMountedAerbunny(aerbunny);
         }
      }
   }
}
