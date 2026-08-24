package net.raphimc.immediatelyfast.injection.mixins.core;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import java.util.SequencedMap;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.raphimc.immediatelyfast.ImmediatelyFast;
import net.raphimc.immediatelyfast.feature.core.BatchableBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin({MultiBufferSource.class})
public interface MixinVertexConsumerProvider {
   @Overwrite
   static BufferSource immediateWithBuffers(SequencedMap<RenderType, ByteBufferBuilder> layerBuffers, ByteBufferBuilder fallbackBuffer) {
      return (BufferSource)(ImmediatelyFast.config.debug_only_and_not_recommended_disable_universal_batching
         ? new BufferSource(fallbackBuffer, layerBuffers)
         : new BatchableBufferSource(fallbackBuffer, layerBuffers));
   }
}
