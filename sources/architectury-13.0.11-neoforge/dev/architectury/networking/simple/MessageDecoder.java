package dev.architectury.networking.simple;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.RegistryFriendlyByteBuf;

@FunctionalInterface
public interface MessageDecoder<T extends Message> {
   T decode(RegistryFriendlyByteBuf var1);

   default NetworkManager.NetworkReceiver<RegistryFriendlyByteBuf> createReceiver() {
      return (buf, context) -> {
         Message packet = this.decode(buf);
         context.queue(() -> packet.handle(context));
      };
   }
}
