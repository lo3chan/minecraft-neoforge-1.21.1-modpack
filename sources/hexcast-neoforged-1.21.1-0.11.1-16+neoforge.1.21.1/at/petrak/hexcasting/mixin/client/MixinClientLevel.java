package at.petrak.hexcasting.mixin.client;

import at.petrak.hexcasting.common.particles.ConjureParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({ClientLevel.class})
public abstract class MixinClientLevel {
   @Inject(
      method = {"doAnimateTick"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/block/Block;animateTick(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)V"
      )},
      locals = LocalCapture.CAPTURE_FAILSOFT
   )
   public void addBuddingAmethystParticles(
      int $$0, int $$1, int $$2, int $$3, RandomSource rand, Block $$5, MutableBlockPos pos, CallbackInfo ci, int trueX, int trueY, int trueZ, BlockState state
   ) {
      ClientLevel self = (ClientLevel)this;
      if (state.is(Blocks.BUDDING_AMETHYST)) {
         ParticleOptions options = new ConjureParticleOptions(8991416);
         Vec3 center = Vec3.atCenterOf(pos);

         for (Direction direction : Direction.values()) {
            int dX = direction.getStepX();
            int dY = direction.getStepY();
            int dZ = direction.getStepZ();
            int count = rand.nextInt(10) / 5;

            for (int i = 0; i < count; i++) {
               double pX = center.x + (dX == 0 ? Mth.nextDouble(rand, -0.5, 0.5) : dX * 0.55);
               double pY = center.y + (dY == 0 ? Mth.nextDouble(rand, -0.5, 0.5) : dY * 0.55);
               double pZ = center.z + (dZ == 0 ? Mth.nextDouble(rand, -0.5, 0.5) : dZ * 0.55);
               self.addParticle(options, pX, pY, pZ, 0.0, 0.0, 0.0);
            }
         }
      }
   }
}
