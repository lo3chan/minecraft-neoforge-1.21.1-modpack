package traben.entity_model_features.mixin.mixins.rendering.feature;

import net.minecraft.client.renderer.entity.layers.EyesLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin({EyesLayer.class})
public class MixinEyesFeatureRenderer {
   @ModifyVariable(
      method = {"render"},
      at = @At("HEAD"),
      argsOnly = true,
      ordinal = 0
   )
   private int emf$markEyeLight(int i) {
      return 15728881;
   }
}
