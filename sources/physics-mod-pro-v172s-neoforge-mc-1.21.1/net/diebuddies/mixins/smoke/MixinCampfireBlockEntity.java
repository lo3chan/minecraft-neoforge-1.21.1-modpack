package net.diebuddies.mixins.smoke;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.math.Math;
import net.diebuddies.physics.smoke.SmokeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({CampfireBlockEntity.class})
public class MixinCampfireBlockEntity {
   @Inject(
      at = {@At("HEAD")},
      method = {"particleTick"},
      cancellable = true
   )
   private static void particleTick(Level level, BlockPos blockPos, BlockState blockState, CampfireBlockEntity campfireBlockEntity, CallbackInfo info) {
      if (SmokeHelper.addParticle(
         level,
         blockPos.getX() + 0.5 + Math.random() * 0.5 - 0.25,
         blockPos.getY() + 0.75 + Math.random() * 0.6,
         blockPos.getZ() + 0.5 + Math.random() * 0.5 - 0.25,
         ConfigClient.smokeCampfire
      )) {
         info.cancel();
      }
   }
}
