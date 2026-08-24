package io.github.razordevs.deep_aether.networking.packet;

import com.aetherteam.nitrogen.attachment.INBTSynchable.Type;
import com.aetherteam.nitrogen.network.packet.SyncEntityPacket;
import io.github.razordevs.deep_aether.networking.attachment.DAAttachments;
import io.github.razordevs.deep_aether.networking.attachment.DAPlayerAttachment;
import java.util.function.Supplier;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import oshi.util.tuples.Quartet;

public class DAPlayerSyncPacket extends SyncEntityPacket<DAPlayerAttachment> {
   public static final net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<DAPlayerSyncPacket> TYPE = new net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type(
      ResourceLocation.fromNamespaceAndPath("deep_aether", "sync_da_player_attachment")
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, DAPlayerSyncPacket> STREAM_CODEC = CustomPacketPayload.codec(
      SyncEntityPacket::write, DAPlayerSyncPacket::decode
   );

   public DAPlayerSyncPacket(Quartet<Integer, String, Type, Object> values) {
      super(values);
   }

   public DAPlayerSyncPacket(int playerID, String key, Type type, Object value) {
      super(playerID, key, type, value);
   }

   public static DAPlayerSyncPacket decode(RegistryFriendlyByteBuf buf) {
      return new DAPlayerSyncPacket(SyncEntityPacket.decodeEntityValues(buf));
   }

   public Supplier<AttachmentType<DAPlayerAttachment>> getAttachment() {
      return DAAttachments.PLAYER;
   }

   public net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void execute(DAPlayerSyncPacket payload, IPayloadContext context) {
      SyncEntityPacket.execute(payload, context.player());
   }
}
