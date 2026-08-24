package com.aetherteam.aether.client.renderer.entity;

import com.aetherteam.aether.client.renderer.AetherModelLayers;
import com.aetherteam.aether.client.renderer.entity.model.AerwhaleModel;
import com.aetherteam.aether.client.renderer.entity.model.ClassicAerwhaleModel;
import com.aetherteam.aether.entity.passive.Aerwhale;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class AerwhaleRenderer extends MultiModelRenderer<Aerwhale, EntityModel<Aerwhale>, AerwhaleModel, ClassicAerwhaleModel> {
   private static final ResourceLocation AERWHALE_TEXTURE = ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/aerwhale/aerwhale.png");
   private static final ResourceLocation AERWHALE_CLASSIC_TEXTURE = ResourceLocation.fromNamespaceAndPath(
      "aether", "textures/entity/mobs/aerwhale/aerwhale_classic.png"
   );
   private final AerwhaleModel defaultModel;
   private final ClassicAerwhaleModel oldModel;

   public AerwhaleRenderer(Context context) {
      super(context, new AerwhaleModel(context.bakeLayer(AetherModelLayers.AERWHALE)), 0.5F);
      this.defaultModel = new AerwhaleModel(context.bakeLayer(AetherModelLayers.AERWHALE));
      this.oldModel = new ClassicAerwhaleModel(context.bakeLayer(AetherModelLayers.AERWHALE_CLASSIC));
   }

   protected void scale(Aerwhale aerwhale, PoseStack poseStack, float partialTickTime) {
      poseStack.translate(0.0, -0.5, 0.0);
      poseStack.scale(2.0F, 2.0F, 2.0F);
   }

   protected void setupRotations(Aerwhale aerwhale, PoseStack poseStack, float bob, float yBodyRot, float partialTick, float scale) {
      super.setupRotations(aerwhale, poseStack, bob, yBodyRot, partialTick, scale);
      poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTick, aerwhale.getXRotOData(), aerwhale.getXRotData())));
   }

   public AerwhaleModel getDefaultModel() {
      return this.defaultModel;
   }

   public ClassicAerwhaleModel getOldModel() {
      return this.oldModel;
   }

   @Override
   public ResourceLocation getDefaultTexture() {
      return AERWHALE_TEXTURE;
   }

   @Override
   public ResourceLocation getOldTexture() {
      return AERWHALE_CLASSIC_TEXTURE;
   }
}
