package net.diebuddies.physics.verlet.constraints;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import net.diebuddies.compat.Optifine;
import net.diebuddies.compat.SableCreate;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.ocean.EntityOcean;
import net.diebuddies.physics.ocean.OceanWorld;
import net.diebuddies.physics.verlet.VerletSimulation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.vehicle.Boat;
import org.joml.Matrix4d;
import org.joml.Matrix4fStack;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class OceanPhysicsDisplacementConstraint extends RenderConstraint {
   private Entity entity;
   private Matrix4d gravityTransformation;
   private Vector3d gravity;

   public OceanPhysicsDisplacementConstraint(Entity entity) {
      this.entity = entity;
      this.gravityTransformation = new Matrix4d();
      this.gravity = new Vector3d();
   }

   @Override
   public void render(Matrix4fStack matrixStack, double renderPercent, VerletSimulation simulation) {
      if (ConfigClient.areOceanPhysicsEnabled()) {
         double px = Mth.lerp(renderPercent, this.entity.xOld, this.entity.getX());
         double py = Mth.lerp(renderPercent, this.entity.yOld, this.entity.getY());
         double pz = Mth.lerp(renderPercent, this.entity.zOld, this.entity.getZ());
         OceanWorld oceanWorld = PhysicsMod.getInstance(this.entity.level()).getPhysicsWorld().getOceanWorld();
         float yRot = Mth.lerp((float)renderPercent, this.entity.yRotO, this.entity.getYRot());
         Vector3d offset = simulation.getOffset();
         oceanWorld.computeEntityOffset(
            matrixStack, null, this.entity.level(), this.entity, px, py, pz, offset.x, offset.y, offset.z, yRot, (float)renderPercent
         );
         this.calculateGravityTransformation(renderPercent);
         RenderSystem.applyModelViewMatrix();
         if (StarterClient.optifabric && Optifine.areShadersEnabled()) {
            Optifine.setModelViewMatrix(RenderSystem.getModelViewMatrix());
         }
      }

      super.render(matrixStack, renderPercent, simulation);
   }

   @Override
   public void renderBefore(Matrix4fStack matrixStack, double delta, VerletSimulation simulation) {
      if (ConfigClient.areOceanPhysicsEnabled() && StarterClient.sable) {
         simulation.hide = SableCreate.hasShipMount(this.entity) != null;
      }

      super.renderBefore(matrixStack, delta, simulation);
   }

   private void calculateGravityTransformation(double renderPercent) {
      float actualYRot = 0.0F;
      Entity vehicle = this.entity.getVehicle();
      EntityOcean entityOcean = (EntityOcean)this.entity;
      if (this.entity instanceof LivingEntity living) {
         actualYRot = Mth.rotLerp((float)renderPercent, living.yBodyRotO, living.yBodyRot);
      } else {
         actualYRot = this.entity.getViewYRot((float)renderPercent);
      }

      float currentYRot = (float)(-Math.toRadians(actualYRot - 3.1415927F));
      double forwardZ = Math.cos(currentYRot);
      double forwardX = Math.sin(currentYRot);
      double leftZ = -forwardX;
      double roll = entityOcean.getPhysicsRoll((float)renderPercent);
      double pitch = entityOcean.getPhysicsPitch((float)renderPercent);
      float diffRot = 0.0F;
      if (vehicle != null && vehicle instanceof Boat) {
         diffRot = vehicle.getViewYRot((float)renderPercent) - actualYRot;
      }

      this.gravityTransformation.identity();
      this.gravityTransformation.rotate(Axis.YP.rotationDegrees(-diffRot));
      this.gravityTransformation.rotate(Axis.of(new Vector3f((float)forwardX, 0.0F, (float)forwardZ)).rotationDegrees((float)(-Math.toDegrees(roll))));
      this.gravityTransformation.rotate(Axis.of(new Vector3f((float)forwardZ, 0.0F, (float)leftZ)).rotationDegrees((float)Math.toDegrees(pitch)));
      this.gravityTransformation.invert();
   }

   @Override
   public void updateBefore(double delta, VerletSimulation simulation) {
      super.updateBefore(delta, simulation);
      this.gravity.set(simulation.getGravity());
      this.gravityTransformation.transformDirection(simulation.getGravity());
   }

   @Override
   public void updateAfter(double delta, VerletSimulation simulation) {
      super.updateAfter(delta, simulation);
      simulation.setGravity(this.gravity);
   }
}
