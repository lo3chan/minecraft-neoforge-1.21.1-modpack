/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.entity.EntityRenderDispatcher
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.HumanoidArm
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.projectile.FishingHook
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.joml.Math;
import org.joml.Matrix4fStack;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class FishingHookConstraint
implements VerletConstraint {
    private Vector3d playerPosAsync = new Vector3d();
    private Vector3d hookPosAsync = new Vector3d();
    private Vector3d playerPos = new Vector3d();
    private Vector3d hookPos = new Vector3d();
    private FishingHook fishingHook;
    private Player player;
    private EntityRenderDispatcher entityRenderDispatcher;

    public FishingHookConstraint(VerletSimulation simulation, FishingHook fishingHook, Player player, EntityRenderDispatcher entityRenderDispatcher, float tickDelta) {
        int i;
        this.fishingHook = fishingHook;
        this.player = player;
        this.entityRenderDispatcher = entityRenderDispatcher;
        this.calculatePlayerAndHookPos(tickDelta, this.playerPos, this.hookPos);
        int pointCount = 48;
        double totalLength = ConfigClient.fishingLineLength;
        for (i = 0; i < pointCount; ++i) {
            float perc = (float)(i - 1) / (float)pointCount;
            Vector3d position = new Vector3d(Math.lerp((double)this.playerPos.x, (double)this.hookPos.x, (double)perc), Math.lerp((double)this.playerPos.y, (double)this.hookPos.y, (double)perc), Math.lerp((double)this.playerPos.z, (double)this.hookPos.z, (double)perc));
            VerletPoint point = new VerletPoint(position);
            point.uv.set(0.01f, 0.99f);
            point.rgba.set(0.0f, 0.0f, 0.0f, 1.0f);
            point.locked = i == 0 || i == pointCount - 1;
            simulation.addPoint(point);
        }
        for (i = 0; i < pointCount - 1; ++i) {
            simulation.addStick(new VerletStick(simulation.getPoints().get(i), simulation.getPoints().get(i + 1), totalLength / (double)pointCount));
            simulation.addLine(new VerletLine(simulation.getPoints().get(i), simulation.getPoints().get(i + 1)));
        }
    }

    private void calculatePlayerAndHookPos(float tickDelta, Vector3d playerPos, Vector3d hookPos) {
        float playerEyeHeight;
        double playerZ;
        double playerY;
        double playerX;
        double hookX;
        int arm = this.player.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
        ItemStack itemStack = this.player.getMainHandItem();
        if (!itemStack.is(Items.FISHING_ROD)) {
            arm = -arm;
        }
        float attackRotation = this.player.getAttackAnim(tickDelta);
        float attackRotationSin = Mth.sin((float)(Mth.sqrt((float)attackRotation) * (float)java.lang.Math.PI));
        float bodyRotation = Mth.lerp((float)tickDelta, (float)this.player.yBodyRotO, (float)this.player.yBodyRot) * ((float)java.lang.Math.PI / 180);
        double bodyRotationSin = Mth.sin((float)bodyRotation);
        double bodyRotationCos = Mth.cos((float)bodyRotation);
        double armShort = (double)arm * 0.35;
        if ((this.entityRenderDispatcher.options == null || this.entityRenderDispatcher.options.getCameraType().isFirstPerson()) && this.player == Minecraft.getInstance().player) {
            hookX = 960.0 / (double)((Integer)Minecraft.getInstance().options.fov().get()).floatValue();
            Vec3 firstPersonOffset = this.entityRenderDispatcher.camera.getNearPlane().getPointOnPlane((float)arm * 0.525f, -0.1f);
            firstPersonOffset = firstPersonOffset.scale(hookX);
            firstPersonOffset = firstPersonOffset.yRot(attackRotationSin * 0.5f);
            firstPersonOffset = firstPersonOffset.xRot(-attackRotationSin * 0.7f);
            playerX = Mth.lerp((double)tickDelta, (double)this.player.xo, (double)this.player.getX()) + firstPersonOffset.x;
            playerY = Mth.lerp((double)tickDelta, (double)this.player.yo, (double)this.player.getY()) + firstPersonOffset.y;
            playerZ = Mth.lerp((double)tickDelta, (double)this.player.zo, (double)this.player.getZ()) + firstPersonOffset.z;
            playerEyeHeight = this.player.getEyeHeight();
        } else {
            playerX = Mth.lerp((double)tickDelta, (double)this.player.xo, (double)this.player.getX()) - bodyRotationCos * armShort - bodyRotationSin * 0.8;
            playerY = this.player.yo + (double)this.player.getEyeHeight() + (this.player.getY() - this.player.yo) * (double)tickDelta - 0.45;
            playerZ = Mth.lerp((double)tickDelta, (double)this.player.zo, (double)this.player.getZ()) - bodyRotationSin * armShort + bodyRotationCos * 0.8;
            playerEyeHeight = this.player.isCrouching() ? -0.1875f : 0.0f;
        }
        hookX = Mth.lerp((double)tickDelta, (double)this.fishingHook.xo, (double)this.fishingHook.getX());
        double hookY = Mth.lerp((double)tickDelta, (double)this.fishingHook.yo, (double)this.fishingHook.getY()) + 0.25;
        double hookZ = Mth.lerp((double)tickDelta, (double)this.fishingHook.zo, (double)this.fishingHook.getZ());
        if (ConfigClient.areOceanPhysicsEnabled()) {
            OceanWorld oceanWorld = PhysicsMod.getInstance(this.player.level()).getPhysicsWorld().getOceanWorld();
            playerY += oceanWorld.computeYOffset(this.player.level(), (Entity)this.player, 1.0f);
            hookY += oceanWorld.computeYOffset(this.fishingHook.level(), (Entity)this.fishingHook, 1.0f);
        }
        playerPos.set(playerX, playerY + (double)playerEyeHeight, playerZ);
        hookPos.set(hookX, hookY, hookZ);
    }

    @Override
    public boolean initAsyncData(PhysicsWorld world, VerletSimulation simulation) {
        this.calculatePlayerAndHookPos(1.0f, this.playerPosAsync, this.hookPosAsync);
        return false;
    }

    @Override
    public void updateBefore(double delta, VerletSimulation simulation) {
        VerletPoint armPoint = simulation.getPoints().get(0);
        VerletPoint hookPoint = simulation.getPoints().get(simulation.getPoints().size() - 1);
        armPoint.position.set((Vector3dc)this.playerPosAsync).sub((Vector3dc)simulation.getOffset());
        hookPoint.position.set((Vector3dc)this.hookPosAsync).sub((Vector3dc)simulation.getOffset());
    }

    @Override
    public void subStep(double percent, VerletSimulation simulation) {
    }

    @Override
    public void updateAfter(double delta, VerletSimulation simulation) {
    }

    @Override
    public void renderBefore(Matrix4fStack matrixStack, double delta, VerletSimulation simulation) {
        this.calculatePlayerAndHookPos((float)delta, this.playerPos, this.hookPos);
        VerletPoint armPoint = simulation.getPoints().get(0);
        VerletPoint hookPoint = simulation.getPoints().get(simulation.getPoints().size() - 1);
        armPoint.bufferPosition.set((Vector3dc)this.playerPos).sub((Vector3dc)simulation.getOffset());
        armPoint.bufferPrevPosition.set((Vector3dc)armPoint.bufferPosition);
        hookPoint.bufferPosition.set((Vector3dc)this.hookPos).sub((Vector3dc)simulation.getOffset());
        hookPoint.bufferPrevPosition.set((Vector3dc)hookPoint.bufferPosition);
    }

    @Override
    public void renderAfter(Matrix4fStack matrixStack, double delta, VerletSimulation simulation) {
    }

    @Override
    public void render(Matrix4fStack matrixStack, double delta, VerletSimulation simulation) {
    }
}

