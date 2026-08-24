package traben.entity_model_features.models.parts;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.ModelPart.Cube;
import net.minecraft.resources.ResourceLocation;
import traben.entity_model_features.utils.EMFUtils;

public abstract class EMFModelPartWithState extends EMFModelPart {
   public final Map<Integer, EMFModelPartWithState.EMFModelState> allKnownStateVariants = new HashMap<Integer, EMFModelPartWithState.EMFModelState>() {
      public EMFModelPartWithState.EMFModelState get(Object k) {
         EMFModelPartWithState.EMFModelState val = (EMFModelPartWithState.EMFModelState)super.get(k);
         if (val == null) {
            EMFUtils.logWarn(
               "EMFModelState variant with key " + k + " does not exist in part [" + EMFModelPartWithState.this.toStringShort() + "], returning copy of 0"
            );
            val = this.get(0).copy();
            this.put((Integer)k, val);
         }

         return val;
      }
   };
   public int currentModelVariant = 0;
   Map<String, ModelPart> vanillaChildren = new HashMap<>();

   public EMFModelPartWithState(List<Cube> cuboids, Map<String, ModelPart> children, EMFModelPartRoot root) {
      super(cuboids, children, root);
   }

   @Override
   public void render(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int k) {
      EMFModelPartRoot root = this.getRoot();
      root.oneTimeRunnable();
      root.animate();
      super.render(matrices, vertices, light, overlay, k);
   }

   EMFModelPartWithState.EMFModelState getCurrentState() {
      return new EMFModelPartWithState.EMFModelState(
         this.getInitialPose(), this.cubes, this.children, this.xScale, this.yScale, this.zScale, this.visible, this.skipDraw, this.textureOverride
      );
   }

   EMFModelPartWithState.EMFModelState getStateOf(ModelPart modelPart) {
      return modelPart instanceof EMFModelPartWithState emf
         ? new EMFModelPartWithState.EMFModelState(
            modelPart.getInitialPose(),
            modelPart.cubes,
            modelPart.children,
            modelPart.xScale,
            modelPart.yScale,
            modelPart.zScale,
            modelPart.visible,
            modelPart.skipDraw,
            emf.textureOverride
         )
         : new EMFModelPartWithState.EMFModelState(
            modelPart.getInitialPose(),
            modelPart.cubes,
            new HashMap<>(),
            modelPart.xScale,
            modelPart.yScale,
            modelPart.zScale,
            modelPart.visible,
            modelPart.skipDraw,
            null
         );
   }

   void setFromState(EMFModelPartWithState.EMFModelState newState) {
      this.setInitialPose(newState.defaultTransform());
      this.loadPose(this.getInitialPose());
      this.cubes = newState.cuboids();
      this.children = newState.variantChildren();
      this.xScale = newState.xScale();
      this.yScale = newState.yScale();
      this.zScale = newState.zScale();
      this.visible = newState.visible();
      this.skipDraw = newState.hidden();
      this.textureOverride = newState.texture();
   }

   protected void resetState() {
      this.setFromState(this.allKnownStateVariants.get(this.currentModelVariant));
   }

   public void setVariantStateTo(int newVariant) {
      if (this.currentModelVariant != newVariant) {
         this.setFromState(this.allKnownStateVariants.get(newVariant));
         this.currentModelVariant = newVariant;

         for (ModelPart part : this.children.values()) {
            if (part instanceof EMFModelPartWithState p3) {
               p3.setVariantStateTo(newVariant);
            }
         }
      }
   }

   public void copyVariantTo(int from, int to) {
      this.allKnownStateVariants.putIfAbsent(to, this.allKnownStateVariants.get(from).copy());

      for (ModelPart value : this.children.values()) {
         if (value instanceof EMFModelPartWithState p3) {
            p3.copyVariantTo(from, to);
         }
      }
   }

   public record EMFModelState(
      PartPose defaultTransform,
      List<Cube> cuboids,
      Map<String, ModelPart> variantChildren,
      float xScale,
      float yScale,
      float zScale,
      boolean visible,
      boolean hidden,
      ResourceLocation texture
   ) {
      public EMFModelPartWithState.EMFModelState copy() {
         return this.copy(this.visible());
      }

      public EMFModelPartWithState.EMFModelState copy(boolean visibleOverride) {
         PartPose trans = this.defaultTransform();
         return new EMFModelPartWithState.EMFModelState(
            PartPose.offsetAndRotation(trans.x, trans.y, trans.z, trans.xRot, trans.yRot, trans.zRot),
            new ArrayList<>(this.cuboids()),
            new HashMap<>(this.variantChildren()),
            this.xScale(),
            this.yScale(),
            this.zScale(),
            visibleOverride,
            this.hidden(),
            this.texture()
         );
      }
   }
}
