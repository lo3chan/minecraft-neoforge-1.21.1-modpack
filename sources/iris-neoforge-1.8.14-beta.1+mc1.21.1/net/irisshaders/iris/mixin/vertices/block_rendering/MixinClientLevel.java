package net.irisshaders.iris.mixin.vertices.block_rendering;

import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin({ClientLevel.class})
public class MixinClientLevel {
   @ModifyVariable(
      method = {"getShade"},
      at = @At("HEAD"),
      argsOnly = true
   )
   private boolean iris$maybeDisableDirectionalShading(boolean shaded) {
      return WorldRenderingSettings.INSTANCE.shouldDisableDirectionalShading() ? false : shaded;
   }
}
