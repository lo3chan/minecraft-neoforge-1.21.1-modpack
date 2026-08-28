/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.math.Axis
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.vehicle.Boat
 *  org.joml.Matrix4d
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fStack
 *  org.joml.Quaternionfc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.joml.Vector3f
 */
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
import net.diebuddies.physics.verlet.constraints.RenderConstraint;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.vehicle.Boat;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaternionfc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;

public class OceanPhysicsDisplacementConstraint
extends RenderConstraint {
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
            double px = Mth.lerp((double)renderPercent, (double)this.entity.xOld, (double)this.entity.getX());
            double py = Mth.lerp((double)renderPercent, (double)this.entity.yOld, (double)this.entity.getY());
            double pz = Mth.lerp((double)renderPercent, (double)this.entity.zOld, (double)this.entity.getZ());
            OceanWorld oceanWorld = PhysicsMod.getInstance(this.entity.level()).getPhysicsWorld().getOceanWorld();
            float yRot = Mth.lerp((float)((float)renderPercent), (float)this.entity.yRotO, (float)this.entity.getYRot());
            Vector3d offset = simulation.getOffset();
            oceanWorld.computeEntityOffset((Matrix4f)matrixStack, null, this.entity.level(), this.entity, px, py, pz, offset.x, offset.y, offset.z, yRot, (float)renderPercent);
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
        float actualYRot = 0.0f;
        Entity vehicle = this.entity.getVehicle();
        EntityOcean entityOcean = (EntityOcean)this.entity;
        Entity entity = this.entity;
        if (entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity)entity;
            actualYRot = Mth.rotLerp((float)((float)renderPercent), (float)living.yBodyRotO, (float)living.yBodyRot);
        } else {
            actualYRot = this.entity.getViewYRot((float)renderPercent);
        }
        float currentYRot = (float)(-Math.toRadians(actualYRot - (float)Math.PI));
        double forwardZ = Math.cos(currentYRot);
        double forwardX = Math.sin(currentYRot);
        double leftX = forwardZ;
        double leftZ = -forwardX;
        double roll = entityOcean.getPhysicsRoll((float)renderPercent);
        double pitch = entityOcean.getPhysicsPitch((float)renderPercent);
        float diffRot = 0.0f;
        if (vehicle != null && vehicle instanceof Boat) {
            diffRot = vehicle.getViewYRot((float)renderPercent) - actualYRot;
        }
        this.gravityTransformation.identity();
        this.gravityTransformation.rotate((Quaternionfc)Axis.YP.rotationDegrees(-diffRot));
        this.gravityTransformation.rotate((Quaternionfc)Axis.of((Vector3f)new Vector3f((float)forwardX, 0.0f, (float)forwardZ)).rotationDegrees((float)(-Math.toDegrees(roll))));
        this.gravityTransformation.rotate((Quaternionfc)Axis.of((Vector3f)new Vector3f((float)leftX, 0.0f, (float)leftZ)).rotationDegrees((float)Math.toDegrees(pitch)));
        this.gravityTransformation.invert();
    }

    @Override
    public void updateBefore(double delta, VerletSimulation simulation) {
        super.updateBefore(delta, simulation);
        this.gravity.set((Vector3dc)simulation.getGravity());
        this.gravityTransformation.transformDirection(simulation.getGravity());
    }

    @Override
    public void updateAfter(double delta, VerletSimulation simulation) {
        super.updateAfter(delta, simulation);
        simulation.setGravity(this.gravity);
    }
}

