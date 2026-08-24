package software.bernie.geckolib.animatable;

import java.util.function.Consumer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import software.bernie.geckolib.GeckoLibServices;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.constant.dataticket.SerializableDataTicket;

public interface GeoReplacedEntity extends SingletonGeoAnimatable {
   EntityType<?> getReplacingEntityType();

   @NonExtendable
   @Nullable
   default <D> D getAnimData(Entity entity, SerializableDataTicket<D> dataTicket) {
      return this.getAnimatableInstanceCache().getManagerForId(entity.getId()).getData(dataTicket);
   }

   @NonExtendable
   default <D> void setAnimData(Entity relatedEntity, SerializableDataTicket<D> dataTicket, D data) {
      if (relatedEntity.level().isClientSide()) {
         this.getAnimatableInstanceCache().getManagerForId(relatedEntity.getId()).setData(dataTicket, data);
      } else {
         GeckoLibServices.NETWORK.syncEntityAnimData(relatedEntity, true, dataTicket, data);
      }
   }

   @NonExtendable
   default void triggerAnim(Entity relatedEntity, @Nullable String controllerName, String animName) {
      if (relatedEntity.level().isClientSide()) {
         if (controllerName != null) {
            this.getAnimatableInstanceCache().getManagerForId(relatedEntity.getId()).tryTriggerAnimation(controllerName, animName);
         } else {
            this.getAnimatableInstanceCache().getManagerForId(relatedEntity.getId()).tryTriggerAnimation(animName);
         }
      } else {
         GeckoLibServices.NETWORK.triggerEntityAnim(relatedEntity, true, controllerName, animName);
      }
   }

   @NonExtendable
   default void stopTriggeredAnim(Entity relatedEntity, @Nullable String controllerName, @Nullable String animName) {
      if (relatedEntity.level().isClientSide()) {
         AnimatableManager<GeoAnimatable> animatableManager = this.getAnimatableInstanceCache().getManagerForId(relatedEntity.getId());
         if (animatableManager == null) {
            return;
         }

         if (controllerName != null) {
            animatableManager.stopTriggeredAnimation(controllerName, animName);
         } else {
            animatableManager.stopTriggeredAnimation(animName);
         }
      } else {
         GeckoLibServices.NETWORK.stopTriggeredEntityAnim(relatedEntity, true, controllerName, animName);
      }
   }

   @Override
   default double getTick(Object entity) {
      return ((Entity)entity).tickCount;
   }

   @NonExtendable
   @Override
   default void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
   }

   @NonExtendable
   @Override
   default Object getRenderProvider() {
      return null;
   }
}
