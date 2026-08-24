package corgitaco.corgilib.network;

import corgitaco.corgilib.CorgiLib;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface Packet extends CustomPacketPayload {
   List<Packet.Handler<?>> PACKETS = (List<Packet.Handler<?>>)Util.make(
      new ArrayList(),
      list -> {
         CorgiLib.LOGGER.info("Initializing network...");
         list.add(
            new Packet.Handler<>(
               EntityIsInsideStructureTrackerUpdatePacket.TYPE,
               Packet.PacketDirection.SERVER_TO_CLIENT,
               EntityIsInsideStructureTrackerUpdatePacket.CODEC,
               EntityIsInsideStructureTrackerUpdatePacket::handle
            )
         );
         list.add(
            new Packet.Handler<>(
               UpdateStructureBoxPacketC2S.TYPE,
               Packet.PacketDirection.CLIENT_TO_SERVER,
               UpdateStructureBoxPacketC2S.CODEC,
               UpdateStructureBoxPacketC2S::handle
            )
         );
         CorgiLib.LOGGER.info("Initialized network!");
      }
   );

   void handle(@Nullable Level var1, @Nullable Player var2);

   @FunctionalInterface
   public interface Handle<T extends Packet> {
      void handle(T var1, Level var2, Player var3);
   }

   public record Handler<T extends Packet>(
      Type<T> type, Packet.PacketDirection direction, StreamCodec<RegistryFriendlyByteBuf, T> serializer, Packet.Handle<T> handle
   ) {
   }

   public static enum PacketDirection {
      SERVER_TO_CLIENT,
      CLIENT_TO_SERVER,
      BI_DIRECTIONAL;
   }
}
