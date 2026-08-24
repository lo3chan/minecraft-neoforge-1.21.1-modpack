package traben.entity_model_features.mixin.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.EMF;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.parts.EMFModelPartRoot;
import traben.entity_model_features.utils.EMFUtils;

@Mixin(
   value = {PlayerModel.class},
   priority = 2000
)
public abstract class MixinCapeModels {
   @Unique
   private ModelPart emf$capeModelPart = null;

   @Inject(
      method = {"<init>"},
      at = {@At("TAIL")}
   )
   private void setEmf$Model(CallbackInfo ci) {
      if (!EMF.testForForgeLoadingError()) {
         ModelLayerLocation layer = new ModelLayerLocation(EMFUtils.res("minecraft", "player"), "cape");
         ModelPart capeModel = EMFManager.getInstance()
            .injectIntoModelRootGetter(layer, PlayerModel.createMesh(CubeDeformation.NONE, false).getRoot().bake(64, 64));
         if (capeModel instanceof EMFModelPartRoot && capeModel.hasChild("cloak")) {
            this.emf$capeModelPart = capeModel.getChild("cloak");
         }
      }
   }

   @Inject(
      method = {"renderCloak"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void emf$RenderCustomModelOnly(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int j, CallbackInfo ci) {
      if (this.emf$capeModelPart != null) {
         poseStack.popPose();
         poseStack.pushPose();
         if (!(EMFAnimationEntityContext.getEMFEntity() instanceof Player player)) {
            return;
         }

         if (player.getItemBySlot(EquipmentSlot.CHEST).is(ItemTags.CHEST_ARMOR)) {
            poseStack.translate(0.0F, -0.0625F, 0.1875F);
         } else {
            poseStack.translate(0.0F, 0.0F, 0.125F);
         }

         poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
         this.emf$capeModelPart.render(poseStack, vertexConsumer, i, j);
         ci.cancel();
      }
   }
}
