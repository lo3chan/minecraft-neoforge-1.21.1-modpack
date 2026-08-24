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

public class TailWagPacket extends AbstractPacket {
   public static final StreamCodec<RegistryFriendlyByteBuf, TailWagPacket> CODEC = StreamCodec.ofMember(TailWagPacket::encode, TailWagPacket::new);
   public static final Type<TailWagPacket> TYPE = new Type(HexereiUtil.getResource("tail_wag"));
   int sourceId;
   int duration;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public TailWagPacket(Entity entity, int duration) {
      this.sourceId = entity.getId();
      this.duration = duration;
   }

   public TailWagPacket(RegistryFriendlyByteBuf buf) {
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
      if (player.level().getEntity(this.sourceId) != null) {
         if (player.level().getEntity(this.sourceId) instanceof CrowEntity crow) {
            crow.tailWag = true;
            crow.tailWagTimer = 15;
         }

         if (player.level().getEntity(this.sourceId) instanceof OwlEntity owl) {
            owl.tailWagAnimation.start();
            owl.tailWagAnimation.activeTimer = this.duration;
         }
      }
   }
}
