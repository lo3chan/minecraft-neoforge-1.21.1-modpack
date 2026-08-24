package net.diebuddies.mixins.vines;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.snow.SnowSearcher;
import net.diebuddies.physics.vines.VineHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({BlockRenderDispatcher.class})
public class MixinBlockRenderManager {
   @Inject(
      at = {@At("HEAD")},
      method = {"renderBatched(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;Lnet/neoforged/neoforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)V"},
      cancellable = true,
      remap = false,
      require = -1
   )
   public void renderBatched(
      BlockState state,
      BlockPos pos,
      BlockAndTintGetter world,
      PoseStack matrix,
      VertexConsumer vertexConsumer,
      boolean cull,
      RandomSource random,
      ModelData modelData,
      RenderType renderType,
      CallbackInfo info
   ) {
      if (ConfigClient.areDynamicBlockPhysicsEnabled() && VineHelper.getSetting(state) != null && VineHelper.isChunkInRange(pos)) {
         info.cancel();
      }

      if (ConfigClient.areSnowPhysicsEnabled() && SnowSearcher.getSnowProperty(state) != null) {
         info.cancel();
      }
   }
}
