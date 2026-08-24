package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class OwlHootPacket extends AbstractPacket {
   public static final StreamCodec<RegistryFriendlyByteBuf, OwlHootPacket> CODEC = StreamCodec.ofMember(OwlHootPacket::encode, OwlHootPacket::new);
   public static final Type<OwlHootPacket> TYPE = new Type(HexereiUtil.getResource("owl_hoot"));
   int sourceId;
   int duration;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public OwlHootPacket(Entity entity, int duration) {
      this.sourceId = entity.getId();
      this.duration = duration;
   }

   public OwlHootPacket(FriendlyByteBuf buf) {
      this.sourceId = buf.readInt();
      this.duration = buf.readInt();
   }

   @Override
   public void encode(RegistryFriendlyByteBuf buffer) {
      buffer.writeInt(this.sourceId);
      buffer.writeInt(this.duration);
   }

   @Override
   public void onClientReceived(Minecraft minecraft, Player player) {
      if (player.level().getEntity(this.sourceId) != null && player.level().getEntity(this.sourceId) instanceof OwlEntity owl) {
         owl.hootAnimation.start();
         owl.hootAnimation.activeTimer = this.duration;
      }
   }
}
