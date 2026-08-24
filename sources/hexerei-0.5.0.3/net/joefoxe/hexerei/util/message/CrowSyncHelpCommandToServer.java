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

public class CrowSyncHelpCommandToServer extends AbstractPacket {
   public static final StreamCodec<RegistryFriendlyByteBuf, CrowSyncHelpCommandToServer> CODEC = StreamCodec.ofMember(
      CrowSyncHelpCommandToServer::encode, CrowSyncHelpCommandToServer::new
   );
   public static final Type<CrowSyncHelpCommandToServer> TYPE = new Type(HexereiUtil.getResource("crow_sync_help_command_server"));
   int sourceId;
   int command;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public CrowSyncHelpCommandToServer(Entity entity, int tag) {
      this.sourceId = entity.getId();
      this.command = tag;
   }

   public CrowSyncHelpCommandToServer(RegistryFriendlyByteBuf buf) {
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
         crowEntity.setHelpCommand(this.command);
      }
   }
}
