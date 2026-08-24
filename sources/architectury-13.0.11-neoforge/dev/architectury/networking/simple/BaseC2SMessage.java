package dev.architectury.networking.simple;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public abstract class BaseC2SMessage extends Message {
   @OnlyIn(Dist.CLIENT)
   public final void sendToServer() {
      if (Minecraft.getInstance().getConnection() != null) {
         Minecraft.getInstance().getConnection().send(this.toPacket(Minecraft.getInstance().level.registryAccess()));
      } else {
         throw new IllegalStateException("Unable to send packet to the server while not in game!");
      }
   }
}
