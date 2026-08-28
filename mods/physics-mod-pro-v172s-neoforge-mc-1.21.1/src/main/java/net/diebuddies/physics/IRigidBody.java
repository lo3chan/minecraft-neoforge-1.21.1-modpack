/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.BubbleColumnBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.EmptyFluid
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix4dc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3d
 *  org.joml.Vector3f
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 */
package net.diebuddies.physics;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.math.Math;
import net.diebuddies.math.Vector3i;
import net.diebuddies.minecraft.weather.WeatherEffects;
import net.diebuddies.physics.PhysicsEntity;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.ocean.OceanWorld;
import net.diebuddies.physics.smoke.ParticleInfo;
import net.diebuddies.physics.wind.WeatherDomain;
import net.diebuddies.util.DoublyLinkedList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.EmptyFluid;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4dc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import physx.NativeObject;
import physx.common.PxVec3;
import physx.physics.PxActorFlagEnum;
import physx.physics.PxArticulationLink;
import physx.physics.PxForceModeEnum;
import physx.physics.PxRigidActor;
import physx.physics.PxRigidBody;
import physx.physics.PxRigidBodyFlagEnum;
import physx.physics.PxRigidDynamic;
import physx.physics.PxShape;

public abstract class IRigidBody
implements DoublyLinkedList.NodeStorage<IRigidBody> {
    private static int counter;
    private int hashCode;
    private PhysicsWorld physics;
    protected PxShape shape;
    protected PxRigidActor rigidBody;
    protected PhysicsEntity entity;
    protected Object userData;
    private float mass;
    private BlockPos.MutableBlockPos blockPos;
    private BlockState blockState;
    private Vector3d fluidVelocity;
    private Vector3i lastChunk;
    private float fluidHeight;
    private float angularDamping;
    private float linearDamping;
    private boolean inWater;
    private boolean wasSleeping = false;
    private boolean isSleeping = false;
    private boolean kinematic;
    private boolean frozen;
    private boolean gravity = true;
    public boolean liquid = false;
    public boolean smoke = false;
    private boolean gravityBefore = true;
    private boolean changedTransformation = false;
    public boolean separateController;
    protected boolean destroyed = false;
    private Vector3f changedTranslation = new Vector3f();
    private Quaternionf changedRotation = new Quaternionf();
    private BlockPos.MutableBlockPos tmpPos;
    private DoublyLinkedList.Node<IRigidBody> node;

    public IRigidBody() {
        this.hashCode = counter++;
        this.blockPos = new BlockPos.MutableBlockPos();
        this.tmpPos = new BlockPos.MutableBlockPos();
        this.fluidVelocity = new Vector3d();
    }

    public void destroy() {
        if (!this.destroyed) {
            if (this.entity != null) {
                this.entity.destroy();
            }
            if (this.shape != null) {
                this.shape.release();
            }
            if (this.rigidBody != null) {
                this.rigidBody.release();
            }
        }
        this.destroyed = true;
    }

    public boolean isDestroyed() {
        return this.destroyed;
    }

    public PhysicsEntity getEntity() {
        return this.entity;
    }

    public Object getUserData() {
        return this.userData;
    }

    public void setUserData(Object userData) {
        this.userData = userData;
    }

    public PxShape getShape() {
        return this.shape;
    }

    public PxRigidActor getRigidBody() {
        return this.rigidBody;
    }

    public void updateTransformations(PhysicsWorld physics, double diff) {
        this.physics = physics;
        if (this.changedTransformation) {
            if (this.liquid) {
                double yPos;
                this.entity.getOldTransformation().set((Matrix4dc)this.entity.getTransformation());
                if (this.entity.getRotation() != null) {
                    this.entity.getOldRotation().set((Quaternionfc)this.entity.getRotation());
                }
                if ((yPos = (double)this.changedTranslation.y + physics.getOffset().y) < (double)(physics.getWorld().getMinBuildHeight() - 10)) {
                    this.entity.startDespawnAnimation(physics.getWorld());
                }
                this.entity.getTransformation().translation((double)this.changedTranslation.x, (double)this.changedTranslation.y, (double)this.changedTranslation.z);
            } else if (!this.smoke) {
                double yPos;
                this.entity.getOldTransformation().set((Matrix4dc)this.entity.getTransformation());
                if (this.entity.getRotation() != null) {
                    this.entity.getOldRotation().set((Quaternionfc)this.entity.getRotation());
                }
                if ((yPos = (double)this.changedTranslation.y + physics.getOffset().y) < (double)(physics.getWorld().getMinBuildHeight() - 10)) {
                    this.entity.startDespawnAnimation(physics.getWorld());
                }
                this.entity.getTransformation().translationRotate((double)this.changedTranslation.x, (double)this.changedTranslation.y, (double)this.changedTranslation.z, (double)this.changedRotation.x, (double)this.changedRotation.y, (double)this.changedRotation.z, (double)this.changedRotation.w);
                if (this.entity.getRotation() != null) {
                    this.entity.getRotation().set((Quaternionfc)this.changedRotation);
                }
            }
        }
        this.changedTransformation = false;
    }

    public void setPhysicsWorld(PhysicsWorld physics) {
        this.physics = physics;
    }

    public void updatePhysics(PhysicsWorld physics, double diff, boolean blocksChanged) {
        float posZ;
        float posY;
        float posX;
        NativeObject transform;
        PxRigidActor rigidBody;
        PxRigidActor pxRigidActor;
        this.physics = physics;
        if (ConfigClient.windPhysics && (pxRigidActor = this.getRigidBody()) instanceof PxRigidBody) {
            rigidBody = (PxRigidBody)pxRigidActor;
            if (!this.isKinematicOrFrozen()) {
                int rz;
                int ry;
                int rx;
                transform = rigidBody.getGlobalPose();
                posX = MemoryUtil.memGetFloat((long)(transform.getAddress() + 16L));
                posY = MemoryUtil.memGetFloat((long)(transform.getAddress() + 20L));
                posZ = MemoryUtil.memGetFloat((long)(transform.getAddress() + 24L));
                Vector3d offset = physics.getOffset();
                WeatherDomain weatherDomain = physics.getWeatherDomain();
                float forceStrength = weatherDomain.getWindStrength(rx = Mth.floor((double)((double)posX + offset.x)), ry = Mth.floor((double)((double)posY + offset.y)), rz = Mth.floor((double)((double)posZ + offset.z)));
                if (forceStrength > 0.001f) {
                    Vector3f windDirection = weatherDomain.getWindDirection(rx, ry, rz);
                    try (MemoryStack mem = MemoryStack.stackPush();){
                        float strengthMultiplier = 0.35f;
                        PxVec3 counterForce = PxVec3.createAt(mem, MemoryStack::nmalloc, windDirection.x * (forceStrength *= strengthMultiplier), windDirection.y * forceStrength * 0.2f, windDirection.z * forceStrength);
                        ((PxRigidBody)rigidBody).addForce(counterForce, PxForceModeEnum.eVELOCITY_CHANGE, true);
                    }
                }
            }
        }
        if (this.liquid) {
            rigidBody = this.getRigidBody();
            transform = rigidBody.getGlobalPose();
            posX = MemoryUtil.memGetFloat((long)(transform.getAddress() + 16L));
            posY = MemoryUtil.memGetFloat((long)(transform.getAddress() + 20L));
            posZ = MemoryUtil.memGetFloat((long)(transform.getAddress() + 24L));
            this.loadChunkPhysics(posX, posY, posZ);
            this.changedTransformation = true;
            this.changedTranslation.set(posX, posY, posZ);
        } else if (this.smoke) {
            ParticleInfo info = (ParticleInfo)this.getUserData();
            this.loadChunkPhysics((float)info.pos.x, (float)info.pos.y, (float)info.pos.z);
            this.changedTransformation = true;
        } else {
            PxRigidActor rotW2;
            boolean waterBlockForce;
            this.isSleeping = false;
            transform = this.rigidBody;
            if (transform instanceof PxRigidDynamic) {
                PxRigidDynamic dynamicBody = (PxRigidDynamic)transform;
                this.isSleeping = dynamicBody.isSleeping();
            }
            Vector3d waveForce = null;
            if (!this.wasSleeping || !this.isSleeping) {
                this.changedTransformation = true;
                this.entity.getOldTransformation().set((Matrix4dc)this.entity.getTransformation());
                if (this.entity.getRotation() != null) {
                    this.entity.getOldRotation().set((Quaternionfc)this.entity.getRotation());
                }
                transform = this.rigidBody.getGlobalPose();
                float rotX = MemoryUtil.memGetFloat((long)transform.getAddress());
                float rotY = MemoryUtil.memGetFloat((long)(transform.getAddress() + 4L));
                float rotZ = MemoryUtil.memGetFloat((long)(transform.getAddress() + 8L));
                float rotW2 = MemoryUtil.memGetFloat((long)(transform.getAddress() + 12L));
                float posX2 = MemoryUtil.memGetFloat((long)(transform.getAddress() + 16L));
                float posY2 = MemoryUtil.memGetFloat((long)(transform.getAddress() + 20L));
                float posZ2 = MemoryUtil.memGetFloat((long)(transform.getAddress() + 24L));
                if (ConfigClient.areOceanPhysicsEnabled() && !this.isKinematicOrFrozen()) {
                    OceanWorld oceanWorld = physics.getOceanWorld();
                    Vector3d offset = physics.getOffset();
                    double worldX = (double)posX2 + offset.x;
                    double worldY = (double)posY2 + offset.y;
                    double worldZ = (double)posZ2 + offset.z;
                    waveForce = oceanWorld.calculateWaveForce(worldX, worldY, worldZ);
                }
                this.changedTranslation.set(posX2, posY2, posZ2);
                this.changedRotation.set(rotX, rotY, rotZ, rotW2).normalize();
                this.loadChunkPhysics(posX2, posY2, posZ2);
                if (this.blockState == null || !this.tmpPos.equals((Object)this.blockPos) || blocksChanged) {
                    BlockPos.MutableBlockPos tmp = this.blockPos;
                    this.blockPos = this.tmpPos;
                    this.tmpPos = tmp;
                    this.blockState = physics.getWorld().getBlockState((BlockPos)this.blockPos);
                    if (!(this.blockState.getFluidState().getType() instanceof EmptyFluid)) {
                        this.fluidHeight = this.blockState.getFluidState().getOwnHeight();
                        if (this.blockState.getFluidState().getType().isSame(physics.getWorld().getFluidState((BlockPos)this.tmpPos.set((Vec3i)this.blockPos).move(0, 1, 0)).getType())) {
                            this.fluidHeight = 1.0f;
                        }
                        Vec3 fv = this.blockState.getFluidState().getFlow((BlockGetter)physics.getWorld(), (BlockPos)this.blockPos);
                        this.fluidVelocity.set(fv.x(), fv.y(), fv.z());
                        boolean bubble = false;
                        if (this.blockState.getBlock() == Blocks.BUBBLE_COLUMN) {
                            bubble = true;
                            boolean drag = (Boolean)this.blockState.getValue((Property)BubbleColumnBlock.DRAG_DOWN);
                            this.fluidVelocity.y = drag ? -2.0 : 8.0;
                        }
                        this.fluidVelocity.y = bubble ? this.fluidVelocity.y : java.lang.Math.max(this.fluidVelocity.y, 0.0);
                    } else {
                        this.fluidHeight = -1.0f;
                        this.fluidVelocity.set(0.0);
                    }
                }
            }
            this.wasSleeping = this.isSleeping;
            float height = this.getFluidHeight();
            boolean oceanWaveForce = waveForce != null && waveForce.y >= 0.0;
            boolean bl = waterBlockForce = height >= 0.0f && (this.getEntity().getTransformation().m31() + physics.getOffset().y) % 1.0 < (double)height;
            if ((oceanWaveForce || waterBlockForce && (waveForce == null || waveForce.y >= 0.0)) && (rotW2 = this.getRigidBody()) instanceof PxRigidBody) {
                PxRigidBody rigidBody2 = (PxRigidBody)rotW2;
                if (!this.inWater) {
                    float vy;
                    this.inWater = true;
                    this.gravityBefore = this.gravity;
                    this.setGravity(false);
                    if (ConfigClient.areOceanPhysicsEnabled() && !this.isKinematicOrFrozen() && (ConfigClient.oceanRipples || ConfigClient.oceanParticles) && (double)java.lang.Math.abs(vy = rigidBody2.getLinearVelocity().getY()) > 4.52) {
                        Vector3d offset = physics.getOffset();
                        double worldX = this.entity.getTransformation().m30() + offset.x;
                        double worldY = this.entity.getTransformation().m31() + offset.y;
                        double worldZ = this.entity.getTransformation().m32() + offset.z;
                        double objectSize = this.entity.getBoundingSphereRadius();
                        if (ConfigClient.oceanRipples) {
                            double speed = Math.remapClamp(objectSize, 0.1, 2.0, 0.0325, 0.0625);
                            int amount = (int)Math.remapClamp(objectSize, 0.1, 2.0, 140.0, 240.0);
                            int lifetime = (int)Math.remapClamp(objectSize, 0.1, 2.0, 60.0, 80.0);
                            float scale = (float)Math.remapClamp(objectSize, 0.1, 2.0, 0.15, 0.65);
                            OceanWorld oceanWorld = physics.getOceanWorld();
                            oceanWorld.spawnRipple(amount, lifetime, scale, worldX, worldY, worldZ, speed);
                        }
                        if (objectSize > 0.4) {
                            int splashamount = (int)Math.remapClamp(objectSize, 0.4, 2.0, 30.0, 75.0);
                            double intensity = Math.remapClamp(objectSize, 0.4, 2.0, 0.25, 0.7);
                            float volume = (float)intensity * ConfigClient.oceanSplashVolume;
                            float pitch = Math.random() * 0.4f + 0.7f;
                            Level level = physics.getLevel();
                            level.playLocalSound(worldX, worldY, worldZ, WeatherEffects.SPLASH_SOUND_EVENT, SoundSource.AMBIENT, volume, pitch, true);
                            if (ConfigClient.oceanParticles) {
                                OceanWorld.createWaterSplash(level, worldX, worldY, worldZ, 0.0, 0.0, 0.0, 0.25, intensity, splashamount);
                            }
                        }
                    }
                }
                float mass = this.getMass();
                float flowStrength = 4.6f;
                this.setAngularDamping(2.355f);
                this.setLinearDamping(2.355f);
                try (MemoryStack mem = MemoryStack.stackPush();){
                    Vector3d direction = this.getFluidVelocity();
                    Vector3f buoyancy = physics.getDynamicsWorld().getBuoyancy();
                    if (rigidBody2 instanceof PxArticulationLink) {
                        PxVec3 v = rigidBody2.getLinearVelocity();
                        PxVec3 a = rigidBody2.getAngularVelocity();
                        float damping = 0.885f;
                        float aDamping = 0.885f;
                        float vx = MemoryUtil.memGetFloat((long)v.getAddress());
                        float vy = MemoryUtil.memGetFloat((long)(v.getAddress() + 4L));
                        float vz = MemoryUtil.memGetFloat((long)(v.getAddress() + 8L));
                        float ax = MemoryUtil.memGetFloat((long)a.getAddress());
                        float ay = MemoryUtil.memGetFloat((long)(a.getAddress() + 4L));
                        float az = MemoryUtil.memGetFloat((long)(a.getAddress() + 8L));
                        PxVec3 flowForce = PxVec3.createAt(mem, MemoryStack::nmalloc, -vx * (1.0f - damping), -vy * (1.0f - damping), -vz * (1.0f - damping));
                        rigidBody2.addForce(flowForce, PxForceModeEnum.eVELOCITY_CHANGE);
                        PxVec3 angularForce = PxVec3.createAt(mem, MemoryStack::nmalloc, -ax * (1.0f - aDamping), -ay * (1.0f - aDamping), -az * (1.0f - aDamping));
                        rigidBody2.addTorque(angularForce, PxForceModeEnum.eVELOCITY_CHANGE);
                    }
                    float forceX = buoyancy.x + (float)direction.x * flowStrength;
                    float forceY = buoyancy.y + (float)direction.y * flowStrength;
                    float forceZ = buoyancy.z + (float)direction.z * flowStrength;
                    if (waveForce != null) {
                        forceX = (float)((double)forceX + ((double)buoyancy.x * waveForce.y + -waveForce.x * 3.0));
                        forceY = (float)((double)forceY + (double)buoyancy.y * waveForce.y);
                        forceZ = (float)((double)forceZ + ((double)buoyancy.z * waveForce.y + -waveForce.z * 3.0));
                    }
                    PxVec3 counterForce = PxVec3.createAt(mem, MemoryStack::nmalloc, forceX * mass, forceY * mass, forceZ * mass);
                    rigidBody2.addForce(counterForce, PxForceModeEnum.eFORCE);
                }
            } else {
                if (this.inWater) {
                    this.setGravity(this.gravityBefore);
                    this.inWater = false;
                }
                this.setAngularDamping(0.0f);
                this.setLinearDamping(0.0f);
            }
        }
    }

    private void loadChunkPhysics(float x, float y, float z) {
        Vector3d offset = this.physics.getOffset();
        this.tmpPos.set((double)x + offset.x, (double)y + offset.y, (double)z + offset.z);
        if (!(this.blockState != null && this.tmpPos.equals((Object)this.blockPos) || this.isKinematicOrFrozen())) {
            int cx = this.tmpPos.getX() >> PhysicsWorld.CHUNK_SIZE_NUM_BITS;
            int cy = this.tmpPos.getY() >> PhysicsWorld.CHUNK_SIZE_NUM_BITS;
            int cz = this.tmpPos.getZ() >> PhysicsWorld.CHUNK_SIZE_NUM_BITS;
            if (this.lastChunk == null || !this.lastChunk.equals(cx, cy, cz)) {
                if (this.lastChunk == null) {
                    this.lastChunk = new Vector3i(cx, cy, cz);
                } else {
                    this.physics.removeLoadedChunkEntity(this.lastChunk);
                    this.lastChunk.set(cx, cy, cz);
                }
                this.physics.addLoadedChunkEntity(this.lastChunk);
            }
        }
    }

    public boolean hasTransformationChanged() {
        if (this.kinematic || this.frozen) {
            return false;
        }
        return !this.wasSleeping || !this.isSleeping;
    }

    public void setKinematic(boolean kinematic) {
        if (this.kinematic != kinematic) {
            if (this.rigidBody instanceof PxRigidDynamic) {
                ((PxRigidDynamic)this.rigidBody).setRigidBodyFlag(PxRigidBodyFlagEnum.eKINEMATIC, kinematic);
                if (!this.isKinematicOrFrozen() && this.physics != null && this.lastChunk != null && kinematic) {
                    this.physics.removeLoadedChunkEntity(this.lastChunk);
                    this.lastChunk = null;
                }
            }
            this.kinematic = kinematic;
        }
    }

    public boolean isKinematicOrFrozen() {
        return this.kinematic || this.frozen;
    }

    public void setFrozen(boolean frozen) {
        if (this.frozen != frozen) {
            if (frozen) {
                if (!this.isKinematicOrFrozen() && this.physics != null && this.lastChunk != null) {
                    this.physics.removeLoadedChunkEntity(this.lastChunk);
                    this.lastChunk = null;
                }
                this.entity.getOldTransformation().set((Matrix4dc)this.entity.getTransformation());
                if (this.entity.getRotation() != null) {
                    this.entity.getOldRotation().set((Quaternionfc)this.entity.getRotation());
                }
            }
            this.rigidBody.setActorFlag(PxActorFlagEnum.eDISABLE_SIMULATION, frozen);
            this.frozen = frozen;
        }
    }

    public void recalculateLight() {
        this.entity.invalidateBrightness();
    }

    public void setGravity(boolean gravity) {
        if (this.gravity != gravity) {
            this.rigidBody.setActorFlag(PxActorFlagEnum.eDISABLE_GRAVITY, !gravity);
            this.gravity = gravity;
        }
    }

    public void applyRandomSpawnForces(float strength) {
        PxRigidActor pxRigidActor = this.getRigidBody();
        if (pxRigidActor instanceof PxRigidDynamic) {
            PxRigidDynamic rigidBody = (PxRigidDynamic)pxRigidActor;
            PxVec3 v = rigidBody.getLinearVelocity();
            v.setX((Math.random() - 0.5f) * strength);
            v.setY((Math.random() - 0.5f) * strength);
            v.setZ((Math.random() - 0.5f) * strength);
            rigidBody.setLinearVelocity(v);
        }
    }

    public void applyRandomSpawnForces() {
        this.applyRandomSpawnForces(9.0f);
    }

    public boolean hasGravity() {
        return this.gravity;
    }

    public boolean isFrozen() {
        return this.frozen;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public float getMass() {
        return this.mass;
    }

    public BlockState getBlockState() {
        return this.blockState;
    }

    public BlockPos.MutableBlockPos getBlockPos() {
        return this.blockPos;
    }

    public float getFluidHeight() {
        return this.fluidHeight;
    }

    public Vector3d getFluidVelocity() {
        return this.fluidVelocity;
    }

    public void setAngularDamping(float angularDamping) {
        if (this.angularDamping != angularDamping) {
            this.angularDamping = angularDamping;
            ((PxRigidBody)this.rigidBody).setAngularDamping(angularDamping);
        }
    }

    public void setLinearDamping(float linearDamping) {
        if (this.linearDamping != linearDamping) {
            this.linearDamping = linearDamping;
            ((PxRigidBody)this.rigidBody).setLinearDamping(linearDamping);
        }
    }

    public boolean isInWater() {
        return this.inWater;
    }

    public Vector3i getLastChunk() {
        return this.lastChunk;
    }

    @Override
    public void setNode(DoublyLinkedList.Node<IRigidBody> node) {
        this.node = node;
    }

    @Override
    public DoublyLinkedList.Node<IRigidBody> getNode() {
        return this.node;
    }

    public int hashCode() {
        return this.hashCode;
    }
}

