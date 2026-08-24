package fuzs.eternalnether.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.eternalnether.EternalNether;
import fuzs.eternalnether.client.model.CorporModel;
import fuzs.eternalnether.world.entity.monster.Corpor;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class CorporRenderer extends HumanoidMobRenderer<Corpor, CorporModel> {
   private static final ResourceLocation TEXTURE_LOCATION = EternalNether.id("textures/entity/skeleton/corpor.png");

   public CorporRenderer(Context context) {
      super(context, new CorporModel(CorporModel.createBodyLayer().bakeRoot()), 0.5F);
   }

   public ResourceLocation getTextureLocation(Corpor corpor) {
      return TEXTURE_LOCATION;
   }

   protected void scale(Corpor corpor, PoseStack poseStack, float partialTicks) {
      poseStack.scale(1.2F, 1.2F, 1.2F);
   }
}
