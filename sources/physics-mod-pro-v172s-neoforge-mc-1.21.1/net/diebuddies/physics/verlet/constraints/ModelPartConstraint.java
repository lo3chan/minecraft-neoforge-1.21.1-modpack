package net.diebuddies.physics.verlet.constraints;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.diebuddies.bridge.ReflectionsForge;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.math.MatrixUtil;
import net.diebuddies.model.ColladaMesh;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.ocean.OceanWorld;
import net.diebuddies.physics.settings.cloth.ClothConstants;
import net.diebuddies.physics.verlet.ModelPartParent;
import net.diebuddies.physics.verlet.VerletHelper;
import net.diebuddies.physics.verlet.VerletPoint;
import net.diebuddies.physics.verlet.VerletSimulation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class ModelPartConstraint implements VerletConstraint {
   private LivingEntity entity;
   private Model model;
   public boolean changeInstantly;
   private boolean lastCrouch;
   private VerletHelper helper = new VerletHelper();
   private List<ModelCube> modelCubes;
   private ModelCube attachedTo;
   private double initialFriction;
   private PoseStack modelMatrix = new PoseStack();
   private PoseStack headMatrix = new PoseStack();
   private ModelPartConstraint.CustomTransformation customTransformation;
   private boolean isHead;
   private Vector3d invPoint = new Vector3d();
   private Matrix4d transform = new Matrix4d();
   private Matrix4d invTransform = new Matrix4d();
   private Vector3d tmp = new Vector3d();
   private Matrix4d partTransformation = new Matrix4d();
   private Matrix4d oldPartTransformation;
   private Matrix4d currentPartTransformation = new Matrix4d();
   private Matrix4d tmpMat = new Matrix4d();
   private static Matrix4d elytraFix = new Matrix4d();
   private Quaternionf tmpQuat = new Quaternionf();

   public ModelPartConstraint(VerletSimulation simulation, Set<String> ignoreParts, @Nullable LivingEntity entity, String attachedToName, Model model) {
      this.entity = entity;
      this.model = model;
      this.lastCrouch = entity == null ? false : entity.isCrouching();
      this.initialFriction = simulation.getFriction();
      this.modelCubes = new ObjectArrayList();
      ObjectListIterator var6 = ClothConstants.getModelParts(model).iterator();

      while (var6.hasNext()) {
         ModelPart part = (ModelPart)var6.next();
         String name = ((ModelPartParent)part).physicsmod$getName();
         if (attachedToName.equals(name)) {
            this.attachedTo = new ModelCube();
            this.attachedTo.part = part;
            this.attachedTo.pose = part.storePose();
         }

         if (!ignoreParts.contains(name)) {
            ModelCube modelCube = new ModelCube();
            modelCube.part = part;
            this.modelCubes.add(modelCube);
         }
      }

      this.isHead = entity == Minecraft.getInstance().player;
   }

   private void storePoses() {
      for (int i = 0; i < this.modelCubes.size(); i++) {
         ModelCube modelCube = this.modelCubes.get(i);
         modelCube.pose = modelCube.part.storePose();
         modelCube.updateHitbox();
      }

      this.attachedTo.pose = this.attachedTo.part.storePose();
      this.attachedTo.updateHitbox();
   }

   public static ModelPart getPart(Model model, String name) {
      ObjectListIterator var2 = ClothConstants.getModelParts(model).iterator();

      while (var2.hasNext()) {
         ModelPart part = (ModelPart)var2.next();
         if (name.equals(((ModelPartParent)part).physicsmod$getName())) {
            return part;
         }
      }

      return null;
   }

   public static boolean exists(EntityModel<LivingEntity> model, String name) {
      ObjectListIterator var2 = ClothConstants.getModelParts(model).iterator();

      while (var2.hasNext()) {
         ModelPart part = (ModelPart)var2.next();
         if (name.equals(((ModelPartParent)part).physicsmod$getName())) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean initAsyncData(PhysicsWorld world, VerletSimulation simulation) {
      if (this.entity != null) {
         if (this.lastCrouch != this.entity.isCrouching()) {
            this.changeInstantly = true;
         } else {
            this.changeInstantly = false;
         }

         this.lastCrouch = this.entity.isCrouching();
      }

      this.modelMatrix.pushPose();
      if (this.entity != null) {
         entityTransformation(this.modelMatrix, simulation, this.entity, this.model, 1.0F);
      } else if (this.customTransformation != null) {
         this.customTransformation.doTransformation(this.modelMatrix);
      }

      this.storePoses();

      for (int i = 0; i < this.modelCubes.size(); i++) {
         ModelCube modelCube = this.modelCubes.get(i);
         modelCube.transform.set(this.modelMatrix.last().pose());
         this.translateAndRotate(modelCube.transform, modelCube.pose);
      }

      this.modelPartTransformation(this.modelMatrix.last().pose());
      if (this.oldPartTransformation != null) {
         this.oldPartTransformation.set(this.partTransformation);
      }

      this.partTransformation.set(this.modelMatrix.last().pose());
      if (this.oldPartTransformation == null) {
         this.oldPartTransformation = new Matrix4d();
         this.oldPartTransformation.set(this.partTransformation);
      }

      simulation.setTransformation(this.partTransformation);
      this.modelMatrix.popPose();
      if (this.entity != null) {
         if (this.entity.isUnderWater()) {
            OceanWorld oceanWorld = PhysicsMod.getInstance(this.entity.level()).getPhysicsWorld().getOceanWorld();
            Vector3d waveForce = oceanWorld.calculateWaveForce(this.entity.getX(), this.entity.getY(), this.entity.getZ());
            Vector3d gravity = simulation.getGravity();
            gravity.set(ConfigClient.getBuoyancy(this.entity.getCommandSenderWorld().dimension().location()));
            if (waveForce != null) {
               double forceStrength = 10.0;
               gravity.add(waveForce.x * forceStrength, 0.0, waveForce.z * forceStrength);
            }

            simulation.setFriction(0.699999988079071);
         } else {
            simulation.getGravity().set(ConfigClient.getGravity(this.entity.getCommandSenderWorld().dimension().location()));
            simulation.setFriction(this.initialFriction);
         }
      }

      return this.changeInstantly;
   }

   @Override
   public void updateBefore(double delta, VerletSimulation simulation) {
      for (int i = 0; i < this.modelCubes.size(); i++) {
         this.modelCubes.get(i).updateTransformation();
      }
   }

   private void updateFixedPoints(VerletSimulation simulation, Matrix4d currentPartTransformation) {
      List<VerletPoint> points = simulation.getPoints();
      double lengthDiff = 1000.0;
      ColladaMesh mesh = simulation.cloth.mesh;
      int size = mesh.positions.size();
      List<Vector3f> positions = mesh.positions;

      for (int i = 0; i < points.size() && i < size; i++) {
         VerletPoint point = points.get(i);
         if (point.locked) {
            Vector3f pos = positions.get(i);
            this.tmp.set(pos.x, pos.y, pos.z);
            currentPartTransformation.transformPosition(this.tmp);
            if (this.tmp.distanceSquared(point.position) > lengthDiff) {
               simulation.destroyed = true;
            }

            point.position.set(this.tmp);
            if (this.changeInstantly) {
               point.prevPosition.set(point.position);
            }
         } else if (point.softRestriction != null) {
            Vector3f posx = positions.get(i);
            this.tmp.set(posx.x, posx.y, posx.z);
            currentPartTransformation.transformPosition(this.tmp);
            point.softRestriction.set(this.tmp);
         }
      }
   }

   private void updateFixedPoints(double percent, VerletSimulation simulation) {
      this.updateFixedPoints(simulation, MatrixUtil.slerp(this.oldPartTransformation, this.partTransformation, percent, this.currentPartTransformation));
   }

   @Override
   public void renderBefore(Matrix4fStack matrixStack, double delta, VerletSimulation simulation) {
      if (this.isHead) {
         this.modelMatrix.pushPose();
         if (this.entity != null) {
            entityTransformation(this.modelMatrix, simulation, this.entity, null, (float)delta);
         } else if (this.customTransformation != null) {
            this.customTransformation.doTransformation(this.modelMatrix);
         }

         this.attachedTo.transform.set(this.modelMatrix.last().pose());
         this.attachedTo.pose = this.attachedTo.part.storePose();
         this.translateAndRotate(this.attachedTo.transform, this.attachedTo.pose);
         this.currentPartTransformation.set(this.attachedTo.transform);
         List<VerletPoint> points = simulation.getPoints();
         ColladaMesh mesh = simulation.cloth.mesh;
         int size = mesh.positions.size();
         List<Vector3f> positions = mesh.positions;

         for (int i = 0; i < points.size() && i < size; i++) {
            VerletPoint point = points.get(i);
            if (point.locked) {
               Vector3f pos = positions.get(i);
               this.tmp.set(pos.x, pos.y, pos.z);
               this.currentPartTransformation.transformPosition(this.tmp);
               point.bufferPosition.set(this.tmp);
               point.bufferPrevPosition.set(this.tmp);
            }
         }

         this.modelMatrix.popPose();
      }
   }

   @Override
   public void preSubStep(double percent, VerletSimulation simulation) {
      this.updateFixedPoints(percent, simulation);
   }

   @Override
   public void subStep(double percent, VerletSimulation simulation) {
      this.doCollisionCheck(percent, simulation);
   }

   @Override
   public void updateAfter(double delta, VerletSimulation simulation) {
   }

   public void modelPartTransformation(Matrix4f modelMatrix) {
      this.translateAndRotate(modelMatrix, this.attachedTo.pose);
   }

   public static void modelPartTransformation(ModelPart part, PoseStack modelMatrix) {
      part.translateAndRotate(modelMatrix);
   }

   public static void entityTransformation(PoseStack modelMatrix, VerletSimulation simulation, LivingEntity entity, Model model, float tickDelta) {
      LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>> renderer = (LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>>)Minecraft.getInstance()
         .getEntityRenderDispatcher()
         .getRenderer(entity);
      double px = Mth.lerp(tickDelta, entity.xOld, entity.getX());
      double py = Mth.lerp(tickDelta, entity.yOld, entity.getY());
      double pz = Mth.lerp(tickDelta, entity.zOld, entity.getZ());
      if (simulation.getOffset() != null) {
         px -= simulation.getOffset().x;
         py -= simulation.getOffset().y;
         pz -= simulation.getOffset().z;
      } else {
         px = 0.0;
         py = 0.0;
         pz = 0.0;
      }

      Vec3 positionOffset = renderer.getRenderOffset(entity, tickDelta);
      modelMatrix.translate(positionOffset.x + px, positionOffset.y + py, positionOffset.z + pz);
      float yaw = Mth.rotLerp(tickDelta, entity.yBodyRotO, entity.yBodyRot);
      if (entity.getPose() == Pose.SLEEPING) {
         Direction direction = entity.getBedOrientation();
         if (direction != null) {
            float eyeHeight = entity.getEyeHeight(Pose.STANDING) - 0.1F;
            modelMatrix.translate(-direction.getStepX() * eyeHeight, 0.0, -direction.getStepZ() * eyeHeight);
         }
      }

      elytraFix.set(modelMatrix.last().pose());

      try {
         ReflectionsForge.setupRotations.invoke(renderer, entity, modelMatrix, tickDelta, yaw, tickDelta, 1.0F);
      } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var22) {
         var22.printStackTrace();
      }

      if (!modelMatrix.last().pose().isFinite()) {
         modelMatrix.last().pose().set(elytraFix);
      }

      modelMatrix.scale(-1.0F, -1.0F, 1.0F);
      modelMatrix.scale(0.9375F, 0.9375F, 0.9375F);
      modelMatrix.translate(0.0, -1.5010000467300415, 0.0);
      if (model != null) {
         float bodyRot = Mth.rotLerp(tickDelta, entity.yBodyRotO, entity.yBodyRot);
         float headRot = Mth.rotLerp(tickDelta, entity.yHeadRotO, entity.yHeadRot);
         float headBodyDiff = headRot - bodyRot;
         if (entity.isPassenger() && entity.getVehicle() instanceof LivingEntity) {
            LivingEntity vehicle = (LivingEntity)entity.getVehicle();
            bodyRot = Mth.rotLerp(tickDelta, vehicle.yBodyRotO, vehicle.yBodyRot);
            headBodyDiff = headRot - bodyRot;
            float diffWrapped = Mth.wrapDegrees(headBodyDiff);
            if (diffWrapped < -85.0F) {
               diffWrapped = -85.0F;
            }

            if (diffWrapped >= 85.0F) {
               diffWrapped = 85.0F;
            }

            bodyRot = headRot - diffWrapped;
            if (diffWrapped * diffWrapped > 2500.0F) {
               bodyRot += diffWrapped * 0.2F;
            }

            headBodyDiff = headRot - bodyRot;
         }

         float xRot = Mth.lerp(tickDelta, entity.xRotO, entity.getXRot());
         if (LivingEntityRenderer.isEntityUpsideDown(entity)) {
            xRot *= -1.0F;
            headBodyDiff *= -1.0F;
         }

         float bob = entity.tickCount + tickDelta;
         float animationSpeed = 0.0F;
         float animationPosition = 0.0F;
         if (!entity.isPassenger() && entity.isAlive()) {
            animationSpeed = entity.walkAnimation.speed(tickDelta);
            animationPosition = entity.walkAnimation.position(tickDelta);
            if (entity.isBaby()) {
               animationPosition *= 3.0F;
            }

            if (animationSpeed > 1.0F) {
               animationSpeed = 1.0F;
            }
         }

         if (model instanceof EntityModel<LivingEntity> entityModel) {
            entityModel.prepareMobModel(entity, animationPosition, animationSpeed, tickDelta);
            entityModel.setupAnim(entity, animationPosition, animationSpeed, bob, headBodyDiff, xRot);
         }
      }
   }

   public static void entityTransformation(PoseStack modelMatrix, LivingEntity entity, float tickDelta) {
      LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>> renderer = (LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>>)Minecraft.getInstance()
         .getEntityRenderDispatcher()
         .getRenderer(entity);
      Vec3 positionOffset = renderer.getRenderOffset(entity, tickDelta);
      modelMatrix.translate(positionOffset.x, positionOffset.y, positionOffset.z);
      float yaw = Mth.rotLerp(tickDelta, entity.yBodyRotO, entity.yBodyRot);
      if (entity.getPose() == Pose.SLEEPING) {
         Direction direction = entity.getBedOrientation();
         if (direction != null) {
            float eyeHeight = entity.getEyeHeight(Pose.STANDING) - 0.1F;
            modelMatrix.translate(-direction.getStepX() * eyeHeight, 0.0, -direction.getStepZ() * eyeHeight);
         }
      }

      try {
         ReflectionsForge.setupRotations.invoke(renderer, entity, modelMatrix, tickDelta, yaw, tickDelta, 1.0F);
      } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var8) {
         var8.printStackTrace();
      }

      modelMatrix.scale(-1.0F, -1.0F, 1.0F);
      modelMatrix.scale(0.9375F, 0.9375F, 0.9375F);
      modelMatrix.translate(0.0, -1.5010000467300415, 0.0);
   }

   private void doCollisionCheck(double percent, VerletSimulation simulation) {
      for (int i = 0; i < this.modelCubes.size(); i++) {
         ModelCube modelCube = this.modelCubes.get(i);
         ModelPart part = modelCube.part;
         if (part != null && !part.cubes.isEmpty()) {
            modelCube.getTransform(percent, this.transform);
            this.transform.invert(this.invTransform);
            float enlarge = 0.075F;
            float minX = modelCube.minX - enlarge;
            float minY = modelCube.minY - enlarge;
            float minZ = modelCube.minZ - enlarge;
            float maxX = modelCube.maxX + enlarge;
            float maxY = modelCube.maxY + enlarge;
            float maxZ = modelCube.maxZ + enlarge;
            List<VerletPoint> points = simulation.getPoints();

            for (int j = 0; j < points.size(); j++) {
               VerletPoint point = points.get(j);
               if (!point.locked) {
                  this.invTransform.transformPosition(this.invPoint.set(point.position));
                  if (this.helper.movePointOutOfBox(this.invPoint, minX, minY, minZ, maxX, maxY, maxZ)) {
                     point.position.set(this.transform.transformPosition(this.invPoint));
                     if (this.changeInstantly) {
                        point.prevPosition.set(point.position);
                     }
                  }
               }
            }
         }
      }
   }

   public void translateAndRotate(Matrix4d transform, PartPose pose) {
      transform.translate(pose.x / 16.0F, pose.y / 16.0F, pose.z / 16.0F);
      if (pose.zRot != 0.0F) {
         transform.rotate(this.tmpQuat.rotationZ(pose.zRot));
      }

      if (pose.yRot != 0.0F) {
         transform.rotate(this.tmpQuat.rotationY(pose.yRot));
      }

      if (pose.xRot != 0.0F) {
         transform.rotate(this.tmpQuat.rotationX(pose.xRot));
      }
   }

   public void translateAndRotate(Matrix4f transform, PartPose pose) {
      transform.translate(pose.x / 16.0F, pose.y / 16.0F, pose.z / 16.0F);
      if (pose.zRot != 0.0F) {
         transform.rotate(this.tmpQuat.rotationZ(pose.zRot));
      }

      if (pose.yRot != 0.0F) {
         transform.rotate(this.tmpQuat.rotationY(pose.yRot));
      }

      if (pose.xRot != 0.0F) {
         transform.rotate(this.tmpQuat.rotationX(pose.xRot));
      }
   }

   public ModelCube getAttachedToPart() {
      return this.attachedTo;
   }

   public void setCustomTransformation(ModelPartConstraint.CustomTransformation customTransformation) {
      this.customTransformation = customTransformation;
   }

   public Matrix4d getCurrentPartTransformation(double percent) {
      return this.changeInstantly
         ? this.tmpMat.set(this.partTransformation)
         : MatrixUtil.slerp(this.oldPartTransformation, this.partTransformation, percent, this.tmpMat);
   }

   public interface CustomTransformation {
      void doTransformation(PoseStack var1);
   }
}
