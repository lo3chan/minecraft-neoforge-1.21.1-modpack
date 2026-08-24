package net.mcreator.undeadrevamp.client.renderer;

import net.mcreator.undeadrevamp.entity.NeocrorinesEntity;
import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class NeocrorinesRenderer extends MobRenderer<NeocrorinesEntity, ChickenModel<NeocrorinesEntity>> {
   public NeocrorinesRenderer(Context context) {
      super(context, new ChickenModel(context.bakeLayer(ModelLayers.CHICKEN)), 0.5F);
   }

   public ResourceLocation getTextureLocation(NeocrorinesEntity entity) {
      return ResourceLocation.parse("undead_revamp2:textures/entities/emptytexture.png");
   }
}
