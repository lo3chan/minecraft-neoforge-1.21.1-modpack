package traben.entity_texture_features.mixin.mixins;

import java.util.Optional;
import net.minecraft.client.renderer.RenderType.CompositeRenderType;
import net.minecraft.client.renderer.RenderType.CompositeState;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import traben.entity_texture_features.utils.ETFRenderLayerWithTexture;

@Pseudo
@Mixin({CompositeRenderType.class})
public abstract class MixinMultiPhase implements ETFRenderLayerWithTexture {
   @Shadow
   @Final
   public CompositeState state;

   @Override
   public Optional<ResourceLocation> etf$getId() {
      return this.state.textureState.cutoutTexture();
   }
}
