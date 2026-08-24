package software.bernie.geckolib.animatable.stateless;

import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.GeckoLibServices;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.network.packet.StatelessEntityPlayAnimPacket;
import software.bernie.geckolib.network.packet.StatelessEntityStopAnimPacket;

public non-sealed interface StatelessGeoEntity extends StatelessAnimatable, GeoEntity {
   @Override
   default void playAnimation(RawAnimation animation) {
      if (this instanceof Entity self) {
         if (self.level().isClientSide) {
            this.handleClientAnimationPlay(this, self.getId(), animation);
         } else {
            GeckoLibServices.NETWORK.sendToAllPlayersTrackingEntity(new StatelessEntityPlayAnimPacket(self.getId(), false, animation), self);
         }
      } else {
         throw new ClassCastException("Cannot use StatelessGeoEntity on a non-entity animatable!");
      }
   }

   @Override
   default void stopAnimation(String animation) {
      if (this instanceof Entity self) {
         if (self.level().isClientSide) {
            this.handleClientAnimationStop(this, self.getId(), animation);
         } else {
            GeckoLibServices.NETWORK.sendToAllPlayersTrackingEntity(new StatelessEntityStopAnimPacket(self.getId(), false, animation), self);
         }
      } else {
         throw new ClassCastException("Cannot use StatelessGeoEntity on a non-entity animatable!");
      }
   }
}
