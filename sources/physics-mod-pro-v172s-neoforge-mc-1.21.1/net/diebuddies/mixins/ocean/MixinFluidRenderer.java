package net.diebuddies.mixins.ocean;

import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.DefaultFluidRenderer;
import net.diebuddies.config.ConfigClient;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({DefaultFluidRenderer.class})
public class MixinFluidRenderer {
   @Unique
   private final MutableBlockPos tmpPos = new MutableBlockPos();

   @Inject(
      at = {@At("HEAD")},
      method = {"isSideExposed"},
      remap = false,
      cancellable = true
   )
   public void isSideExposed(BlockAndTintGetter world, int x, int y, int z, Direction dir, float height, CallbackInfoReturnable<Boolean> info) {
      if (ConfigClient.areOceanPhysicsEnabled() && dir == Direction.UP) {
         BlockState state = world.getBlockState(this.tmpPos.set(x, y, z));
         FluidState fluidState = state.getFluidState();
         if (fluidState.is(FluidTags.WATER) && !state.blocksMotion()) {
            Vec3 flow = state.getFluidState().getFlow(world, this.tmpPos);
            if (flow.x == 0.0 && flow.z == 0.0) {
               info.setReturnValue(false);
            }
         }
      }
   }
}
