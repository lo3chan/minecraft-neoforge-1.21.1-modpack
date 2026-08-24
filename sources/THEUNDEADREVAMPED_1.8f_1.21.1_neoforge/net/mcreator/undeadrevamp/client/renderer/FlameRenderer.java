package net.mcreator.undeadrevamp.client.renderer;

import net.mcreator.undeadrevamp.entity.FlameEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;

public class FlameRenderer extends HumanoidMobRenderer<FlameEntity, HumanoidModel<FlameEntity>> {
   public FlameRenderer(Context context) {
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

   public ResourceLocation getTextureLocation(FlameEntity entity) {
      return ResourceLocation.parse("undead_revamp2:textures/entities/emptytexture.png");
   }
}
