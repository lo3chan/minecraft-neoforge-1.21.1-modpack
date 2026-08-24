package fuzs.puzzleslib.api.client.renderer.v1.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import java.util.function.Function;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

public abstract class RootedEntityModel<E extends Entity> extends HierarchicalModel<E> {
   protected final ModelPart root;
   private final List<ModelPart> allParts;

   public RootedEntityModel(ModelPart root) {
      this.root = root;
      this.allParts = root.getAllParts().toList();
   }

   public RootedEntityModel(ModelPart root, Function<ResourceLocation, RenderType> renderType) {
      super(renderType);
      this.root = root;
      this.allParts = root.getAllParts().toList();
   }

   public final void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
      super.renderToBuffer(poseStack, buffer, packedLight, packedOverlay, color);
   }

   public final ModelPart root() {
      return this.root;
   }

   public final List<ModelPart> allParts() {
      return this.allParts;
   }

   @MustBeInvokedByOverriders
   public void setupAnim(E entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      this.resetPose();
   }

   public final void resetPose() {
      for (ModelPart modelPart : this.allParts) {
         modelPart.resetPose();
      }
   }
}
