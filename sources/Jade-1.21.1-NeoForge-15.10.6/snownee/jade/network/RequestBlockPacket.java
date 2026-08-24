package snownee.jade.network;

import java.util.List;
import java.util.Objects;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.JadeIds;
import snownee.jade.impl.BlockAccessorImpl;
import snownee.jade.impl.WailaCommonRegistration;

public record RequestBlockPacket(BlockAccessorImpl.SyncData data, List<IServerDataProvider<BlockAccessor>> dataProviders) implements CustomPacketPayload {
   public static final Type<RequestBlockPacket> TYPE = new Type(JadeIds.PACKET_REQUEST_BLOCK);
   public static final StreamCodec<RegistryFriendlyByteBuf, RequestBlockPacket> CODEC = StreamCodec.composite(
      BlockAccessorImpl.SyncData.STREAM_CODEC,
      RequestBlockPacket::data,
      ByteBufCodecs.list()
         .apply(
            ByteBufCodecs.idMapper(
               $ -> (IServerDataProvider)Objects.requireNonNull(WailaCommonRegistration.instance().blockDataProviders.idMapper()).byId($),
               $ -> Objects.requireNonNull(WailaCommonRegistration.instance().blockDataProviders.idMapper()).getIdOrThrow($)
            )
         ),
      RequestBlockPacket::dataProviders,
      RequestBlockPacket::new
   );

   public static void handle(RequestBlockPacket message, ServerPayloadContext context) {
      BlockAccessorImpl.handleRequest(message, context, tag -> ReceiveDataPacket.send(tag, context));
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
