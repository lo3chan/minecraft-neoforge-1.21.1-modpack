package traben.entity_model_features.mixin.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Map;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.models.EMFModel_ID;
import traben.entity_model_features.models.IEMFModelNameContainer;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.utils.IEMFTextureSizeSupplier;

@Mixin({ModelPart.class})
public class MixinModelPart implements IEMFModelNameContainer, IEMFTextureSizeSupplier {
   @Shadow
   public Map<String, ModelPart> children;
   @Unique
   EMFModel_ID emf$modelInfo = null;
   @Unique
   private int[] emf$textureSize = null;

   @Inject(
      method = {"render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"},
      at = {@At("HEAD")}
   )
   private void emf$injectAnnouncerPart(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int j, int k, CallbackInfo ci) {
      if (EMFAnimationEntityContext.doAnnounceModels() && this.emf$modelInfo != null) {
         EMFManager.getInstance().modelsAnnounced.add(this.emf$modelInfo);
      }
   }

   @Override
   public void emf$insertKnownMappings(EMFModel_ID newName) {
      this.emf$modelInfo = newName;
      this.children.values().forEach(part -> ((IEMFModelNameContainer)part).emf$insertKnownMappings(newName));
   }

   @Override
   public int[] emf$getTextureSize() {
      return this.emf$textureSize;
   }

   @Override
   public void emf$setTextureSize(int[] size) {
      this.emf$textureSize = size;
   }
}
