package software.bernie.geckolib.network.packet;

import java.util.function.Consumer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.GeoReplacedEntity;
import software.bernie.geckolib.constant.dataticket.SerializableDataTicket;
import software.bernie.geckolib.util.ClientUtil;
import software.bernie.geckolib.util.RenderUtil;

public record EntityDataSyncPacket<D>(int entityId, boolean isReplacedEntity, SerializableDataTicket<D> dataTicket, D data) implements MultiloaderPacket {
   public static final Type<EntityDataSyncPacket<?>> TYPE = new Type(GeckoLibConstants.id("entity_data_sync"));
   public static final StreamCodec<RegistryFriendlyByteBuf, EntityDataSyncPacket<?>> CODEC = StreamCodec.of((buf, packet) -> {
      SerializableDataTicket.STREAM_CODEC.encode(buf, packet.dataTicket);
      buf.writeVarInt(packet.entityId);
      buf.writeBoolean(packet.isReplacedEntity);
      packet.dataTicket.streamCodec().encode(buf, packet.data);
   }, buf -> {
      SerializableDataTicket dataTicket = (SerializableDataTicket)SerializableDataTicket.STREAM_CODEC.decode(buf);
      return new EntityDataSyncPacket<>(buf.readVarInt(), buf.readBoolean(), dataTicket, dataTicket.streamCodec().decode(buf));
   });

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   @Override
   public void receiveMessage(@Nullable Player sender, Consumer<Runnable> workQueue) {
      workQueue.accept(() -> {
         Entity entity = ClientUtil.getLevel().getEntity(this.entityId);
         if (entity != null) {
            if (!this.isReplacedEntity) {
               if (entity instanceof GeoEntity geoEntity) {
                  geoEntity.setAnimData(this.dataTicket, this.data);
               }
            } else {
               if (RenderUtil.getReplacedAnimatable(entity.getType()) instanceof GeoReplacedEntity replacedEntity) {
                  replacedEntity.setAnimData(entity, this.dataTicket, this.data);
               }
            }
         }
      });
   }
}
