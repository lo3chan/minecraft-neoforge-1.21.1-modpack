package net.mcreator.borninchaosv.entity.model;

import net.mcreator.borninchaosv.entity.ThornshellCrabEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ThornshellCrabModel extends GeoModel<ThornshellCrabEntity> {
   public ResourceLocation getAnimationResource(ThornshellCrabEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:animations/thornshell.animation.json");
   }

   public ResourceLocation getModelResource(ThornshellCrabEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:geo/thornshell.geo.json");
   }

   public ResourceLocation getTextureResource(ThornshellCrabEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:textures/entities/" + entity.getTexture() + ".png");
   }
}
