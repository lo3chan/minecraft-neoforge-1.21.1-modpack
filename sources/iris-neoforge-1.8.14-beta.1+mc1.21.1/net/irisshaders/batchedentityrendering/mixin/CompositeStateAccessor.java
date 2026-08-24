package net.irisshaders.batchedentityrendering.mixin;

import net.minecraft.client.renderer.RenderStateShard.DepthTestStateShard;
import net.minecraft.client.renderer.RenderStateShard.TransparencyStateShard;
import net.minecraft.client.renderer.RenderType.CompositeState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({CompositeState.class})
public interface CompositeStateAccessor {
   @Accessor("transparencyState")
   TransparencyStateShard getTransparency();

   @Accessor("depthTestState")
   DepthTestStateShard getDepth();
}
