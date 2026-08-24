package software.bernie.geckolib.animatable.stateless;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import software.bernie.geckolib.GeckoLibServices;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.network.packet.StatelessBlockEntityPlayAnimPacket;
import software.bernie.geckolib.network.packet.StatelessBlockEntityStopAnimPacket;

public non-sealed interface StatelessGeoBlockEntity extends StatelessAnimatable, GeoBlockEntity {
   @Override
   default void playAnimation(RawAnimation animation) {
      if (this instanceof BlockEntity self) {
         if (self.getLevel() instanceof ServerLevel level) {
            GeckoLibServices.NETWORK
               .sendToAllPlayersTrackingBlock(new StatelessBlockEntityPlayAnimPacket(self.getBlockPos(), animation), level, self.getBlockPos());
         } else {
            this.handleClientAnimationPlay(this, 0L, animation);
         }
      } else {
         throw new ClassCastException("Cannot use StatelessGeoBlockEntity on a non-blockentity animatable!");
      }
   }

   @Override
   default void stopAnimation(String animation) {
      if (this instanceof BlockEntity self) {
         if (self.getLevel() instanceof ServerLevel level) {
            GeckoLibServices.NETWORK
               .sendToAllPlayersTrackingBlock(new StatelessBlockEntityStopAnimPacket(self.getBlockPos(), animation), level, self.getBlockPos());
         } else {
            this.handleClientAnimationStop(this, 0L, animation);
         }
      } else {
         throw new ClassCastException("Cannot use StatelessGeoBlockEntity on a non-blockentity animatable!");
      }
   }
}
