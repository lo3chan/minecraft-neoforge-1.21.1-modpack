package software.bernie.geckolib.animatable.stateless;

import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.GeckoLibServices;
import software.bernie.geckolib.animatable.GeoReplacedEntity;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.network.packet.StatelessEntityPlayAnimPacket;
import software.bernie.geckolib.network.packet.StatelessEntityStopAnimPacket;

public interface StatelessGeoReplacedEntity extends StatelessGeoSingletonAnimatable, GeoReplacedEntity {
   default void playAnimation(String animation, Entity relatedEntity) {
      this.playAnimation(animation, relatedEntity, relatedEntity.getId());
   }

   default void playLoopingAnimation(String animation, Entity relatedEntity) {
      this.playLoopingAnimation(animation, relatedEntity, relatedEntity.getId());
   }

   default void playAndHoldAnimation(String animation, Entity relatedEntity) {
      this.playAndHoldAnimation(animation, relatedEntity, relatedEntity.getId());
   }

   default void stopAnimation(RawAnimation animation, Entity relatedEntity) {
      this.stopAnimation(animation, relatedEntity, relatedEntity.getId());
   }

   default void playAnimation(RawAnimation animation, Entity relatedEntity) {
      this.playAnimation(animation, relatedEntity, relatedEntity.getId());
   }

   default void stopAnimation(String animation, Entity relatedEntity) {
      this.stopAnimation(animation, relatedEntity, relatedEntity.getId());
   }

   @Override
   default void playAnimation(RawAnimation animation, Entity relatedEntity, long instanceId) {
      if (relatedEntity.level().isClientSide) {
         this.handleClientAnimationPlay(this, instanceId, animation);
      } else {
         GeckoLibServices.NETWORK.sendToAllPlayersTrackingEntity(new StatelessEntityPlayAnimPacket((int)instanceId, true, animation), relatedEntity);
      }
   }

   @Override
   default void stopAnimation(String animation, Entity relatedEntity, long instanceId) {
      if (relatedEntity.level().isClientSide) {
         this.handleClientAnimationStop(this, instanceId, animation);
      } else {
         GeckoLibServices.NETWORK.sendToAllPlayersTrackingEntity(new StatelessEntityStopAnimPacket((int)instanceId, true, animation), relatedEntity);
      }
   }
}
