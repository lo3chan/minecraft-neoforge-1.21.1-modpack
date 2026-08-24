package net.diebuddies.physics.verlet.constraints;

import com.mojang.blaze3d.vertex.PoseStack;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import net.diebuddies.bridge.ReflectionsForge;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.verlet.VerletHelper;
import net.diebuddies.physics.verlet.VerletPoint;
import net.diebuddies.physics.verlet.VerletSimulation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;
import org.joml.Vector3d;

public class ClosestPlayerConstraint implements VerletConstraint {
   private Player player;
   private Level level;
   private PlayerModel<Player> model;
   private ModelCube[] modelCubes = new ModelCube[6];
   private VerletHelper helper = new VerletHelper();
   private double playerx;
   private double playery;
   private double playerz;
   private Vector3d invPoint = new Vector3d();
   private Matrix4d transform = new Matrix4d();
   private Matrix4d invTransform = new Matrix4d();
   private PoseStack modelMatrix = new PoseStack();
   private Quaternionf tmpQuat = new Quaternionf();

   public ClosestPlayerConstraint(Level level) {
      this.level = level;

      for (int i = 0; i < this.modelCubes.length; i++) {
         this.modelCubes[i] = new ModelCube();
      }
   }

   @Override
   public boolean initAsyncData(PhysicsWorld world, VerletSimulation simulation) {
      Vector3d offset = simulation.getOffset();
      double px = offset.x;
      double py = offset.y;
      double pz = offset.z;
      if (simulation.getPoints().size() > 0) {
         Vector3d pos = simulation.getPoints().get(0).position;
         px += pos.x;
         py += pos.y;
         pz += pos.z;
      }

      this.player = this.level.getNearestPlayer(px, py, pz, 10.0, false);
      if (this.player != null) {
         LivingEntityRenderer<Player, PlayerModel<Player>> renderer = (LivingEntityRenderer<Player, PlayerModel<Player>>)Minecraft.getInstance()
            .getEntityRenderDispatcher()
            .getRenderer(this.player);
         this.model = (PlayerModel<Player>)renderer.getModel();
         this.modelCubes[0].part = this.model.hat;
         this.modelCubes[1].part = this.model.body;
         this.modelCubes[2].part = this.model.rightArm;
         this.modelCubes[3].part = this.model.leftArm;
         this.modelCubes[4].part = this.model.rightLeg;
         this.modelCubes[5].part = this.model.leftLeg;

         for (int i = 0; i < this.modelCubes.length; i++) {
            this.modelCubes[i].pose = this.modelCubes[i].part.storePose();
            this.modelCubes[i].updateHitbox();
         }

         this.modelMatrix.pushPose();
         this.setupModelAnimations(1.0F);
         this.playerTransformation(this.modelMatrix, simulation, this.player, 1.0F, 1.0F);

         for (int i = 0; i < this.modelCubes.length; i++) {
            ModelCube modelCube = this.modelCubes[i];
            Matrix4f currentPose = this.modelMatrix.last().pose();
            modelCube.transform.set(currentPose);
            this.translateAndRotate(modelCube.transform, modelCube.pose);
         }

         this.modelMatrix.popPose();
      }

      return false;
   }

   @Override
   public void updateBefore(double delta, VerletSimulation simulation) {
      for (int i = 0; i < this.modelCubes.length; i++) {
         this.modelCubes[i].updateTransformation();
      }
   }

   @Override
   public void subStep(double percent, VerletSimulation simulation) {
      if (this.player != null) {
         this.doCollisionCheck(percent, simulation);
      }
   }

   @Override
   public void updateAfter(double delta, VerletSimulation simulation) {
   }

   private void playerTransformation(PoseStack modelMatrix, VerletSimulation simulation, Player player, float tickDelta, float animationProgress) {
      LivingEntityRenderer<Player, PlayerModel<Player>> renderer = (LivingEntityRenderer<Player, PlayerModel<Player>>)Minecraft.getInstance()
         .getEntityRenderDispatcher()
         .getRenderer(player);
      this.playerx = Mth.lerp(tickDelta, player.xOld, player.getX());
      this.playery = Mth.lerp(tickDelta, player.yOld, player.getY());
      this.playerz = Mth.lerp(tickDelta, player.zOld, player.getZ());
      this.playerx = this.playerx - simulation.getOffset().x;
      this.playery = this.playery - simulation.getOffset().y;
      this.playerz = this.playerz - simulation.getOffset().z;
      Vec3 positionOffset = renderer.getRenderOffset(player, tickDelta);
      modelMatrix.translate(positionOffset.x + this.playerx, positionOffset.y + this.playery, positionOffset.z + this.playerz);
      float yaw = Mth.rotLerp(tickDelta, player.yBodyRotO, player.yBodyRot);
      if (player.getPose() == Pose.SLEEPING) {
         Direction direction = player.getBedOrientation();
         if (direction != null) {
            float eyeHeight = player.getEyeHeight(Pose.STANDING) - 0.1F;
            modelMatrix.translate(-direction.getStepX() * eyeHeight, 0.0, -direction.getStepZ() * eyeHeight);
         }
      }

      try {
         ReflectionsForge.setupRotations.invoke(renderer, player, modelMatrix, animationProgress, yaw, tickDelta, 1.0F);
      } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var11) {
         var11.printStackTrace();
      }

      modelMatrix.scale(-1.0F, -1.0F, 1.0F);
      modelMatrix.scale(0.9375F, 0.9375F, 0.9375F);
      modelMatrix.translate(0.0, -1.5010000467300415, 0.0);
   }

   private void doCollisionCheck(double percent, VerletSimulation simulation) {
      float enlarge = 0.175F;
      if (!(simulation.aabb.distanceSquared(this.playerx, this.playery, this.playerz) > 16.0)) {
         for (int i = 0; i < this.modelCubes.length; i++) {
            ModelCube modelCube = this.modelCubes[i];
            ModelPart part = modelCube.part;
            if (part != null && !part.cubes.isEmpty()) {
               modelCube.getTransform(percent, this.transform);
               this.transform.invert(this.invTransform);
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
                        point.friction = 0.6;
                     }
                  }
               }
            }
         }
      }
   }

   private void setupModelAnimations(float tickDelta) {
      float h = Mth.rotLerp(tickDelta, this.player.yBodyRotO, this.player.yBodyRot);
      float j = Mth.rotLerp(tickDelta, this.player.yHeadRotO, this.player.yHeadRot);
      float k = j - h;
      if (this.player.isPassenger() && this.player.getVehicle() instanceof LivingEntity livingEntity2) {
         h = Mth.rotLerp(tickDelta, livingEntity2.yBodyRotO, livingEntity2.yBodyRot);
         k = j - h;
         float o = Mth.wrapDegrees(k);
         if (o < -85.0F) {
            o = -85.0F;
         }

         if (o >= 85.0F) {
            o = 85.0F;
         }

         h = j - o;
         if (o * o > 2500.0F) {
            h += o * 0.2F;
         }

         k = j - h;
      }

      float m = Mth.lerp(tickDelta, this.player.xRotO, this.player.getXRot());
      if (this.player.getPose() == Pose.SLEEPING) {
         Direction direction = this.player.getBedOrientation();
         if (direction != null) {
            float var14 = this.player.getEyeHeight(Pose.STANDING) - 0.1F;
         }
      }

      float ox = tickDelta;
      float p = 0.0F;
      float q = 0.0F;
      if (!this.player.isPassenger() && this.player.isAlive()) {
         p = this.player.walkAnimation.speed(tickDelta);
         ox = this.player.walkAnimation.position(tickDelta);
         if (this.player.isBaby()) {
            ox *= 3.0F;
         }

         if (p > 1.0F) {
            p = 1.0F;
         }
      }

      this.model.crouching = this.player.isCrouching();
      this.model.setupAnim(this.player, q, p, ox, k, m);
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

   @Override
   public void renderBefore(Matrix4fStack matrixStack, double delta, VerletSimulation simulation) {
   }

   @Override
   public void renderAfter(Matrix4fStack matrixStack, double delta, VerletSimulation simulation) {
   }

   @Override
   public void render(Matrix4fStack matrixStack, double delta, VerletSimulation simulation) {
   }
}
