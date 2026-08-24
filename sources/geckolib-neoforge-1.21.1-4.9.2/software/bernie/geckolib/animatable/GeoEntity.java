package software.bernie.geckolib.animatable;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import software.bernie.geckolib.GeckoLibServices;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.constant.dataticket.SerializableDataTicket;

public interface GeoEntity extends GeoAnimatable {
   @NonExtendable
   @Nullable
   default <D> D getAnimData(SerializableDataTicket<D> dataTicket) {
      return this.getAnimatableInstanceCache().getManagerForId(((Entity)this).getId()).getData(dataTicket);
   }

   @NonExtendable
   default <D> void setAnimData(SerializableDataTicket<D> dataTicket, D data) {
      Entity entity = (Entity)this;
      if (entity.level().isClientSide()) {
         this.getAnimatableInstanceCache().getManagerForId(entity.getId()).setData(dataTicket, data);
      } else {
         GeckoLibServices.NETWORK.syncEntityAnimData(entity, false, dataTicket, data);
      }
   }

   @NonExtendable
   default void triggerAnim(@Nullable String controllerName, String animName) {
      Entity entity = (Entity)this;
      if (entity.level().isClientSide()) {
         if (controllerName != null) {
            this.getAnimatableInstanceCache().getManagerForId(entity.getId()).tryTriggerAnimation(controllerName, animName);
         } else {
            this.getAnimatableInstanceCache().getManagerForId(entity.getId()).tryTriggerAnimation(animName);
         }
      } else {
         GeckoLibServices.NETWORK.triggerEntityAnim(entity, false, controllerName, animName);
      }
   }

   @NonExtendable
   default void stopTriggeredAnim(@Nullable String controllerName, @Nullable String animName) {
      Entity entity = (Entity)this;
      if (entity.level().isClientSide()) {
         AnimatableManager<GeoAnimatable> animatableManager = this.getAnimatableInstanceCache().getManagerForId(entity.getId());
         if (animatableManager == null) {
            return;
         }

         if (controllerName != null) {
            animatableManager.stopTriggeredAnimation(controllerName, animName);
         } else {
            animatableManager.stopTriggeredAnimation(animName);
         }
      } else {
         GeckoLibServices.NETWORK.stopTriggeredEntityAnim(entity, false, controllerName, animName);
      }
   }

   @Override
   default double getTick(Object entity) {
      return ((Entity)entity).tickCount;
   }
}
