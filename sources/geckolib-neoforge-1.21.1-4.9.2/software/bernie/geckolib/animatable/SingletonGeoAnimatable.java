package software.bernie.geckolib.animatable;

import java.util.function.Consumer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import software.bernie.geckolib.GeckoLibServices;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.constant.dataticket.SerializableDataTicket;
import software.bernie.geckolib.util.GeckoLibUtil;

public interface SingletonGeoAnimatable extends GeoAnimatable {
   static void registerSyncedAnimatable(GeoAnimatable animatable) {
      GeckoLibUtil.registerSyncedAnimatable(animatable);
   }

   @NonExtendable
   @Nullable
   default <D> D getAnimData(long instanceId, SerializableDataTicket<D> dataTicket) {
      return this.getAnimatableInstanceCache().getManagerForId(instanceId).getData(dataTicket);
   }

   @NonExtendable
   default <D> void setAnimData(Entity relatedEntity, long instanceId, SerializableDataTicket<D> dataTicket, D data) {
      if (relatedEntity.level().isClientSide()) {
         this.getAnimatableInstanceCache().getManagerForId(instanceId).setData(dataTicket, data);
      } else {
         this.syncAnimData(instanceId, dataTicket, data, relatedEntity);
      }
   }

   @NonExtendable
   default <D> void syncAnimData(long instanceId, SerializableDataTicket<D> dataTicket, D data, Entity entityToTrack) {
      GeckoLibServices.NETWORK.syncSingletonAnimData(this, instanceId, dataTicket, data, entityToTrack);
   }

   @NonExtendable
   default <D> void triggerAnim(Entity relatedEntity, long instanceId, @Nullable String controllerName, String animName) {
      if (relatedEntity.level().isClientSide()) {
         if (controllerName != null) {
            this.getAnimatableInstanceCache().getManagerForId(instanceId).tryTriggerAnimation(controllerName, animName);
         } else {
            this.getAnimatableInstanceCache().getManagerForId(instanceId).tryTriggerAnimation(animName);
         }
      } else {
         GeckoLibServices.NETWORK.triggerSingletonAnim(this, relatedEntity, instanceId, controllerName, animName);
      }
   }

   @NonExtendable
   default void stopTriggeredAnim(Entity relatedEntity, long instanceId, @Nullable String controllerName, @Nullable String animName) {
      if (relatedEntity.level().isClientSide()) {
         AnimatableManager<GeoAnimatable> animatableManager = this.getAnimatableInstanceCache().getManagerForId(instanceId);
         if (animatableManager == null) {
            return;
         }

         if (controllerName != null) {
            animatableManager.stopTriggeredAnimation(controllerName, animName);
         } else {
            animatableManager.stopTriggeredAnimation(animName);
         }
      } else {
         GeckoLibServices.NETWORK.stopTriggeredSingletonAnim(this, relatedEntity, instanceId, controllerName, animName);
      }
   }

   @NonExtendable
   default void triggerArmorAnim(Entity relatedEntity, long instanceId, @Nullable String controllerName, String animName) {
      this.triggerAnim(relatedEntity, -instanceId, controllerName, animName);
   }

   @NonExtendable
   default void stopTriggeredArmorAnim(Entity relatedEntity, long instanceId, @Nullable String controllerName, @Nullable String animName) {
      this.stopTriggeredAnim(relatedEntity, -instanceId, controllerName, animName);
   }

   @Nullable
   @Override
   default AnimatableInstanceCache animatableCacheOverride() {
      return new SingletonAnimatableInstanceCache(this);
   }

   default void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
   }

   default Object getRenderProvider() {
      return this.getAnimatableInstanceCache().getRenderProvider();
   }
}
