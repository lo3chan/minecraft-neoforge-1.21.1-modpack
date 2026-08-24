package traben.entity_model_features.mixin.mixins.rendering.feature;

import net.minecraft.client.model.WolfModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.WolfCollarLayer;
import net.minecraft.world.entity.animal.Wolf;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.EMF;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.models.IEMFModel;
import traben.entity_model_features.models.parts.EMFModelPartRoot;
import traben.entity_model_features.utils.EMFUtils;
import traben.entity_model_features.utils.IEMFWolfCollarHolder;

@Mixin({WolfCollarLayer.class})
public abstract class MixinWolfCollarFeatureRenderer extends RenderLayer<Wolf, WolfModel<Wolf>> {
   @Unique
   private static final ModelLayerLocation emf$collar_layer = new ModelLayerLocation(EMFUtils.res("minecraft", "wolf"), "collar");
   @Unique
   private static final ModelLayerLocation emf$collar_layer_baby = new ModelLayerLocation(EMFUtils.res("minecraft", "wolf_baby"), "collar");

   public MixinWolfCollarFeatureRenderer() {
      super(null);
   }

   @Inject(
      method = {"<init>"},
      at = {@At("TAIL")}
   )
   private void setEmf$Model(RenderLayerParent<?, ?> featureRendererContext, CallbackInfo ci) {
      if (!EMF.testForForgeLoadingError()) {
         ModelPart collarModel = EMFManager.getInstance()
            .injectIntoModelRootGetter(emf$collar_layer, WolfModel.createMeshDefinition(CubeDeformation.NONE).getRoot().bake(64, 32));
         if (collarModel instanceof EMFModelPartRoot || ((IEMFModel)featureRendererContext.getModel()).emf$isEMFModel()) {
            try {
               if (featureRendererContext.getModel() instanceof IEMFWolfCollarHolder<?> holder) {
                  holder.emf$setCollarModel(new WolfModel(collarModel));
               }
            } catch (Exception var6) {
            }
         }
      }
   }

   @NotNull
   public WolfModel<Wolf> getParentModel() {
      WolfModel<Wolf> base = (WolfModel<Wolf>)super.getParentModel();
      if (base instanceof IEMFWolfCollarHolder<?> holder && holder.emf$hasCollarModel()) {
         WolfModel<Wolf> model = (WolfModel<Wolf>)holder.emf$getCollarModel();
         model.attackTime = base.attackTime;
         model.riding = base.riding;
         model.young = base.young;
         return model;
      } else {
         return base;
      }
   }
}
