package net.diebuddies.mixins.iris;

import com.mojang.blaze3d.shaders.Uniform;
import net.irisshaders.iris.pipeline.programs.ExtendedShader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

@Pseudo
@Mixin(
   value = {ExtendedShader.class},
   remap = false
)
public interface MixinExtendedShaderAccessor {
   @Accessor("modelViewInverse")
   Uniform getModelViewInverse();

   @Accessor("normalMatrix")
   Uniform getNormalMatrix();
}
