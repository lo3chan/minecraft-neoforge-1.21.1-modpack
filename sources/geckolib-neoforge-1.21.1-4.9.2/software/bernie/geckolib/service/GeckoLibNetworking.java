package software.bernie.geckolib.service;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;
import software.bernie.geckolib.GeckoLibServices;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.constant.dataticket.SerializableDataTicket;
import software.bernie.geckolib.network.packet.BlockEntityAnimTriggerPacket;
import software.bernie.geckolib.network.packet.BlockEntityDataSyncPacket;
import software.bernie.geckolib.network.packet.EntityAnimTriggerPacket;
import software.bernie.geckolib.network.packet.EntityDataSyncPacket;
import software.bernie.geckolib.network.packet.MultiloaderPacket;
import software.bernie.geckolib.network.packet.SingletonAnimTriggerPacket;
import software.bernie.geckolib.network.packet.SingletonDataSyncPacket;
import software.bernie.geckolib.network.packet.StatelessBlockEntityPlayAnimPacket;
import software.bernie.geckolib.network.packet.StatelessBlockEntityStopAnimPacket;
import software.bernie.geckolib.network.packet.StatelessEntityPlayAnimPacket;
import software.bernie.geckolib.network.packet.StatelessEntityStopAnimPacket;
import software.bernie.geckolib.network.packet.StatelessSingletonPlayAnimPacket;
import software.bernie.geckolib.network.packet.StatelessSingletonStopAnimPacket;
import software.bernie.geckolib.network.packet.StopTriggeredBlockEntityAnimPacket;
import software.bernie.geckolib.network.packet.StopTriggeredEntityAnimPacket;
import software.bernie.geckolib.network.packet.StopTriggeredSingletonAnimPacket;
import software.bernie.geckolib.util.GeckoLibUtil;

public interface GeckoLibNetworking {
   static void init() {
      registerPacket(BlockEntityDataSyncPacket.TYPE, BlockEntityDataSyncPacket.CODEC, true);
      registerPacket(EntityDataSyncPacket.TYPE, EntityDataSyncPacket.CODEC, true);
      registerPacket(SingletonDataSyncPacket.TYPE, SingletonDataSyncPacket.CODEC, true);
      registerPacket(BlockEntityAnimTriggerPacket.TYPE, BlockEntityAnimTriggerPacket.CODEC, true);
      registerPacket(SingletonAnimTriggerPacket.TYPE, SingletonAnimTriggerPacket.CODEC, true);
      registerPacket(EntityAnimTriggerPacket.TYPE, EntityAnimTriggerPacket.CODEC, true);
      registerPacket(StopTriggeredBlockEntityAnimPacket.TYPE, StopTriggeredBlockEntityAnimPacket.CODEC, true);
      registerPacket(StopTriggeredEntityAnimPacket.TYPE, StopTriggeredEntityAnimPacket.CODEC, true);
      registerPacket(StopTriggeredSingletonAnimPacket.TYPE, StopTriggeredSingletonAnimPacket.CODEC, true);
      registerPacket(StatelessEntityPlayAnimPacket.TYPE, StatelessEntityPlayAnimPacket.CODEC, true);
      registerPacket(StatelessBlockEntityPlayAnimPacket.TYPE, StatelessBlockEntityPlayAnimPacket.CODEC, true);
      registerPacket(StatelessSingletonPlayAnimPacket.TYPE, StatelessSingletonPlayAnimPacket.CODEC, true);
      registerPacket(StatelessEntityStopAnimPacket.TYPE, StatelessEntityStopAnimPacket.CODEC, true);
      registerPacket(StatelessBlockEntityStopAnimPacket.TYPE, StatelessBlockEntityStopAnimPacket.CODEC, true);
      registerPacket(StatelessSingletonStopAnimPacket.TYPE, StatelessSingletonStopAnimPacket.CODEC, true);
   }

   @Internal
   private static <B extends FriendlyByteBuf, P extends MultiloaderPacket> void registerPacket(
      Type<P> payloadType, StreamCodec<B, P> codec, boolean isClientBound
   ) {
      GeckoLibServices.NETWORK.registerPacketInternal(payloadType, codec, isClientBound);
   }

   @Internal
   <B extends FriendlyByteBuf, P extends MultiloaderPacket> void registerPacketInternal(Type<P> var1, StreamCodec<B, P> var2, boolean var3);

   void sendToAllPlayersTrackingEntity(MultiloaderPacket var1, Entity var2);

   void sendToAllPlayersTrackingBlock(MultiloaderPacket var1, ServerLevel var2, BlockPos var3);

   void sendToPlayer(MultiloaderPacket var1, ServerPlayer var2);

   default <D> void syncBlockEntityAnimData(BlockPos pos, SerializableDataTicket<D> dataTicket, D data, ServerLevel level) {
      this.sendToAllPlayersTrackingBlock(new BlockEntityDataSyncPacket<>(pos, dataTicket, data), level, pos);
   }

   default <D> void syncEntityAnimData(Entity entity, boolean isReplacedEntity, SerializableDataTicket<D> dataTicket, D data) {
      this.sendToAllPlayersTrackingEntity(new EntityDataSyncPacket<>(entity.getId(), isReplacedEntity, dataTicket, data), entity);
   }

   @Deprecated(
      forRemoval = true
   )
   default <D> void syncSingletonAnimData(long instanceId, SerializableDataTicket<D> dataTicket, D data, Entity entityToTrack) {
   }

   @Deprecated(
      forRemoval = true
   )
   default <D> void syncSingletonAnimData(Class<?> animatableClass, long instanceId, SerializableDataTicket<D> dataTicket, D data, Entity entityToTrack) {
   }

   default <D> void syncSingletonAnimData(GeoAnimatable animatable, long instanceId, SerializableDataTicket<D> dataTicket, D data, Entity entityToTrack) {
      this.sendToAllPlayersTrackingEntity(
         new SingletonDataSyncPacket<>(GeckoLibUtil.getSyncedSingletonAnimatableId(animatable), instanceId, dataTicket, data), entityToTrack
      );
   }

   default void triggerBlockEntityAnim(BlockPos pos, @Nullable String controllerName, String animName, ServerLevel level) {
      this.sendToAllPlayersTrackingBlock(new BlockEntityAnimTriggerPacket(pos, controllerName == null ? "" : controllerName, animName), level, pos);
   }

   default void triggerEntityAnim(Entity entity, boolean isReplacedEntity, @Nullable String controllerName, String animName) {
      this.sendToAllPlayersTrackingEntity(
         new EntityAnimTriggerPacket(entity.getId(), isReplacedEntity, controllerName == null ? "" : controllerName, animName), entity
      );
   }

   @Deprecated(
      forRemoval = true
   )
   default void triggerSingletonAnim(String animatableClassName, Entity entityToTrack, long instanceId, @Nullable String controllerName, String animName) {
      this.sendToAllPlayersTrackingEntity(new SingletonAnimTriggerPacket(animatableClassName, instanceId, controllerName, animName), entityToTrack);
   }

   @Deprecated(
      forRemoval = true
   )
   default void triggerSingletonAnim(Class<?> animatableClass, Entity entityToTrack, long instanceId, @Nullable String controllerName, String animName) {
   }

   default void triggerSingletonAnim(GeoAnimatable animatable, Entity entityToTrack, long instanceId, @Nullable String controllerName, String animName) {
      this.triggerSingletonAnim(
         GeckoLibUtil.getSyncedSingletonAnimatableId(animatable), entityToTrack, instanceId, controllerName == null ? "" : controllerName, animName
      );
   }

   default void stopTriggeredBlockEntityAnim(BlockPos pos, ServerLevel level, @Nullable String controllerName, @Nullable String animName) {
      this.sendToAllPlayersTrackingBlock(
         new StopTriggeredBlockEntityAnimPacket(pos, controllerName == null ? "" : controllerName, animName == null ? "" : animName), level, pos
      );
   }

   default void stopTriggeredEntityAnim(Entity entity, boolean isReplacedEntity, @Nullable String controllerName, @Nullable String animName) {
      this.sendToAllPlayersTrackingEntity(
         new StopTriggeredEntityAnimPacket(entity.getId(), isReplacedEntity, controllerName == null ? "" : controllerName, animName == null ? "" : animName),
         entity
      );
   }

   @Deprecated(
      forRemoval = true
   )
   default void stopTriggeredSingletonAnim(
      Class<?> animatableClass, Entity entityToTrack, long instanceId, @Nullable String controllerName, @Nullable String animName
   ) {
      this.sendToAllPlayersTrackingEntity(
         new StopTriggeredSingletonAnimPacket(
            animatableClass.getName() + "0", instanceId, controllerName == null ? "" : controllerName, animName == null ? "" : animName
         ),
         entityToTrack
      );
   }

   default void stopTriggeredSingletonAnim(
      GeoAnimatable animatable, Entity entityToTrack, long instanceId, @Nullable String controllerName, @Nullable String animName
   ) {
      this.sendToAllPlayersTrackingEntity(
         new StopTriggeredSingletonAnimPacket(
            GeckoLibUtil.getSyncedSingletonAnimatableId(animatable), instanceId, controllerName == null ? "" : controllerName, animName == null ? "" : animName
         ),
         entityToTrack
      );
   }
}
