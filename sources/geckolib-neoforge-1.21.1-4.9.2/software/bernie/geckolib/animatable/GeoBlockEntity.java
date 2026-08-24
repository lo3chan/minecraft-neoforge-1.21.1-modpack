package software.bernie.geckolib.animatable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.GeckoLibServices;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.constant.dataticket.SerializableDataTicket;
import software.bernie.geckolib.util.RenderUtil;

public interface GeoBlockEntity extends GeoAnimatable {
   @NonExtendable
   @Nullable
   default <D> D getAnimData(SerializableDataTicket<D> dataTicket) {
      return this.getAnimatableInstanceCache().getManagerForId(0L).getData(dataTicket);
   }

   @NonExtendable
   default <D> void setAnimData(SerializableDataTicket<D> dataTicket, D data) {
      BlockEntity blockEntity = (BlockEntity)this;
      Level level = blockEntity.getLevel();
      if (level == null) {
         GeckoLibConstants.LOGGER
            .error(
               "Attempting to set animation data for BlockEntity too early! Must wait until after the BlockEntity has been set in the world. ("
                  + blockEntity.getClass().toString()
                  + ")"
            );
      } else {
         if (level.isClientSide()) {
            this.getAnimatableInstanceCache().getManagerForId(0L).setData(dataTicket, data);
         } else {
            GeckoLibServices.NETWORK.syncBlockEntityAnimData(blockEntity.getBlockPos(), dataTicket, data, (ServerLevel)level);
         }
      }
   }

   @NonExtendable
   default void triggerAnim(@Nullable String controllerName, String animName) {
      BlockEntity blockEntity = (BlockEntity)this;
      Level level = blockEntity.getLevel();
      if (level == null) {
         GeckoLibConstants.LOGGER
            .error(
               "Attempting to trigger an animation for a BlockEntity too early! Must wait until after the BlockEntity has been set in the world. ("
                  + blockEntity.getClass().toString()
                  + ")"
            );
      } else {
         if (level.isClientSide()) {
            if (controllerName != null) {
               this.getAnimatableInstanceCache().getManagerForId(0L).tryTriggerAnimation(controllerName, animName);
            } else {
               this.getAnimatableInstanceCache().getManagerForId(0L).tryTriggerAnimation(animName);
            }
         } else {
            GeckoLibServices.NETWORK.triggerBlockEntityAnim(blockEntity.getBlockPos(), controllerName, animName, (ServerLevel)level);
         }
      }
   }

   @NonExtendable
   default void stopTriggeredAnim(@Nullable String controllerName, @Nullable String animName) {
      BlockEntity blockEntity = (BlockEntity)this;
      Level level = blockEntity.getLevel();
      if (level == null) {
         GeckoLibConstants.LOGGER
            .error(
               "Attempting to stop a triggered animation for a BlockEntity too early! Must wait until after the BlockEntity has been set in the world. ("
                  + blockEntity.getClass().toString()
                  + ")"
            );
      } else {
         if (level.isClientSide()) {
            AnimatableManager<GeoAnimatable> animatableManager = this.getAnimatableInstanceCache().getManagerForId(0L);
            if (controllerName != null) {
               animatableManager.stopTriggeredAnimation(controllerName, animName);
            } else {
               animatableManager.stopTriggeredAnimation(animName);
            }
         } else {
            GeckoLibServices.NETWORK.stopTriggeredBlockEntityAnim(blockEntity.getBlockPos(), (ServerLevel)level, controllerName, animName);
         }
      }
   }

   @Override
   default double getTick(Object blockEntity) {
      return RenderUtil.getCurrentTick();
   }
}
