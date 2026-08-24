package dev.corgitaco.dataanchor.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface Packet extends CustomPacketPayload {
   void handle(@Nullable Level var1, @Nullable Player var2);

   @FunctionalInterface
   public interface Handle<T extends Packet> {
      void handle(T var1, Level var2, Player var3);
   }

   public record Handler<T extends Packet>(Class<T> clazz, Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> serializer, Packet.Handle<T> handle) {
   }
}
