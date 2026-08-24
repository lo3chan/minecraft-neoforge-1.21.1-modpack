package traben.entity_texture_features.mixin.mixins.mods.iris;

import java.util.Optional;
import net.irisshaders.iris.layer.InnerWrappedRenderType;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import traben.entity_texture_features.utils.ETFRenderLayerWithTexture;

@Pseudo
@Mixin({InnerWrappedRenderType.class})
public abstract class MixinInnerWrappedRenderType implements ETFRenderLayerWithTexture {
   @Shadow
   public abstract RenderType unwrap();

   @Override
   public Optional<ResourceLocation> etf$getId() {
      return this.unwrap() instanceof ETFRenderLayerWithTexture etf ? etf.etf$getId() : Optional.empty();
   }
}
