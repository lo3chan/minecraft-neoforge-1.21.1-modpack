package traben.entity_texture_features.mixin.mixins.entity.renderer.feature;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.MushroomCowMushroomLayer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.texture_handlers.ETFTexture;
import traben.entity_texture_features.utils.ETFUtils2;

@Mixin({MushroomCowMushroomLayer.class})
public abstract class MixinMooshroomMushroomFeatureRenderer {
   @Unique
   private static final ResourceLocation RED_SHROOM = ETFUtils2.res("textures/entity/cow/red_mushroom.png");
   @Unique
   private static final ResourceLocation BROWN_SHROOM = ETFUtils2.res("textures/entity/cow/brown_mushroom.png");
   @Unique
   private static final ModelPart[] entity_texture_features$shroomAsEntityModel = entity_texture_features$getModelData();
   @Unique
   private static ResourceLocation entity_texture_features$redEmissive = null;
   @Unique
   private static ResourceLocation entity_texture_features$brownEmissive = null;
   @Unique
   private boolean isRed = false;
   @Unique
   private boolean isBrown = false;

   @Unique
   private static ModelPart[] entity_texture_features$getModelData() {
      CubeDeformation dilation = new CubeDeformation(0.0F);
      MeshDefinition modelData = new MeshDefinition();
      PartDefinition modelPartData = modelData.getRoot();
      modelPartData.addOrReplaceChild("shroom1", CubeListBuilder.create().texOffs(32, 16).addBox(0.0F, 0.0F, 8.0F, 16.0F, 16.0F, 0.0F, dilation), PartPose.ZERO);
      modelPartData.addOrReplaceChild("shroom2", CubeListBuilder.create().texOffs(32, 16).addBox(8.0F, 0.0F, 0.0F, 0.0F, 16.0F, 16.0F, dilation), PartPose.ZERO);
      ModelPart shroom1 = modelData.getRoot().getChild("shroom1").bake(32, 16);
      ModelPart shroom2 = modelData.getRoot().getChild("shroom2").bake(32, 16);
      return new ModelPart[]{shroom1, shroom2};
   }

   @Unique
   @Nullable
   private Boolean entity_texture_features$returnRedTrueBrownFalseVanillaNull() {
      if (ETF.config().getConfig().enableCustomTextures) {
         if (this.isRed) {
            if (ETFManager.getInstance().mooshroomRedCustomShroomExists == null) {
               if (Minecraft.getInstance().getResourceManager().getResource(RED_SHROOM).isPresent()) {
                  ETFManager.getInstance().mooshroomRedCustomShroomExists = entity_texture_features$prepareMushroomTextures(true);
               } else {
                  ETFManager.getInstance().mooshroomRedCustomShroomExists = false;
               }
            }

            return ETFManager.getInstance().mooshroomRedCustomShroomExists;
         }

         if (this.isBrown) {
            if (ETFManager.getInstance().mooshroomBrownCustomShroomExists == null) {
               if (Minecraft.getInstance().getResourceManager().getResource(BROWN_SHROOM).isPresent()) {
                  ETFManager.getInstance().mooshroomBrownCustomShroomExists = entity_texture_features$prepareMushroomTextures(false);
               } else {
                  ETFManager.getInstance().mooshroomBrownCustomShroomExists = false;
               }
            }

            return ETFManager.getInstance().mooshroomBrownCustomShroomExists;
         }
      }

      return null;
   }

   @Unique
   @NotNull
   private static Boolean entity_texture_features$prepareMushroomTextures(boolean isRed) {
      Boolean bool = entity_texture_features$prepareMushroomTextures(isRed, false);
      return bool != null && bool;
   }

   @Unique
   @Nullable
   private static Boolean entity_texture_features$prepareMushroomTextures(boolean isRed, boolean doingEmissive) {
      ResourceLocation idOfOriginal = isRed ? RED_SHROOM : BROWN_SHROOM;
      String suffix = null;
      if (doingEmissive) {
         boolean found = false;

         for (String str : ETFManager.getInstance().EMISSIVE_SUFFIX_LIST) {
            ResourceLocation test = ETFUtils2.res(idOfOriginal.toString().replace(".png", str + ".png"));
            if (Minecraft.getInstance().getResourceManager().getResource(test).isPresent()) {
               suffix = str;
               idOfOriginal = test;
               found = true;
               break;
            }
         }

         if (!found) {
            return null;
         }
      }

      NativeImage originalImagePreFlip = ETFUtils2.getNativeImageElseNull(idOfOriginal);
      if (originalImagePreFlip != null) {
         try {
            NativeImage flippedOriginalImage = ETFUtils2.emptyNativeImage(originalImagePreFlip.getWidth(), originalImagePreFlip.getHeight());

            NativeImage newImage;
            try {
               for (int x = 0; x < flippedOriginalImage.getWidth(); x++) {
                  for (int y = 0; y < flippedOriginalImage.getHeight(); y++) {
                     ETFUtils2.setPixel(flippedOriginalImage, x, y, ETFUtils2.getPixel(originalImagePreFlip, x, originalImagePreFlip.getHeight() - 1 - y));
                  }
               }

               newImage = ETFUtils2.emptyNativeImage(flippedOriginalImage.getWidth() * 2, flippedOriginalImage.getHeight());

               for (int x = 0; x < newImage.getWidth(); x++) {
                  for (int y = 0; y < newImage.getHeight(); y++) {
                     if (x < flippedOriginalImage.getWidth()) {
                        ETFUtils2.setPixel(newImage, x, y, ETFUtils2.getPixel(flippedOriginalImage, x, y));
                     } else {
                        ETFUtils2.setPixel(
                           newImage,
                           x,
                           y,
                           ETFUtils2.getPixel(flippedOriginalImage, flippedOriginalImage.getWidth() - 1 - (x - flippedOriginalImage.getWidth()), y)
                        );
                     }
                  }
               }
            } catch (Throwable var10) {
               if (flippedOriginalImage != null) {
                  try {
                     flippedOriginalImage.close();
                  } catch (Throwable var9) {
                     var10.addSuppressed(var9);
                  }
               }

               throw var10;
            }

            if (flippedOriginalImage != null) {
               flippedOriginalImage.close();
            }

            ResourceLocation idOfNew = isRed ? ETFUtils2.res("etf", "red_shroom_alt.png") : ETFUtils2.res("etf", "brown_shroom_alt.png");
            if (doingEmissive && suffix != null) {
               ResourceLocation emissive = ETFUtils2.res(idOfNew.toString().replace(".png", suffix + ".png"));
               ETFUtils2.registerNativeImageToIdentifier(newImage, emissive);
               if (isRed) {
                  entity_texture_features$redEmissive = emissive;
               } else {
                  entity_texture_features$brownEmissive = emissive;
               }
            } else {
               ETFUtils2.registerNativeImageToIdentifier(newImage, idOfNew);
            }

            if (!doingEmissive) {
               entity_texture_features$prepareMushroomTextures(isRed, true);
               if (isRed) {
                  ETFManager.getInstance().redMooshroomAlt = ETFTexture.ofUnmodifiable(idOfNew, entity_texture_features$redEmissive);
               } else {
                  ETFManager.getInstance().brownMooshroomAlt = ETFTexture.ofUnmodifiable(idOfNew, entity_texture_features$brownEmissive);
               }
            }

            return isRed;
         } catch (Exception var11) {
            ETFUtils2.logError("Mooshroom custom mushroom texture could not be loaded. " + var11);
         }
      }

      return null;
   }

   @Inject(
      method = {"render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/animal/MushroomCow;FFFFFF)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V"
      )}
   )
   private void etf$injected(CallbackInfo ci, @Local BlockState mushroomState) {
      this.isRed = mushroomState.is(Blocks.RED_MUSHROOM);
      this.isBrown = mushroomState.is(Blocks.BROWN_MUSHROOM);
   }

   @Inject(
      method = {"renderMushroomBlock"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void etf$injected(
      PoseStack matrices,
      MultiBufferSource vertexConsumers,
      int light,
      boolean renderAsModel,
      BlockState mushroomState,
      int overlay,
      BakedModel mushroomModel,
      CallbackInfo ci
   ) {
      Boolean shroomType = this.entity_texture_features$returnRedTrueBrownFalseVanillaNull();
      if (shroomType != null) {
         ETFTexture thisTexture = shroomType ? ETFManager.getInstance().redMooshroomAlt : ETFManager.getInstance().brownMooshroomAlt;
         if (thisTexture != null) {
            for (ModelPart model : entity_texture_features$shroomAsEntityModel) {
               VertexConsumer texturedConsumer = vertexConsumers.getBuffer(RenderType.entityCutout(thisTexture.thisIdentifier));
               model.render(matrices, texturedConsumer, light, overlay);
               thisTexture.renderEmissive(matrices, vertexConsumers, model);
            }

            ci.cancel();
         }
      }
   }
}
