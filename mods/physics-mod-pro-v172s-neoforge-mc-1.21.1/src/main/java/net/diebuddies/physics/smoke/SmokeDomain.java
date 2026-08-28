/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2BooleanMap
 *  it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.joml.Math
 *  org.joml.Matrix4d
 *  org.joml.Vector3d
 *  org.joml.Vector3f
 *  org.joml.Vector3i
 *  org.joml.Vector3ic
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 */
package net.diebuddies.physics.smoke;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.math.Math;
import net.diebuddies.physics.Explosion;
import net.diebuddies.physics.IRigidBody;
import net.diebuddies.physics.PhysicsEntity;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.animation.Animation;
import net.diebuddies.physics.animation.AnimationType;
import net.diebuddies.physics.animation.CurveType;
import net.diebuddies.physics.smoke.ParticleInfo;
import net.diebuddies.physics.smoke.SmokeUpdateCallback;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.physics.PxForceModeEnum;
import physx.physics.PxRigidBody;
import physx.physics.PxRigidDynamic;

public class SmokeDomain {
    private static final double EFFECT_DISTANCE = 0.5;
    private static final double EFFECT_DISTANCE_INV = 2.0;
    private static final double EFFECT_DISTANCE_SQUARED = 0.25;
    private static final double DENSITY_DISTANCE = 1.0;
    private static final double DENSITY_DISTANCE_SQUARED = 1.0;
    private static final double EFFECT_STRENGTH = 2.25;
    private static final float MAX_SPEED = 2.9f;
    private static final float DESPAWN_ANIMATION_TIME = 2.0f;
    private static final float DAMPING = 0.07f;
    private static final float GRAVITY_MODIFIER = 0.7f;
    private static final int CHUNK_SIZE = (int)java.lang.Math.round(java.lang.Math.ceil(0.5));
    private Animation smokeDespawn;
    private final Map<Vector3i, ChunkInfo> chunks;
    private final Object2BooleanMap<Vector3i> masks;
    private final List<IRigidBody> allParticles;
    private final List<IRigidBody> changedParticles;
    private final Vector3i tmp = new Vector3i();
    public int density;
    public PhysicsWorld world;
    public Random random;
    public SmokeUpdateCallback smokeUpdateCallback;

    public SmokeDomain(PhysicsWorld world) {
        this.world = world;
        this.chunks = new Object2ObjectOpenHashMap();
        this.masks = new Object2BooleanOpenHashMap();
        this.masks.defaultReturnValue(false);
        this.changedParticles = new ObjectArrayList();
        this.allParticles = new ObjectArrayList();
        this.random = new Random(System.nanoTime());
        this.smokeDespawn = new Animation("smoke_vanish", CurveType.Ease_out, 2.0f);
        this.smokeDespawn.despawnType = AnimationType.Vanish;
    }

    public void update(double diff) {
        this.updateParticles(diff);
        if (this.smokeUpdateCallback != null) {
            this.smokeUpdateCallback.smokeUpdate(this);
        }
    }

    private void updateParticles(double diff) {
        Vector3i chunk;
        int i;
        Iterator<Map.Entry<Vector3i, ChunkInfo>> it = this.chunks.entrySet().iterator();
        this.changedParticles.clear();
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 camPos = camera.getPosition();
        double maxSmokeDistance = ConfigClient.smokePhysicsRange * ConfigClient.smokePhysicsRange;
        Vector3d physicsOffset = this.world.getOffset();
        for (i = 0; i < this.allParticles.size(); ++i) {
            IRigidBody body = this.allParticles.get(i);
            if (body.isDestroyed()) continue;
            PxTransform transform = body.getRigidBody().getGlobalPose();
            float posX = MemoryUtil.memGetFloat((long)(transform.getAddress() + 16L));
            float posY = MemoryUtil.memGetFloat((long)(transform.getAddress() + 20L));
            float posZ = MemoryUtil.memGetFloat((long)(transform.getAddress() + 24L));
            ParticleInfo info = (ParticleInfo)body.getUserData();
            info.pos.set((double)posX, (double)posY, (double)posZ);
            PhysicsEntity entity = body.getEntity();
            Matrix4d oldTransformation = entity.getOldTransformation();
            Matrix4d transformation = entity.getTransformation();
            oldTransformation.m30(transformation.m30());
            oldTransformation.m31(transformation.m31());
            oldTransformation.m32(transformation.m32());
            transformation.setTranslation((double)posX, (double)posY, (double)posZ);
            info.averagedDensity = org.joml.Math.lerp((float)info.density, (float)info.averagedDensity, (float)0.94f);
            info.density = 1.0f;
            if (!(camPos.distanceToSqr(info.pos.x + physicsOffset.x, info.pos.y + physicsOffset.y, info.pos.z + physicsOffset.z) > maxSmokeDistance)) continue;
            this.world.removeBody(body);
            if (body.getLastChunk() != null && !body.isKinematicOrFrozen()) {
                this.world.removeLoadedChunkEntity(body.getLastChunk());
            }
            this.world.getDynamicsWorld().removeActor(body.getRigidBody());
            body.destroy();
        }
        while (it.hasNext()) {
            Map.Entry<Vector3i, ChunkInfo> entry = it.next();
            chunk = entry.getKey();
            ChunkInfo chunkInfo = entry.getValue();
            List<IRigidBody> particles = chunkInfo.bodies;
            for (int i2 = 0; i2 < particles.size(); ++i2) {
                IRigidBody particle = particles.get(i2);
                if (particle.isDestroyed()) {
                    this.allParticles.remove(particle);
                    particles.remove(i2--);
                    continue;
                }
                Vector3d pos = ((ParticleInfo)particle.getUserData()).pos;
                int cx = Math.fastRound(pos.x) / CHUNK_SIZE;
                int cy = Math.fastRound(pos.y) / CHUNK_SIZE;
                int cz = Math.fastRound(pos.z) / CHUNK_SIZE;
                if (cx == chunk.x && cy == chunk.y && cz == chunk.z) continue;
                particles.remove(i2--);
                this.changedParticles.add(particle);
            }
            if (particles.size() != 0) continue;
            it.remove();
        }
        for (i = 0; i < this.changedParticles.size(); ++i) {
            IRigidBody particle = this.changedParticles.get(i);
            Vector3d pos = ((ParticleInfo)particle.getUserData()).pos;
            int cx = Math.fastRound(pos.x) / CHUNK_SIZE;
            int cy = Math.fastRound(pos.y) / CHUNK_SIZE;
            int cz = Math.fastRound(pos.z) / CHUNK_SIZE;
            this.tmp.set(cx, cy, cz);
            ChunkInfo chunkInfo = this.chunks.get(this.tmp);
            if (chunkInfo == null) {
                chunkInfo = new ChunkInfo(this);
                this.chunks.put(new Vector3i((Vector3ic)this.tmp), chunkInfo);
            }
            chunkInfo.bodies.add(particle);
        }
        for (Map.Entry<Vector3i, ChunkInfo> entry : this.chunks.entrySet()) {
            chunk = entry.getKey();
            ChunkInfo info = entry.getValue();
            for (int x = -1; x <= 1; ++x) {
                for (int y = -1; y <= 1; ++y) {
                    for (int z = -1; z <= 1; ++z) {
                        if (x == 0 && y == 0 && z == 0) continue;
                        this.tmp.set(chunk.x + x, chunk.y + y, chunk.z + z);
                        ChunkInfo otherInfo = this.chunks.get(this.tmp);
                        if (otherInfo == null || !this.masks.getBoolean((Object)this.tmp)) continue;
                        this.repellParticles(info.bodies, otherInfo.bodies);
                    }
                }
            }
            this.repellParticles(info.bodies, info.bodies);
            for (int i3 = 0; i3 < info.bodies.size(); ++i3) {
                this.updateParticle(info.bodies.get(i3), diff);
            }
            this.masks.put((Object)chunk, true);
        }
        this.masks.clear();
    }

    private void updateParticle(IRigidBody particle, double diff) {
        PhysicsEntity entity = particle.getEntity();
        Vector3d pos = ((ParticleInfo)particle.getUserData()).pos;
        double x = pos.x;
        double y = pos.y;
        double z = pos.z;
        Vector3d offset = this.world.getOffset();
        if (entity.info == null && SmokeDomain.isInOpenAir(this.world.getLevel(), Mth.floor((double)(x + offset.x)), Mth.floor((double)(y + offset.y)), Mth.floor((double)(z + offset.z)))) {
            entity.time = (float)java.lang.Math.min((double)entity.time, java.lang.Math.max(2.0, ConfigClient.particleDespawnTimeSmoke + (double)Math.random() * ConfigClient.particleDespawnTimeVarianceSmoke));
            entity.info = true;
        }
        PxRigidBody rigidBody = (PxRigidBody)particle.getRigidBody();
        Vector3f gravity = this.world.getDynamicsWorld().getGravity();
        ParticleInfo info = (ParticleInfo)particle.getUserData();
        Vector3d velocity = info.vel;
        try (MemoryStack mem = MemoryStack.stackPush();){
            PxVec3 v = rigidBody.getLinearVelocity();
            float vx = MemoryUtil.memGetFloat((long)v.getAddress());
            float vy = MemoryUtil.memGetFloat((long)(v.getAddress() + 4L));
            float vz = MemoryUtil.memGetFloat((long)(v.getAddress() + 8L));
            float cvx = (float)Math.clamp(velocity.x, (double)-2.9f, (double)2.9f);
            float cvy = (float)Math.clamp(velocity.y, (double)-2.9f, (double)2.9f);
            float cvz = (float)Math.clamp(velocity.z, (double)-2.9f, (double)2.9f);
            PxVec3 repellForce = PxVec3.createAt(mem, MemoryStack::nmalloc, -vx * 0.07f - gravity.x * (float)diff * 0.7f + cvx, -vy * 0.07f - gravity.y * (float)diff * 0.7f + cvy, -vz * 0.07f - gravity.z * (float)diff * 0.7f + cvz);
            rigidBody.addForce(repellForce, PxForceModeEnum.eVELOCITY_CHANGE);
        }
        velocity.set(0.0);
    }

    private void repellParticles(List<IRigidBody> particles, List<IRigidBody> otherParticles) {
        boolean same = particles == otherParticles;
        for (int i = 0; i < particles.size(); ++i) {
            IRigidBody particle1 = particles.get(i);
            ParticleInfo info1 = (ParticleInfo)particle1.getUserData();
            Vector3d pos1 = info1.pos;
            if (this.density != -1) {
                info1.density = this.density;
            }
            for (int j = 0; j < otherParticles.size(); ++j) {
                IRigidBody particle2 = otherParticles.get(j);
                if (particle1 == particle2) continue;
                ParticleInfo info2 = (ParticleInfo)particle2.getUserData();
                Vector3d pos2 = info2.pos;
                double dx = pos1.x - pos2.x;
                double dy = pos1.y - pos2.y;
                double dz = pos1.z - pos2.z;
                double distanceSquared = dx * dx + dy * dy + dz * dz;
                if (distanceSquared < 1.0) {
                    info1.density += 1.0f;
                    info2.density += 1.0f;
                }
                if (!(distanceSquared < 0.25)) continue;
                double length = java.lang.Math.sqrt(distanceSquared);
                double effect = 1.0 - length * 2.0;
                double invLength = 1.0 / length;
                if (length <= 0.001) {
                    dy = 1.0;
                    effect = 1.0;
                    invLength = 1.0;
                }
                double totalStrength = 2.25 * effect;
                info1.vel.add((dx *= invLength) * totalStrength, (dy *= invLength) * totalStrength, (dz *= invLength) * totalStrength);
                if (same) continue;
                info2.vel.add(-dx * totalStrength, -dy * totalStrength, -dz * totalStrength);
            }
        }
    }

    public void clearParticles() {
        for (int i = 0; i < this.allParticles.size(); ++i) {
            this.allParticles.get((int)i).getEntity().time = -1.0f;
        }
    }

    public void killExcessParticles() {
        if (this.allParticles.size() > ConfigClient.smokeParticleLimit) {
            for (int i = 0; i < this.allParticles.size() - ConfigClient.smokeParticleLimit; ++i) {
                IRigidBody body = this.allParticles.get(i);
                if (body.isDestroyed()) continue;
                body.getEntity().time = -1.0f;
            }
        }
    }

    public void spawnParticle(double x, double y, double z, float scale) {
        if (!ConfigClient.smokePhysics) {
            return;
        }
        if (this.allParticles.size() > ConfigClient.smokeParticleLimit) {
            for (int i = 0; i < this.allParticles.size(); ++i) {
                IRigidBody body = this.allParticles.get(i);
                if (body.isDestroyed() || !((double)body.getEntity().time >= 0.0)) continue;
                body.getEntity().time = -1.0f;
                break;
            }
        }
        PhysicsEntity entity = new PhysicsEntity(PhysicsEntity.Type.SMOKE, null);
        entity.scale = scale;
        entity.models = null;
        int cx = Math.fastRound(x) / CHUNK_SIZE;
        int cy = Math.fastRound(y) / CHUNK_SIZE;
        int cz = Math.fastRound(z) / CHUNK_SIZE;
        this.tmp.set(cx, cy, cz);
        ChunkInfo chunkInfo = this.chunks.get(this.tmp);
        if (chunkInfo == null) {
            chunkInfo = new ChunkInfo(this);
            this.chunks.put(new Vector3i((Vector3ic)this.tmp), chunkInfo);
        }
        entity.getTransformation().translation(x, y, z);
        entity.getOldTransformation().translation(x, y, z);
        entity.setAnimation(this.smokeDespawn);
        IRigidBody body = this.world.addSmokeSphere(entity, 0.15f * scale);
        ParticleInfo info = new ParticleInfo();
        info.pos.x = x;
        info.pos.y = y;
        info.pos.z = z;
        body.setUserData(info);
        body.smoke = true;
        entity.setColor(this.random.nextInt());
        ((PxRigidDynamic)body.getRigidBody()).setMaxAngularVelocity(0.0f);
        body.setGravity(false);
        chunkInfo.bodies.add(body);
        this.allParticles.add(body);
    }

    public static boolean isInOpenAir(Level level, int x, int y, int z) {
        return y >= level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
    }

    public void executeExplosion(Explosion explosion) {
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < 300; ++i) {
            double x = (double)Math.random() - 0.5;
            double y = (double)Math.random() - 0.5;
            double z = (double)Math.random() - 0.5;
            double vectorLength = java.lang.Math.sqrt(x * x + y * y + z * z);
            while (vectorLength == 0.0) {
                x = (double)Math.random() - 0.5;
                y = (double)Math.random() - 0.5;
                z = (double)Math.random() - 0.5;
                vectorLength = java.lang.Math.sqrt(x * x + y * y + z * z);
            }
            x /= vectorLength;
            y /= vectorLength;
            z /= vectorLength;
            double length = (double)Math.random() * java.lang.Math.max(1.0, (double)explosion.strength);
            x = x * length + explosion.position.x;
            y = y * length + explosion.position.y;
            z = z * length + explosion.position.z;
            blockPos.set(x, y, z);
            Level level = this.world.getLevel();
            BlockState state = level.getBlockState((BlockPos)blockPos);
            FluidState fluidState = state.getFluidState();
            if (fluidState.getAmount() != 0 || Block.isShapeFullBlock((VoxelShape)state.getShape((BlockGetter)level, (BlockPos)blockPos)) && !state.getCollisionShape((BlockGetter)level, (BlockPos)blockPos).isEmpty()) continue;
            this.spawnParticle(x, y, z, Math.random() * 2.5f + 1.0f);
        }
    }

    public void setSmokeUpdateCallback(SmokeUpdateCallback smokeUpdateCallback) {
        this.smokeUpdateCallback = smokeUpdateCallback;
    }

    public SmokeUpdateCallback getSmokeUpdateCallback() {
        return this.smokeUpdateCallback;
    }

    public void destroy() {
        for (int i = 0; i < this.allParticles.size(); ++i) {
            this.allParticles.get(i).destroy();
        }
        this.allParticles.clear();
    }

    public List<IRigidBody> getAllParticles() {
        return this.allParticles;
    }

    public PhysicsWorld getWorld() {
        return this.world;
    }

    class ChunkInfo {
        public List<IRigidBody> bodies = new ObjectArrayList();

        public ChunkInfo(SmokeDomain this$0) {
        }
    }
}

