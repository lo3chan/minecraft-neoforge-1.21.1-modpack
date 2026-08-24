package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class HeadTiltPacket extends AbstractPacket {
   public static final StreamCodec<RegistryFriendlyByteBuf, HeadTiltPacket> CODEC = StreamCodec.ofMember(HeadTiltPacket::encode, HeadTiltPacket::new);
   public static final Type<HeadTiltPacket> TYPE = new Type(HexereiUtil.getResource("head_tilt"));
   int sourceId;
   int duration;
   float xTilt;
   float zTilt;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public HeadTiltPacket(Entity entity, int duration, float xTilt, float zTilt) {
      this.sourceId = entity.getId();
      this.duration = duration;
      this.xTilt = xTilt;
      this.zTilt = zTilt;
   }

   public HeadTiltPacket(RegistryFriendlyByteBuf buf) {
      this.sourceId = buf.readInt();
      this.duration = buf.readInt();
      this.xTilt = buf.readFloat();
      this.zTilt = buf.readFloat();
   }

   @Override
   public void encode(RegistryFriendlyByteBuf buffer) {
      buffer.writeInt(this.sourceId);
      buffer.writeInt(this.duration);
      buffer.writeFloat(this.xTilt);
      buffer.writeFloat(this.zTilt);
   }

   @Override
   public void onClientReceived(Minecraft minecraft, Player player) {
      if (player.level().getEntity(this.sourceId) != null) {
         if (player.level().getEntity(this.sourceId) instanceof CrowEntity crow) {
            crow.peck();
         }

         if (player.level().getEntity(this.sourceId) instanceof OwlEntity owl) {
            owl.headTiltAnimation.start();
            owl.headTiltAnimation.xTiltTarget = this.xTilt;
            owl.headTiltAnimation.zTiltTarget = this.zTilt;
            owl.headTiltAnimation.activeTimer = this.duration;
         }
      }
   }
}
