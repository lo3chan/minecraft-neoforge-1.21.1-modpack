package net.diebuddies.mixins.iris;

import net.irisshaders.iris.pathways.HandRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

@Pseudo
@Mixin(
   value = {HandRenderer.class},
   remap = false
)
public interface MixinHandRendererAccessor {
   @Accessor("renderingSolid")
   void setRenderingSolid(boolean var1);
}
