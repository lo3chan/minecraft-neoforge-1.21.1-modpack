package snownee.jade.network;

import java.util.Objects;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public interface ServerPayloadContext {
   default void execute(Runnable runnable) {
      Objects.requireNonNull(this.player().getServer()).execute(runnable);
   }

   default void sendPacket(CustomPacketPayload payload) {
      this.player().connection.send(new ClientboundCustomPayloadPacket(payload));
   }

   ServerPlayer player();
}
