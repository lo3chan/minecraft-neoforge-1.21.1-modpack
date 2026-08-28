/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.entity.EntityRenderDispatcher
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Math
 *  org.joml.Matrix4fStack
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package net.diebuddies.physics.verlet.constraints;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.ocean.OceanWorld;
import net.diebuddies.physics.verlet.VerletLine;
import net.diebuddies.physics.verlet.VerletPoint;
import net.diebuddies.physics.verlet.VerletSimulation;
import net.diebuddies.physics.verlet.VerletStick;
import net.diebuddies.physics.verlet.constraints.VerletConstraint;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Math;
import org.joml.Matrix4fStack;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class LeashConstraint
implements VerletConstraint {
    private Vector3d leashOriginAsync = new Vector3d();
    private Vector3d leashTargetAsync = new Vector3d();
    private Vector3d leashOrigin = new Vector3d();
    private Vector3d leashTarget = new Vector3d();
    private Entity mob;
    private Entity entity;
    private EntityRenderDispatcher entityRenderDispatcher;

    public LeashConstraint(VerletSimulation simulation, Entity mob, Entity entity, EntityRenderDispatcher entityRenderDispatcher, float tickDelta) {
        int i;
        this.mob = mob;
        this.entity = entity;
        this.entityRenderDispatcher = entityRenderDispatcher;
        this.calculateLeashOriginAndTarget(tickDelta, this.leashOrigin, this.leashTarget);
        int pointCount = 20;
        double totalLength = ConfigClient.leashLength;
        for (i = 0; i < pointCount; ++i) {
            float perc = (float)(i - 1) / (float)pointCount;
            Vector3d position = new Vector3d(Math.lerp((double)this.leashOrigin.x, (double)this.leashTarget.x, (double)perc), Math.lerp((double)this.leashOrigin.y, (double)this.leashTarget.y, (double)perc), Math.lerp((double)this.leashOrigin.z, (double)this.leashTarget.z, (double)perc));
            VerletPoint point = new VerletPoint(position);
            point.uv.set(0.01f, 0.99f);
            float colMod = i % 2 == 0 ? 0.7f : 1.0f;
            float r = 0.5f * colMod;
            float g = 0.4f * colMod;
            float b = 0.3f * colMod;
            point.rgba.set(r, g, b, 1.0f);
            point.locked = i == 0 || i == pointCount - 1;
            simulation.addPoint(point);
        }
        for (i = 0; i < pointCount - 1; ++i) {
            simulation.addStick(new VerletStick(simulation.getPoints().get(i), simulation.getPoints().get(i + 1), totalLength / (double)pointCount));
            simulation.addLine(new VerletLine(simulation.getPoints().get(i), simulation.getPoints().get(i + 1)));
        }
    }

    private void calculateLeashOriginAndTarget(float tickDelta, Vector3d leashOrigin, Vector3d leashTarget) {
        Vec3 ropePosition = this.entity.getRopeHoldPosition(tickDelta);
        double bodyRot = (double)(this.entity.getPreciseBodyRotation(tickDelta) * ((float)java.lang.Math.PI / 180)) + 1.5707963267948966;
        Vec3 leashOffset = this.mob.getLeashOffset(tickDelta);
        double leashOffset1 = java.lang.Math.cos(bodyRot) * leashOffset.z + java.lang.Math.sin(bodyRot) * leashOffset.x;
        double leashOffset2 = java.lang.Math.sin(bodyRot) * leashOffset.z - java.lang.Math.cos(bodyRot) * leashOffset.x;
        double mobX = Mth.lerp((double)tickDelta, (double)this.mob.xo, (double)this.mob.getX()) + leashOffset1;
        double mobY = Mth.lerp((double)tickDelta, (double)this.mob.yo, (double)this.mob.getY()) + leashOffset.y;
        double mobZ = Mth.lerp((double)tickDelta, (double)this.mob.zo, (double)this.mob.getZ()) + leashOffset2;
        float ropeDirX = (float)(ropePosition.x - mobX);
        float ropeDirY = (float)(ropePosition.y - mobY);
        float ropeDirZ = (float)(ropePosition.z - mobZ);
        float hangingRate = Mth.invSqrt((float)(ropeDirX * ropeDirX + ropeDirZ * ropeDirZ)) * 0.025f / 2.0f;
        float hangingRateZ = ropeDirZ * hangingRate;
        float hangingRateX = ropeDirX * hangingRate;
        BlockPos mob1Pos = BlockPos.containing((Position)this.mob.getEyePosition(tickDelta));
        BlockPos mob2Pos = BlockPos.containing((Position)this.entity.getEyePosition(tickDelta));
        double ropeOffset = 0.0;
        if (ConfigClient.areOceanPhysicsEnabled()) {
            OceanWorld oceanWorld = PhysicsMod.getInstance(this.mob.level()).getPhysicsWorld().getOceanWorld();
            ropeOffset += oceanWorld.computeYOffset(this.entity.level(), this.entity, 1.0f);
            mobY += oceanWorld.computeYOffset(this.mob.level(), this.mob, 1.0f);
        }
        leashOrigin.set(ropePosition.x, ropePosition.y + ropeOffset, ropePosition.z);
        leashTarget.set(mobX, mobY, mobZ);
    }

    @Override
    public boolean initAsyncData(PhysicsWorld world, VerletSimulation simulation) {
        this.calculateLeashOriginAndTarget(1.0f, this.leashOriginAsync, this.leashTargetAsync);
        return false;
    }

    @Override
    public void updateBefore(double delta, VerletSimulation simulation) {
        VerletPoint originPoint = simulation.getPoints().get(0);
        VerletPoint targetPoint = simulation.getPoints().get(simulation.getPoints().size() - 1);
        originPoint.position.set((Vector3dc)this.leashOriginAsync).sub((Vector3dc)simulation.getOffset());
        targetPoint.position.set((Vector3dc)this.leashTargetAsync).sub((Vector3dc)simulation.getOffset());
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
        armPoint.bufferPosition.set((Vector3dc)this.leashOrigin).sub((Vector3dc)simulation.getOffset());
        armPoint.bufferPrevPosition.set((Vector3dc)armPoint.bufferPosition);
        hookPoint.bufferPosition.set((Vector3dc)this.leashTarget).sub((Vector3dc)simulation.getOffset());
        hookPoint.bufferPrevPosition.set((Vector3dc)hookPoint.bufferPosition);
    }

    @Override
    public void renderAfter(Matrix4fStack matrixStack, double delta, VerletSimulation simulation) {
    }

    @Override
    public void render(Matrix4fStack matrixStack, double delta, VerletSimulation simulation) {
    }
}

