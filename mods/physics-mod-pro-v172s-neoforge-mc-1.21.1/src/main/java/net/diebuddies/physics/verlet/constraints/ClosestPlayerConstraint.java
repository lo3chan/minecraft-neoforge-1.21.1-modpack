/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.model.PlayerModel
 *  net.minecraft.client.model.geom.ModelPart
 *  net.minecraft.client.model.geom.PartPose
 *  net.minecraft.client.renderer.entity.LivingEntityRenderer
 *  net.minecraft.core.Direction
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Pose
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix4d
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fStack
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package net.diebuddies.physics.verlet.constraints;

import com.mojang.blaze3d.vertex.PoseStack;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import net.diebuddies.bridge.ReflectionsForge;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.verlet.VerletHelper;
import net.diebuddies.physics.verlet.VerletPoint;
import net.diebuddies.physics.verlet.VerletSimulation;
import net.diebuddies.physics.verlet.constraints.ModelCube;
import net.diebuddies.physics.verlet.constraints.VerletConstraint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class ClosestPlayerConstraint
implements VerletConstraint {
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
        for (int i = 0; i < this.modelCubes.length; ++i) {
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
            Vector3d pos = simulation.getPoints().get((int)0).position;
            px += pos.x;
            py += pos.y;
            pz += pos.z;
        }
        this.player = this.level.getNearestPlayer(px, py, pz, 10.0, false);
        if (this.player != null) {
            int i;
            LivingEntityRenderer renderer = (LivingEntityRenderer)Minecraft.getInstance().getEntityRenderDispatcher().getRenderer((Entity)this.player);
            this.model = (PlayerModel)renderer.getModel();
            this.modelCubes[0].part = this.model.hat;
            this.modelCubes[1].part = this.model.body;
            this.modelCubes[2].part = this.model.rightArm;
            this.modelCubes[3].part = this.model.leftArm;
            this.modelCubes[4].part = this.model.rightLeg;
            this.modelCubes[5].part = this.model.leftLeg;
            for (i = 0; i < this.modelCubes.length; ++i) {
                this.modelCubes[i].pose = this.modelCubes[i].part.storePose();
                this.modelCubes[i].updateHitbox();
            }
            this.modelMatrix.pushPose();
            this.setupModelAnimations(1.0f);
            this.playerTransformation(this.modelMatrix, simulation, this.player, 1.0f, 1.0f);
            for (i = 0; i < this.modelCubes.length; ++i) {
                ModelCube modelCube = this.modelCubes[i];
                Matrix4f currentPose = this.modelMatrix.last().pose();
                modelCube.transform.set((Matrix4fc)currentPose);
                this.translateAndRotate(modelCube.transform, modelCube.pose);
            }
            this.modelMatrix.popPose();
        }
        return false;
    }

    @Override
    public void updateBefore(double delta, VerletSimulation simulation) {
        for (int i = 0; i < this.modelCubes.length; ++i) {
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
        Direction direction;
        LivingEntityRenderer renderer = (LivingEntityRenderer)Minecraft.getInstance().getEntityRenderDispatcher().getRenderer((Entity)player);
        this.playerx = Mth.lerp((double)tickDelta, (double)player.xOld, (double)player.getX());
        this.playery = Mth.lerp((double)tickDelta, (double)player.yOld, (double)player.getY());
        this.playerz = Mth.lerp((double)tickDelta, (double)player.zOld, (double)player.getZ());
        this.playerx -= simulation.getOffset().x;
        this.playery -= simulation.getOffset().y;
        this.playerz -= simulation.getOffset().z;
        Vec3 positionOffset = renderer.getRenderOffset((Entity)player, tickDelta);
        modelMatrix.translate(positionOffset.x + this.playerx, positionOffset.y + this.playery, positionOffset.z + this.playerz);
        float yaw = Mth.rotLerp((float)tickDelta, (float)player.yBodyRotO, (float)player.yBodyRot);
        if (player.getPose() == Pose.SLEEPING && (direction = player.getBedOrientation()) != null) {
            float eyeHeight = player.getEyeHeight(Pose.STANDING) - 0.1f;
            modelMatrix.translate((double)((float)(-direction.getStepX()) * eyeHeight), 0.0, (double)((float)(-direction.getStepZ()) * eyeHeight));
        }
        try {
            ReflectionsForge.setupRotations.invoke(renderer, player, modelMatrix, Float.valueOf(animationProgress), Float.valueOf(yaw), Float.valueOf(tickDelta), Float.valueOf(1.0f));
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        modelMatrix.scale(-1.0f, -1.0f, 1.0f);
        modelMatrix.scale(0.9375f, 0.9375f, 0.9375f);
        modelMatrix.translate(0.0, (double)-1.501f, 0.0);
    }

    private void doCollisionCheck(double percent, VerletSimulation simulation) {
        float enlarge = 0.175f;
        if (simulation.aabb.distanceSquared(this.playerx, this.playery, this.playerz) > 16.0) {
            return;
        }
        for (int i = 0; i < this.modelCubes.length; ++i) {
            ModelCube modelCube = this.modelCubes[i];
            ModelPart part = modelCube.part;
            if (part == null || part.cubes.isEmpty()) continue;
            modelCube.getTransform(percent, this.transform);
            this.transform.invert(this.invTransform);
            float minX = modelCube.minX - enlarge;
            float minY = modelCube.minY - enlarge;
            float minZ = modelCube.minZ - enlarge;
            float maxX = modelCube.maxX + enlarge;
            float maxY = modelCube.maxY + enlarge;
            float maxZ = modelCube.maxZ + enlarge;
            List<VerletPoint> points = simulation.getPoints();
            for (int j = 0; j < points.size(); ++j) {
                VerletPoint point = points.get(j);
                if (point.locked) continue;
                this.invTransform.transformPosition(this.invPoint.set((Vector3dc)point.position));
                if (!this.helper.movePointOutOfBox(this.invPoint, minX, minY, minZ, maxX, maxY, maxZ)) continue;
                point.position.set((Vector3dc)this.transform.transformPosition(this.invPoint));
                point.friction = 0.6;
            }
        }
    }

    private void setupModelAnimations(float tickDelta) {
        Direction direction;
        float o;
        Entity entity;
        float h = Mth.rotLerp((float)tickDelta, (float)this.player.yBodyRotO, (float)this.player.yBodyRot);
        float j = Mth.rotLerp((float)tickDelta, (float)this.player.yHeadRotO, (float)this.player.yHeadRot);
        float k = j - h;
        if (this.player.isPassenger() && (entity = this.player.getVehicle()) instanceof LivingEntity) {
            LivingEntity livingEntity2 = (LivingEntity)entity;
            h = Mth.rotLerp((float)tickDelta, (float)livingEntity2.yBodyRotO, (float)livingEntity2.yBodyRot);
            k = j - h;
            o = Mth.wrapDegrees((float)k);
            if (o < -85.0f) {
                o = -85.0f;
            }
            if (o >= 85.0f) {
                o = 85.0f;
            }
            h = j - o;
            if (o * o > 2500.0f) {
                h += o * 0.2f;
            }
            k = j - h;
        }
        float m = Mth.lerp((float)tickDelta, (float)this.player.xRotO, (float)this.player.getXRot());
        if (this.player.getPose() == Pose.SLEEPING && (direction = this.player.getBedOrientation()) != null) {
            float f = this.player.getEyeHeight(Pose.STANDING) - 0.1f;
        }
        o = tickDelta;
        float p = 0.0f;
        float q = 0.0f;
        if (!this.player.isPassenger() && this.player.isAlive()) {
            p = this.player.walkAnimation.speed(tickDelta);
            o = this.player.walkAnimation.position(tickDelta);
            if (this.player.isBaby()) {
                o *= 3.0f;
            }
            if (p > 1.0f) {
                p = 1.0f;
            }
        }
        this.model.crouching = this.player.isCrouching();
        this.model.setupAnim((LivingEntity)this.player, q, p, o, k, m);
    }

    public void translateAndRotate(Matrix4d transform, PartPose pose) {
        transform.translate((double)(pose.x / 16.0f), (double)(pose.y / 16.0f), (double)(pose.z / 16.0f));
        if (pose.zRot != 0.0f) {
            transform.rotate((Quaternionfc)this.tmpQuat.rotationZ(pose.zRot));
        }
        if (pose.yRot != 0.0f) {
            transform.rotate((Quaternionfc)this.tmpQuat.rotationY(pose.yRot));
        }
        if (pose.xRot != 0.0f) {
            transform.rotate((Quaternionfc)this.tmpQuat.rotationX(pose.xRot));
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

