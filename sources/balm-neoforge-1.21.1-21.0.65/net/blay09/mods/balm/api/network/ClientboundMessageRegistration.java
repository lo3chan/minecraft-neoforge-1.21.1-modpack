package net.blay09.mods.balm.api.network;

import java.util.function.BiConsumer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.entity.player.Player;

public class ClientboundMessageRegistration<TBuffer extends FriendlyByteBuf, TPayload extends CustomPacketPayload>
   extends MessageRegistration<TBuffer, TPayload> {
   private final BiConsumer<Player, TPayload> handler;

   public ClientboundMessageRegistration(Type<TPayload> type, StreamCodec<TBuffer, TPayload> codec, BiConsumer<Player, TPayload> handler) {
      super(type, codec);
      this.handler = handler;
   }

   public BiConsumer<Player, TPayload> getHandler() {
      return this.handler;
   }
}
