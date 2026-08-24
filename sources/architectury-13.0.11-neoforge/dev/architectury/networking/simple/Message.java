package dev.architectury.networking.simple;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public abstract class Message {
   Message() {
   }

   public abstract MessageType getType();

   public abstract void write(RegistryFriendlyByteBuf var1);

   public abstract void handle(NetworkManager.PacketContext var1);

   public final Packet<?> toPacket(RegistryAccess access) {
      RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), access);
      this.write(buf);
      return NetworkManager.toPacket(this.getType().getSide(), this.getType().getId(), buf);
   }
}
