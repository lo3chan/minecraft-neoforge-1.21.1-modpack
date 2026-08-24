package net.diebuddies.mixins.vines;

import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderContext;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.snow.SnowSearcher;
import net.diebuddies.physics.vines.VineHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(
   targets = {"link.infra.indium.renderer.render.TerrainRenderContext"}
)
public class MixinIndiumTerrainRenderContext {
   @Inject(
      at = {@At("HEAD")},
      method = {"tessellateBlock"},
      remap = false,
      cancellable = true
   )
   public void tessellateBlock(BlockRenderContext ctx, CallbackInfo info) {
      if (ConfigClient.areDynamicBlockPhysicsEnabled() && VineHelper.getSetting(ctx.state()) != null && VineHelper.isChunkInRange(ctx.pos())) {
         info.cancel();
      }

      if (ConfigClient.areSnowPhysicsEnabled() && SnowSearcher.getSnowProperty(ctx.state()) != null) {
         info.cancel();
      }
   }
}
