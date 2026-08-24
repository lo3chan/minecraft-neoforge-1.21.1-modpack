package net.mcreator.undeadrevamp.block.renderer;

import net.mcreator.undeadrevamp.block.display.BasaltechesteDisplayItem;
import net.mcreator.undeadrevamp.block.model.BasaltechesteDisplayModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class BasaltechesteDisplayItemRenderer extends GeoItemRenderer<BasaltechesteDisplayItem> {
   public BasaltechesteDisplayItemRenderer() {
      super(new BasaltechesteDisplayModel());
   }

   public RenderType getRenderType(BasaltechesteDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
      return RenderType.entityTranslucent(this.getTextureLocation(animatable));
   }
}
