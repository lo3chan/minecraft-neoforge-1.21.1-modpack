package net.joefoxe.hexerei.client.renderer.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.BroomType;
import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.joefoxe.hexerei.client.renderer.entity.model.ArmorModels;
import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.custom.BroomAttachmentItem;
import net.joefoxe.hexerei.item.custom.BroomBrushItem;
import net.joefoxe.hexerei.item.custom.BroomItem;
import net.joefoxe.hexerei.item.custom.BroomStickItem;
import net.joefoxe.hexerei.item.custom.KeychainItem;
import net.joefoxe.hexerei.item.custom.SatchelItem;
import net.joefoxe.hexerei.util.HexereiTags;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

public class BroomRenderer extends EntityRenderer<BroomEntity> {
   protected static final ResourceLocation TEXTURE = HexereiUtil.getResource("textures/entity/broom.png");
   private static final ResourceLocation POWER_LOCATION = HexereiUtil.getResource("textures/entity/power_layer_light.png");

   public BroomRenderer(Context context) {
      super(context);
      this.shadowRadius = 0.0F;
      ArmorModels.init(context);
   }

   public ResourceLocation getTextureLocation(BroomEntity p_114482_) {
      return TEXTURE;
   }

   public void render(BroomEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
      BroomType broomType = entityIn.getBroomType();
      matrixStackIn.pushPose();
      if (entityIn.deltaMovementOld == null) {
         entityIn.deltaMovementOld = entityIn.getDeltaMovement();
      }

      if (entityIn.deltaRotationOld == 0.0F) {
         entityIn.deltaRotationOld = entityIn.deltaRotation;
      }

      float deltaRotation = Math.clamp(
         Mth.lerp(partialTicks, entityIn.deltaRotationOld, entityIn.deltaRotation),
         -13.0F + entityIn.deltaRotation / 22.5F,
         13.0F + entityIn.deltaRotation / 22.5F
      );
      float floatingOffset = Mth.lerp(partialTicks, entityIn.floatingOffsetOld, entityIn.floatingOffset);
      float deltaMovementX = Mth.lerp(partialTicks, (float)entityIn.deltaMovementOld.x(), (float)entityIn.getDeltaMovement().x());
      float deltaMovementY = Mth.lerp(partialTicks, (float)entityIn.deltaMovementOld.y(), (float)entityIn.getDeltaMovement().y());
      float deltaMovementZ = Mth.lerp(partialTicks, (float)entityIn.deltaMovementOld.z(), (float)entityIn.getDeltaMovement().z());
      new Vec3(deltaMovementX, deltaMovementY, deltaMovementZ);
      matrixStackIn.translate(0.0, 0.375 + floatingOffset, 0.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F - HexereiUtil.lerpAngle(entityIn.yRotO, entityIn.getYRot(), partialTicks) - deltaRotation * 2.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(deltaMovementY * 25.0F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(deltaRotation * 3.0F));
      float f = entityIn.getTimeSinceHit() - partialTicks;
      float f1 = entityIn.getDamageTaken() - partialTicks;
      if (f1 < 0.0F) {
         f1 = 0.0F;
      }

      if (f > 0.0F) {
         matrixStackIn.mulPose(Axis.XP.rotationDegrees(Mth.sin(f) * f * f1 / 10.0F * entityIn.getForwardDirection()));
      }

      float f2 = entityIn.getRockingAngle(partialTicks);
      if (!Mth.equal(f2, 0.0F)) {
         matrixStackIn.mulPose(new Quaternionf().setAngleAxis(f2 * 0.017453292F, 1.0F, 0.0F, 1.0F));
      }

      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(0.0, -1.6, 0.0);
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(180.0F));
      matrixStackIn.translate(0.0, -2.75, 0.0);
      if (broomType.item() instanceof BroomStickItem broomItem) {
         if (broomItem.model == null) {
            broomItem.bakeModels();
         }

         if (broomItem.model != null) {
            VertexConsumer ivertexbuilderStick = bufferIn.getBuffer(broomItem.model.renderType(broomItem.texture));
            broomItem.model
               .renderToBuffer(
                  matrixStackIn, ivertexbuilderStick, packedLightIn, OverlayTexture.NO_OVERLAY, HexereiUtil.getColorValueAlpha(1.0F, 1.0F, 1.0F, 1.0F)
               );
         }

         if (BroomEntity.getDyeColorNamed(entityIn) != null && broomItem.outter_model != null) {
            DyeColor dyeColor = BroomEntity.getDyeColorNamed(entityIn);
            float[] afloat = new float[]{1.0F, 1.0F, 1.0F};
            if (dyeColor != null) {
               afloat = HexereiUtil.rgbIntToFloatArray(dyeColor.getTextureDiffuseColor());
            }

            float offset = ClientEvents.getClientTicks() + partialTicks;
            VertexConsumer ivertexbuilderStick = bufferIn.getBuffer(RenderType.energySwirl(POWER_LOCATION, offset * 0.01F % 1.0F, offset * 0.01F % 1.0F));
            broomItem.outter_model
               .renderToBuffer(
                  matrixStackIn,
                  ivertexbuilderStick,
                  packedLightIn,
                  OverlayTexture.NO_OVERLAY,
                  HexereiUtil.getColorValueAlpha(afloat[0], afloat[1], afloat[2], 1.0F)
               );
         }
      }

      ItemStack brushStack = entityIn.itemHandler.getStackInSlot(2);
      if (brushStack.getItem() instanceof BroomBrushItem brushItem && brushItem.model == null) {
         brushItem.bakeModels();
      }

      if (entityIn.itemHandler.getStackInSlot(2).is(HexereiTags.Items.BROOM_BRUSH)) {
         matrixStackIn.pushPose();
         if (brushStack.getItem() instanceof BroomBrushItem brushItem && brushItem.model != null) {
            if (broomType.item() instanceof BroomItem broomItem) {
               matrixStackIn.translate(broomItem.getBrushOffset().x(), broomItem.getBrushOffset().y(), broomItem.getBrushOffset().z());
            }

            int light = packedLightIn;
            if (brushItem.shouldGlow(Minecraft.getInstance().level, brushStack)) {
               light = 15728880;
            }

            Model broomBrushModel = brushItem.model;
            VertexConsumer brushVertexConsumer = bufferIn.getBuffer(broomBrushModel.renderType(brushItem.texture));
            broomBrushModel.renderToBuffer(
               matrixStackIn, brushVertexConsumer, light, OverlayTexture.NO_OVERLAY, HexereiUtil.getColorValueAlpha(1.0F, 1.0F, 1.0F, 1.0F)
            );
            if (entityIn.hasCustomName() && BroomEntity.getDyeColorNamed(entityIn) != null) {
               DyeColor dyeColor = BroomEntity.getDyeColorNamed(entityIn);
               float[] afloat = new float[]{1.0F, 1.0F, 1.0F};
               if (dyeColor != null) {
                  afloat = HexereiUtil.rgbIntToFloatArray(dyeColor.getTextureDiffuseColor());
               }

               float offset = ClientEvents.getClientTicks() + partialTicks;
               VertexConsumer vertexconsumer = bufferIn.getBuffer(RenderType.energySwirl(POWER_LOCATION, offset * 0.01F % 1.0F, offset * 0.01F % 1.0F));
               broomBrushModel.renderToBuffer(
                  matrixStackIn, vertexconsumer, light, OverlayTexture.NO_OVERLAY, HexereiUtil.getColorValueAlpha(afloat[0], afloat[1], afloat[2], 1.0F)
               );
            }
         }

         matrixStackIn.popPose();
      }

      ItemStack satchelStack = entityIn.itemHandler.getStackInSlot(1);
      if (satchelStack.getItem() instanceof BroomAttachmentItem satchelItem && satchelItem.model == null) {
         satchelItem.bakeModels();
      }

      if (entityIn.itemHandler.getStackInSlot(1).is(HexereiTags.Items.ALL_SATCHELS)
         && satchelStack.getItem() instanceof BroomAttachmentItem satchelItem
         && satchelItem.model != null) {
         if (broomType.item() instanceof BroomItem broomItem) {
            matrixStackIn.translate(broomItem.getSatchelOffset().x(), broomItem.getSatchelOffset().y(), broomItem.getSatchelOffset().z());
         }

         Model satchelModel = satchelItem.model;
         VertexConsumer vertexConsumer = bufferIn.getBuffer(satchelModel.renderType(satchelItem.texture));
         satchelModel.renderToBuffer(
            matrixStackIn, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, HexereiUtil.getColorValueAlpha(1.0F, 1.0F, 1.0F, 1.0F)
         );
         if (satchelItem.dye_texture != null) {
            if (SatchelItem.getDyeColorNamed(satchelStack) != null) {
               float[] afloat = HexereiUtil.rgbIntToFloatArray(SatchelItem.getDyeColorNamed(satchelStack).getTextureDiffuseColor());
               VertexConsumer vertexConsumerDye = ItemRenderer.getFoilBuffer(bufferIn, RenderType.entityCutoutNoCull(satchelItem.dye_texture), false, false);
               satchelModel.renderToBuffer(
                  matrixStackIn,
                  vertexConsumerDye,
                  packedLightIn,
                  OverlayTexture.NO_OVERLAY,
                  HexereiUtil.getColorValueAlpha(afloat[0], afloat[1], afloat[2], 1.0F)
               );
            } else {
               int col = SatchelItem.getColorValue(SatchelItem.getDyeColorNamed(satchelStack), satchelStack);
               int i = (col & 0xFF0000) >> 16;
               int j = (col & 0xFF00) >> 8;
               int k = col & 0xFF;
               float[] afloat = new float[]{i / 255.0F, j / 255.0F, k / 255.0F};
               VertexConsumer vertexConsumerDye = ItemRenderer.getFoilBuffer(bufferIn, RenderType.entityCutoutNoCull(satchelItem.dye_texture), false, false);
               satchelModel.renderToBuffer(
                  matrixStackIn,
                  vertexConsumerDye,
                  packedLightIn,
                  OverlayTexture.NO_OVERLAY,
                  HexereiUtil.getColorValueAlpha(afloat[0], afloat[1], afloat[2], 1.0F)
               );
            }
         }
      }

      ItemStack miscStack = entityIn.itemHandler.getStackInSlot(0);
      if (miscStack.getItem() instanceof BroomAttachmentItem miscItem && miscItem.model == null) {
         miscItem.bakeModels();
      }

      if (miscStack.is(HexereiTags.Items.BROOM_MISC)) {
         if (miscStack.getItem() instanceof KeychainItem keychainItem && keychainItem.model != null) {
            if (broomType.item() instanceof BroomItem broomItem) {
               matrixStackIn.translate(broomItem.getTipOffset().x(), broomItem.getTipOffset().y(), broomItem.getTipOffset().z());
            }

            Model broomKeychainModel = keychainItem.model;
            VertexConsumer ivertexbuilderRings = bufferIn.getBuffer(broomKeychainModel.renderType(keychainItem.texture));
            broomKeychainModel.renderToBuffer(
               matrixStackIn, ivertexbuilderRings, packedLightIn, OverlayTexture.NO_OVERLAY, HexereiUtil.getColorValueAlpha(1.0F, 1.0F, 1.0F, 1.0F)
            );
            matrixStackIn.translate(-1.1875F, 0.0F, -0.025F);
            matrixStackIn.translate(0.0, 2.75, 0.0);
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(180.0F));
            matrixStackIn.translate(0.0, 1.3, 0.0);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-deltaMovementY * 20.0F));
            matrixStackIn.mulPose(Axis.YP.rotationDegrees((float)(Math.atan2(deltaMovementZ, deltaMovementX) / 6.283185307179586 * 360.0) - entityYaw));
            if (entityIn.selfItem != null
               && ItemStack.isSameItemSameComponents(entityIn.selfItem, Hexerei.proxy.getPlayer().getMainHandItem())
               && Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
               matrixStackIn.mulPose(Axis.XP.rotationDegrees((Hexerei.proxy.getPlayer().yHeadRot - Hexerei.proxy.getPlayer().yHeadRotO) * 1.5F));
               matrixStackIn.mulPose(
                  Axis.XP
                     .rotationDegrees(
                        Mth.clamp(
                           (float)Hexerei.proxy.getPlayer().getDeltaMovement().yRot(-90.0F).dot(Hexerei.proxy.getPlayer().getLookAngle()) * -125.0F,
                           -70.0F,
                           70.0F
                        )
                     )
               );
               matrixStackIn.mulPose(Axis.ZP.rotationDegrees((float)(Hexerei.proxy.getPlayer().getLookAngle().y * -50.0) - 50.0F));
               matrixStackIn.mulPose(
                  Axis.ZP
                     .rotationDegrees(
                        Mth.clamp((float)Hexerei.proxy.getPlayer().getDeltaMovement().dot(Hexerei.proxy.getPlayer().getLookAngle()) * -125.0F, -70.0F, 70.0F)
                     )
               );
            }

            matrixStackIn.mulPose(Axis.ZP.rotationDegrees((float)Mth.length(deltaMovementX, deltaMovementZ) * 50.0F));
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
            matrixStackIn.translate(0.0, -1.3, 0.0);
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(180.0F));
            matrixStackIn.translate(0.0, -2.79375, 0.0);
            Model broomKeychainChainModel = (Model)keychainItem.chain_resources.getSecond();
            VertexConsumer ivertexbuilderChain = bufferIn.getBuffer(
               broomKeychainChainModel.renderType((ResourceLocation)keychainItem.chain_resources.getFirst())
            );
            broomKeychainChainModel.renderToBuffer(
               matrixStackIn, ivertexbuilderRings, packedLightIn, OverlayTexture.NO_OVERLAY, HexereiUtil.getColorValueAlpha(1.0F, 1.0F, 1.0F, 1.0F)
            );
            matrixStackIn.translate(0.0, 1.71875, 0.0);
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(180.0F));
            if (entityIn.selfItem != null
               && ItemStack.isSameItemSameComponents(entityIn.selfItem, Hexerei.proxy.getPlayer().getMainHandItem())
               && Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
               matrixStackIn.mulPose(Axis.ZP.rotationDegrees((float)(Hexerei.proxy.getPlayer().getLookAngle().y * 20.0)));
               matrixStackIn.mulPose(
                  Axis.ZP
                     .rotationDegrees(
                        Mth.clamp((float)Hexerei.proxy.getPlayer().getDeltaMovement().dot(Hexerei.proxy.getPlayer().getLookAngle()) * 50.0F, -20.0F, 20.0F)
                     )
               );
            }

            matrixStackIn.mulPose(Axis.ZP.rotationDegrees((float)Mth.length(deltaMovementX, deltaMovementZ) * -20.0F));
            NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
            if (miscStack.has(DataComponents.CUSTOM_DATA)) {
               ContainerHelper.loadAllItems(
                  ((CustomData)miscStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag(), items, entityIn.level().registryAccess()
               );
            }

            if (((ItemStack)items.get(0)).getItem() instanceof BroomItem) {
               matrixStackIn.scale(0.45F, 0.45F, 0.45F);
            } else {
               matrixStackIn.scale(0.25F, 0.25F, 0.25F);
            }

            this.renderItem((ItemStack)items.get(0), entityIn.level(), matrixStackIn, bufferIn, packedLightIn);
         } else if (miscStack.is((Item)ModItems.BROOM_NETHERITE_TIP.get())) {
            if (miscStack.getItem() instanceof BroomAttachmentItem miscItem && miscItem.model != null) {
               int lightx = packedLightIn / 15 * (15 - (int)(8.0F * ((float)miscStack.getDamageValue() / miscStack.getMaxDamage())));
               Model miscModel = miscItem.model;
               VertexConsumer vertexConsumer = bufferIn.getBuffer(miscModel.renderType(miscItem.texture));
               miscModel.renderToBuffer(
                  matrixStackIn, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, HexereiUtil.getColorValueAlpha(1.0F, 1.0F, 1.0F, 1.0F)
               );
               matrixStackIn.translate(-1.375F, 0.0F, -0.025F);
               matrixStackIn.translate(0.0, 2.68, 0.0);
               matrixStackIn.mulPose(Axis.XP.rotationDegrees(180.0F));
               matrixStackIn.translate(0.0, 1.3, 0.0);
               matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
               matrixStackIn.mulPose(Axis.ZP.rotationDegrees(90.0F));
               matrixStackIn.scale(0.3F, 0.3F, 0.3F);
               this.renderItem(new ItemStack((ItemLike)ModItems.SELENITE_SHARD.get()), entityIn.level(), matrixStackIn, bufferIn, lightx);
            }
         } else if (miscStack.is((Item)ModItems.BROOM_WATERPROOF_TIP.get())) {
            if (miscStack.getItem() instanceof BroomAttachmentItem miscItem && miscItem.model != null) {
               int lightx = packedLightIn / 15 * (15 - (int)(8.0F * ((float)miscStack.getDamageValue() / miscStack.getMaxDamage())));
               Model miscModel = miscItem.model;
               VertexConsumer vertexConsumer = bufferIn.getBuffer(miscModel.renderType(miscItem.texture));
               miscModel.renderToBuffer(
                  matrixStackIn, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, HexereiUtil.getColorValueAlpha(1.0F, 1.0F, 1.0F, 1.0F)
               );
               matrixStackIn.translate(-1.375F, 0.0F, -0.025F);
               matrixStackIn.translate(0.0, 2.68, 0.0);
               matrixStackIn.mulPose(Axis.XP.rotationDegrees(180.0F));
               matrixStackIn.translate(0.0, 1.3, 0.0);
               matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
               matrixStackIn.mulPose(Axis.ZP.rotationDegrees(90.0F));
               matrixStackIn.scale(0.3F, 0.3F, 0.3F);
               this.renderItem(new ItemStack(Items.CONDUIT), entityIn.level(), matrixStackIn, bufferIn, lightx);
            }
         } else if (miscStack.getItem() instanceof BroomAttachmentItem miscItem && miscItem.model != null) {
            Model miscModel = miscItem.model;
            VertexConsumer vertexConsumer = bufferIn.getBuffer(miscModel.renderType(miscItem.texture));
            miscModel.renderToBuffer(
               matrixStackIn, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, HexereiUtil.getColorValueAlpha(1.0F, 1.0F, 1.0F, 1.0F)
            );
            if (miscItem.dye_texture != null) {
               if (SatchelItem.getDyeColorNamed(satchelStack) != null) {
                  float[] afloat = HexereiUtil.rgbIntToFloatArray(SatchelItem.getDyeColorNamed(satchelStack).getTextureDiffuseColor());
                  VertexConsumer vertexConsumerDye = ItemRenderer.getFoilBuffer(bufferIn, RenderType.entityCutoutNoCull(miscItem.dye_texture), false, false);
                  miscModel.renderToBuffer(
                     matrixStackIn,
                     vertexConsumerDye,
                     packedLightIn,
                     OverlayTexture.NO_OVERLAY,
                     HexereiUtil.getColorValueAlpha(afloat[0], afloat[1], afloat[2], 1.0F)
                  );
               } else {
                  int col = SatchelItem.getColorValue(SatchelItem.getDyeColorNamed(satchelStack), satchelStack);
                  int i = (col & 0xFF0000) >> 16;
                  int j = (col & 0xFF00) >> 8;
                  int k = col & 0xFF;
                  float[] afloat = new float[]{i / 255.0F, j / 255.0F, k / 255.0F};
                  VertexConsumer vertexConsumerDye = ItemRenderer.getFoilBuffer(bufferIn, RenderType.entityCutoutNoCull(miscItem.dye_texture), false, false);
                  miscModel.renderToBuffer(
                     matrixStackIn,
                     vertexConsumerDye,
                     packedLightIn,
                     OverlayTexture.NO_OVERLAY,
                     HexereiUtil.getColorValueAlpha(afloat[0], afloat[1], afloat[2], 1.0F)
                  );
               }
            }
         }
      }

      matrixStackIn.popPose();
      super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
   }

   private void renderItem(ItemStack stack, Level level, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn) {
      Minecraft.getInstance()
         .getItemRenderer()
         .renderStatic(stack, ItemDisplayContext.FIXED, combinedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn, level, 1);
   }
}
