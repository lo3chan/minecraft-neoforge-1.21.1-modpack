package software.bernie.geckolib.animation;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import net.minecraft.util.Mth;
import org.apache.logging.log4j.Level;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.keyframe.AnimationPoint;
import software.bernie.geckolib.animation.keyframe.BoneAnimationQueue;
import software.bernie.geckolib.animation.state.BoneSnapshot;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.loading.math.MolangQueries;
import software.bernie.geckolib.model.GeoModel;

public class AnimationProcessor<T extends GeoAnimatable> {
   private final Map<String, GeoBone> bones = new Object2ObjectOpenHashMap();
   private final GeoModel<T> model;
   public boolean reloadAnimations = false;

   public AnimationProcessor(GeoModel<T> model) {
      this.model = model;
   }

   public Queue<AnimationProcessor.QueuedAnimation> buildAnimationQueue(T animatable, RawAnimation rawAnimation) {
      LinkedList<AnimationProcessor.QueuedAnimation> animations = new LinkedList<>();
      boolean error = false;

      for (RawAnimation.Stage stage : rawAnimation.getAnimationStages()) {
         Animation animation = null;
         if (stage.animationName() == "internal.wait") {
            animation = Animation.generateWaitAnimation(stage.additionalTicks());
         } else {
            try {
               animation = this.model.getAnimation(animatable, stage.animationName());
            } catch (RuntimeException var9) {
               GeckoLibConstants.LOGGER
                  .log(Level.ERROR, "Unable to find animation: " + stage.animationName() + " for " + animatable.getClass().getSimpleName());
               error = true;
               var9.printStackTrace();
            }
         }

         if (animation != null) {
            animations.add(new AnimationProcessor.QueuedAnimation(animation, stage.loopType()));
         }
      }

      return error ? null : animations;
   }

   public void tickAnimation(
      T animatable, GeoModel<T> model, AnimatableManager<T> animatableManager, double animTime, AnimationState<T> state, boolean crashWhenCantFindBone
   ) {
      Map<String, BoneSnapshot> boneSnapshots = this.updateBoneSnapshots(animatableManager.getBoneSnapshotCollection());

      for (AnimationController<T> controller : animatableManager.getAnimationControllers().values()) {
         if (this.reloadAnimations) {
            controller.forceAnimationReset();
            controller.getBoneAnimationQueues().clear();
         }

         controller.isJustStarting = animatableManager.isFirstTick();
         state.withController(controller);
         controller.process(model, state, this.bones, boneSnapshots, animTime, crashWhenCantFindBone);

         for (BoneAnimationQueue boneAnimation : controller.getBoneAnimationQueues().values()) {
            GeoBone bone = boneAnimation.bone();
            BoneSnapshot snapshot = boneSnapshots.get(bone.getName());
            BoneSnapshot initialSnapshot = bone.getInitialSnapshot();
            AnimationPoint rotXPoint = boneAnimation.rotationXQueue().poll();
            AnimationPoint rotYPoint = boneAnimation.rotationYQueue().poll();
            AnimationPoint rotZPoint = boneAnimation.rotationZQueue().poll();
            AnimationPoint posXPoint = boneAnimation.positionXQueue().poll();
            AnimationPoint posYPoint = boneAnimation.positionYQueue().poll();
            AnimationPoint posZPoint = boneAnimation.positionZQueue().poll();
            AnimationPoint scaleXPoint = boneAnimation.scaleXQueue().poll();
            AnimationPoint scaleYPoint = boneAnimation.scaleYQueue().poll();
            AnimationPoint scaleZPoint = boneAnimation.scaleZQueue().poll();
            EasingType easingType = controller.overrideEasingTypeFunction.apply(animatable);
            if (rotXPoint != null && rotYPoint != null && rotZPoint != null) {
               bone.setRotX((float)EasingType.lerpWithOverride(rotXPoint, easingType) + initialSnapshot.getRotX());
               bone.setRotY((float)EasingType.lerpWithOverride(rotYPoint, easingType) + initialSnapshot.getRotY());
               bone.setRotZ((float)EasingType.lerpWithOverride(rotZPoint, easingType) + initialSnapshot.getRotZ());
               snapshot.updateRotation(bone.getRotX(), bone.getRotY(), bone.getRotZ());
               snapshot.startRotAnim();
               bone.markRotationAsChanged();
            }

            if (posXPoint != null && posYPoint != null && posZPoint != null) {
               bone.setPosX((float)EasingType.lerpWithOverride(posXPoint, easingType));
               bone.setPosY((float)EasingType.lerpWithOverride(posYPoint, easingType));
               bone.setPosZ((float)EasingType.lerpWithOverride(posZPoint, easingType));
               snapshot.updateOffset(bone.getPosX(), bone.getPosY(), bone.getPosZ());
               snapshot.startPosAnim();
               bone.markPositionAsChanged();
            }

            if (scaleXPoint != null && scaleYPoint != null && scaleZPoint != null) {
               bone.setScaleX((float)EasingType.lerpWithOverride(scaleXPoint, easingType));
               bone.setScaleY((float)EasingType.lerpWithOverride(scaleYPoint, easingType));
               bone.setScaleZ((float)EasingType.lerpWithOverride(scaleZPoint, easingType));
               snapshot.updateScale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
               snapshot.startScaleAnim();
               bone.markScaleAsChanged();
            }
         }
      }

      this.reloadAnimations = false;
      double resetTickLength = animatable.getBoneResetTime();

      for (GeoBone bonex : this.getRegisteredBones()) {
         if (!bonex.hasRotationChanged()) {
            BoneSnapshot initialSnapshotx = bonex.getInitialSnapshot();
            BoneSnapshot saveSnapshot = boneSnapshots.get(bonex.getName());
            if (saveSnapshot.isRotAnimInProgress()) {
               saveSnapshot.stopRotAnim(animTime);
            }

            double percentageReset = resetTickLength == 0.0 ? 1.0 : Math.min((animTime - saveSnapshot.getLastResetRotationTick()) / resetTickLength, 1.0);
            float initialRotX = initialSnapshotx.getRotX();
            float initialRotY = initialSnapshotx.getRotY();
            float initialRotZ = initialSnapshotx.getRotZ();
            float lastXRot = saveSnapshot.getRotX();
            float lastYRot = saveSnapshot.getRotY();
            float lastZRot = saveSnapshot.getRotZ();
            if (percentageReset == 0.0) {
               if (lastXRot != initialRotX && this.isSuspectedCompletedRotation(lastXRot)) {
                  lastXRot = initialRotX;
                  percentageReset = 1.0;
               }

               if (lastYRot != initialRotY && this.isSuspectedCompletedRotation(lastYRot)) {
                  lastYRot = initialRotY;
                  percentageReset = 1.0;
               }

               if (lastZRot != initialRotZ && this.isSuspectedCompletedRotation(lastZRot)) {
                  lastZRot = initialRotZ;
                  percentageReset = 1.0;
               }
            }

            bonex.setRotX((float)Mth.lerp(percentageReset, lastXRot, initialRotX));
            bonex.setRotY((float)Mth.lerp(percentageReset, lastYRot, initialRotY));
            bonex.setRotZ((float)Mth.lerp(percentageReset, lastZRot, initialRotZ));
            if (percentageReset >= 1.0) {
               saveSnapshot.updateRotation(bonex.getRotX(), bonex.getRotY(), bonex.getRotZ());
            }
         }

         if (!bonex.hasPositionChanged()) {
            BoneSnapshot initialSnapshotxx = bonex.getInitialSnapshot();
            BoneSnapshot saveSnapshotx = boneSnapshots.get(bonex.getName());
            if (saveSnapshotx.isPosAnimInProgress()) {
               saveSnapshotx.stopPosAnim(animTime);
            }

            double percentageResetx = resetTickLength == 0.0 ? 1.0 : Math.min((animTime - saveSnapshotx.getLastResetPositionTick()) / resetTickLength, 1.0);
            bonex.setPosX((float)Mth.lerp(percentageResetx, saveSnapshotx.getOffsetX(), initialSnapshotxx.getOffsetX()));
            bonex.setPosY((float)Mth.lerp(percentageResetx, saveSnapshotx.getOffsetY(), initialSnapshotxx.getOffsetY()));
            bonex.setPosZ((float)Mth.lerp(percentageResetx, saveSnapshotx.getOffsetZ(), initialSnapshotxx.getOffsetZ()));
            if (percentageResetx >= 1.0) {
               saveSnapshotx.updateOffset(bonex.getPosX(), bonex.getPosY(), bonex.getPosZ());
            }
         }

         if (!bonex.hasScaleChanged()) {
            BoneSnapshot initialSnapshotxxx = bonex.getInitialSnapshot();
            BoneSnapshot saveSnapshotxx = boneSnapshots.get(bonex.getName());
            if (saveSnapshotxx.isScaleAnimInProgress()) {
               saveSnapshotxx.stopScaleAnim(animTime);
            }

            double percentageResetx = resetTickLength == 0.0 ? 1.0 : Math.min((animTime - saveSnapshotxx.getLastResetScaleTick()) / resetTickLength, 1.0);
            bonex.setScaleX((float)Mth.lerp(percentageResetx, saveSnapshotxx.getScaleX(), initialSnapshotxxx.getScaleX()));
            bonex.setScaleY((float)Mth.lerp(percentageResetx, saveSnapshotxx.getScaleY(), initialSnapshotxxx.getScaleY()));
            bonex.setScaleZ((float)Mth.lerp(percentageResetx, saveSnapshotxx.getScaleZ(), initialSnapshotxxx.getScaleZ()));
            if (percentageResetx >= 1.0) {
               saveSnapshotxx.updateScale(bonex.getScaleX(), bonex.getScaleY(), bonex.getScaleZ());
            }
         }
      }

      this.resetBoneTransformationMarkers();
      animatableManager.finishFirstTick();
   }

   private boolean isSuspectedCompletedRotation(float lastRotation) {
      float rotations = Mth.abs(lastRotation / 6.2831855F);
      float partialRotation = 1.0F - (rotations - (int)rotations);
      return partialRotation == 1.0F || partialRotation < 0.026 * rotations;
   }

   private void resetBoneTransformationMarkers() {
      this.getRegisteredBones().forEach(GeoBone::resetStateChanges);
   }

   private Map<String, BoneSnapshot> updateBoneSnapshots(Map<String, BoneSnapshot> snapshots) {
      for (GeoBone bone : this.getRegisteredBones()) {
         if (!snapshots.containsKey(bone.getName())) {
            snapshots.put(bone.getName(), BoneSnapshot.copy(bone.getInitialSnapshot()));
         }
      }

      return snapshots;
   }

   public GeoBone getBone(String boneName) {
      return this.bones.get(boneName);
   }

   public void registerGeoBone(GeoBone bone) {
      bone.saveInitialSnapshot();
      this.bones.put(bone.getName(), bone);

      for (GeoBone child : bone.getChildBones()) {
         this.registerGeoBone(child);
      }
   }

   public void setActiveModel(BakedGeoModel model) {
      this.bones.clear();

      for (GeoBone bone : model.topLevelBones()) {
         this.registerGeoBone(bone);
      }
   }

   public Collection<GeoBone> getRegisteredBones() {
      return this.bones.values();
   }

   public void preAnimationSetup(AnimationState<T> animationState, double animTime) {
      MolangQueries.updateActor(animationState, animTime);
      this.model.applyMolangQueries(animationState, animTime);
   }

   public record QueuedAnimation(Animation animation, Animation.LoopType loopType) {
   }
}
