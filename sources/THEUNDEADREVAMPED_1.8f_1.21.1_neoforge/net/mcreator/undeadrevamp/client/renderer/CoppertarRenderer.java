package net.mcreator.undeadrevamp.client.renderer;

import net.mcreator.undeadrevamp.client.model.Modeltar;
import net.mcreator.undeadrevamp.entity.CoppertarEntity;
import net.mcreator.undeadrevamp.procedures.CoppertarTransparentEntityModelConditionProcedure;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class CoppertarRenderer extends MobRenderer<CoppertarEntity, Modeltar<CoppertarEntity>> {
   public CoppertarRenderer(Context context) {
      super(context, new Modeltar(context.bakeLayer(Modeltar.LAYER_LOCATION)), 0.0F);
   }

   public ResourceLocation getTextureLocation(CoppertarEntity entity) {
      return ResourceLocation.parse("undead_revamp2:textures/entities/tars.png");
   }

   protected boolean isBodyVisible(CoppertarEntity entity) {
      Level world = entity.level();
      double x = entity.getX();
      double y = entity.getY();
      double z = entity.getZ();
      return !CoppertarTransparentEntityModelConditionProcedure.execute(entity);
   }
}
