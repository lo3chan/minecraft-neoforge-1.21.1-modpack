package software.bernie.geckolib.renderer;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.joml.Matrix4f;
import software.bernie.geckolib.GeckoLibServices;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.texture.AnimatableTexture;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayersContainer;
import software.bernie.geckolib.util.RenderUtil;

public class GeoItemRenderer<T extends Item & GeoAnimatable> extends BlockEntityWithoutLevelRenderer implements GeoRenderer<T> {
   protected final GeoRenderLayersContainer<T> renderLayers = new GeoRenderLayersContainer<>(this);
   protected final GeoModel<T> model;
   protected ItemStack currentItemStack;
   protected ItemDisplayContext renderPerspective;
   protected T animatable;
   protected float scaleWidth = 1.0F;
   protected float scaleHeight = 1.0F;
   protected boolean useEntityGuiLighting = false;
   protected Matrix4f itemRenderTranslations = new Matrix4f();
   protected Matrix4f modelRenderTranslations = new Matrix4f();

   public <I extends T> GeoItemRenderer(I item) {
      this(new DefaultedItemGeoModel<>(BuiltInRegistries.ITEM.getKey(item)));
   }

   public GeoItemRenderer(GeoModel<T> model) {
      this(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels(), model);
   }

   public GeoItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet, GeoModel<T> model) {
      super(dispatcher, modelSet);
      this.model = model;
   }

   @Override
   public GeoModel<T> getGeoModel() {
      return this.model;
   }

   public T getAnimatable() {
      return this.animatable;
   }

   public ItemStack getCurrentItemStack() {
      return this.currentItemStack;
   }

   public GeoItemRenderer<T> useAlternateGuiLighting() {
      this.useEntityGuiLighting = true;
      return this;
   }

   public long getInstanceId(T animatable) {
      return GeoItem.getId(this.currentItemStack);
   }

   public ResourceLocation getTextureLocation(T animatable) {
      return GeoRenderer.super.getTextureLocation(animatable);
   }

   @Override
   public List<GeoRenderLayer<T>> getRenderLayers() {
      return this.renderLayers.getRenderLayers();
   }

   public GeoItemRenderer<T> addRenderLayer(GeoRenderLayer<T> renderLayer) {
      this.renderLayers.addLayer(renderLayer);
      return this;
   }

   public GeoItemRenderer<T> withScale(float scale) {
      return this.withScale(scale, scale);
   }

   public GeoItemRenderer<T> withScale(float scaleWidth, float scaleHeight) {
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
      this.itemRenderTranslations = new Matrix4f(poseStack.last().pose());
      this.scaleModelForRender(this.scaleWidth, this.scaleHeight, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
      if (!isReRender) {
         poseStack.translate(0.5F, 0.51F, 0.5F);
      }
   }

   @Internal
   public void renderByItem(
      ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay
   ) {
      this.animatable = (T)stack.getItem();
      this.currentItemStack = stack;
      this.renderPerspective = transformType;
      float partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
      if (transformType == ItemDisplayContext.GUI) {
         this.renderInGui(transformType, poseStack, bufferSource, packedLight, packedOverlay, partialTick);
      } else {
         RenderType renderType = this.getRenderType(this.animatable, this.getTextureLocation(this.animatable), bufferSource, partialTick);
         VertexConsumer buffer = ItemRenderer.getFoilBufferDirect(
            bufferSource, renderType, false, this.currentItemStack != null && this.currentItemStack.hasFoil()
         );
         this.defaultRender(poseStack, this.animatable, bufferSource, renderType, buffer, 0.0F, partialTick, packedLight);
      }

      this.animatable = null;
   }

   protected void renderInGui(
      ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, float partialTick
   ) {
      this.setupLightingForGuiRender();
      BufferSource defaultBufferSource = bufferSource instanceof BufferSource bufferSource2
         ? bufferSource2
         : Minecraft.getInstance().levelRenderer.renderBuffers.bufferSource();
      RenderType renderType = this.getRenderType(this.animatable, this.getTextureLocation(this.animatable), defaultBufferSource, partialTick);
      VertexConsumer buffer = ItemRenderer.getFoilBufferDirect(bufferSource, renderType, true, this.currentItemStack != null && this.currentItemStack.hasFoil());
      poseStack.pushPose();
      this.defaultRender(poseStack, this.animatable, defaultBufferSource, renderType, buffer, 0.0F, partialTick, packedLight);
      defaultBufferSource.endBatch();
      RenderSystem.enableDepthTest();
      Lighting.setupFor3DItems();
      poseStack.popPose();
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
         animationState.setData(DataTickets.TICK, animatable.getTick(this.currentItemStack));
         animationState.setData(DataTickets.ITEM_RENDER_PERSPECTIVE, this.renderPerspective);
         animationState.setData(DataTickets.ITEMSTACK, this.currentItemStack);
         animatable.getAnimatableInstanceCache().getManagerForId(instanceId).setData(DataTickets.ITEM_RENDER_PERSPECTIVE, this.renderPerspective);
         currentModel.addAdditionalStateData(animatable, instanceId, (x$0, x$1) -> animationState.setData(x$0, x$1));
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
      this.currentItemStack = null;
      this.renderPerspective = null;
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
      if (bone.isTrackingMatrices()) {
         Matrix4f poseState = new Matrix4f(poseStack.last().pose());
         bone.setModelSpaceMatrix(RenderUtil.invertAndMultiplyMatrices(poseState, this.modelRenderTranslations));
         bone.setLocalSpaceMatrix(RenderUtil.invertAndMultiplyMatrices(poseState, this.itemRenderTranslations));
      }

      GeoRenderer.super.renderRecursively(
         poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour
      );
   }

   public void setupLightingForGuiRender() {
      if (this.useEntityGuiLighting) {
         Lighting.setupForEntityInInventory();
      } else {
         Lighting.setupForFlatItems();
      }
   }

   public void updateAnimatedTextureFrame(T animatable) {
      AnimatableTexture.setAndUpdate(this.getTextureLocation(animatable));
   }

   @Override
   public void fireCompileRenderLayersEvent() {
      GeckoLibServices.Client.EVENTS.fireCompileItemRenderLayers(this);
   }

   @Override
   public boolean firePreRenderEvent(PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
      return GeckoLibServices.Client.EVENTS.fireItemPreRender(this, poseStack, model, bufferSource, partialTick, packedLight);
   }

   @Override
   public void firePostRenderEvent(PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
      GeckoLibServices.Client.EVENTS.fireItemPostRender(this, poseStack, model, bufferSource, partialTick, packedLight);
   }
}
