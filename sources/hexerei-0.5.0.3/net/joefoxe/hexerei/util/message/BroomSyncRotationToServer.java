package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class BroomSyncRotationToServer extends AbstractPacket {
   public static final StreamCodec<RegistryFriendlyByteBuf, BroomSyncRotationToServer> CODEC = StreamCodec.ofMember(
      BroomSyncRotationToServer::encode, BroomSyncRotationToServer::new
   );
   public static final Type<BroomSyncRotationToServer> TYPE = new Type(HexereiUtil.getResource("broom_sync_rot_server"));
   int sourceId;
   float deltaRotation;
   Vec3 deltaMovement;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public BroomSyncRotationToServer(BroomEntity broom) {
      this.sourceId = broom.getId();
      this.deltaRotation = broom.deltaRotation;
      this.deltaMovement = broom.getDeltaMovement();
   }

   public BroomSyncRotationToServer(RegistryFriendlyByteBuf buf) {
      this.sourceId = buf.readInt();
      this.deltaRotation = buf.readFloat();
      this.deltaMovement = buf.readVec3();
   }

   @Override
   public void encode(RegistryFriendlyByteBuf buffer) {
      buffer.writeInt(this.sourceId);
      buffer.writeFloat(this.deltaRotation);
      buffer.writeVec3(this.deltaMovement);
   }

   @Override
   public void onServerReceived(MinecraftServer server, ServerPlayer player) {
      if (player.level().getEntity(this.sourceId) instanceof BroomEntity broom) {
         HexereiPacketHandler.sendToNearbyClient(broom.level(), broom, new BroomSyncRotation(this.sourceId, this.deltaRotation, this.deltaMovement));
      }
   }
}
