package software.bernie.geckolib.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.joml.Matrix4f;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.GeckoLibServices;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.texture.AnimatableTexture;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.DefaultedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayersContainer;
import software.bernie.geckolib.util.Color;
import software.bernie.geckolib.util.RenderUtil;

public class GeoArmorRenderer<T extends Item & GeoItem> extends HumanoidModel implements GeoRenderer<T> {
   protected final GeoRenderLayersContainer<T> renderLayers = new GeoRenderLayersContainer<>(this);
   protected final GeoModel<T> model;
   protected T animatable;
   protected HumanoidModel<?> baseModel;
   protected float scaleWidth = 1.0F;
   protected float scaleHeight = 1.0F;
   protected Matrix4f entityRenderTranslations = new Matrix4f();
   protected Matrix4f modelRenderTranslations = new Matrix4f();
   protected BakedGeoModel lastModel = null;
   protected GeoBone head = null;
   protected GeoBone body = null;
   protected GeoBone rightArm = null;
   protected GeoBone leftArm = null;
   protected GeoBone rightLeg = null;
   protected GeoBone leftLeg = null;
   protected GeoBone rightBoot = null;
   protected GeoBone leftBoot = null;
   protected Entity currentEntity = null;
   protected ItemStack currentStack = null;
   protected EquipmentSlot currentSlot = null;
   protected MultiBufferSource bufferSource = null;
   protected float partialTick;
   protected float limbSwing;
   protected float limbSwingAmount;
   protected float netHeadYaw;
   protected float headPitch;

   public <I extends T> GeoArmorRenderer(I armorItem) {
      this(new DefaultedGeoModel<T>(BuiltInRegistries.ITEM.getKey(armorItem)) {
         @Override
         protected String subtype() {
            return "armor";
         }
      });
   }

   public GeoArmorRenderer(GeoModel<T> model) {
      super(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_INNER_ARMOR));
      this.model = model;
      this.young = false;
   }

   @Override
   public GeoModel<T> getGeoModel() {
      return this.model;
   }

   public T getAnimatable() {
      return this.animatable;
   }

   public Entity getCurrentEntity() {
      return this.currentEntity;
   }

   public ItemStack getCurrentStack() {
      return this.currentStack;
   }

   public EquipmentSlot getCurrentSlot() {
      return this.currentSlot;
   }

   public long getInstanceId(T animatable) {
      long stackId = GeoItem.getId(this.currentStack);
      return stackId == 9223372036854775807L ? (long)Math.pow(this.currentEntity.getId(), 7.0) * -(this.currentSlot.ordinal() + 1) : -stackId;
   }

   public RenderType getRenderType(T animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
      return RenderType.armorCutoutNoCull(texture);
   }

   @Override
   public List<GeoRenderLayer<T>> getRenderLayers() {
      return this.renderLayers.getRenderLayers();
   }

   public GeoArmorRenderer<T> addRenderLayer(GeoRenderLayer<T> renderLayer) {
      this.renderLayers.addLayer(renderLayer);
      return this;
   }

   public GeoArmorRenderer<T> withScale(float scale) {
      return this.withScale(scale, scale);
   }

   public GeoArmorRenderer<T> withScale(float scaleWidth, float scaleHeight) {
      this.scaleWidth = scaleWidth;
      this.scaleHeight = scaleHeight;
      return this;
   }

   @Nullable
   public GeoBone getHeadBone(GeoModel<T> model) {
      return model.getBone("armorHead").orElse(null);
   }

   @Nullable
   public GeoBone getBodyBone(GeoModel<T> model) {
      return model.getBone("armorBody").orElse(null);
   }

   @Nullable
   public GeoBone getRightArmBone(GeoModel<T> model) {
      return model.getBone("armorRightArm").orElse(null);
   }

   @Nullable
   public GeoBone getLeftArmBone(GeoModel<T> model) {
      return model.getBone("armorLeftArm").orElse(null);
   }

   @Nullable
   public GeoBone getRightLegBone(GeoModel<T> model) {
      return model.getBone("armorRightLeg").orElse(null);
   }

   @Nullable
   public GeoBone getLeftLegBone(GeoModel<T> model) {
      return model.getBone("armorLeftLeg").orElse(null);
   }

   @Nullable
   public GeoBone getRightBootBone(GeoModel<T> model) {
      return model.getBone("armorRightBoot").orElse(null);
   }

   @Nullable
   public GeoBone getLeftBootBone(GeoModel<T> model) {
      return model.getBone("armorLeftBoot").orElse(null);
   }

   public Color getRenderColor(T animatable, float partialTick, int packedLight) {
      return this.currentStack.is(ItemTags.DYEABLE) ? Color.ofOpaque(DyedItemColor.getOrDefault(this.currentStack, -6265536)) : Color.WHITE;
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
      this.entityRenderTranslations = new Matrix4f(poseStack.last().pose());
      this.applyBaseModel(this.baseModel);
      this.grabRelevantBones(model);
      this.applyBaseTransformations(this.baseModel);
      this.scaleModelForBaby(poseStack, animatable, partialTick, isReRender);
      this.scaleModelForRender(this.scaleWidth, this.scaleHeight, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
      if (!(this.currentEntity instanceof GeoAnimatable)) {
         this.applyBoneVisibilityBySlot(this.currentSlot);
      }
   }

   @Internal
   public void renderToBuffer(PoseStack poseStack, @Nullable VertexConsumer buffer, int packedLight, int packedOverlay, int colour) {
      if (this.currentEntity == null) {
         GeckoLibConstants.LOGGER
            .error(
               "A mod is attempting to render GeckoLib armor ({}) without using GeoRenderProvider! IClientItemExtensions is not supported for GeckoLib!",
               this.getClass()
            );
      } else {
         Minecraft mc = Minecraft.getInstance();
         MultiBufferSource bufferSource = mc.levelRenderer.renderBuffers.bufferSource();
         if (mc.levelRenderer.shouldShowEntityOutlines() && mc.shouldEntityAppearGlowing(this.currentEntity)) {
            bufferSource = mc.levelRenderer.renderBuffers.outlineBufferSource();
         }

         float partialTick = mc.getTimer().getGameTimeDeltaPartialTick(true);
         RenderType renderType = this.getRenderType(this.animatable, this.getTextureLocation(this.animatable), bufferSource, partialTick);
         buffer = ItemRenderer.getArmorFoilBuffer(bufferSource, renderType, this.currentStack.hasFoil());
         this.defaultRender(poseStack, this.animatable, bufferSource, null, buffer, 0.0F, partialTick, packedLight);
         this.animatable = null;
      }
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
      poseStack.pushPose();
      poseStack.translate(0.0F, 1.5F, 0.0F);
      poseStack.scale(-1.0F, -1.0F, 1.0F);
      if (!isReRender) {
         AnimationState<T> animationState = new AnimationState<>(animatable, 0.0F, 0.0F, partialTick, false);
         long instanceId = this.getInstanceId(animatable);
         GeoModel<T> currentModel = this.getGeoModel();
         animationState.setData(DataTickets.TICK, animatable.getTick(this.currentEntity));
         animationState.setData(DataTickets.ITEMSTACK, this.currentStack);
         animationState.setData(DataTickets.ENTITY, this.currentEntity);
         animationState.setData(DataTickets.EQUIPMENT_SLOT, this.currentSlot);
         currentModel.addAdditionalStateData(animatable, instanceId, (x$0, x$1) -> animationState.setData(x$0, x$1));
         currentModel.handleAnimations(animatable, instanceId, animationState, partialTick);
      }

      this.modelRenderTranslations = new Matrix4f(poseStack.last().pose());
      if (buffer != null) {
         GeoRenderer.super.actuallyRender(
            poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour
         );
      }

      poseStack.popPose();
   }

   @Override
   public void doPostRenderCleanup() {
      this.baseModel = null;
      this.currentEntity = null;
      this.currentStack = null;
      this.animatable = null;
      this.currentSlot = null;
      this.bufferSource = null;
      this.partialTick = 0.0F;
      this.limbSwing = 0.0F;
      this.limbSwingAmount = 0.0F;
      this.netHeadYaw = 0.0F;
      this.headPitch = 0.0F;
   }

   @Deprecated(
      forRemoval = true
   )
   public void doArmourPostRenderCleanup() {
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
         bone.setLocalSpaceMatrix(RenderUtil.invertAndMultiplyMatrices(poseState, this.entityRenderTranslations));
      }

      GeoRenderer.super.renderRecursively(
         poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour
      );
   }

   protected void grabRelevantBones(BakedGeoModel bakedModel) {
      if (this.lastModel != bakedModel) {
         GeoModel<T> model = this.getGeoModel();
         this.lastModel = bakedModel;
         this.head = this.getHeadBone(model);
         this.body = this.getBodyBone(model);
         this.rightArm = this.getRightArmBone(model);
         this.leftArm = this.getLeftArmBone(model);
         this.rightLeg = this.getRightLegBone(model);
         this.leftLeg = this.getLeftLegBone(model);
         this.rightBoot = this.getRightBootBone(model);
         this.leftBoot = this.getLeftBootBone(model);
      }
   }

   @Deprecated(
      forRemoval = true
   )
   public void prepForRender(@Nullable Entity entity, ItemStack stack, @Nullable EquipmentSlot slot, @Nullable HumanoidModel<?> baseModel) {
      if (entity != null && slot != null && baseModel != null) {
         Minecraft mc = Minecraft.getInstance();
         this.prepForRender(
            entity,
            stack,
            slot,
            baseModel,
            mc.levelRenderer.renderBuffers.bufferSource(),
            mc.getTimer().getGameTimeDeltaPartialTick(true),
            0.0F,
            0.0F,
            0.0F,
            0.0F
         );
      }
   }

   public void prepForRender(
      Entity entity,
      ItemStack stack,
      EquipmentSlot slot,
      HumanoidModel<?> baseModel,
      MultiBufferSource bufferSource,
      float partialTick,
      float limbSwing,
      float limbSwingAmount,
      float netHeadYaw,
      float headPitch
   ) {
      this.baseModel = baseModel;
      this.currentEntity = entity;
      this.currentStack = stack;
      this.animatable = (T)stack.getItem();
      this.currentSlot = slot;
      this.bufferSource = bufferSource;
      this.partialTick = partialTick;
      this.limbSwing = limbSwing;
      this.limbSwingAmount = limbSwingAmount;
      this.netHeadYaw = netHeadYaw;
      this.headPitch = headPitch;
   }

   protected void applyBaseModel(HumanoidModel<?> baseModel) {
      super.young = baseModel.young;
      super.crouching = baseModel.crouching;
      super.riding = baseModel.riding;
      super.rightArmPose = baseModel.rightArmPose;
      super.leftArmPose = baseModel.leftArmPose;
      super.head.visible = baseModel.head.visible;
      super.hat.visible = baseModel.hat.visible;
      super.body.visible = baseModel.body.visible;
      super.rightArm.visible = baseModel.rightArm.visible;
      super.leftArm.visible = baseModel.leftArm.visible;
      super.rightLeg.visible = baseModel.rightLeg.visible;
      super.leftLeg.visible = baseModel.leftLeg.visible;
   }

   protected void applyBoneVisibilityBySlot(EquipmentSlot currentSlot) {
      this.setAllBonesVisible(false);
      switch (currentSlot) {
         case HEAD:
            this.setBoneVisible(this.head, super.head.visible);
            break;
         case CHEST:
            this.setBoneVisible(this.body, super.body.visible);
            this.setBoneVisible(this.rightArm, super.rightArm.visible);
            this.setBoneVisible(this.leftArm, super.leftArm.visible);
            break;
         case LEGS:
            this.setBoneVisible(this.rightLeg, super.rightLeg.visible);
            this.setBoneVisible(this.leftLeg, super.leftLeg.visible);
            break;
         case FEET:
            this.setBoneVisible(this.rightBoot, super.rightLeg.visible);
            this.setBoneVisible(this.leftBoot, super.leftLeg.visible);
      }
   }

   public void applyBoneVisibilityByPart(EquipmentSlot currentSlot, ModelPart currentPart, HumanoidModel<?> model) {
      this.setAllVisible(false);
      currentPart.visible = true;
      GeoBone bone = null;
      if (currentPart == model.hat || currentPart == model.head) {
         bone = this.head;
      } else if (currentPart == model.body) {
         bone = this.body;
      } else if (currentPart == model.leftArm) {
         bone = this.leftArm;
      } else if (currentPart == model.rightArm) {
         bone = this.rightArm;
      } else if (currentPart == model.leftLeg) {
         bone = currentSlot == EquipmentSlot.FEET ? this.leftBoot : this.leftLeg;
      } else if (currentPart == model.rightLeg) {
         bone = currentSlot == EquipmentSlot.FEET ? this.rightBoot : this.rightLeg;
      }

      if (bone != null) {
         bone.setHidden(false);
      }
   }

   protected void applyBaseTransformations(HumanoidModel<?> baseModel) {
      if (this.head != null) {
         ModelPart headPart = baseModel.head;
         RenderUtil.matchModelPartRot(headPart, this.head);
         this.head.updatePosition(headPart.x, -headPart.y, headPart.z);
      }

      if (this.body != null) {
         ModelPart bodyPart = baseModel.body;
         RenderUtil.matchModelPartRot(bodyPart, this.body);
         this.body.updatePosition(bodyPart.x, -bodyPart.y, bodyPart.z);
      }

      if (this.rightArm != null) {
         ModelPart rightArmPart = baseModel.rightArm;
         RenderUtil.matchModelPartRot(rightArmPart, this.rightArm);
         this.rightArm.updatePosition(rightArmPart.x + 5.0F, 2.0F - rightArmPart.y, rightArmPart.z);
      }

      if (this.leftArm != null) {
         ModelPart leftArmPart = baseModel.leftArm;
         RenderUtil.matchModelPartRot(leftArmPart, this.leftArm);
         this.leftArm.updatePosition(leftArmPart.x - 5.0F, 2.0F - leftArmPart.y, leftArmPart.z);
      }

      if (this.rightLeg != null) {
         ModelPart rightLegPart = baseModel.rightLeg;
         RenderUtil.matchModelPartRot(rightLegPart, this.rightLeg);
         this.rightLeg.updatePosition(rightLegPart.x + 2.0F, 12.0F - rightLegPart.y, rightLegPart.z);
         if (this.rightBoot != null) {
            RenderUtil.matchModelPartRot(rightLegPart, this.rightBoot);
            this.rightBoot.updatePosition(rightLegPart.x + 2.0F, 12.0F - rightLegPart.y, rightLegPart.z);
         }
      }

      if (this.leftLeg != null) {
         ModelPart leftLegPart = baseModel.leftLeg;
         RenderUtil.matchModelPartRot(leftLegPart, this.leftLeg);
         this.leftLeg.updatePosition(leftLegPart.x - 2.0F, 12.0F - leftLegPart.y, leftLegPart.z);
         if (this.leftBoot != null) {
            RenderUtil.matchModelPartRot(leftLegPart, this.leftBoot);
            this.leftBoot.updatePosition(leftLegPart.x - 2.0F, 12.0F - leftLegPart.y, leftLegPart.z);
         }
      }
   }

   public void setAllVisible(boolean visible) {
      super.setAllVisible(visible);
      this.setAllBonesVisible(visible);
   }

   protected void setAllBonesVisible(boolean visible) {
      this.setBoneVisible(this.head, visible);
      this.setBoneVisible(this.body, visible);
      this.setBoneVisible(this.rightArm, visible);
      this.setBoneVisible(this.leftArm, visible);
      this.setBoneVisible(this.rightLeg, visible);
      this.setBoneVisible(this.leftLeg, visible);
      this.setBoneVisible(this.rightBoot, visible);
      this.setBoneVisible(this.leftBoot, visible);
   }

   public void scaleModelForBaby(PoseStack poseStack, T animatable, float partialTick, boolean isReRender) {
      if (this.young && !isReRender) {
         if (this.currentSlot == EquipmentSlot.HEAD) {
            if (this.baseModel.scaleHead) {
               float headScale = 1.5F / this.baseModel.babyHeadScale;
               poseStack.scale(headScale, headScale, headScale);
            }

            poseStack.translate(0.0F, this.baseModel.babyYHeadOffset / 16.0F, this.baseModel.babyZHeadOffset / 16.0F);
         } else {
            float bodyScale = 1.0F / this.baseModel.babyBodyScale;
            poseStack.scale(bodyScale, bodyScale, bodyScale);
            poseStack.translate(0.0F, this.baseModel.bodyYOffset / 16.0F, 0.0F);
         }
      }
   }

   protected void setBoneVisible(@Nullable GeoBone bone, boolean visible) {
      if (bone != null) {
         bone.setHidden(!visible);
      }
   }

   public void updateAnimatedTextureFrame(T animatable) {
      if (this.currentEntity != null) {
         AnimatableTexture.setAndUpdate(this.getTextureLocation(animatable));
      }
   }

   @Override
   public void fireCompileRenderLayersEvent() {
      GeckoLibServices.Client.EVENTS.fireCompileArmorRenderLayers(this);
   }

   @Override
   public boolean firePreRenderEvent(PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
      return GeckoLibServices.Client.EVENTS.fireArmorPreRender(this, poseStack, model, bufferSource, partialTick, packedLight);
   }

   @Override
   public void firePostRenderEvent(PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
      GeckoLibServices.Client.EVENTS.fireArmorPostRender(this, poseStack, model, bufferSource, partialTick, packedLight);
   }
}
