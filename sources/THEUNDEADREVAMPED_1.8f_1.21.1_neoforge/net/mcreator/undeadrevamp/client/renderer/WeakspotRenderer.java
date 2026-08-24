package net.mcreator.undeadrevamp.client.renderer;

import net.mcreator.undeadrevamp.entity.WeakspotEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;

public class WeakspotRenderer extends HumanoidMobRenderer<WeakspotEntity, HumanoidModel<WeakspotEntity>> {
   public WeakspotRenderer(Context context) {
      super(context, new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER)), 0.0F);
      this.addLayer(
         new HumanoidArmorLayer(
            this,
            new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
            new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),
            context.getModelManager()
         )
      );
   }

   public ResourceLocation getTextureLocation(WeakspotEntity entity) {
      return ResourceLocation.parse("undead_revamp2:textures/entities/emptytexture.png");
   }
}
