package com.aetherteam.aether.network.packet;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherTimeAttachment;
import com.aetherteam.nitrogen.attachment.INBTSynchable.Type;
import com.aetherteam.nitrogen.network.packet.SyncLevelPacket;
import com.aetherteam.nitrogen.network.packet.SyncPacket;
import java.util.function.Supplier;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.apache.commons.lang3.tuple.Triple;

public class AetherTimeSyncPacket extends SyncLevelPacket<AetherTimeAttachment> {
   public static final net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<AetherTimeSyncPacket> TYPE = new net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type(
      ResourceLocation.fromNamespaceAndPath("aether", "sync_aether_time_attachment")
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, AetherTimeSyncPacket> STREAM_CODEC = CustomPacketPayload.codec(
      SyncPacket::write, AetherTimeSyncPacket::decode
   );

   public AetherTimeSyncPacket(Triple<String, Type, Object> values) {
      super(values);
   }

   public AetherTimeSyncPacket(String key, Type type, Object value) {
      super(key, type, value);
   }

   public net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<AetherTimeSyncPacket> type() {
      return TYPE;
   }

   public static AetherTimeSyncPacket decode(RegistryFriendlyByteBuf buf) {
      return new AetherTimeSyncPacket(SyncLevelPacket.decodeValues(buf));
   }

   public Supplier<AttachmentType<AetherTimeAttachment>> getAttachment() {
      return AetherDataAttachments.AETHER_TIME;
   }

   public static void execute(AetherTimeSyncPacket payload, IPayloadContext context) {
      SyncLevelPacket.execute(payload, context.player());
   }
}
