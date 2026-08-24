package software.bernie.geckolib.network.packet;

import java.util.function.Consumer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.constant.dataticket.SerializableDataTicket;
import software.bernie.geckolib.util.ClientUtil;
import software.bernie.geckolib.util.GeckoLibUtil;

public record SingletonDataSyncPacket<D>(String syncableId, long instanceId, SerializableDataTicket<D> dataTicket, D data) implements MultiloaderPacket {
   public static final Type<SingletonDataSyncPacket<?>> TYPE = new Type(GeckoLibConstants.id("singleton_data_sync"));
   public static final StreamCodec<RegistryFriendlyByteBuf, SingletonDataSyncPacket<?>> CODEC = StreamCodec.of((buf, packet) -> {
      SerializableDataTicket.STREAM_CODEC.encode(buf, packet.dataTicket);
      buf.writeUtf(packet.syncableId);
      buf.writeVarLong(packet.instanceId);
      packet.dataTicket.streamCodec().encode(buf, packet.data);
   }, buf -> {
      SerializableDataTicket dataTicket = (SerializableDataTicket)SerializableDataTicket.STREAM_CODEC.decode(buf);
      return new SingletonDataSyncPacket<>(buf.readUtf(), buf.readVarLong(), dataTicket, dataTicket.streamCodec().decode(buf));
   });

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   @Override
   public void receiveMessage(@Nullable Player sender, Consumer<Runnable> workQueue) {
      workQueue.accept(() -> {
         if (GeckoLibUtil.getSyncedAnimatable(this.syncableId) instanceof SingletonGeoAnimatable singleton) {
            singleton.setAnimData(ClientUtil.getClientPlayer(), this.instanceId, this.dataTicket, this.data);
         }
      });
   }
}
