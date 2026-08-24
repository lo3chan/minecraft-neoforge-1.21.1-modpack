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

public class BroomDamageBrushToServer extends AbstractPacket {
   public static final StreamCodec<RegistryFriendlyByteBuf, BroomDamageBrushToServer> CODEC = StreamCodec.ofMember(
      BroomDamageBrushToServer::encode, BroomDamageBrushToServer::new
   );
   public static final Type<BroomDamageBrushToServer> TYPE = new Type(HexereiUtil.getResource("broom_damage_brush_server"));
   int sourceId;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public BroomDamageBrushToServer(int id) {
      this.sourceId = id;
   }

   public BroomDamageBrushToServer(RegistryFriendlyByteBuf buffer) {
      this(buffer.readInt());
   }

   @Override
   public void encode(RegistryFriendlyByteBuf buffer) {
      buffer.writeInt(this.sourceId);
   }

   @Override
   public void onServerReceived(MinecraftServer server, ServerPlayer player) {
      if (server.overworld().getEntity(this.sourceId) instanceof BroomEntity broom) {
         broom.damageBrush();
      }
   }
}
