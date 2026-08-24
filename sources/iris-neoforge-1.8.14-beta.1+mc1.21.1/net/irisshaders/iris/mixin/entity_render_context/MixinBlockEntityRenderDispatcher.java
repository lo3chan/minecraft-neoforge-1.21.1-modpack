package net.irisshaders.iris.mixin.entity_render_context;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.irisshaders.iris.layer.BlockEntityRenderStateShard;
import net.irisshaders.iris.layer.BufferSourceWrapper;
import net.irisshaders.iris.layer.OuterWrappedRenderType;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.irisshaders.iris.vertices.ImmediateState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({BlockEntityRenderDispatcher.class})
public class MixinBlockEntityRenderDispatcher {
   @Unique
   private static final String RUN_REPORTED = "Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderDispatcher;tryRender(Lnet/minecraft/world/level/block/entity/BlockEntity;Ljava/lang/Runnable;)V";

   @ModifyVariable(
      method = {"render"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/block/entity/BlockEntityType;isValid(Lnet/minecraft/world/level/block/state/BlockState;)Z"
      ),
      allow = 1,
      require = 1,
      argsOnly = true
   )
   private MultiBufferSource iris$wrapBufferSource(MultiBufferSource bufferSource, BlockEntity blockEntity) {
      BlockState state = blockEntity.getBlockState();
      Object2IntMap<BlockState> blockStateIds = WorldRenderingSettings.INSTANCE.getBlockStateIds();
      if (blockStateIds != null && ImmediateState.isRenderingLevel) {
         int intId = blockStateIds.getOrDefault(state, -1);
         CapturedRenderingState.INSTANCE.setCurrentBlockEntity(intId);
         return new BufferSourceWrapper(
            bufferSource, renderType -> OuterWrappedRenderType.wrapExactlyOnce("iris:block_entity", renderType, BlockEntityRenderStateShard.INSTANCE)
         );
      } else {
         return bufferSource;
      }
   }

   @Inject(
      method = {"render"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderDispatcher;tryRender(Lnet/minecraft/world/level/block/entity/BlockEntity;Ljava/lang/Runnable;)V",
         shift = Shift.AFTER
      )}
   )
   private void iris$afterRender(BlockEntity blockEntity, float tickDelta, PoseStack matrix, MultiBufferSource bufferSource, CallbackInfo ci) {
      CapturedRenderingState.INSTANCE.setCurrentBlockEntity(0);
   }
}
