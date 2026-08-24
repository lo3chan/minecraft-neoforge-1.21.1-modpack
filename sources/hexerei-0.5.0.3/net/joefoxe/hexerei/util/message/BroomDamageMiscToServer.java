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

public class BroomDamageMiscToServer extends AbstractPacket {
   public static final StreamCodec<RegistryFriendlyByteBuf, BroomDamageMiscToServer> CODEC = StreamCodec.ofMember(
      BroomDamageMiscToServer::encode, BroomDamageMiscToServer::new
   );
   public static final Type<BroomDamageMiscToServer> TYPE = new Type(HexereiUtil.getResource("broom_damage_misc"));
   int sourceId;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public BroomDamageMiscToServer(int id) {
      this.sourceId = id;
   }

   public BroomDamageMiscToServer(RegistryFriendlyByteBuf buf) {
      this.sourceId = buf.readInt();
   }

   @Override
   public void encode(RegistryFriendlyByteBuf buffer) {
      buffer.writeInt(this.sourceId);
   }

   @Override
   public void onServerReceived(MinecraftServer server, ServerPlayer player) {
      if (player.level().getEntity(this.sourceId) instanceof BroomEntity broom) {
         broom.damageMisc();
      }
   }
}
