package net.irisshaders.batchedentityrendering.mixin;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderStateShard.TransparencyStateShard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({RenderStateShard.class})
public interface RenderStateShardAccessor {
   @Accessor("NO_TRANSPARENCY")
   static TransparencyStateShard getNO_TRANSPARENCY() {
      throw new AssertionError();
   }

   @Accessor("GLINT_TRANSPARENCY")
   static TransparencyStateShard getGLINT_TRANSPARENCY() {
      throw new AssertionError();
   }

   @Accessor("CRUMBLING_TRANSPARENCY")
   static TransparencyStateShard getCRUMBLING_TRANSPARENCY() {
      throw new AssertionError();
   }
}
