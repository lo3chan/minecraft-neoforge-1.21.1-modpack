package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class CrowCawPacket extends AbstractPacket {
   public static final StreamCodec<RegistryFriendlyByteBuf, CrowCawPacket> CODEC = StreamCodec.ofMember(CrowCawPacket::encode, CrowCawPacket::new);
   public static final Type<CrowCawPacket> TYPE = new Type(HexereiUtil.getResource("crow_caw"));
   int sourceId;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public CrowCawPacket(Entity entity) {
      this.sourceId = entity.getId();
   }

   public CrowCawPacket(RegistryFriendlyByteBuf buf) {
      this.sourceId = buf.readInt();
   }

   @Override
   public void encode(RegistryFriendlyByteBuf buffer) {
      buffer.writeInt(this.sourceId);
   }

   @Override
   public void onClientReceived(Minecraft minecraft, Player player) {
      if (player.level().getEntity(this.sourceId) != null && player.level().getEntity(this.sourceId) instanceof CrowEntity crow) {
         crow.caw = true;
         crow.cawTimer = 15;
         crow.cawTiltAngle = 80.0F;
      }
   }
}
