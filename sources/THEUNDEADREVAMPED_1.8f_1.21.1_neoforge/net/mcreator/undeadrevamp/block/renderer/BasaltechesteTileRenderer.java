package net.mcreator.undeadrevamp.block.renderer;

import net.mcreator.undeadrevamp.block.entity.BasaltechesteTileEntity;
import net.mcreator.undeadrevamp.block.model.BasaltechesteBlockModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class BasaltechesteTileRenderer extends GeoBlockRenderer<BasaltechesteTileEntity> {
   public BasaltechesteTileRenderer() {
      super(new BasaltechesteBlockModel());
   }

   public RenderType getRenderType(BasaltechesteTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
      return RenderType.entityTranslucent(this.getTextureLocation(animatable));
   }
}
