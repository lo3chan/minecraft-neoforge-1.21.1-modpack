package net.mehvahdjukaar.moonlight.api.platform.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.TypeAndCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public interface Message extends CustomPacketPayload {
   static <T extends Message> TypeAndCodec<RegistryFriendlyByteBuf, T> makeType(ResourceLocation id, StreamDecoder<RegistryFriendlyByteBuf, T> decoder) {
      return new TypeAndCodec(new Type(id), StreamCodec.ofMember(Message::write, decoder));
   }

   void write(RegistryFriendlyByteBuf var1);

   void handle(Message.Context var1);

   public interface Context {
      Message.NetworkDir getDirection();

      Player getPlayer();

      void disconnect(Component var1);

      void reply(CustomPacketPayload var1);
   }

   public static enum NetworkDir {
      SERVER_BOUND,
      CLIENT_BOUND;

      public Message.NetworkDir getOpposite() {
         return this == SERVER_BOUND ? CLIENT_BOUND : SERVER_BOUND;
      }
   }
}
