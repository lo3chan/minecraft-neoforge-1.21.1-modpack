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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class StartRidingPacket extends AbstractPacket {
   public static final StreamCodec<RegistryFriendlyByteBuf, StartRidingPacket> CODEC = StreamCodec.ofMember(StartRidingPacket::encode, StartRidingPacket::new);
   public static final Type<StartRidingPacket> TYPE = new Type(HexereiUtil.getResource("start_riding"));
   int sourceIdCrow;
   int sourceIdPlayer;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public StartRidingPacket(Entity entity, Player player) {
      this.sourceIdCrow = entity.getId();
      this.sourceIdPlayer = player.getId();
   }

   public StartRidingPacket(RegistryFriendlyByteBuf buf) {
      this.sourceIdCrow = buf.readInt();
      this.sourceIdPlayer = buf.readInt();
   }

   @Override
   public void encode(RegistryFriendlyByteBuf buffer) {
      buffer.writeInt(this.sourceIdCrow);
      buffer.writeInt(this.sourceIdPlayer);
   }

   @Override
   public void onClientReceived(Minecraft minecraft, Player player) {
      if (player.level().getEntity(this.sourceIdPlayer) instanceof LivingEntity livingEntity) {
         if (player.level().getEntity(this.sourceIdCrow) instanceof CrowEntity crow) {
            crow.startRiding(livingEntity, true);
         }

         if (player.level().getEntity(this.sourceIdCrow) instanceof OwlEntity owl) {
            owl.startRiding(livingEntity, true);
         }
      }
   }
}
