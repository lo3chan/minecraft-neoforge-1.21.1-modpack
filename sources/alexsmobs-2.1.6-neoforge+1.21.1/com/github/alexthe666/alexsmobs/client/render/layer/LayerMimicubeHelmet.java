package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.model.ModelMimicube;
import com.github.alexthe666.alexsmobs.client.render.AMRenderCompat;
import com.github.alexthe666.alexsmobs.client.render.RenderMimicube;
import com.github.alexthe666.alexsmobs.entity.EntityMimicube;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorMaterial.Layer;
import net.neoforged.neoforge.client.ClientHooks;

public class LayerMimicubeHelmet extends RenderLayer<EntityMimicube, ModelMimicube> {
   private static final Map<String, ResourceLocation> ARMOR_TEXTURE_RES_MAP = Maps.newHashMap();
   private final HumanoidModel defaultBipedModel;
   private final RenderMimicube renderer;

   public LayerMimicubeHelmet(RenderMimicube render, Context renderManagerIn) {
      super(render);
      this.renderer = render;
      this.defaultBipedModel = new HumanoidModel(renderManagerIn.bakeLayer(AMRenderCompat.armorStandArmorLayer(EquipmentSlot.HEAD)));
   }

   public static ResourceLocation getArmorResource(Entity entity, ItemStack stack, EquipmentSlot slot, @Nullable String type) {
      List<Layer> layers = ((ArmorMaterial)((ArmorItem)stack.getItem()).getMaterial().value()).layers();
      Layer layer = layers.get(type != null && layers.size() >= 2 ? 1 : 0);
      return ClientHooks.getArmorTexture(entity, stack, layer, false, slot);
   }

   public void render(
      PoseStack matrixStackIn,
      MultiBufferSource bufferIn,
      int packedLightIn,
      EntityMimicube cube,
      float limbSwing,
      float limbSwingAmount,
      float partialTicks,
      float ageInTicks,
      float netHeadYaw,
      float headPitch
   ) {
      matrixStackIn.pushPose();
      ItemStack itemstack = cube.getItemBySlot(EquipmentSlot.HEAD);
      float helmetSwap = Mth.lerp(partialTicks, cube.prevHelmetSwapProgress, cube.helmetSwapProgress) * 0.2F;
      if (AMCompat.isArmor(itemstack)) {
         ArmorItem armoritem = (ArmorItem)itemstack.getItem();
         if (AMCompat.equipmentSlotFor(itemstack) == EquipmentSlot.HEAD) {
            HumanoidModel a = this.defaultBipedModel;
            a = this.getArmorModelHook(cube, itemstack, EquipmentSlot.HEAD, a);
            boolean notAVanillaModel = a != this.defaultBipedModel;
            this.setModelSlotVisible(a, EquipmentSlot.HEAD);
            boolean flag = false;
            ((ModelMimicube)this.renderer.getModel()).root.translateAndRotate(matrixStackIn);
            ((ModelMimicube)this.renderer.getModel()).innerbody.translateAndRotate(matrixStackIn);
            matrixStackIn.translate(0.0F, notAVanillaModel ? 0.25F : -0.75F, 0.0F);
            matrixStackIn.scale(1.0F + 0.3F * (1.0F - helmetSwap), 1.0F + 0.3F * (1.0F - helmetSwap), 1.0F + 0.3F * (1.0F - helmetSwap));
            boolean flag1 = itemstack.hasFoil();
            int clampedLight = helmetSwap > 0.0F ? (int)(-100.0F * helmetSwap) : packedLightIn;
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(360.0F * helmetSwap));
            if (AMCompat.hasCustomColor(itemstack)) {
               int i = AMCompat.getDyedColor(itemstack, 16777215);
               float f = (i >> 16 & 0xFF) / 255.0F;
               float f1 = (i >> 8 & 0xFF) / 255.0F;
               float f2 = (i & 0xFF) / 255.0F;
               this.renderArmor(
                  cube,
                  matrixStackIn,
                  bufferIn,
                  clampedLight,
                  flag1,
                  a,
                  f,
                  f1,
                  f2,
                  getArmorResource(cube, itemstack, EquipmentSlot.HEAD, null),
                  notAVanillaModel
               );
               this.renderArmor(
                  cube,
                  matrixStackIn,
                  bufferIn,
                  clampedLight,
                  flag1,
                  a,
                  1.0F,
                  1.0F,
                  1.0F,
                  getArmorResource(cube, itemstack, EquipmentSlot.HEAD, "overlay"),
                  notAVanillaModel
               );
            } else {
               this.renderArmor(
                  cube,
                  matrixStackIn,
                  bufferIn,
                  clampedLight,
                  flag1,
                  a,
                  1.0F,
                  1.0F,
                  1.0F,
                  getArmorResource(cube, itemstack, EquipmentSlot.HEAD, null),
                  notAVanillaModel
               );
            }
         }
      }

      matrixStackIn.popPose();
   }

   private void renderArmor(
      EntityMimicube entity,
      PoseStack matrixStackIn,
      MultiBufferSource bufferIn,
      int packedLightIn,
      boolean glintIn,
      HumanoidModel modelIn,
      float red,
      float green,
      float blue,
      ResourceLocation armorResource,
      boolean notAVanillaModel
   ) {
      VertexConsumer ivertexbuilder = ItemRenderer.getFoilBuffer(bufferIn, RenderType.entityCutoutNoCull(armorResource), false, glintIn);
      if (notAVanillaModel) {
         ((ModelMimicube)this.renderer.getModel()).copyPropertiesTo(modelIn);
         modelIn.body.y = 0.0F;
         modelIn.head.setPos(0.0F, 1.0F, 0.0F);
         modelIn.hat.y = 0.0F;
         modelIn.head.xRot = ((ModelMimicube)this.renderer.getModel()).body.rotateAngleX;
         modelIn.head.yRot = ((ModelMimicube)this.renderer.getModel()).body.rotateAngleY;
         modelIn.head.zRot = ((ModelMimicube)this.renderer.getModel()).body.rotateAngleZ;
         modelIn.head.x = ((ModelMimicube)this.renderer.getModel()).body.rotationPointX;
         modelIn.head.y = ((ModelMimicube)this.renderer.getModel()).body.rotationPointY;
         modelIn.head.z = ((ModelMimicube)this.renderer.getModel()).body.rotationPointZ;
         modelIn.hat.copyFrom(modelIn.head);
         modelIn.body.copyFrom(modelIn.head);
      }

      AMRenderCompat.renderToBuffer(modelIn, matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);
   }

   protected void setModelSlotVisible(HumanoidModel p_188359_1_, EquipmentSlot slotIn) {
      this.setModelVisible(p_188359_1_);
      switch (slotIn) {
         case HEAD:
            p_188359_1_.head.visible = true;
            p_188359_1_.hat.visible = true;
            break;
         case CHEST:
            p_188359_1_.body.visible = true;
            p_188359_1_.rightArm.visible = true;
            p_188359_1_.leftArm.visible = true;
            break;
         case LEGS:
            p_188359_1_.body.visible = true;
            p_188359_1_.rightLeg.visible = true;
            p_188359_1_.leftLeg.visible = true;
            break;
         case FEET:
            p_188359_1_.rightLeg.visible = true;
            p_188359_1_.leftLeg.visible = true;
      }
   }

   protected void setModelVisible(HumanoidModel model) {
      model.setAllVisible(false);
   }

   protected HumanoidModel<?> getArmorModelHook(LivingEntity entity, ItemStack itemStack, EquipmentSlot slot, HumanoidModel model) {
      try {
         Model basicModel = ClientHooks.getArmorModel(entity, itemStack, slot, model);
         return basicModel instanceof HumanoidModel ? (HumanoidModel)basicModel : model;
      } catch (Exception var6) {
         return model;
      }
   }
}
