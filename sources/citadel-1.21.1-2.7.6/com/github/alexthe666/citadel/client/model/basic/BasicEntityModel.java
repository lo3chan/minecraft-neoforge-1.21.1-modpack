package com.github.alexthe666.citadel.client.model.basic;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Function;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public abstract class BasicEntityModel<T extends Entity> extends EntityModel<T> {
   public int textureWidth = 64;
   public int textureHeight = 32;

   protected BasicEntityModel() {
      this(RenderType::entityCutoutNoCull);
   }

   protected BasicEntityModel(Function<ResourceLocation, RenderType> p_102613_) {
      super(p_102613_);
   }

   public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLightIn, int packedOverlayIn, int color) {
      this.parts().forEach(part -> part.render(poseStack, vertexConsumer, packedLightIn, packedOverlayIn, color));
   }

   public abstract Iterable<BasicModelPart> parts();

   public abstract void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6);

   public void prepareMobModel(T p_102614_, float p_102615_, float p_102616_, float p_102617_) {
   }
}
