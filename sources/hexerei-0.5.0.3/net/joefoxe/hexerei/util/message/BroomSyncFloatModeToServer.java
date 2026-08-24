package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class BroomSyncFloatModeToServer extends AbstractPacket {
   public static final StreamCodec<RegistryFriendlyByteBuf, BroomSyncFloatModeToServer> CODEC = StreamCodec.ofMember(
      BroomSyncFloatModeToServer::encode, BroomSyncFloatModeToServer::new
   );
   public static final Type<BroomSyncFloatModeToServer> TYPE = new Type(HexereiUtil.getResource("broom_sync_mode"));
   int sourceId;
   boolean mode;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public BroomSyncFloatModeToServer(int id, boolean mode) {
      this.sourceId = id;
      this.mode = mode;
   }

   public BroomSyncFloatModeToServer(RegistryFriendlyByteBuf buffer) {
      this(buffer.readInt(), buffer.readBoolean());
   }

   @Override
   public void encode(RegistryFriendlyByteBuf buffer) {
      buffer.writeInt(this.sourceId);
      buffer.writeBoolean(this.mode);
   }

   @Override
   public void onServerReceived(MinecraftServer server, ServerPlayer player) {
      if (server.overworld().getEntity(this.sourceId) instanceof BroomEntity broom) {
         broom.setFloatMode(this.mode);
      }
   }
}
