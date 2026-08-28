/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  javax.annotation.Nullable
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.model.EntityModel
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.model.geom.ModelPart
 *  net.minecraft.client.model.geom.PartPose
 *  net.minecraft.client.renderer.entity.LivingEntityRenderer
 *  net.minecraft.core.Direction
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Pose
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix4d
 *  org.joml.Matrix4dc
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fStack
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.diebuddies.physics.verlet.constraints;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
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
import net.diebuddies.physics.verlet.constraints.ModelCube;
import net.diebuddies.physics.verlet.constraints.VerletConstraint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4d;
import org.joml.Matrix4dc;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class ModelPartConstraint
implements VerletConstraint {
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
    private CustomTransformation customTransformation;
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
        for (ModelPart part : ClothConstants.getModelParts(model)) {
            String name = ((ModelPartParent)part).physicsmod$getName();
            if (attachedToName.equals(name)) {
                this.attachedTo = new ModelCube();
                this.attachedTo.part = part;
                this.attachedTo.pose = part.storePose();
            }
            if (ignoreParts.contains(name)) continue;
            ModelCube modelCube = new ModelCube();
            modelCube.part = part;
            this.modelCubes.add(modelCube);
        }
        this.isHead = entity == Minecraft.getInstance().player;
    }

    private void storePoses() {
        for (int i = 0; i < this.modelCubes.size(); ++i) {
            ModelCube modelCube = this.modelCubes.get(i);
            modelCube.pose = modelCube.part.storePose();
            modelCube.updateHitbox();
        }
        this.attachedTo.pose = this.attachedTo.part.storePose();
        this.attachedTo.updateHitbox();
    }

    public static ModelPart getPart(Model model, String name) {
        for (ModelPart part : ClothConstants.getModelParts(model)) {
            if (!name.equals(((ModelPartParent)part).physicsmod$getName())) continue;
            return part;
        }
        return null;
    }

    public static boolean exists(EntityModel<LivingEntity> model, String name) {
        for (ModelPart part : ClothConstants.getModelParts(model)) {
            if (!name.equals(((ModelPartParent)part).physicsmod$getName())) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean initAsyncData(PhysicsWorld world, VerletSimulation simulation) {
        if (this.entity != null) {
            this.changeInstantly = this.lastCrouch != this.entity.isCrouching();
            this.lastCrouch = this.entity.isCrouching();
        }
        this.modelMatrix.pushPose();
        if (this.entity != null) {
            ModelPartConstraint.entityTransformation(this.modelMatrix, simulation, this.entity, this.model, 1.0f);
        } else if (this.customTransformation != null) {
            this.customTransformation.doTransformation(this.modelMatrix);
        }
        this.storePoses();
        for (int i = 0; i < this.modelCubes.size(); ++i) {
            ModelCube modelCube = this.modelCubes.get(i);
            modelCube.transform.set((Matrix4fc)this.modelMatrix.last().pose());
            this.translateAndRotate(modelCube.transform, modelCube.pose);
        }
        this.modelPartTransformation(this.modelMatrix.last().pose());
        if (this.oldPartTransformation != null) {
            this.oldPartTransformation.set((Matrix4dc)this.partTransformation);
        }
        this.partTransformation.set((Matrix4fc)this.modelMatrix.last().pose());
        if (this.oldPartTransformation == null) {
            this.oldPartTransformation = new Matrix4d();
            this.oldPartTransformation.set((Matrix4dc)this.partTransformation);
        }
        simulation.setTransformation(this.partTransformation);
        this.modelMatrix.popPose();
        if (this.entity != null) {
            if (this.entity.isUnderWater()) {
                OceanWorld oceanWorld = PhysicsMod.getInstance(this.entity.level()).getPhysicsWorld().getOceanWorld();
                Vector3d waveForce = oceanWorld.calculateWaveForce(this.entity.getX(), this.entity.getY(), this.entity.getZ());
                Vector3d gravity = simulation.getGravity();
                gravity.set((Vector3fc)ConfigClient.getBuoyancy(this.entity.getCommandSenderWorld().dimension().location()));
                if (waveForce != null) {
                    double forceStrength = 10.0;
                    gravity.add(waveForce.x * forceStrength, 0.0, waveForce.z * forceStrength);
                }
                simulation.setFriction(0.7f);
            } else {
                simulation.getGravity().set((Vector3fc)ConfigClient.getGravity(this.entity.getCommandSenderWorld().dimension().location()));
                simulation.setFriction(this.initialFriction);
            }
        }
        return this.changeInstantly;
    }

    @Override
    public void updateBefore(double delta, VerletSimulation simulation) {
        for (int i = 0; i < this.modelCubes.size(); ++i) {
            this.modelCubes.get(i).updateTransformation();
        }
    }

    private void updateFixedPoints(VerletSimulation simulation, Matrix4d currentPartTransformation) {
        List<VerletPoint> points = simulation.getPoints();
        double lengthDiff = 1000.0;
        ColladaMesh mesh = simulation.cloth.mesh;
        int size = mesh.positions.size();
        List<Vector3f> positions = mesh.positions;
        for (int i = 0; i < points.size() && i < size; ++i) {
            Vector3f pos;
            VerletPoint point = points.get(i);
            if (point.locked) {
                pos = positions.get(i);
                this.tmp.set((double)pos.x, (double)pos.y, (double)pos.z);
                currentPartTransformation.transformPosition(this.tmp);
                if (this.tmp.distanceSquared((Vector3dc)point.position) > lengthDiff) {
                    simulation.destroyed = true;
                }
                point.position.set((Vector3dc)this.tmp);
                if (!this.changeInstantly) continue;
                point.prevPosition.set((Vector3dc)point.position);
                continue;
            }
            if (point.softRestriction == null) continue;
            pos = positions.get(i);
            this.tmp.set((double)pos.x, (double)pos.y, (double)pos.z);
            currentPartTransformation.transformPosition(this.tmp);
            point.softRestriction.set((Vector3dc)this.tmp);
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
                ModelPartConstraint.entityTransformation(this.modelMatrix, simulation, this.entity, null, (float)delta);
            } else if (this.customTransformation != null) {
                this.customTransformation.doTransformation(this.modelMatrix);
            }
            this.attachedTo.transform.set((Matrix4fc)this.modelMatrix.last().pose());
            this.attachedTo.pose = this.attachedTo.part.storePose();
            this.translateAndRotate(this.attachedTo.transform, this.attachedTo.pose);
            this.currentPartTransformation.set((Matrix4dc)this.attachedTo.transform);
            List<VerletPoint> points = simulation.getPoints();
            ColladaMesh mesh = simulation.cloth.mesh;
            int size = mesh.positions.size();
            List<Vector3f> positions = mesh.positions;
            for (int i = 0; i < points.size() && i < size; ++i) {
                VerletPoint point = points.get(i);
                if (!point.locked) continue;
                Vector3f pos = positions.get(i);
                this.tmp.set((double)pos.x, (double)pos.y, (double)pos.z);
                this.currentPartTransformation.transformPosition(this.tmp);
                point.bufferPosition.set((Vector3dc)this.tmp);
                point.bufferPrevPosition.set((Vector3dc)this.tmp);
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
        Direction direction;
        LivingEntityRenderer renderer = (LivingEntityRenderer)Minecraft.getInstance().getEntityRenderDispatcher().getRenderer((Entity)entity);
        double px = Mth.lerp((double)tickDelta, (double)entity.xOld, (double)entity.getX());
        double py = Mth.lerp((double)tickDelta, (double)entity.yOld, (double)entity.getY());
        double pz = Mth.lerp((double)tickDelta, (double)entity.zOld, (double)entity.getZ());
        if (simulation.getOffset() != null) {
            px -= simulation.getOffset().x;
            py -= simulation.getOffset().y;
            pz -= simulation.getOffset().z;
        } else {
            px = 0.0;
            py = 0.0;
            pz = 0.0;
        }
        Vec3 positionOffset = renderer.getRenderOffset((Entity)entity, tickDelta);
        modelMatrix.translate(positionOffset.x + px, positionOffset.y + py, positionOffset.z + pz);
        float yaw = Mth.rotLerp((float)tickDelta, (float)entity.yBodyRotO, (float)entity.yBodyRot);
        if (entity.getPose() == Pose.SLEEPING && (direction = entity.getBedOrientation()) != null) {
            float eyeHeight = entity.getEyeHeight(Pose.STANDING) - 0.1f;
            modelMatrix.translate((double)((float)(-direction.getStepX()) * eyeHeight), 0.0, (double)((float)(-direction.getStepZ()) * eyeHeight));
        }
        elytraFix.set((Matrix4fc)modelMatrix.last().pose());
        try {
            ReflectionsForge.setupRotations.invoke(renderer, entity, modelMatrix, Float.valueOf(tickDelta), Float.valueOf(yaw), Float.valueOf(tickDelta), Float.valueOf(1.0f));
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        if (!modelMatrix.last().pose().isFinite()) {
            modelMatrix.last().pose().set((Matrix4dc)elytraFix);
        }
        modelMatrix.scale(-1.0f, -1.0f, 1.0f);
        modelMatrix.scale(0.9375f, 0.9375f, 0.9375f);
        modelMatrix.translate(0.0, (double)-1.501f, 0.0);
        if (model != null) {
            float bodyRot = Mth.rotLerp((float)tickDelta, (float)entity.yBodyRotO, (float)entity.yBodyRot);
            float headRot = Mth.rotLerp((float)tickDelta, (float)entity.yHeadRotO, (float)entity.yHeadRot);
            float headBodyDiff = headRot - bodyRot;
            if (entity.isPassenger() && entity.getVehicle() instanceof LivingEntity) {
                LivingEntity vehicle = (LivingEntity)entity.getVehicle();
                bodyRot = Mth.rotLerp((float)tickDelta, (float)vehicle.yBodyRotO, (float)vehicle.yBodyRot);
                headBodyDiff = headRot - bodyRot;
                float diffWrapped = Mth.wrapDegrees((float)headBodyDiff);
                if (diffWrapped < -85.0f) {
                    diffWrapped = -85.0f;
                }
                if (diffWrapped >= 85.0f) {
                    diffWrapped = 85.0f;
                }
                bodyRot = headRot - diffWrapped;
                if (diffWrapped * diffWrapped > 2500.0f) {
                    bodyRot += diffWrapped * 0.2f;
                }
                headBodyDiff = headRot - bodyRot;
            }
            float xRot = Mth.lerp((float)tickDelta, (float)entity.xRotO, (float)entity.getXRot());
            if (LivingEntityRenderer.isEntityUpsideDown((LivingEntity)entity)) {
                xRot *= -1.0f;
                headBodyDiff *= -1.0f;
            }
            float bob = (float)entity.tickCount + tickDelta;
            float animationSpeed = 0.0f;
            float animationPosition = 0.0f;
            if (!entity.isPassenger() && entity.isAlive()) {
                animationSpeed = entity.walkAnimation.speed(tickDelta);
                animationPosition = entity.walkAnimation.position(tickDelta);
                if (entity.isBaby()) {
                    animationPosition *= 3.0f;
                }
                if (animationSpeed > 1.0f) {
                    animationSpeed = 1.0f;
                }
            }
            if (model instanceof EntityModel) {
                EntityModel entityModel = (EntityModel)model;
                entityModel.prepareMobModel((Entity)entity, animationPosition, animationSpeed, tickDelta);
                entityModel.setupAnim((Entity)entity, animationPosition, animationSpeed, bob, headBodyDiff, xRot);
            }
        }
    }

    public static void entityTransformation(PoseStack modelMatrix, LivingEntity entity, float tickDelta) {
        Direction direction;
        LivingEntityRenderer renderer = (LivingEntityRenderer)Minecraft.getInstance().getEntityRenderDispatcher().getRenderer((Entity)entity);
        Vec3 positionOffset = renderer.getRenderOffset((Entity)entity, tickDelta);
        modelMatrix.translate(positionOffset.x, positionOffset.y, positionOffset.z);
        float yaw = Mth.rotLerp((float)tickDelta, (float)entity.yBodyRotO, (float)entity.yBodyRot);
        if (entity.getPose() == Pose.SLEEPING && (direction = entity.getBedOrientation()) != null) {
            float eyeHeight = entity.getEyeHeight(Pose.STANDING) - 0.1f;
            modelMatrix.translate((double)((float)(-direction.getStepX()) * eyeHeight), 0.0, (double)((float)(-direction.getStepZ()) * eyeHeight));
        }
        try {
            ReflectionsForge.setupRotations.invoke(renderer, entity, modelMatrix, Float.valueOf(tickDelta), Float.valueOf(yaw), Float.valueOf(tickDelta), Float.valueOf(1.0f));
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        modelMatrix.scale(-1.0f, -1.0f, 1.0f);
        modelMatrix.scale(0.9375f, 0.9375f, 0.9375f);
        modelMatrix.translate(0.0, (double)-1.501f, 0.0);
    }

    private void doCollisionCheck(double percent, VerletSimulation simulation) {
        for (int i = 0; i < this.modelCubes.size(); ++i) {
            ModelCube modelCube = this.modelCubes.get(i);
            ModelPart part = modelCube.part;
            if (part == null || part.cubes.isEmpty()) continue;
            modelCube.getTransform(percent, this.transform);
            this.transform.invert(this.invTransform);
            float enlarge = 0.075f;
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
                if (!this.changeInstantly) continue;
                point.prevPosition.set((Vector3dc)point.position);
            }
        }
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

    public void translateAndRotate(Matrix4f transform, PartPose pose) {
        transform.translate(pose.x / 16.0f, pose.y / 16.0f, pose.z / 16.0f);
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

    public ModelCube getAttachedToPart() {
        return this.attachedTo;
    }

    public void setCustomTransformation(CustomTransformation customTransformation) {
        this.customTransformation = customTransformation;
    }

    public Matrix4d getCurrentPartTransformation(double percent) {
        if (this.changeInstantly) {
            return this.tmpMat.set((Matrix4dc)this.partTransformation);
        }
        return MatrixUtil.slerp(this.oldPartTransformation, this.partTransformation, percent, this.tmpMat);
    }

    public static interface CustomTransformation {
        public void doTransformation(PoseStack var1);
    }
}

