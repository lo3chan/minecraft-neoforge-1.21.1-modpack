package traben.entity_model_features.mixin.mixins.rendering;

import net.minecraft.client.renderer.entity.EntityRenderers;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({EntityRenderers.class})
public class MixinEntityRenderers {
   private static final String method = "method_32174";
}
