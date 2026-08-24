package net.blay09.mods.balm.api.network;

import java.util.function.BiConsumer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.level.ServerPlayer;

public class ServerboundMessageRegistration<TBuffer extends FriendlyByteBuf, TPayload extends CustomPacketPayload>
   extends MessageRegistration<TBuffer, TPayload> {
   private final BiConsumer<ServerPlayer, TPayload> handler;

   public ServerboundMessageRegistration(Type<TPayload> type, StreamCodec<TBuffer, TPayload> codec, BiConsumer<ServerPlayer, TPayload> handler) {
      super(type, codec);
      this.handler = handler;
   }

   public BiConsumer<ServerPlayer, TPayload> getHandler() {
      return this.handler;
   }
}
