package fuzs.puzzleslib.api.client.renderer.v1;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.puzzleslib.api.client.renderer.v1.model.RootedModel;
import java.util.Objects;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public abstract class SingleChestRenderer<T extends BlockEntity & LidBlockEntity, M extends SingleChestRenderer.ChestModel> extends ChestRenderer<T> {
   protected final M model;
   @Nullable
   private T blockEntity;
   @Nullable
   private Float partialTick;
   @Nullable
   private MultiBufferSource bufferSource;

   protected SingleChestRenderer(Context context, M model) {
      super(context);
      this.model = model;
   }

   public final void render(T blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
      this.blockEntity = blockEntity;
      this.partialTick = partialTick;
      this.bufferSource = bufferSource;
      super.render(blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
      this.blockEntity = null;
      this.partialTick = null;
      this.bufferSource = null;
   }

   @Internal
   protected final void render(
      PoseStack poseStack,
      VertexConsumer consumer,
      ModelPart lidPart,
      ModelPart lockPart,
      ModelPart bottomPart,
      float openness,
      int packedLight,
      int packedOverlay
   ) {
      Objects.requireNonNull(this.blockEntity, "block entity is null");
      Objects.requireNonNull(this.partialTick, "partial tick is null");
      Objects.requireNonNull(this.bufferSource, "buffer source is null");
      this.model.setupAnim(openness);
      this.renderModel(this.blockEntity, this.partialTick, poseStack, this.bufferSource, packedLight, packedOverlay);
   }

   protected void renderModel(T blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
      VertexConsumer vertexConsumer = this.getChestMaterial(blockEntity, this.getXmasTextures()).buffer(bufferSource, RenderType::entityCutout);
      this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay);
   }

   protected abstract Material getChestMaterial(T var1, boolean var2);

   protected boolean getXmasTextures() {
      return this.xmasTextures;
   }

   public static class ChestModel extends RootedModel {
      private final ModelPart lid;
      private final ModelPart lock;

      public ChestModel(ModelPart root) {
         super(root, RenderType::entitySolid);
         this.lid = root.getChild("lid");
         this.lock = root.getChild("lock");
      }

      public void setupAnim(float openness) {
         this.resetPose();
         this.lid.xRot = -(openness * 1.5707964F);
         this.lock.xRot = this.lid.xRot;
      }
   }
}
