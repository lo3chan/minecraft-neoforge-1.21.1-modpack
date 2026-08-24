package software.bernie.geckolib.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.List;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import software.bernie.geckolib.GeckoLibServices;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.texture.AnimatableTexture;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayersContainer;
import software.bernie.geckolib.util.RenderUtil;

public class GeoBlockRenderer<T extends BlockEntity & GeoAnimatable> implements GeoRenderer<T>, BlockEntityRenderer<T> {
   protected final GeoRenderLayersContainer<T> renderLayers = new GeoRenderLayersContainer<>(this);
   protected final GeoModel<T> model;
   protected T animatable;
   protected float scaleWidth = 1.0F;
   protected float scaleHeight = 1.0F;
   protected Matrix4f blockRenderTranslations = new Matrix4f();
   protected Matrix4f modelRenderTranslations = new Matrix4f();

   public GeoBlockRenderer(BlockEntityType<? extends T> blockEntityType) {
      this(new DefaultedBlockGeoModel<>(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntityType)));
   }

   public GeoBlockRenderer(GeoModel<T> model) {
      this.model = model;
   }

   @Override
   public GeoModel<T> getGeoModel() {
      return this.model;
   }

   public T getAnimatable() {
      return this.animatable;
   }

   public long getInstanceId(T animatable) {
      return animatable.getBlockPos().hashCode();
   }

   @Override
   public List<GeoRenderLayer<T>> getRenderLayers() {
      return this.renderLayers.getRenderLayers();
   }

   public GeoBlockRenderer<T> addRenderLayer(GeoRenderLayer<T> renderLayer) {
      this.renderLayers.addLayer(renderLayer);
      return this;
   }

   public GeoBlockRenderer<T> withScale(float scale) {
      return this.withScale(scale, scale);
   }

   public GeoBlockRenderer<T> withScale(float scaleWidth, float scaleHeight) {
      this.scaleWidth = scaleWidth;
      this.scaleHeight = scaleHeight;
      return this;
   }

   public void preRender(
      PoseStack poseStack,
      T animatable,
      BakedGeoModel model,
      @Nullable MultiBufferSource bufferSource,
      @Nullable VertexConsumer buffer,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay,
      int colour
   ) {
      this.blockRenderTranslations = new Matrix4f(poseStack.last().pose());
      if (!isReRender) {
         poseStack.translate(0.5, 0.0, 0.5);
      }

      this.scaleModelForRender(this.scaleWidth, this.scaleHeight, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
   }

   @Internal
   public void render(T animatable, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
      this.animatable = animatable;
      this.defaultRender(poseStack, this.animatable, bufferSource, null, null, 0.0F, partialTick, packedLight);
   }

   public void actuallyRender(
      PoseStack poseStack,
      T animatable,
      BakedGeoModel model,
      @Nullable RenderType renderType,
      MultiBufferSource bufferSource,
      @Nullable VertexConsumer buffer,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay,
      int colour
   ) {
      if (!isReRender) {
         AnimationState<T> animationState = new AnimationState<>(animatable, 0.0F, 0.0F, partialTick, false);
         long instanceId = this.getInstanceId(animatable);
         GeoModel<T> currentModel = this.getGeoModel();
         animationState.setData(DataTickets.TICK, animatable.getTick(animatable));
         animationState.setData(DataTickets.BLOCK_ENTITY, animatable);
         currentModel.addAdditionalStateData(animatable, instanceId, (x$0, x$1) -> animationState.setData(x$0, x$1));
         this.rotateBlock(this.getFacing(animatable), poseStack);
         currentModel.handleAnimations(animatable, instanceId, animationState, partialTick);
      }

      this.modelRenderTranslations = new Matrix4f(poseStack.last().pose());
      if (buffer != null) {
         GeoRenderer.super.actuallyRender(
            poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour
         );
      }
   }

   @Override
   public void doPostRenderCleanup() {
      this.animatable = null;
   }

   public void renderRecursively(
      PoseStack poseStack,
      T animatable,
      GeoBone bone,
      RenderType renderType,
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay,
      int colour
   ) {
      poseStack.pushPose();
      RenderUtil.translateMatrixToBone(poseStack, bone);
      RenderUtil.translateToPivotPoint(poseStack, bone);
      RenderUtil.rotateMatrixAroundBone(poseStack, bone);
      RenderUtil.scaleMatrixForBone(poseStack, bone);
      if (bone.isTrackingMatrices()) {
         Matrix4f poseState = new Matrix4f(poseStack.last().pose());
         Matrix4f localMatrix = RenderUtil.invertAndMultiplyMatrices(poseState, this.blockRenderTranslations);
         Matrix4f worldState = new Matrix4f(localMatrix);
         BlockPos pos = this.animatable.getBlockPos();
         bone.setModelSpaceMatrix(RenderUtil.invertAndMultiplyMatrices(poseState, this.modelRenderTranslations));
         bone.setLocalSpaceMatrix(localMatrix);
         bone.setWorldSpaceMatrix(worldState.translate(new Vector3f(pos.getX(), pos.getY(), pos.getZ())));
      }

      RenderUtil.translateAwayFromPivotPoint(poseStack, bone);
      buffer = this.checkAndRefreshBuffer(isReRender, buffer, bufferSource, renderType);
      this.renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, colour);
      if (!isReRender) {
         this.applyRenderLayersForBone(poseStack, this.getAnimatable(), bone, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
      }

      this.renderChildBones(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
      poseStack.popPose();
   }

   protected void rotateBlock(Direction facing, PoseStack poseStack) {
      switch (facing) {
         case SOUTH:
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            break;
         case WEST:
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            break;
         case NORTH:
            poseStack.mulPose(Axis.YP.rotationDegrees(0.0F));
            break;
         case EAST:
            poseStack.mulPose(Axis.YP.rotationDegrees(270.0F));
            break;
         case UP:
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            break;
         case DOWN:
            poseStack.mulPose(Axis.XN.rotationDegrees(90.0F));
      }
   }

   protected Direction getFacing(T block) {
      BlockState blockState = block.getBlockState();
      if (blockState.hasProperty(HorizontalDirectionalBlock.FACING)) {
         return (Direction)blockState.getValue(HorizontalDirectionalBlock.FACING);
      } else {
         return blockState.hasProperty(DirectionalBlock.FACING) ? (Direction)blockState.getValue(DirectionalBlock.FACING) : Direction.NORTH;
      }
   }

   public void updateAnimatedTextureFrame(T animatable) {
      AnimatableTexture.setAndUpdate(this.getTextureLocation(animatable));
   }

   @Override
   public void fireCompileRenderLayersEvent() {
      GeckoLibServices.Client.EVENTS.fireCompileBlockRenderLayers(this);
   }

   @Override
   public boolean firePreRenderEvent(PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
      return GeckoLibServices.Client.EVENTS.fireBlockPreRender(this, poseStack, model, bufferSource, partialTick, packedLight);
   }

   @Override
   public void firePostRenderEvent(PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
      GeckoLibServices.Client.EVENTS.fireBlockPostRender(this, poseStack, model, bufferSource, partialTick, packedLight);
   }
}
