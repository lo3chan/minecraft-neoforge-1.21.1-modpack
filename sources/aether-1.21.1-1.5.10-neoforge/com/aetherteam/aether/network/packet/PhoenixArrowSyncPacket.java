package com.aetherteam.aether.network.packet;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.PhoenixArrowAttachment;
import com.aetherteam.nitrogen.attachment.INBTSynchable.Type;
import com.aetherteam.nitrogen.network.packet.SyncEntityPacket;
import java.util.function.Supplier;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import oshi.util.tuples.Quartet;

public class PhoenixArrowSyncPacket extends SyncEntityPacket<PhoenixArrowAttachment> {
   public static final net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<PhoenixArrowSyncPacket> TYPE = new net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type(
      ResourceLocation.fromNamespaceAndPath("aether", "sync_phoenix_arrow_attachment")
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, PhoenixArrowSyncPacket> STREAM_CODEC = CustomPacketPayload.codec(
      SyncEntityPacket::write, PhoenixArrowSyncPacket::decode
   );

   public PhoenixArrowSyncPacket(Quartet<Integer, String, Type, Object> values) {
      super(values);
   }

   public PhoenixArrowSyncPacket(int playerID, String key, Type type, Object value) {
      super(playerID, key, type, value);
   }

   public net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<PhoenixArrowSyncPacket> type() {
      return TYPE;
   }

   public static PhoenixArrowSyncPacket decode(RegistryFriendlyByteBuf buf) {
      return new PhoenixArrowSyncPacket(SyncEntityPacket.decodeEntityValues(buf));
   }

   public Supplier<AttachmentType<PhoenixArrowAttachment>> getAttachment() {
      return AetherDataAttachments.PHOENIX_ARROW;
   }

   public static void execute(PhoenixArrowSyncPacket payload, IPayloadContext context) {
      SyncEntityPacket.execute(payload, context.player());
   }
}
