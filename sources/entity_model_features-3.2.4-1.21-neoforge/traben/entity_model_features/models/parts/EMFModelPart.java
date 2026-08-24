package traben.entity_model_features.models.parts;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexMultiConsumer.Double;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelPart.Cube;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SpriteCoordinateExpander;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.phys.AABB;
import traben.entity_model_features.EMF;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.mod_compat.IrisShadowPassDetection;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.utils.EMFUtils;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.texture_handlers.ETFTexture;
import traben.entity_texture_features.utils.ETFUtils2;
import traben.entity_texture_features.utils.ETFVertexConsumer;
import traben.entity_texture_features.utils.ETFUtils2.RenderMethodForOverlay;

public abstract class EMFModelPart extends ModelPart {
   public ResourceLocation textureOverride;
   public boolean isSetByAnimation = false;
   private final EMFModelPartRoot root;

   public EMFModelPartRoot getRoot() {
      return this.root;
   }

   public EMFModelPart(List<Cube> cuboids, Map<String, ModelPart> children, EMFModelPartRoot root) {
      super(cuboids, children);
      this.cubes = new ArrayList<>(cuboids);
      this.children = new HashMap<>(children);
      this.root = root;
   }

   public void processArmItemOverrides(PoseStack matrices) {
      matrices.pushPose();
      this.translateAndRotate(matrices);
      this.children.values().forEach(v -> ((EMFModelPart)v).processArmItemOverrides(matrices));
      matrices.popPose();
   }

   public void render(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int k) {
      try {
         EMFConfig.RenderModeChoice choice = ((EMFConfig)EMF.config().getConfig()).getRenderModeFor(EMFAnimationEntityContext.getEMFEntity());
         if (choice == EMFConfig.RenderModeChoice.NORMAL) {
            this.renderWithTextureOverride(matrices, vertices, light, overlay, k);
            return;
         }

         if (((EMFConfig)EMF.config().getConfig()).onlyDebugRenderOnHover && !EMFAnimationEntityContext.isClientHovered()) {
            this.renderWithTextureOverride(matrices, vertices, light, overlay, k);
            return;
         }

         switch (choice) {
            case GREEN:
               this.renderDebugTinted(matrices, vertices, light, overlay, k);
               break;
            case LINES:
               this.renderBoxes(matrices, Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(this.lines()));
               break;
            case LINES_AND_TEXTURE:
               this.renderWithTextureOverride(matrices, vertices, light, overlay, k);
               this.renderBoxesNoChildren(matrices, Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(this.lines()), 1.0F);
               break;
            case LINES_AND_TEXTURE_FLASH:
               this.renderWithTextureOverride(matrices, vertices, light, overlay, k);
               float flash = (Mth.sin((float)System.currentTimeMillis() / 1000.0F) + 1.0F) / 2.0F;
               this.renderBoxesNoChildren(matrices, Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(this.lines()), flash);
            case NONE:
         }
      } catch (Exception var8) {
         var8.printStackTrace();
      }
   }

   private RenderType lines() {
      return RenderType.lines();
   }

   private void renderDebugTinted(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int k) {
      float flash = Math.abs(Mth.sin((float)System.currentTimeMillis() / 1000.0F));
      int col = ARGB32.color((int)(255.0F * flash), ARGB32.green(k), (int)(255.0F * flash), ARGB32.alpha(k));
      this.renderWithTextureOverride(matrices, vertices, light, overlay, col);
   }

   void renderWithTextureOverride(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int k) {
      if (this.textureOverride != null && (!EMFAnimationEntityContext.isLayerPhase() || !this.getRoot().isMainModel)) {
         if (light != 15728881 && !ETFRenderContext.isIsInSpecialRenderOverlayPhase()) {
            if (EMFAnimationEntityContext.getEMFEntity() != null
               && EMFAnimationEntityContext.getEMFEntity().etf$isBlockEntity()
               && ETF.IRIS_DETECTED
               && IrisShadowPassDetection.getInstance().inShadowPass()) {
               this.renderLikeETF(matrices, vertices, light, overlay, k);
               return;
            }

            if (vertices instanceof ETFVertexConsumer etfVertexConsumer) {
               ETFTexture etfTextureTest = etfVertexConsumer.etf$getETFTexture();
               if (etfTextureTest != null && etfTextureTest.thisIdentifier.equals(this.textureOverride)) {
                  this.renderLikeETF(matrices, vertices, light, overlay, k);
                  return;
               }

               RenderType originalLayer = etfVertexConsumer.etf$getRenderLayer();
               if (originalLayer == null) {
                  return;
               }

               MultiBufferSource provider = etfVertexConsumer.etf$getProvider();
               if (provider == null) {
                  return;
               }

               this.renderTextureOverrideWithoutReset(provider, matrices, light, overlay, k);
               provider.getBuffer(originalLayer);
            } else {
               MultiBufferSource provider = Minecraft.getInstance().renderBuffers().bufferSource();
               this.renderTextureOverrideWithoutReset(provider, matrices, light, overlay, k);
            }
         }
      } else {
         this.renderLikeETF(matrices, vertices, light, overlay, k);
      }
   }

   private void renderTextureOverrideWithoutReset(MultiBufferSource provider, PoseStack matrices, int light, int overlay, int k) {
      RenderType layerModified = EMFAnimationEntityContext.getLayerFromRecentFactoryOrETFOverrideOrTranslucent(this.textureOverride);
      VertexConsumer newConsumer = provider.getBuffer(layerModified);
      this.renderLikeVanilla(matrices, newConsumer, light, overlay, k);
      if (newConsumer instanceof ETFVertexConsumer newETFConsumer) {
         ETFTexture etfTexture = newETFConsumer.etf$getETFTexture();
         if (etfTexture == null) {
            return;
         }

         RenderMethodForOverlay renderMethodForOverlay = (prov, ligh) -> this.renderLikeVanilla(matrices, prov, ligh, overlay, k);
         ETFUtils2.renderEmissive(etfTexture, provider, renderMethodForOverlay);
         ETFUtils2.renderEnchanted(etfTexture, provider, light, renderMethodForOverlay);
      }
   }

   void renderLikeVanilla(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int k) {
      if (this.visible && (!this.cubes.isEmpty() || !this.children.isEmpty())) {
         matrices.pushPose();
         this.translateAndRotate(matrices);
         if (!this.skipDraw) {
            this.compile(matrices.last(), vertices, light, overlay, k);
         }

         for (ModelPart modelPart : this.children.values()) {
            modelPart.render(matrices, vertices, light, overlay, k);
         }

         matrices.popPose();
      }
   }

   private VertexConsumer testForBuildingException(VertexConsumer vertices) {
      if (vertices instanceof BufferBuilder testBuildingx
         || vertices instanceof SpriteCoordinateExpander sprite && sprite.delegate instanceof BufferBuilder testBuildingx
         || vertices instanceof Double dub && dub.second instanceof BufferBuilder testBuildingx) {
         if (testBuildingx != null && !testBuildingx.building) {
            if (!(testBuildingx instanceof ETFVertexConsumer etf) || etf.etf$getRenderLayer() == null || etf.etf$getProvider() == null) {
               return null;
            }

            boolean allowed = ETFRenderContext.isAllowedToRenderLayerTextureModify();
            ETFRenderContext.preventRenderLayerTextureModify();
            vertices = etf.etf$getProvider().getBuffer(etf.etf$getRenderLayer());
            if (allowed) {
               ETFRenderContext.allowRenderLayerTextureModify();
            }
         }

         return vertices;
      } else {
         return vertices;
      }
   }

   void renderLikeETF(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int k) {
      vertices = this.testForBuildingException(vertices);
      if (vertices != null) {
         ETFRenderContext.incrementCurrentModelPartDepth();
         this.renderLikeVanilla(matrices, vertices, light, overlay, k);
         if (ETFRenderContext.getCurrentModelPartDepth() != 1) {
            ETFRenderContext.decrementCurrentModelPartDepth();
         } else {
            if (ETFRenderContext.isCurrentlyRenderingEntity() && vertices instanceof ETFVertexConsumer etfVertexConsumer) {
               ETFTexture texture = etfVertexConsumer.etf$getETFTexture();
               if (texture != null && (texture.isEmissive() || texture.isEnchanted())) {
                  MultiBufferSource provider = etfVertexConsumer.etf$getProvider();
                  RenderType layer = etfVertexConsumer.etf$getRenderLayer();
                  if (provider != null && layer != null) {
                     RenderMethodForOverlay renderMethodForOverlay = (prov, ligh) -> this.renderLikeVanilla(matrices, prov, ligh, overlay, k);
                     if (ETFUtils2.renderEmissive(texture, provider, renderMethodForOverlay)
                        | ETFUtils2.renderEnchanted(texture, provider, light, renderMethodForOverlay)) {
                        provider.getBuffer(layer);
                     }
                  }
               }
            }

            ETFRenderContext.resetCurrentModelPartDepth();
         }
      }
   }

   public void renderBoxes(PoseStack matrices, VertexConsumer vertices) {
      if (this.visible && (!this.cubes.isEmpty() || !this.children.isEmpty())) {
         matrices.pushPose();
         this.translateAndRotate(matrices);
         if (!this.skipDraw) {
            for (Cube cuboid : this.cubes) {
               AABB box = new AABB(cuboid.minX / 16.0F, cuboid.minY / 16.0F, cuboid.minZ / 16.0F, cuboid.maxX / 16.0F, cuboid.maxY / 16.0F, cuboid.maxZ / 16.0F);
               float[] col = this.debugBoxColor();
               LevelRenderer.renderLineBox(matrices, vertices, box.inflate(1.0E-4), col[0], col[1], col[2], 1.0F);
            }
         }

         for (ModelPart modelPart : this.children.values()) {
            if (modelPart instanceof EMFModelPart emf) {
               emf.renderBoxes(matrices, vertices);
            }
         }

         matrices.popPose();
      }
   }

   protected abstract float[] debugBoxColor();

   public void renderBoxesNoChildren(PoseStack matrices, VertexConsumer vertices, float alpha) {
      if (this.visible && (!this.cubes.isEmpty() || !this.children.isEmpty())) {
         matrices.pushPose();
         this.translateAndRotate(matrices);
         if (!this.skipDraw) {
            for (Cube cuboid : this.cubes) {
               AABB box = new AABB(cuboid.minX / 16.0F, cuboid.minY / 16.0F, cuboid.minZ / 16.0F, cuboid.maxX / 16.0F, cuboid.maxY / 16.0F, cuboid.maxZ / 16.0F);
               float[] col = this.debugBoxColor();
               LevelRenderer.renderLineBox(matrices, vertices, box.inflate(1.0E-4), col[0], col[1], col[2], alpha);
            }
         }

         matrices.popPose();
      }
   }

   public void compile(Pose pose, VertexConsumer vertexConsumer, int i, int j, int k) {
      try {
         for (Cube cuboid : this.cubes) {
            cuboid.compile(pose, vertexConsumer, i, j, k);
         }
      } catch (IllegalStateException var8) {
         EMFUtils.logWarn("IllegalStateException caught in EMF model part");
      }
   }

   public String simplePrintChildren(int depth) {
      StringBuilder mapper = new StringBuilder();
      mapper.append("\n  | ");
      mapper.append("- ".repeat(Math.max(0, depth)));
      mapper.append(this.toStringShort());

      for (ModelPart child : this.children.values()) {
         if (child instanceof EMFModelPart emf) {
            mapper.append(emf.simplePrintChildren(depth + 1));
         }
      }

      return mapper.toString();
   }

   public String toStringShort() {
      return this.toString();
   }

   public String toString() {
      return "generic emf part";
   }

   public ModelPart getVanillaModelPartsOfCurrentState() {
      Map<String, ModelPart> children = new HashMap<>();

      for (Entry<String, ModelPart> child : this.children.entrySet()) {
         if (child.getValue() instanceof EMFModelPart emf) {
            children.put(child.getKey(), emf.getVanillaModelPartsOfCurrentState());
         }
      }

      List<Cube> finalCubes;
      if (this.cubes.isEmpty()) {
         finalCubes = List.of(new Cube(0, 0, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, false, 0.0F, 0.0F, Set.of()));
      } else {
         finalCubes = this.cubes;
      }

      ModelPart part = new ModelPart(finalCubes, children);
      part.setInitialPose(this.getInitialPose());
      part.xRot = this.xRot;
      part.zRot = this.zRot;
      part.yRot = this.yRot;
      part.z = this.z;
      part.y = this.y;
      part.x = this.x;
      part.xScale = this.xScale;
      part.yScale = this.yScale;
      part.zScale = this.zScale;
      return part;
   }

   public HashMap<String, EMFModelPart> getAllChildPartsAsAnimationMap(String prefixableParents, int variantNum, Map<String, String> optifinePartNameMap) {
      if (this instanceof EMFModelPartRoot root) {
         root.setVariantStateTo(variantNum);
      }

      HashMap<String, EMFModelPart> mapOfAll = new HashMap<>();

      for (ModelPart part : this.children.values()) {
         if (part instanceof EMFModelPart emfPart) {
            String thisKey = "NULL_KEY_NAME";
            boolean addThis = false;
            if (part instanceof EMFModelPartCustom partCustom) {
               thisKey = partCustom.id;
               addThis = true;
            } else if (part instanceof EMFModelPartVanilla partVanilla) {
               thisKey = partVanilla.name;
               addThis = partVanilla.isOptiFinePartSpecified;
            }

            for (Entry<String, String> entry : optifinePartNameMap.entrySet()) {
               if (entry.getValue().equals(thisKey)) {
                  thisKey = entry.getKey();
                  break;
               }
            }

            if (addThis) {
               mapOfAll.putIfAbsent(thisKey, emfPart);
               if (prefixableParents.isBlank()) {
                  mapOfAll.putAll(emfPart.getAllChildPartsAsAnimationMap(thisKey, variantNum, optifinePartNameMap));
               } else {
                  mapOfAll.putIfAbsent(prefixableParents + ":" + thisKey, emfPart);
                  mapOfAll.putAll(emfPart.getAllChildPartsAsAnimationMap(prefixableParents + ":" + thisKey, variantNum, optifinePartNameMap));
               }
            } else {
               mapOfAll.putAll(emfPart.getAllChildPartsAsAnimationMap(prefixableParents, variantNum, optifinePartNameMap));
            }
         }
      }

      return mapOfAll;
   }
}
