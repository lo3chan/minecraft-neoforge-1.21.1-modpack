package net.irisshaders.iris.mixin.rendertype;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderStateShard.TransparencyStateShard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({RenderStateShard.class})
public interface RenderStateShardAccessor {
   @Accessor("TRANSLUCENT_TRANSPARENCY")
   static TransparencyStateShard getTranslucentTransparency() {
      throw new AssertionError();
   }

   @Accessor("name")
   String getName();
}
