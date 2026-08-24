package net.diebuddies.physics.smoke;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.Explosion;
import net.diebuddies.physics.IRigidBody;
import net.diebuddies.physics.PhysicsEntity;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.animation.Animation;
import net.diebuddies.physics.animation.AnimationType;
import net.diebuddies.physics.animation.CurveType;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;
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
   private static final float MAX_SPEED = 2.9F;
   private static final float DESPAWN_ANIMATION_TIME = 2.0F;
   private static final float DAMPING = 0.07F;
   private static final float GRAVITY_MODIFIER = 0.7F;
   private static final int CHUNK_SIZE = (int)Math.round(Math.ceil(0.5));
   private Animation smokeDespawn;
   private final Map<Vector3i, SmokeDomain.ChunkInfo> chunks;
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
      this.smokeDespawn = new Animation("smoke_vanish", CurveType.Ease_out, 2.0F);
      this.smokeDespawn.despawnType = AnimationType.Vanish;
   }

   public void update(double diff) {
      this.updateParticles(diff);
      if (this.smokeUpdateCallback != null) {
         this.smokeUpdateCallback.smokeUpdate(this);
      }
   }

   private void updateParticles(double diff) {
      Iterator<Entry<Vector3i, SmokeDomain.ChunkInfo>> it = this.chunks.entrySet().iterator();
      this.changedParticles.clear();
      Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
      Vec3 camPos = camera.getPosition();
      double maxSmokeDistance = ConfigClient.smokePhysicsRange * ConfigClient.smokePhysicsRange;
      Vector3d physicsOffset = this.world.getOffset();

      for (int i = 0; i < this.allParticles.size(); i++) {
         IRigidBody body = this.allParticles.get(i);
         if (!body.isDestroyed()) {
            PxTransform transform = body.getRigidBody().getGlobalPose();
            float posX = MemoryUtil.memGetFloat(transform.getAddress() + 16L);
            float posY = MemoryUtil.memGetFloat(transform.getAddress() + 20L);
            float posZ = MemoryUtil.memGetFloat(transform.getAddress() + 24L);
            ParticleInfo info = (ParticleInfo)body.getUserData();
            info.pos.set(posX, posY, posZ);
            PhysicsEntity entity = body.getEntity();
            Matrix4d oldTransformation = entity.getOldTransformation();
            Matrix4d transformation = entity.getTransformation();
            oldTransformation.m30(transformation.m30());
            oldTransformation.m31(transformation.m31());
            oldTransformation.m32(transformation.m32());
            transformation.setTranslation(posX, posY, posZ);
            info.averagedDensity = org.joml.Math.lerp(info.density, info.averagedDensity, 0.94F);
            info.density = 1.0F;
            if (camPos.distanceToSqr(info.pos.x + physicsOffset.x, info.pos.y + physicsOffset.y, info.pos.z + physicsOffset.z) > maxSmokeDistance) {
               this.world.removeBody(body);
               if (body.getLastChunk() != null && !body.isKinematicOrFrozen()) {
                  this.world.removeLoadedChunkEntity(body.getLastChunk());
               }

               this.world.getDynamicsWorld().removeActor(body.getRigidBody());
               body.destroy();
            }
         }
      }

      while (it.hasNext()) {
         Entry<Vector3i, SmokeDomain.ChunkInfo> entry = it.next();
         Vector3i chunk = entry.getKey();
         SmokeDomain.ChunkInfo chunkInfo = entry.getValue();
         List<IRigidBody> particles = chunkInfo.bodies;

         for (int ix = 0; ix < particles.size(); ix++) {
            IRigidBody particle = particles.get(ix);
            if (particle.isDestroyed()) {
               this.allParticles.remove(particle);
               particles.remove(ix--);
            } else {
               Vector3d pos = ((ParticleInfo)particle.getUserData()).pos;
               int cx = net.diebuddies.math.Math.fastRound(pos.x) / CHUNK_SIZE;
               int cy = net.diebuddies.math.Math.fastRound(pos.y) / CHUNK_SIZE;
               int cz = net.diebuddies.math.Math.fastRound(pos.z) / CHUNK_SIZE;
               if (cx != chunk.x || cy != chunk.y || cz != chunk.z) {
                  particles.remove(ix--);
                  this.changedParticles.add(particle);
               }
            }
         }

         if (particles.size() == 0) {
            it.remove();
         }
      }

      for (int ixx = 0; ixx < this.changedParticles.size(); ixx++) {
         IRigidBody particle = this.changedParticles.get(ixx);
         Vector3d pos = ((ParticleInfo)particle.getUserData()).pos;
         int cx = net.diebuddies.math.Math.fastRound(pos.x) / CHUNK_SIZE;
         int cy = net.diebuddies.math.Math.fastRound(pos.y) / CHUNK_SIZE;
         int cz = net.diebuddies.math.Math.fastRound(pos.z) / CHUNK_SIZE;
         this.tmp.set(cx, cy, cz);
         SmokeDomain.ChunkInfo chunkInfo = this.chunks.get(this.tmp);
         if (chunkInfo == null) {
            chunkInfo = new SmokeDomain.ChunkInfo();
            this.chunks.put(new Vector3i(this.tmp), chunkInfo);
         }

         chunkInfo.bodies.add(particle);
      }

      for (Entry<Vector3i, SmokeDomain.ChunkInfo> entry : this.chunks.entrySet()) {
         Vector3i chunk = entry.getKey();
         SmokeDomain.ChunkInfo info = entry.getValue();

         for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
               for (int z = -1; z <= 1; z++) {
                  if (x != 0 || y != 0 || z != 0) {
                     this.tmp.set(chunk.x + x, chunk.y + y, chunk.z + z);
                     SmokeDomain.ChunkInfo otherInfo = this.chunks.get(this.tmp);
                     if (otherInfo != null && this.masks.getBoolean(this.tmp)) {
                        this.repellParticles(info.bodies, otherInfo.bodies);
                     }
                  }
               }
            }
         }

         this.repellParticles(info.bodies, info.bodies);

         for (int ixx = 0; ixx < info.bodies.size(); ixx++) {
            this.updateParticle(info.bodies.get(ixx), diff);
         }

         this.masks.put(chunk, true);
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
      if (entity.info == null && isInOpenAir(this.world.getLevel(), Mth.floor(x + offset.x), Mth.floor(y + offset.y), Mth.floor(z + offset.z))) {
         entity.time = (float)Math.min(
            (double)entity.time,
            Math.max(2.0, ConfigClient.particleDespawnTimeSmoke + net.diebuddies.math.Math.random() * ConfigClient.particleDespawnTimeVarianceSmoke)
         );
         entity.info = true;
      }

      PxRigidBody rigidBody = (PxRigidBody)particle.getRigidBody();
      Vector3f gravity = this.world.getDynamicsWorld().getGravity();
      ParticleInfo info = (ParticleInfo)particle.getUserData();
      Vector3d velocity = info.vel;
      MemoryStack mem = MemoryStack.stackPush();

      try {
         PxVec3 v = rigidBody.getLinearVelocity();
         float vx = MemoryUtil.memGetFloat(v.getAddress());
         float vy = MemoryUtil.memGetFloat(v.getAddress() + 4L);
         float vz = MemoryUtil.memGetFloat(v.getAddress() + 8L);
         float cvx = (float)net.diebuddies.math.Math.clamp(velocity.x, -2.9000000953674316, 2.9000000953674316);
         float cvy = (float)net.diebuddies.math.Math.clamp(velocity.y, -2.9000000953674316, 2.9000000953674316);
         float cvz = (float)net.diebuddies.math.Math.clamp(velocity.z, -2.9000000953674316, 2.9000000953674316);
         PxVec3 repellForce = PxVec3.createAt(
            mem,
            MemoryStack::nmalloc,
            -vx * 0.07F - gravity.x * (float)diff * 0.7F + cvx,
            -vy * 0.07F - gravity.y * (float)diff * 0.7F + cvy,
            -vz * 0.07F - gravity.z * (float)diff * 0.7F + cvz
         );
         rigidBody.addForce(repellForce, PxForceModeEnum.eVELOCITY_CHANGE);
      } catch (Throwable var27) {
         if (mem != null) {
            try {
               mem.close();
            } catch (Throwable var26) {
               var27.addSuppressed(var26);
            }
         }

         throw var27;
      }

      if (mem != null) {
         mem.close();
      }

      velocity.set(0.0);
   }

   private void repellParticles(List<IRigidBody> particles, List<IRigidBody> otherParticles) {
      boolean same = particles == otherParticles;

      for (int i = 0; i < particles.size(); i++) {
         IRigidBody particle1 = particles.get(i);
         ParticleInfo info1 = (ParticleInfo)particle1.getUserData();
         Vector3d pos1 = info1.pos;
         if (this.density != -1) {
            info1.density = this.density;
         }

         for (int j = 0; j < otherParticles.size(); j++) {
            IRigidBody particle2 = otherParticles.get(j);
            if (particle1 != particle2) {
               ParticleInfo info2 = (ParticleInfo)particle2.getUserData();
               Vector3d pos2 = info2.pos;
               double dx = pos1.x - pos2.x;
               double dy = pos1.y - pos2.y;
               double dz = pos1.z - pos2.z;
               double distanceSquared = dx * dx + dy * dy + dz * dz;
               if (distanceSquared < 1.0) {
                  info1.density++;
                  info2.density++;
               }

               if (distanceSquared < 0.25) {
                  double length = Math.sqrt(distanceSquared);
                  double effect = 1.0 - length * 2.0;
                  double invLength = 1.0 / length;
                  if (length <= 0.001) {
                     dy = 1.0;
                     effect = 1.0;
                     invLength = 1.0;
                  }

                  dx *= invLength;
                  dy *= invLength;
                  dz *= invLength;
                  double totalStrength = 2.25 * effect;
                  info1.vel.add(dx * totalStrength, dy * totalStrength, dz * totalStrength);
                  if (!same) {
                     info2.vel.add(-dx * totalStrength, -dy * totalStrength, -dz * totalStrength);
                  }
               }
            }
         }
      }
   }

   public void clearParticles() {
      for (int i = 0; i < this.allParticles.size(); i++) {
         this.allParticles.get(i).getEntity().time = -1.0F;
      }
   }

   public void killExcessParticles() {
      if (this.allParticles.size() > ConfigClient.smokeParticleLimit) {
         for (int i = 0; i < this.allParticles.size() - ConfigClient.smokeParticleLimit; i++) {
            IRigidBody body = this.allParticles.get(i);
            if (!body.isDestroyed()) {
               body.getEntity().time = -1.0F;
            }
         }
      }
   }

   public void spawnParticle(double x, double y, double z, float scale) {
      if (ConfigClient.smokePhysics) {
         if (this.allParticles.size() > ConfigClient.smokeParticleLimit) {
            for (int i = 0; i < this.allParticles.size(); i++) {
               IRigidBody body = this.allParticles.get(i);
               if (!body.isDestroyed() && body.getEntity().time >= 0.0) {
                  body.getEntity().time = -1.0F;
                  break;
               }
            }
         }

         PhysicsEntity entity = new PhysicsEntity(PhysicsEntity.Type.SMOKE, null);
         entity.scale = scale;
         entity.models = null;
         int cx = net.diebuddies.math.Math.fastRound(x) / CHUNK_SIZE;
         int cy = net.diebuddies.math.Math.fastRound(y) / CHUNK_SIZE;
         int cz = net.diebuddies.math.Math.fastRound(z) / CHUNK_SIZE;
         this.tmp.set(cx, cy, cz);
         SmokeDomain.ChunkInfo chunkInfo = this.chunks.get(this.tmp);
         if (chunkInfo == null) {
            chunkInfo = new SmokeDomain.ChunkInfo();
            this.chunks.put(new Vector3i(this.tmp), chunkInfo);
         }

         entity.getTransformation().translation(x, y, z);
         entity.getOldTransformation().translation(x, y, z);
         entity.setAnimation(this.smokeDespawn);
         IRigidBody body = this.world.addSmokeSphere(entity, 0.15F * scale);
         ParticleInfo info = new ParticleInfo();
         info.pos.x = x;
         info.pos.y = y;
         info.pos.z = z;
         body.setUserData(info);
         body.smoke = true;
         entity.setColor(this.random.nextInt());
         ((PxRigidDynamic)body.getRigidBody()).setMaxAngularVelocity(0.0F);
         body.setGravity(false);
         chunkInfo.bodies.add(body);
         this.allParticles.add(body);
      }
   }

   public static boolean isInOpenAir(Level level, int x, int y, int z) {
      return y >= level.getHeight(Types.MOTION_BLOCKING, x, z);
   }

   public void executeExplosion(Explosion explosion) {
      MutableBlockPos blockPos = new MutableBlockPos();

      for (int i = 0; i < 300; i++) {
         double x = net.diebuddies.math.Math.random() - 0.5;
         double y = net.diebuddies.math.Math.random() - 0.5;
         double z = net.diebuddies.math.Math.random() - 0.5;

         double vectorLength;
         for (vectorLength = Math.sqrt(x * x + y * y + z * z); vectorLength == 0.0; vectorLength = Math.sqrt(x * x + y * y + z * z)) {
            x = net.diebuddies.math.Math.random() - 0.5;
            y = net.diebuddies.math.Math.random() - 0.5;
            z = net.diebuddies.math.Math.random() - 0.5;
         }

         x /= vectorLength;
         y /= vectorLength;
         z /= vectorLength;
         double length = net.diebuddies.math.Math.random() * Math.max(1.0, (double)explosion.strength);
         x = x * length + explosion.position.x;
         y = y * length + explosion.position.y;
         z = z * length + explosion.position.z;
         blockPos.set(x, y, z);
         Level level = this.world.getLevel();
         BlockState state = level.getBlockState(blockPos);
         FluidState fluidState = state.getFluidState();
         if (fluidState.getAmount() == 0 && (!Block.isShapeFullBlock(state.getShape(level, blockPos)) || state.getCollisionShape(level, blockPos).isEmpty())) {
            this.spawnParticle(x, y, z, net.diebuddies.math.Math.random() * 2.5F + 1.0F);
         }
      }
   }

   public void setSmokeUpdateCallback(SmokeUpdateCallback smokeUpdateCallback) {
      this.smokeUpdateCallback = smokeUpdateCallback;
   }

   public SmokeUpdateCallback getSmokeUpdateCallback() {
      return this.smokeUpdateCallback;
   }

   public void destroy() {
      for (int i = 0; i < this.allParticles.size(); i++) {
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

      public ChunkInfo() {
      }
   }
}
