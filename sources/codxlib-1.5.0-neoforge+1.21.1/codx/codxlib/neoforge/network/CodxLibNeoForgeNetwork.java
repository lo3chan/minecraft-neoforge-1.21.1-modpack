package codx.codxlib.neoforge.network;

import codx.codxlib.api.network.CodxNetwork;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class CodxLibNeoForgeNetwork {
   private CodxLibNeoForgeNetwork() {
   }

   public static void installSenders() {
      CodxNetwork.setServerSender((x$0, x$1) -> PacketDistributor.sendToPlayer(x$0, x$1, new CustomPacketPayload[0]));
   }

   public static void register(RegisterPayloadHandlersEvent event) {
      final PayloadRegistrar registrar = event.registrar("1");
      CodxNetwork.visitAll(
         new CodxNetwork.PayloadVisitor() {
            @Override
            public <T extends CustomPacketPayload> void clientbound(
               Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec, CodxNetwork.ClientHandler<T> handler
            ) {
               registrar.playToClient(type, codec, (payload, context) -> context.enqueueWork(() -> handler.handle((T)payload)));
            }

            @Override
            public <T extends CustomPacketPayload> void serverbound(
               Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec, CodxNetwork.ServerHandler<T> handler
            ) {
               registrar.playToServer(type, codec, (payload, context) -> context.enqueueWork(() -> {
                  if (context.player() instanceof ServerPlayer serverPlayer) {
                     handler.handle((T)payload, serverPlayer);
                  }
               }));
            }
         }
      );
   }
}
