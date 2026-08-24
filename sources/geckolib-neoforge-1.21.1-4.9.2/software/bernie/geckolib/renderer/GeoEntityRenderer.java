package software.bernie.geckolib.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.joml.Matrix4f;
import software.bernie.geckolib.GeckoLibServices;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.texture.AnimatableTexture;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayersContainer;
import software.bernie.geckolib.util.ClientUtil;
import software.bernie.geckolib.util.Color;
import software.bernie.geckolib.util.RenderUtil;

public class GeoEntityRenderer<T extends Entity & GeoAnimatable> extends EntityRenderer<T> implements GeoRenderer<T> {
   protected final GeoRenderLayersContainer<T> renderLayers = new GeoRenderLayersContainer<>(this);
   protected final GeoModel<T> model;
   protected T animatable;
   protected float scaleWidth = 1.0F;
   protected float scaleHeight = 1.0F;
   protected Matrix4f entityRenderTranslations = new Matrix4f();
   protected Matrix4f modelRenderTranslations = new Matrix4f();

   public GeoEntityRenderer(Context context, EntityType<? extends T> entityType) {
      this(context, new DefaultedEntityGeoModel<>(BuiltInRegistries.ENTITY_TYPE.getKey(entityType)));
   }

   public GeoEntityRenderer(Context renderManager, GeoModel<T> model) {
      super(renderManager);
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
      return animatable.getId();
   }

   public ResourceLocation getTextureLocation(T animatable) {
      return GeoRenderer.super.getTextureLocation(animatable);
   }

   @Override
   public List<GeoRenderLayer<T>> getRenderLayers() {
      return this.renderLayers.getRenderLayers();
   }

   public GeoEntityRenderer<T> addRenderLayer(GeoRenderLayer<T> renderLayer) {
      this.renderLayers.addLayer(renderLayer);
      return this;
   }

   public GeoEntityRenderer<T> withScale(float scale) {
      return this.withScale(scale, scale);
   }

   public GeoEntityRenderer<T> withScale(float scaleWidth, float scaleHeight) {
      this.scaleWidth = scaleWidth;
      this.scaleHeight = scaleHeight;
      return this;
   }

   public Color getRenderColor(T animatable, float partialTick, int packedLight) {
      Color color = GeoRenderer.super.getRenderColor(animatable, partialTick, packedLight);
      if (animatable.isInvisible() && !animatable.isInvisibleTo(ClientUtil.getClientPlayer())) {
         color = Color.ofARGB(Mth.ceil(color.getAlpha() * 38 / 255.0F), color.getRed(), color.getGreen(), color.getBlue());
      }

      return color;
   }

   @Nullable
   public RenderType getRenderType(T animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
      boolean invisible = animatable.isInvisible();
      if (invisible && !animatable.isInvisibleTo(ClientUtil.getClientPlayer())) {
         return RenderType.itemEntityTranslucentCull(texture);
      } else if (!invisible) {
         return GeoRenderer.super.getRenderType(animatable, texture, bufferSource, partialTick);
      } else {
         return Minecraft.getInstance().shouldEntityAppearGlowing(animatable) ? RenderType.outline(texture) : null;
      }
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
      this.scaleModelForRender(this.scaleWidth, this.scaleHeight, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
   }

   @Internal
   public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
      this.animatable = entity;
      this.defaultRender(poseStack, entity, bufferSource, null, null, entityYaw, partialTick, packedLight);
      this.animatable = null;
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
      LivingEntity livingEntity = animatable instanceof LivingEntity entity ? entity : null;
      boolean shouldSit = animatable.isPassenger() && animatable.getVehicle() != null;
      float lerpBodyRot = livingEntity == null ? 0.0F : Mth.rotLerp(partialTick, livingEntity.yBodyRotO, livingEntity.yBodyRot);
      float lerpHeadRot = livingEntity == null ? 0.0F : Mth.rotLerp(partialTick, livingEntity.yHeadRotO, livingEntity.yHeadRot);
      float netHeadYaw = lerpHeadRot - lerpBodyRot;
      if (shouldSit && animatable.getVehicle() instanceof LivingEntity livingentity) {
         lerpBodyRot = Mth.rotLerp(partialTick, livingentity.yBodyRotO, livingentity.yBodyRot);
         netHeadYaw = lerpHeadRot - lerpBodyRot;
         float clampedHeadYaw = Mth.clamp(Mth.wrapDegrees(netHeadYaw), -85.0F, 85.0F);
         lerpBodyRot = lerpHeadRot - clampedHeadYaw;
         if (clampedHeadYaw * clampedHeadYaw > 2500.0F) {
            lerpBodyRot += clampedHeadYaw * 0.2F;
         }

         netHeadYaw = lerpHeadRot - lerpBodyRot;
      }

      if (animatable.getPose() == Pose.SLEEPING && livingEntity != null) {
         Direction bedDirection = livingEntity.getBedOrientation();
         if (bedDirection != null) {
            float eyePosOffset = livingEntity.getEyeHeight(Pose.STANDING) - 0.1F;
            poseStack.translate(-bedDirection.getStepX() * eyePosOffset, 0.0F, -bedDirection.getStepZ() * eyePosOffset);
         }
      }

      float nativeScale = livingEntity != null ? livingEntity.getScale() : 1.0F;
      float ageInTicks = animatable.tickCount + partialTick;
      float limbSwingAmount = 0.0F;
      float limbSwing = 0.0F;
      poseStack.scale(nativeScale, nativeScale, nativeScale);
      this.applyRotations(animatable, poseStack, ageInTicks, lerpBodyRot, partialTick, nativeScale);
      if (!shouldSit && animatable.isAlive() && livingEntity != null) {
         limbSwingAmount = livingEntity.walkAnimation.speed(partialTick);
         limbSwing = livingEntity.walkAnimation.position(partialTick);
         if (livingEntity.isBaby()) {
            limbSwing *= 3.0F;
         }

         if (limbSwingAmount > 1.0F) {
            limbSwingAmount = 1.0F;
         }
      }

      if (!isReRender) {
         float headPitch = Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot());
         float motionThreshold = this.getMotionAnimThreshold(animatable);
         Vec3 velocity = animatable.getDeltaMovement();
         float avgVelocity = (float)((Math.abs(velocity.x) + Math.abs(velocity.z)) / 2.0);
         AnimationState<T> animationState = new AnimationState<>(
            animatable, limbSwing, limbSwingAmount, partialTick, avgVelocity >= motionThreshold && limbSwingAmount != 0.0F
         );
         long instanceId = this.getInstanceId(animatable);
         GeoModel<T> currentModel = this.getGeoModel();
         animationState.setData(DataTickets.TICK, animatable.getTick(animatable));
         animationState.setData(DataTickets.ENTITY, animatable);
         animationState.setData(
            DataTickets.ENTITY_MODEL_DATA, new EntityModelData(shouldSit, livingEntity != null && livingEntity.isBaby(), -netHeadYaw, -headPitch)
         );
         currentModel.addAdditionalStateData(animatable, instanceId, (x$0, x$1) -> animationState.setData(x$0, x$1));
         currentModel.handleAnimations(animatable, instanceId, animationState, partialTick);
      }

      poseStack.translate(0.0F, 0.01F, 0.0F);
      this.modelRenderTranslations = new Matrix4f(poseStack.last().pose());
      if (buffer != null) {
         GeoRenderer.super.actuallyRender(
            poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour
         );
      }

      poseStack.popPose();
   }

   public void applyRenderLayers(
      PoseStack poseStack,
      T animatable,
      BakedGeoModel model,
      @Nullable RenderType renderType,
      MultiBufferSource bufferSource,
      @Nullable VertexConsumer buffer,
      float partialTick,
      int packedLight,
      int packedOverlay
   ) {
      if (!animatable.isSpectator()) {
         GeoRenderer.super.applyRenderLayers(poseStack, animatable, model, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
      }
   }

   public void renderFinal(
      PoseStack poseStack,
      T animatable,
      BakedGeoModel model,
      MultiBufferSource bufferSource,
      @Nullable VertexConsumer buffer,
      float partialTick,
      int packedLight,
      int packedOverlay,
      int colour
   ) {
      super.render(animatable, 0.0F, partialTick, poseStack, bufferSource, packedLight);
      if (animatable instanceof Mob mob) {
         Entity leashHolder = mob.getLeashHolder();
         if (leashHolder != null) {
            this.renderLeash(mob, partialTick, poseStack, bufferSource, leashHolder);
         }
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
         Matrix4f localMatrix = RenderUtil.invertAndMultiplyMatrices(poseState, this.entityRenderTranslations);
         bone.setModelSpaceMatrix(RenderUtil.invertAndMultiplyMatrices(poseState, this.modelRenderTranslations));
         bone.setLocalSpaceMatrix(RenderUtil.translateMatrix(localMatrix, this.getRenderOffset(this.animatable, 1.0F).toVector3f()));
         bone.setWorldSpaceMatrix(RenderUtil.translateMatrix(new Matrix4f(localMatrix), this.animatable.position().toVector3f()));
      }

      RenderUtil.translateAwayFromPivotPoint(poseStack, bone);
      buffer = this.checkAndRefreshBuffer(isReRender, buffer, bufferSource, renderType);
      this.renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, colour);
      if (!isReRender) {
         this.applyRenderLayersForBone(poseStack, animatable, bone, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
      }

      this.renderChildBones(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
      poseStack.popPose();
   }

   @Deprecated(
      forRemoval = true
   )
   protected void applyRotations(T animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
      this.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick, 1.0F);
   }

   protected void applyRotations(T animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
      if (this.isShaking(animatable)) {
         rotationYaw += (float)(Math.cos(animatable.tickCount * 3.25) * 3.141592653589793 * 0.4);
      }

      if (!animatable.hasPose(Pose.SLEEPING)) {
         poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - rotationYaw));
      }

      if (animatable instanceof LivingEntity livingEntity) {
         if (livingEntity.deathTime > 0) {
            float deathRotation = (livingEntity.deathTime + partialTick - 1.0F) / 20.0F * 1.6F;
            poseStack.mulPose(Axis.ZP.rotationDegrees(Math.min(Mth.sqrt(deathRotation), 1.0F) * this.getDeathMaxRotation(animatable)));
         } else if (livingEntity.isAutoSpinAttack()) {
            poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F - livingEntity.getXRot()));
            poseStack.mulPose(Axis.YP.rotationDegrees((livingEntity.tickCount + partialTick) * -75.0F));
         } else if (animatable.hasPose(Pose.SLEEPING)) {
            Direction bedOrientation = livingEntity.getBedOrientation();
            poseStack.mulPose(Axis.YP.rotationDegrees(bedOrientation != null ? RenderUtil.getDirectionAngle(bedOrientation) : rotationYaw));
            poseStack.mulPose(Axis.ZP.rotationDegrees(this.getDeathMaxRotation(animatable)));
            poseStack.mulPose(Axis.YP.rotationDegrees(270.0F));
         } else if (LivingEntityRenderer.isEntityUpsideDown(livingEntity)) {
            poseStack.translate(0.0F, (animatable.getBbHeight() + 0.1F) / nativeScale, 0.0F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
         }
      }
   }

   protected float getDeathMaxRotation(T animatable) {
      return 90.0F;
   }

   public double getNameRenderCutoffDistance(T animatable) {
      return animatable.isDiscrete() ? 32.0 : 64.0;
   }

   public boolean shouldShowName(T animatable) {
      if (!(animatable instanceof LivingEntity)) {
         return super.shouldShowName(animatable);
      } else {
         double nameRenderCutoff = this.getNameRenderCutoffDistance(animatable);
         if (this.entityRenderDispatcher.distanceToSqr(animatable) >= nameRenderCutoff * nameRenderCutoff) {
            return false;
         } else if (!(animatable instanceof Mob)
            || animatable.shouldShowName()
            || animatable.hasCustomName() && animatable == this.entityRenderDispatcher.crosshairPickEntity) {
            Minecraft minecraft = Minecraft.getInstance();
            boolean visibleToClient = !animatable.isInvisibleTo(minecraft.player);
            Team entityTeam = animatable.getTeam();
            if (entityTeam == null) {
               return Minecraft.renderNames() && animatable != minecraft.getCameraEntity() && visibleToClient && !animatable.isVehicle();
            } else {
               Team playerTeam = minecraft.player.getTeam();

               return switch (entityTeam.getNameTagVisibility()) {
                  case ALWAYS -> visibleToClient;
                  case NEVER -> false;
                  case HIDE_FOR_OTHER_TEAMS -> playerTeam == null
                     ? visibleToClient
                     : entityTeam.isAlliedTo(playerTeam) && (entityTeam.canSeeFriendlyInvisibles() || visibleToClient);
                  case HIDE_FOR_OWN_TEAM -> playerTeam == null ? visibleToClient : !entityTeam.isAlliedTo(playerTeam) && visibleToClient;
                  default -> throw new MatchException(null, null);
               };
            }
         } else {
            return false;
         }
      }
   }

   public int getPackedOverlay(T animatable, float u, float partialTick) {
      return !(animatable instanceof LivingEntity entity)
         ? OverlayTexture.NO_OVERLAY
         : OverlayTexture.pack(OverlayTexture.u(u), OverlayTexture.v(entity.hurtTime > 0 || entity.deathTime > 0));
   }

   public boolean isShaking(T animatable) {
      return animatable.isFullyFrozen();
   }

   public <E extends Entity, M extends Mob> void renderLeash(M mob, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, E leashHolder) {
      double lerpBodyAngle = Mth.lerp(partialTick, mob.yBodyRotO, mob.yBodyRot) * 0.017453292F + 1.5707964F;
      Vec3 leashOffset = mob.getLeashOffset(partialTick);
      double xAngleOffset = Math.cos(lerpBodyAngle) * leashOffset.z + Math.sin(lerpBodyAngle) * leashOffset.x;
      double zAngleOffset = Math.sin(lerpBodyAngle) * leashOffset.z - Math.cos(lerpBodyAngle) * leashOffset.x;
      double lerpOriginX = Mth.lerp(partialTick, mob.xo, mob.getX()) + xAngleOffset;
      double lerpOriginY = Mth.lerp(partialTick, mob.yo, mob.getY()) + leashOffset.y;
      double lerpOriginZ = Mth.lerp(partialTick, mob.zo, mob.getZ()) + zAngleOffset;
      Vec3 ropeGripPosition = leashHolder.getRopeHoldPosition(partialTick);
      float xDif = (float)(ropeGripPosition.x - lerpOriginX);
      float yDif = (float)(ropeGripPosition.y - lerpOriginY);
      float zDif = (float)(ropeGripPosition.z - lerpOriginZ);
      float offsetMod = Mth.invSqrt(xDif * xDif + zDif * zDif) * 0.025F / 2.0F;
      float xOffset = zDif * offsetMod;
      float zOffset = xDif * offsetMod;
      VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.leash());
      BlockPos entityEyePos = BlockPos.containing(mob.getEyePosition(partialTick));
      BlockPos holderEyePos = BlockPos.containing(leashHolder.getEyePosition(partialTick));
      int entityBlockLight = this.getBlockLightLevel(mob, entityEyePos);
      int holderBlockLight = leashHolder.isOnFire() ? 15 : leashHolder.level().getBrightness(LightLayer.BLOCK, holderEyePos);
      int entitySkyLight = mob.level().getBrightness(LightLayer.SKY, entityEyePos);
      int holderSkyLight = mob.level().getBrightness(LightLayer.SKY, holderEyePos);
      poseStack.pushPose();
      poseStack.translate(xAngleOffset, leashOffset.y, zAngleOffset);
      Matrix4f posMatrix = new Matrix4f(poseStack.last().pose());

      for (int segment = 0; segment <= 24; segment++) {
         renderLeashPiece(
            vertexConsumer,
            posMatrix,
            xDif,
            yDif,
            zDif,
            entityBlockLight,
            holderBlockLight,
            entitySkyLight,
            holderSkyLight,
            0.025F,
            0.025F,
            xOffset,
            zOffset,
            segment,
            false
         );
      }

      for (int segment = 24; segment >= 0; segment--) {
         renderLeashPiece(
            vertexConsumer,
            posMatrix,
            xDif,
            yDif,
            zDif,
            entityBlockLight,
            holderBlockLight,
            entitySkyLight,
            holderSkyLight,
            0.025F,
            0.0F,
            xOffset,
            zOffset,
            segment,
            true
         );
      }

      poseStack.popPose();
   }

   private static void renderLeashPiece(
      VertexConsumer buffer,
      Matrix4f positionMatrix,
      float xDif,
      float yDif,
      float zDif,
      int entityBlockLight,
      int holderBlockLight,
      int entitySkyLight,
      int holderSkyLight,
      float width,
      float yOffset,
      float xOffset,
      float zOffset,
      int segment,
      boolean isLeashKnot
   ) {
      float piecePosPercent = segment / 24.0F;
      int lerpBlockLight = (int)Mth.lerp(piecePosPercent, entityBlockLight, holderBlockLight);
      int lerpSkyLight = (int)Mth.lerp(piecePosPercent, entitySkyLight, holderSkyLight);
      int packedLight = LightTexture.pack(lerpBlockLight, lerpSkyLight);
      float knotColourMod = segment % 2 == (isLeashKnot ? 1 : 0) ? 0.7F : 1.0F;
      float red = 0.5F * knotColourMod;
      float green = 0.4F * knotColourMod;
      float blue = 0.3F * knotColourMod;
      float x = xDif * piecePosPercent;
      float y = yDif > 0.0F ? yDif * piecePosPercent * piecePosPercent : yDif - yDif * (1.0F - piecePosPercent) * (1.0F - piecePosPercent);
      float z = zDif * piecePosPercent;
      buffer.addVertex(positionMatrix, x - xOffset, y + yOffset, z + zOffset).setColor(red, green, blue, 1.0F).setLight(packedLight);
      buffer.addVertex(positionMatrix, x + xOffset, y + width - yOffset, z - zOffset).setColor(red, green, blue, 1.0F).setLight(packedLight);
   }

   public void updateAnimatedTextureFrame(T animatable) {
      AnimatableTexture.setAndUpdate(this.getTextureLocation(animatable));
   }

   @Override
   public void fireCompileRenderLayersEvent() {
      GeckoLibServices.Client.EVENTS.fireCompileEntityRenderLayers(this);
   }

   @Override
   public boolean firePreRenderEvent(PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
      return GeckoLibServices.Client.EVENTS.fireEntityPreRender(this, poseStack, model, bufferSource, partialTick, packedLight);
   }

   @Override
   public void firePostRenderEvent(PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
      GeckoLibServices.Client.EVENTS.fireEntityPostRender(this, poseStack, model, bufferSource, partialTick, packedLight);
   }
}
