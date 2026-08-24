package net.diebuddies.physics.verlet.constraints;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.ocean.OceanWorld;
import net.diebuddies.physics.verlet.VerletLine;
import net.diebuddies.physics.verlet.VerletPoint;
import net.diebuddies.physics.verlet.VerletSimulation;
import net.diebuddies.physics.verlet.VerletStick;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Math;
import org.joml.Matrix4fStack;
import org.joml.Vector3d;

public class LeashConstraint implements VerletConstraint {
   private Vector3d leashOriginAsync = new Vector3d();
   private Vector3d leashTargetAsync = new Vector3d();
   private Vector3d leashOrigin = new Vector3d();
   private Vector3d leashTarget = new Vector3d();
   private Entity mob;
   private Entity entity;
   private EntityRenderDispatcher entityRenderDispatcher;

   public LeashConstraint(VerletSimulation simulation, Entity mob, Entity entity, EntityRenderDispatcher entityRenderDispatcher, float tickDelta) {
      this.mob = mob;
      this.entity = entity;
      this.entityRenderDispatcher = entityRenderDispatcher;
      this.calculateLeashOriginAndTarget(tickDelta, this.leashOrigin, this.leashTarget);
      int pointCount = 20;
      double totalLength = ConfigClient.leashLength;

      for (int i = 0; i < pointCount; i++) {
         float perc = (float)(i - 1) / pointCount;
         Vector3d position = new Vector3d(
            Math.lerp(this.leashOrigin.x, this.leashTarget.x, perc),
            Math.lerp(this.leashOrigin.y, this.leashTarget.y, perc),
            Math.lerp(this.leashOrigin.z, this.leashTarget.z, perc)
         );
         VerletPoint point = new VerletPoint(position);
         point.uv.set(0.01F, 0.99F);
         float colMod = i % 2 == 0 ? 0.7F : 1.0F;
         float r = 0.5F * colMod;
         float g = 0.4F * colMod;
         float b = 0.3F * colMod;
         point.rgba.set(r, g, b, 1.0F);
         point.locked = i == 0 || i == pointCount - 1;
         simulation.addPoint(point);
      }

      for (int i = 0; i < pointCount - 1; i++) {
         simulation.addStick(new VerletStick(simulation.getPoints().get(i), simulation.getPoints().get(i + 1), totalLength / pointCount));
         simulation.addLine(new VerletLine(simulation.getPoints().get(i), simulation.getPoints().get(i + 1)));
      }
   }

   private void calculateLeashOriginAndTarget(float tickDelta, Vector3d leashOrigin, Vector3d leashTarget) {
      Vec3 ropePosition = this.entity.getRopeHoldPosition(tickDelta);
      double bodyRot = this.entity.getPreciseBodyRotation(tickDelta) * 0.017453292F + 1.5707963267948966;
      Vec3 leashOffset = this.mob.getLeashOffset(tickDelta);
      double leashOffset1 = java.lang.Math.cos(bodyRot) * leashOffset.z + java.lang.Math.sin(bodyRot) * leashOffset.x;
      double leashOffset2 = java.lang.Math.sin(bodyRot) * leashOffset.z - java.lang.Math.cos(bodyRot) * leashOffset.x;
      double mobX = Mth.lerp(tickDelta, this.mob.xo, this.mob.getX()) + leashOffset1;
      double mobY = Mth.lerp(tickDelta, this.mob.yo, this.mob.getY()) + leashOffset.y;
      double mobZ = Mth.lerp(tickDelta, this.mob.zo, this.mob.getZ()) + leashOffset2;
      float ropeDirX = (float)(ropePosition.x - mobX);
      float ropeDirY = (float)(ropePosition.y - mobY);
      float ropeDirZ = (float)(ropePosition.z - mobZ);
      float hangingRate = Mth.invSqrt(ropeDirX * ropeDirX + ropeDirZ * ropeDirZ) * 0.025F / 2.0F;
      float hangingRateZ = ropeDirZ * hangingRate;
      float hangingRateX = ropeDirX * hangingRate;
      BlockPos mob1Pos = BlockPos.containing(this.mob.getEyePosition(tickDelta));
      BlockPos mob2Pos = BlockPos.containing(this.entity.getEyePosition(tickDelta));
      double ropeOffset = 0.0;
      if (ConfigClient.areOceanPhysicsEnabled()) {
         OceanWorld oceanWorld = PhysicsMod.getInstance(this.mob.level()).getPhysicsWorld().getOceanWorld();
         ropeOffset += oceanWorld.computeYOffset(this.entity.level(), this.entity, 1.0F);
         mobY += oceanWorld.computeYOffset(this.mob.level(), this.mob, 1.0F);
      }

      leashOrigin.set(ropePosition.x, ropePosition.y + ropeOffset, ropePosition.z);
      leashTarget.set(mobX, mobY, mobZ);
   }

   @Override
   public boolean initAsyncData(PhysicsWorld world, VerletSimulation simulation) {
      this.calculateLeashOriginAndTarget(1.0F, this.leashOriginAsync, this.leashTargetAsync);
      return false;
   }

   @Override
   public void updateBefore(double delta, VerletSimulation simulation) {
      VerletPoint originPoint = simulation.getPoints().get(0);
      VerletPoint targetPoint = simulation.getPoints().get(simulation.getPoints().size() - 1);
      originPoint.position.set(this.leashOriginAsync).sub(simulation.getOffset());
      targetPoint.position.set(this.leashTargetAsync).sub(simulation.getOffset());
   }

   @Override
   public void subStep(double percent, VerletSimulation simulation) {
   }

   @Override
   public void updateAfter(double delta, VerletSimulation simulation) {
   }

   @Override
   public void renderBefore(Matrix4fStack matrixStack, double delta, VerletSimulation simulation) {
      this.calculateLeashOriginAndTarget((float)delta, this.leashOrigin, this.leashTarget);
      VerletPoint armPoint = simulation.getPoints().get(0);
      VerletPoint hookPoint = simulation.getPoints().get(simulation.getPoints().size() - 1);
      armPoint.bufferPosition.set(this.leashOrigin).sub(simulation.getOffset());
      armPoint.bufferPrevPosition.set(armPoint.bufferPosition);
      hookPoint.bufferPosition.set(this.leashTarget).sub(simulation.getOffset());
      hookPoint.bufferPrevPosition.set(hookPoint.bufferPosition);
   }

   @Override
   public void renderAfter(Matrix4fStack matrixStack, double delta, VerletSimulation simulation) {
   }

   @Override
   public void render(Matrix4fStack matrixStack, double delta, VerletSimulation simulation) {
   }
}
