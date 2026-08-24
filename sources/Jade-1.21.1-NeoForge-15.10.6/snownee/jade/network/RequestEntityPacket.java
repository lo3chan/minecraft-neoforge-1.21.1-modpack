package snownee.jade.network;

import java.util.List;
import java.util.Objects;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.JadeIds;
import snownee.jade.impl.EntityAccessorImpl;
import snownee.jade.impl.WailaCommonRegistration;

public record RequestEntityPacket(EntityAccessorImpl.SyncData data, List<IServerDataProvider<EntityAccessor>> dataProviders) implements CustomPacketPayload {
   public static final Type<RequestEntityPacket> TYPE = new Type(JadeIds.PACKET_REQUEST_ENTITY);
   public static final StreamCodec<RegistryFriendlyByteBuf, RequestEntityPacket> CODEC = StreamCodec.composite(
      EntityAccessorImpl.SyncData.STREAM_CODEC,
      RequestEntityPacket::data,
      ByteBufCodecs.list()
         .apply(
            ByteBufCodecs.idMapper(
               $ -> (IServerDataProvider)Objects.requireNonNull(WailaCommonRegistration.instance().entityDataProviders.idMapper()).byId($),
               $ -> Objects.requireNonNull(WailaCommonRegistration.instance().entityDataProviders.idMapper()).getIdOrThrow($)
            )
         ),
      RequestEntityPacket::dataProviders,
      RequestEntityPacket::new
   );

   public static void handle(RequestEntityPacket message, ServerPayloadContext context) {
      EntityAccessorImpl.handleRequest(message, context, tag -> ReceiveDataPacket.send(tag, context));
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
