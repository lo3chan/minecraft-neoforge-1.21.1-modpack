package net.joefoxe.hexerei.client.renderer.entity.render;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import java.util.HashMap;
import java.util.Map;
import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.joefoxe.hexerei.client.renderer.entity.model.CrowModel;
import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.custom.BroomItem;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.EnergySwirlLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorMaterial.Layer;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.joml.Matrix4f;

public class CrowRenderer extends MobRenderer<CrowEntity, CrowModel<CrowEntity>> {
   public static final ResourceLocation TEXTURE = HexereiUtil.getResource("textures/entity/crow.png");
   private static final ResourceLocation CROW_COLLAR_LOCATION = HexereiUtil.getResource("textures/entity/crow_collar.png");
   private static final ResourceLocation POWER_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/creeper/creeper_armor.png");
   private final HumanoidModel defaultBipedModel;
   public static Map<Item, ResourceLocation> TRINKET_LOCATION = (Map<Item, ResourceLocation>)Util.make(() -> {
      Map<Item, ResourceLocation> map = new HashMap<>();
      map.put((Item)ModItems.CROW_ANKH_AMULET.get(), ResourceLocation.tryParse("hexerei:textures/item/crow_ankh_amulet_trinket.png"));
      return map;
   });
   private static final Map<CrowVariant, ResourceLocation> LOCATION_BY_VARIANT = (Map<CrowVariant, ResourceLocation>)Util.make(
      Maps.newEnumMap(CrowVariant.class), p_114874_ -> {
         p_114874_.put(CrowVariant.BLACK, HexereiUtil.getResource("textures/entity/crow.png"));
         p_114874_.put(CrowVariant.HOODED, HexereiUtil.getResource("textures/entity/crow_hooded.png"));
         p_114874_.put(CrowVariant.NORTHWESTERN, HexereiUtil.getResource("textures/entity/crow_northwestern.png"));
         p_114874_.put(CrowVariant.PIED, HexereiUtil.getResource("textures/entity/crow_pied.png"));
         p_114874_.put(CrowVariant.ALBINO, HexereiUtil.getResource("textures/entity/crow_albino.png"));
         p_114874_.put(CrowVariant.GRAY, HexereiUtil.getResource("textures/entity/crow_gray.png"));
         p_114874_.put(CrowVariant.DARKBROWN, HexereiUtil.getResource("textures/entity/crow.png"));
      }
   );
   private final Pair<ResourceLocation, CrowModel<CrowEntity>> crowResources;

   public CrowRenderer(Context erm) {
      super(erm, new CrowModel(erm.bakeLayer(CrowModel.LAYER_LOCATION)), 0.25F);
      this.crowResources = Pair.of(TEXTURE, new CrowModel(erm.bakeLayer(CrowModel.LAYER_LOCATION)));
      this.defaultBipedModel = new HumanoidModel(erm.bakeLayer(ModelLayers.ARMOR_STAND_OUTER_ARMOR));
      this.addLayer(new CrowRenderer.LayerCrowItem(this));
      this.addLayer(new CrowRenderer.LayerCrowCollar(this));
      this.addLayer(new CrowRenderer.LayerCrowMisc(this));
      this.addLayer(new CrowRenderer.LayerCrowHelmet(this, erm));
      this.addLayer(new CrowRenderer.CrowPowerLayer(this, erm.getModelSet()));
   }

   public ResourceLocation getTextureLocation(CrowEntity pEntity) {
      return LOCATION_BY_VARIANT.get(pEntity.getVariant());
   }

   public void render(CrowEntity crowEntity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
      poseStack.pushPose();
      super.render(crowEntity, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
      poseStack.popPose();
   }

   protected void scale(CrowEntity entitylivingbaseIn, PoseStack poseStack, float partialTickTime) {
      float f = 1.0F;
      if (entitylivingbaseIn.isBaby()) {
         f *= 0.5F;
         this.shadowRadius = 0.125F;
      } else {
         this.shadowRadius = 0.25F;
      }

      poseStack.scale(f, f, f);
   }

   public class CrowPowerLayer extends EnergySwirlLayer<CrowEntity, CrowModel<CrowEntity>> {
      private final CrowModel<CrowEntity> model;

      public CrowPowerLayer(RenderLayerParent<CrowEntity, CrowModel<CrowEntity>> p_174471_, EntityModelSet p_174472_) {
         super(p_174471_);
         this.model = new CrowModel<>(p_174472_.bakeLayer(CrowModel.POWER_LAYER_LOCATION));
      }

      protected float xOffset(float p_116683_) {
         return p_116683_ * 0.01F;
      }

      protected ResourceLocation getTextureLocation() {
         return CrowRenderer.POWER_LOCATION;
      }

      protected EntityModel<CrowEntity> model() {
         return this.model;
      }
   }

   public class LayerCrowCollar extends RenderLayer<CrowEntity, CrowModel<CrowEntity>> {
      private static final ResourceLocation CROW_COLLAR_LOCATION = HexereiUtil.getResource("textures/entity/crow_collar.png");

      public LayerCrowCollar(RenderLayerParent<CrowEntity, CrowModel<CrowEntity>> p_117707_) {
         super(p_117707_);
      }

      public void render(
         PoseStack poseStack,
         MultiBufferSource bufferIn,
         int packedLightIn,
         CrowEntity entity,
         float p_117724_,
         float p_117725_,
         float p_117726_,
         float p_117727_,
         float p_117728_,
         float p_117729_
      ) {
         if (entity.isTame()
            && !entity.isInvisible()
            && (entity.getDyeColorId() != -1 || entity.getName().getString().equals("jeb_") || entity.getName().getString().equals("joe_"))) {
            float[] afloat = HexereiUtil.rgbIntToFloatArray(entity.getDyeColor().getTextureDiffuseColor());
            VertexConsumer vertexConsumer = ItemRenderer.getFoilBuffer(bufferIn, RenderType.entityCutoutNoCull(CROW_COLLAR_LOCATION), false, false);
            ((CrowModel)this.getParentModel())
               .renderToBuffer(
                  poseStack, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, HexereiUtil.getColorValueAlpha(afloat[0], afloat[1], afloat[2], 1.0F)
               );
         }
      }
   }

   public class LayerCrowHelmet extends RenderLayer<CrowEntity, CrowModel<CrowEntity>> {
      private final RenderLayerParent<CrowEntity, CrowModel<CrowEntity>> renderer;
      private final HumanoidModel<?> defaultBipedModel;
      private final TextureAtlas armorTrimAtlas;

      public LayerCrowHelmet(CrowRenderer renderer, Context renderManagerIn) {
         super(renderer);
         this.renderer = renderer;
         this.defaultBipedModel = new HumanoidModel(renderManagerIn.bakeLayer(ModelLayers.ARMOR_STAND_OUTER_ARMOR));
         this.armorTrimAtlas = Minecraft.getInstance().getModelManager().getAtlas(Sheets.ARMOR_TRIMS_SHEET);
      }

      public void render(
         PoseStack poseStack,
         MultiBufferSource bufferIn,
         int packedLightIn,
         CrowEntity crow,
         float limbSwing,
         float limbSwingAmount,
         float partialTick,
         float ageInTicks,
         float netHeadYaw,
         float headPitch
      ) {
         poseStack.pushPose();
         ItemStack itemstack = crow.itemHandler.getStackInSlot(0);
         if (itemstack.getItem() instanceof ArmorItem armoritem) {
            EquipmentSlot pSlot = armoritem.getEquipmentSlot();
            HumanoidModel<?> a = this.defaultBipedModel;
            a = this.getArmorModelHook(crow, itemstack, EquipmentSlot.HEAD, a);
            a.setAllVisible(false);
            a.hat.visible = true;
            a.head.visible = true;
            this.translateToHead(poseStack);
            poseStack.scale(0.35F, 0.35F, 0.35F);
            poseStack.translate(0.0F, -0.1F, -0.25F);
            Model model = ClientHooks.getArmorModel(crow, itemstack, pSlot, a);
            ArmorMaterial armormaterial = (ArmorMaterial)armoritem.getMaterial().value();
            boolean flag1 = itemstack.hasFoil();
            IClientItemExtensions extensions = IClientItemExtensions.of(itemstack);
            extensions.setupModelAnimations(
               crow, itemstack, EquipmentSlot.HEAD, model, limbSwing, limbSwingAmount, partialTick, ageInTicks, netHeadYaw, headPitch
            );

            for (int layerIdx = 0; layerIdx < armormaterial.layers().size(); layerIdx++) {
               Layer armormaterial$layer = (Layer)armormaterial.layers().get(layerIdx);
               int j = extensions.getArmorLayerTintColor(itemstack, crow, armormaterial$layer, layerIdx, -1);
               ResourceLocation texture = ClientHooks.getArmorTexture(crow, itemstack, armormaterial$layer, false, pSlot);
               this.renderHelmet(poseStack, bufferIn, packedLightIn, flag1, a, j, texture);
            }

            ArmorTrim armortrim = (ArmorTrim)itemstack.get(DataComponents.TRIM);
            if (armortrim != null) {
               this.renderTrim(armoritem.getMaterial(), poseStack, bufferIn, packedLightIn, armortrim, model, false);
            }

            if (itemstack.hasFoil()) {
               this.renderGlint(poseStack, bufferIn, packedLightIn, model);
            }
         } else if (Block.byItem(itemstack.getItem()) instanceof AbstractSkullBlock) {
            this.translateToHand(poseStack);
            poseStack.scale(0.45F, 0.45F, 0.45F);
            poseStack.translate(0.0F, -0.2F, -0.2F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
            this.renderItem(itemstack, crow.level(), poseStack, bufferIn, packedLightIn);
         }

         poseStack.popPose();
      }

      private void renderItem(ItemStack stack, Level level, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn) {
         Minecraft.getInstance()
            .getItemRenderer()
            .renderStatic(stack, ItemDisplayContext.FIXED, combinedLightIn, OverlayTexture.NO_OVERLAY, poseStack, bufferIn, level, 1);
      }

      private void translateToHead(PoseStack poseStack) {
         this.translateToChest(poseStack);
         ((CrowModel)this.renderer.getModel()).head.translateAndRotate(poseStack);
      }

      private void translateToChest(PoseStack poseStack) {
         ((CrowModel)this.renderer.getModel()).body.translateAndRotate(poseStack);
      }

      private void renderHelmet(
         PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, boolean glintIn, HumanoidModel modelIn, int col, ResourceLocation armorResource
      ) {
         VertexConsumer ivertexbuilder = ItemRenderer.getFoilBuffer(bufferIn, RenderType.entityCutoutNoCull(armorResource), false, glintIn);
         ((CrowModel)this.renderer.getModel()).copyPropertiesTo(modelIn);
         modelIn.head.xRot = 0.0F;
         modelIn.head.yRot = 0.0F;
         modelIn.head.zRot = 0.0F;
         modelIn.hat.xRot = 0.0F;
         modelIn.hat.yRot = 0.0F;
         modelIn.hat.zRot = 0.0F;
         modelIn.head.x = 0.0F;
         modelIn.head.y = 0.0F;
         modelIn.head.z = 0.0F;
         modelIn.hat.x = 0.0F;
         modelIn.hat.y = 0.0F;
         modelIn.hat.z = 0.0F;
         modelIn.renderToBuffer(poseStack, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, col);
      }

      private void renderTrim(
         Holder<ArmorMaterial> p_323506_,
         PoseStack p_289687_,
         MultiBufferSource p_289643_,
         int p_289683_,
         ArmorTrim p_289692_,
         Model p_289663_,
         boolean p_289651_
      ) {
         TextureAtlasSprite textureatlassprite = this.armorTrimAtlas
            .getSprite(p_289651_ ? p_289692_.innerTexture(p_323506_) : p_289692_.outerTexture(p_323506_));
         VertexConsumer vertexconsumer = textureatlassprite.wrap(
            p_289643_.getBuffer(Sheets.armorTrimsSheet(((TrimPattern)p_289692_.pattern().value()).decal()))
         );
         p_289663_.renderToBuffer(p_289687_, vertexconsumer, p_289683_, OverlayTexture.NO_OVERLAY);
      }

      private void renderGlint(PoseStack p_289673_, MultiBufferSource p_289654_, int p_289649_, Model p_289659_) {
         p_289659_.renderToBuffer(p_289673_, p_289654_.getBuffer(RenderType.armorEntityGlint()), p_289649_, OverlayTexture.NO_OVERLAY);
      }

      protected HumanoidModel<?> getArmorModelHook(LivingEntity entity, ItemStack itemStack, EquipmentSlot slot, HumanoidModel model) {
         Model basicModel = ClientHooks.getArmorModel(entity, itemStack, slot, model);
         return basicModel instanceof HumanoidModel ? (HumanoidModel)basicModel : model;
      }

      protected void translateToHand(PoseStack matrixStack) {
         ((CrowModel)this.getParentModel()).body.translateAndRotate(matrixStack);
         ((CrowModel)this.getParentModel()).body.getChild("head").translateAndRotate(matrixStack);
      }
   }

   public class LayerCrowItem extends RenderLayer<CrowEntity, CrowModel<CrowEntity>> {
      public LayerCrowItem(CrowRenderer render) {
         super(render);
      }

      public void render(
         PoseStack poseStack,
         MultiBufferSource bufferIn,
         int packedLightIn,
         CrowEntity crow,
         float limbSwing,
         float limbSwingAmount,
         float partialTicks,
         float ageInTicks,
         float netHeadYaw,
         float headPitch
      ) {
         ItemStack itemstack = crow.getItem(1);
         poseStack.pushPose();
         this.translateToHand(poseStack);
         poseStack.translate(0.0F, -0.065F, -0.265F);
         if (itemstack.getItem() instanceof BroomItem) {
            poseStack.translate(0.1F, 0.16F, 0.01F);
         }

         if (crow.isBaby()) {
            poseStack.scale(0.75F, 0.75F, 0.75F);
         }

         poseStack.mulPose(Axis.YP.rotationDegrees(-2.5F));
         if (itemstack.getItem() instanceof BroomItem) {
            poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
         }

         poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
         poseStack.scale(0.75F, 0.75F, 0.75F);
         ItemStack stack = itemstack.copy();
         Minecraft.getInstance().gameRenderer.itemInHandRenderer.renderItem(crow, stack, ItemDisplayContext.GROUND, false, poseStack, bufferIn, packedLightIn);
         poseStack.popPose();
      }

      protected void translateToHand(PoseStack matrixStack) {
         ((CrowModel)this.getParentModel()).body.translateAndRotate(matrixStack);
         ((CrowModel)this.getParentModel()).body.getChild("head").translateAndRotate(matrixStack);
      }
   }

   public class LayerCrowMisc extends RenderLayer<CrowEntity, CrowModel<CrowEntity>> {
      private static final ResourceLocation CROW_AMULET_LOCATION = HexereiUtil.getResource("textures/entity/crow_amulet.png");

      public LayerCrowMisc(RenderLayerParent<CrowEntity, CrowModel<CrowEntity>> p_117707_) {
         super(p_117707_);
      }

      public void render(
         PoseStack poseStack,
         MultiBufferSource bufferIn,
         int packedLightIn,
         CrowEntity crow,
         float p_117724_,
         float p_117725_,
         float p_117726_,
         float p_117727_,
         float p_117728_,
         float p_117729_
      ) {
         ItemStack itemstack = crow.getItem(2);
         if (!crow.isInvisible() && !itemstack.isEmpty()) {
            boolean renderNecklace = false;
            poseStack.pushPose();
            this.translateToBody(poseStack);
            poseStack.translate(0.0F, -0.15F, -0.165F);
            if (itemstack.getItem() instanceof BroomItem) {
               poseStack.translate(0.1F, 0.16F, 0.01F);
            }

            if (crow.isBaby()) {
               poseStack.scale(0.75F, 0.75F, 0.75F);
            }

            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            if (itemstack.getItem() instanceof BroomItem) {
               poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
            }

            poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
            poseStack.scale(0.1F, 0.1F, 0.1F);
            ItemStack stack = itemstack.copy();
            BakedModel itemModel = Minecraft.getInstance().getItemRenderer().getModel(stack, null, null, 0);
            ResourceLocation loc = CrowRenderer.TRINKET_LOCATION.getOrDefault(stack.getItem(), null);
            if (loc != null) {
               poseStack.pushPose();
               poseStack.translate(0.0F, 0.0F, -0.02F);
               poseStack.scale(10.0F, 10.0F, 10.0F);
               poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
               Matrix4f matrix = poseStack.last().pose();
               BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
               VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityCutout(loc));
               poseStack.last().normal().rotate(Axis.XP.rotationDegrees(45.0F));
               Pose normal = poseStack.last();
               int u = 0;
               int v = 0;
               int imageWidth = 16;
               int imageHeight = 16;
               int width = 16;
               int height = 16;
               float u1 = (u + 0.0F) / imageWidth;
               float u2 = ((float)u + width) / imageWidth;
               float v1 = (v + 0.0F) / imageHeight;
               float v2 = ((float)v + height) / imageHeight;
               boolean activeFlag = false;
               int temp = packedLightIn;
               if (stack.has(DataComponents.CUSTOM_DATA)) {
                  CompoundTag tag = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
                  if (tag.contains("Active") && tag.getBoolean("Active")) {
                     packedLightIn = 15728880;
                     activeFlag = true;
                  }
               }

               buffer.addVertex(matrix, 0.0034375F * width, -0.0034375F * height, 0.0F)
                  .setColor(255, 255, 255, 255)
                  .setUv(u1, v1)
                  .setOverlay(OverlayTexture.NO_OVERLAY)
                  .setLight(packedLightIn)
                  .setNormal(normal, 1.0F, 0.0F, 0.0F);
               buffer.addVertex(matrix, 0.0034375F * width, 0.0034375F * height, 0.0F)
                  .setColor(255, 255, 255, 255)
                  .setUv(u1, v2)
                  .setOverlay(OverlayTexture.NO_OVERLAY)
                  .setLight(packedLightIn)
                  .setNormal(normal, 1.0F, 0.0F, 0.0F);
               buffer.addVertex(matrix, -0.0034375F * width, 0.0034375F * height, 0.0F)
                  .setColor(255, 255, 255, 255)
                  .setUv(u2, v2)
                  .setOverlay(OverlayTexture.NO_OVERLAY)
                  .setLight(packedLightIn)
                  .setNormal(normal, 1.0F, 0.0F, 0.0F);
               buffer.addVertex(matrix, -0.0034375F * width, -0.0034375F * height, 0.0F)
                  .setColor(255, 255, 255, 255)
                  .setUv(u2, v1)
                  .setOverlay(OverlayTexture.NO_OVERLAY)
                  .setLight(packedLightIn)
                  .setNormal(normal, 1.0F, 0.0F, 0.0F);
               if (activeFlag) {
                  buffer = bufferSource.getBuffer(
                     RenderType.entityTranslucentEmissive(ResourceLocation.parse("hexerei:textures/item/crow_active_amulet_trinket.png"))
                  );
                  poseStack.translate(0.0F, 0.0F, 0.002F);
                  poseStack.mulPose(Axis.ZP.rotationDegrees(-ClientEvents.getClientTicks() % 360.0F));
                  poseStack.last().normal().rotate(Axis.XP.rotationDegrees(45.0F));
                  normal = poseStack.last();
                  int var36 = 32;
                  int var37 = 32;
                  int var38 = 32;
                  int var39 = 32;
                  u1 = (u + 0.0F) / var36;
                  u2 = ((float)u + var38) / var36;
                  v1 = (v + 0.0F) / var37;
                  v2 = ((float)v + var39) / var37;
                  buffer.addVertex(matrix, 0.0034375F * var38, -0.0034375F * var39, 0.0F)
                     .setColor(1.0F, 1.0F, 1.0F, Math.abs(Mth.cos(ClientEvents.getClientTicks() / 100.0F)))
                     .setUv(u1, v1)
                     .setOverlay(OverlayTexture.NO_OVERLAY)
                     .setLight(packedLightIn)
                     .setNormal(normal, 1.0F, 0.0F, 0.0F);
                  buffer.addVertex(matrix, 0.0034375F * var38, 0.0034375F * var39, 0.0F)
                     .setColor(1.0F, 1.0F, 1.0F, Math.abs(Mth.cos(ClientEvents.getClientTicks() / 100.0F)))
                     .setUv(u1, v2)
                     .setOverlay(OverlayTexture.NO_OVERLAY)
                     .setLight(packedLightIn)
                     .setNormal(normal, 1.0F, 0.0F, 0.0F);
                  buffer.addVertex(matrix, -0.0034375F * var38, 0.0034375F * var39, 0.0F)
                     .setColor(1.0F, 1.0F, 1.0F, Math.abs(Mth.cos(ClientEvents.getClientTicks() / 100.0F)))
                     .setUv(u2, v2)
                     .setOverlay(OverlayTexture.NO_OVERLAY)
                     .setLight(packedLightIn)
                     .setNormal(normal, 1.0F, 0.0F, 0.0F);
                  buffer.addVertex(matrix, -0.0034375F * var38, -0.0034375F * var39, 0.0F)
                     .setColor(1.0F, 1.0F, 1.0F, Math.abs(Mth.cos(ClientEvents.getClientTicks() / 100.0F)))
                     .setUv(u2, v1)
                     .setOverlay(OverlayTexture.NO_OVERLAY)
                     .setLight(packedLightIn)
                     .setNormal(normal, 1.0F, 0.0F, 0.0F);
                  poseStack.scale(1.15F, 1.15F, 1.15F);
                  poseStack.translate(0.0F, 0.0F, -0.004F);
                  poseStack.mulPose(Axis.ZP.rotationDegrees(1.5F * ClientEvents.getClientTicks() % 360.0F));
                  buffer.addVertex(matrix, 0.0034375F * var38, -0.0034375F * var39, 0.0F)
                     .setColor(1.0F, 1.0F, 1.0F, Math.abs(Mth.cos(ClientEvents.getClientTicks() / 75.0F)))
                     .setUv(u1, v1)
                     .setOverlay(OverlayTexture.NO_OVERLAY)
                     .setLight(packedLightIn)
                     .setNormal(normal, 1.0F, 0.0F, 0.0F);
                  buffer.addVertex(matrix, 0.0034375F * var38, 0.0034375F * var39, 0.0F)
                     .setColor(1.0F, 1.0F, 1.0F, Math.abs(Mth.cos(ClientEvents.getClientTicks() / 75.0F)))
                     .setUv(u1, v2)
                     .setOverlay(OverlayTexture.NO_OVERLAY)
                     .setLight(packedLightIn)
                     .setNormal(normal, 1.0F, 0.0F, 0.0F);
                  buffer.addVertex(matrix, -0.0034375F * var38, 0.0034375F * var39, 0.0F)
                     .setColor(1.0F, 1.0F, 1.0F, Math.abs(Mth.cos(ClientEvents.getClientTicks() / 75.0F)))
                     .setUv(u2, v2)
                     .setOverlay(OverlayTexture.NO_OVERLAY)
                     .setLight(packedLightIn)
                     .setNormal(normal, 1.0F, 0.0F, 0.0F);
                  buffer.addVertex(matrix, -0.0034375F * var38, -0.0034375F * var39, 0.0F)
                     .setColor(1.0F, 1.0F, 1.0F, Math.abs(Mth.cos(ClientEvents.getClientTicks() / 75.0F)))
                     .setUv(u2, v1)
                     .setOverlay(OverlayTexture.NO_OVERLAY)
                     .setLight(packedLightIn)
                     .setNormal(normal, 1.0F, 0.0F, 0.0F);
               }

               packedLightIn = temp;
               poseStack.popPose();
               poseStack.translate(0.0F, 0.0F, 0.03F);
               poseStack.scale(1.15F, 1.15F, 1.15F);
               Minecraft.getInstance()
                  .gameRenderer
                  .itemInHandRenderer
                  .renderItem(
                     crow, new ItemStack((ItemLike)ModItems.CROW_BLANK_AMULET_TRINKET.get()), ItemDisplayContext.FIXED, false, poseStack, bufferIn, temp
                  );
               renderNecklace = true;
            } else if (stack.is((Item)ModItems.CROW_BLANK_AMULET.get())) {
               poseStack.pushPose();
               poseStack.translate(0.0F, 0.0F, 0.03F);
               poseStack.scale(1.15F, 1.15F, 1.15F);
               Minecraft.getInstance()
                  .gameRenderer
                  .itemInHandRenderer
                  .renderItem(crow, stack, ItemDisplayContext.FIXED, false, poseStack, bufferIn, packedLightIn);
               poseStack.popPose();
               renderNecklace = true;
            }

            poseStack.popPose();
            if (renderNecklace) {
               VertexConsumer vertexConsumer = ItemRenderer.getFoilBuffer(bufferIn, RenderType.entityCutoutNoCull(CROW_AMULET_LOCATION), false, false);
               ((CrowModel)this.getParentModel())
                  .renderToBuffer(poseStack, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, HexereiUtil.getColorValueAlpha(1.0F, 1.0F, 1.0F, 1.0F));
            }
         }
      }

      protected void translateToBody(PoseStack matrixStack) {
         ((CrowModel)this.getParentModel()).body.translateAndRotate(matrixStack);
      }
   }
}
