package net.irisshaders.batchedentityrendering.mixin;

import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({OutlineBufferSource.class})
public interface OutlineBufferSourceAccessor {
   @Accessor
   BufferSource getOutlineBufferSource();
}
