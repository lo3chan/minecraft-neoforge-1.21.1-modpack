package net.diebuddies.mixins;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.math.Math;
import net.diebuddies.minecraft.ParticleSpawner;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Entity.class})
public class MixinEntity {
   @Inject(
      at = {@At("HEAD")},
      method = {"spawnSprintParticle"},
      cancellable = true
   )
   protected void spawnSprintParticle(CallbackInfo info) {
      Entity entity = (Entity)this;
      if (ConfigClient.sprintingPhysicsParticles && entity.level() instanceof ClientLevel) {
         Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
         if (camera.isInitialized()
            && camera.getPosition().distanceToSqr(entity.getX(), entity.getY(), entity.getZ())
               < ConfigClient.blockPhysicsRange * ConfigClient.blockPhysicsRange) {
            BlockPos blockPos = new BlockPos(Mth.floor(entity.getX()), Mth.floor(entity.getY() - 0.20000000298023224), Mth.floor(entity.getZ()));
            BlockState blockState = entity.level().getBlockState(blockPos);
            if (blockState.getRenderShape() != RenderShape.INVISIBLE) {
               try {
                  EntityDimensions dimensions = entity.getDimensions(entity.getPose());
                  ParticleSpawner.spawnSprintingPhysicsParticle(
                     blockState,
                     blockPos,
                     entity.level(),
                     entity.getX() + (Math.random() - 0.5) * dimensions.width(),
                     entity.getY() + 0.1,
                     entity.getZ() + (Math.random() - 0.5) * dimensions.width()
                  );
               } catch (Exception var7) {
               }
            }

            info.cancel();
         }
      }
   }
}
