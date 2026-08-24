package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class CrowSyncCommandToServer extends AbstractPacket {
   public static final StreamCodec<RegistryFriendlyByteBuf, CrowSyncCommandToServer> CODEC = StreamCodec.ofMember(
      CrowSyncCommandToServer::encode, CrowSyncCommandToServer::new
   );
   public static final Type<CrowSyncCommandToServer> TYPE = new Type(HexereiUtil.getResource("crow_sync_command_server"));
   int sourceId;
   int command;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public CrowSyncCommandToServer(Entity entity, int tag) {
      this.sourceId = entity.getId();
      this.command = tag;
   }

   public CrowSyncCommandToServer(RegistryFriendlyByteBuf buf) {
      this.sourceId = buf.readInt();
      this.command = buf.readInt();
   }

   @Override
   public void encode(RegistryFriendlyByteBuf buffer) {
      buffer.writeInt(this.sourceId);
      buffer.writeInt(this.command);
   }

   @Override
   public void onServerReceived(MinecraftServer server, ServerPlayer player) {
      if (player.level().getEntity(this.sourceId) instanceof CrowEntity crowEntity) {
         crowEntity.setCommand(this.command);
      }
   }
}
