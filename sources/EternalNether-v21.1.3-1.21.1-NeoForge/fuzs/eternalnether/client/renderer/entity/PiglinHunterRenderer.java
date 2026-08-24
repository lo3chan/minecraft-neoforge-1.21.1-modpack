package fuzs.eternalnether.client.renderer.entity;

import fuzs.eternalnether.EternalNether;
import fuzs.eternalnether.client.model.PiglinHunterModel;
import fuzs.eternalnether.world.entity.monster.piglin.PiglinHunter;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;

public class PiglinHunterRenderer extends HumanoidMobRenderer<PiglinHunter, PiglinHunterModel> {
   private static final ResourceLocation TEXTURE_LOCATION = EternalNether.id("textures/entity/piglin/piglin_hunter.png");

   public PiglinHunterRenderer(Context context) {
      super(context, new PiglinHunterModel(PiglinHunterModel.createBodyLayer().bakeRoot()), 0.5F, 1.0019531F, 1.0F, 1.0019531F);
      this.addLayer(
         new HumanoidArmorLayer(
            this,
            new HumanoidArmorModel(context.bakeLayer(ModelLayers.PIGLIN_INNER_ARMOR)),
            new HumanoidArmorModel(context.bakeLayer(ModelLayers.PIGLIN_OUTER_ARMOR)),
            context.getModelManager()
         )
      );
   }

   public ResourceLocation getTextureLocation(PiglinHunter piglinHunter) {
      return TEXTURE_LOCATION;
   }

   protected boolean isShaking(PiglinHunter piglinHunter) {
      return super.isShaking(piglinHunter) || piglinHunter.isConverting();
   }
}
