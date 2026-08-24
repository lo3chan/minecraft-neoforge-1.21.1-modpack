package net.diebuddies.physics.liquid;

import net.diebuddies.math.Vector3i;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.StarterClient;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4d;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import physx.NativeObject;
import physx.common.PxCudaContext;
import physx.common.PxCudaContextManager;
import physx.common.PxCudaTopLevelFunctions;
import physx.common.PxVec4;
import physx.particles.PxParticleBuffer;
import physx.particles.PxParticleBufferDesc;
import physx.particles.PxParticleSystem;
import physx.support.NativeArrayHelpers;
import physx.support.Vector_PxVec4;

public class LiquidCuda extends Liquid {
   private PxParticleBuffer particleBuffer;
   private Vector_PxVec4 particlePositions;
   private Vector3d min;
   private Vector3d max;
   public int density;
   public int textureID;
   public Matrix4d transformation = new Matrix4d();
   public double range = 1.25;
   public Vector3d origin;
   public Vector2f textureScale = new Vector2f(1.0F);
   public int gridSize;
   public float damping = 0.9F;
   public int color = -1;
   public boolean sourceAlive;
   public short materialID = -1;
   public short renderType = 1;
   public float[] cudaPositions;
   public float[] cudaOldPositions;
   public int cudaParticleSize = 0;

   public LiquidCuda(LiquidController controller) {
      super(controller);
      this.sourceAlive = true;
   }

   @Override
   public void blockUpdate(PhysicsWorld physicsWorld, BlockPos pos, BlockState state) {
   }

   @Override
   public boolean update(PhysicsWorld physicsWorld, double diff) {
      this.fetchPositions(physicsWorld);
      this.controller.update(this, diff);
      return !this.sourceAlive;
   }

   private void fetchPositions(PhysicsWorld physicsWorld) {
      PxVec4 devPositions = this.particleBuffer.getPositionInvMasses();
      this.cudaParticleSize = this.particleBuffer.getNbActiveParticles();
      PxCudaContextManager cudaMgr = StarterClient.cudaManager;
      cudaMgr.acquireContext();
      PxCudaContext cudaContext = cudaMgr.getCudaContext();
      cudaContext.memcpyDtoH(this.particlePositions.data(), PxCudaTopLevelFunctions.pxVec4deviceptr(devPositions), PxVec4.SIZEOF * this.cudaParticleSize);
      boolean firstTime = false;
      if (this.min == null) {
         this.min = new Vector3d();
         this.max = new Vector3d();
         firstTime = true;
      } else {
         this.unloadBounds(physicsWorld, this.min, this.max);
      }

      if (this.cudaParticleSize == 0) {
         this.min = null;
         this.max = null;
      } else {
         this.min.set(1.7976931348623157E308);
         this.max.set(-1.7976931348623157E308);
         long baseAddress = this.particlePositions.at(0).getAddress();
         System.arraycopy(this.cudaPositions, 0, this.cudaOldPositions, 0, this.cudaPositions.length);

         for (int i = 0; i < this.cudaParticleSize; i++) {
            long addressOffset = baseAddress + i * 16L;
            float posX = MemoryUtil.memGetFloat(addressOffset);
            float posY = MemoryUtil.memGetFloat(addressOffset + 4L);
            float posZ = MemoryUtil.memGetFloat(addressOffset + 8L);
            int offset = i * 3;
            this.cudaPositions[offset] = posX;
            this.cudaPositions[offset + 1] = posY;
            this.cudaPositions[offset + 2] = posZ;
            this.min.x = posX < this.min.x() ? posX : this.min.x();
            this.min.y = posY < this.min.y() ? posY : this.min.y();
            this.min.z = posZ < this.min.z() ? posZ : this.min.z();
            this.max.x = posX > this.max.x() ? posX : this.max.x();
            this.max.y = posY > this.max.y() ? posY : this.max.y();
            this.max.z = posZ > this.max.z() ? posZ : this.max.z();
         }

         if (firstTime) {
            System.arraycopy(this.cudaPositions, 0, this.cudaOldPositions, 0, this.cudaPositions.length);
         }

         cudaMgr.releaseContext();
         Vector3d offset = physicsWorld.getOffset();
         this.min.add(offset);
         this.max.add(offset);
         this.loadBounds(physicsWorld, this.min, this.max);
      }
   }

   private void unloadBounds(PhysicsWorld physics, Vector3d min, Vector3d max) {
      int startX = Mth.floor(min.x) >> PhysicsWorld.CHUNK_SIZE_NUM_BITS;
      int endX = Mth.ceil(max.x) >> PhysicsWorld.CHUNK_SIZE_NUM_BITS;
      int startY = Mth.floor(min.y) >> PhysicsWorld.CHUNK_SIZE_NUM_BITS;
      int endY = Mth.ceil(max.y) >> PhysicsWorld.CHUNK_SIZE_NUM_BITS;
      int startZ = Mth.floor(min.z) >> PhysicsWorld.CHUNK_SIZE_NUM_BITS;
      int endZ = Mth.ceil(max.z) >> PhysicsWorld.CHUNK_SIZE_NUM_BITS;

      for (int x = startX; x <= endX; x++) {
         for (int y = startY; y <= endY; y++) {
            for (int z = startZ; z <= endZ; z++) {
               physics.decreaseLoadedChunkCounter(new Vector3i(x, y, z));
            }
         }
      }
   }

   private void loadBounds(PhysicsWorld physics, Vector3d min, Vector3d max) {
      int startX = Mth.floor(min.x) >> PhysicsWorld.CHUNK_SIZE_NUM_BITS;
      int endX = Mth.ceil(max.x) >> PhysicsWorld.CHUNK_SIZE_NUM_BITS;
      int startY = Mth.floor(min.y) >> PhysicsWorld.CHUNK_SIZE_NUM_BITS;
      int endY = Mth.ceil(max.y) >> PhysicsWorld.CHUNK_SIZE_NUM_BITS;
      int startZ = Mth.floor(min.z) >> PhysicsWorld.CHUNK_SIZE_NUM_BITS;
      int endZ = Mth.ceil(max.z) >> PhysicsWorld.CHUNK_SIZE_NUM_BITS;

      for (int x = startX; x <= endX; x++) {
         for (int y = startY; y <= endY; y++) {
            for (int z = startZ; z <= endZ; z++) {
               physics.increaseLoadedChunkCounter(new Vector3i(x, y, z));
            }
         }
      }
   }

   public void initBoxParticles(PhysicsWorld physics, double posX, double posY, double posZ, double width, double height, double depth) {
      Vector3d offset = physics.getOffset();
      physics.adjustOffset(posX, posY, posZ);
      posX -= offset.x;
      posY -= offset.y;
      posZ -= offset.z;
      MemoryStack mem = MemoryStack.stackPush();

      try {
         int numX = (int)Math.floor(width / physics.fluidParticleSize);
         int numY = (int)Math.floor(height / physics.fluidParticleSize);
         int numZ = (int)Math.floor(depth / physics.fluidParticleSize);
         int maxParticles = numX * numY * numZ;
         PxCudaContextManager cudaMgr = StarterClient.cudaManager;
         float particleMass = 4187.7397F * physics.fluidParticleSize * physics.fluidParticleSize * physics.fluidParticleSize;
         PxParticleSystem particleSystem = physics.getFluidSystem();
         NativeObject phase = PxCudaTopLevelFunctions.allocPinnedHostBufferPxU32(cudaMgr, maxParticles);
         PxVec4 positionInvMass = PxCudaTopLevelFunctions.allocPinnedHostBufferPxVec4(cudaMgr, maxParticles);
         float x = (float)posX;
         float y = (float)posY;
         float z = (float)posZ;
         float invParticleMass = 1.0F / particleMass;
         int index = 0;
         long baseAddress = PxVec4.arrayGet(positionInvMass.getAddress(), 0).getAddress();
         long phaseAddress = phase.getAddress();
         int fluidPhase = physics.getFluidPhase();

         for (int i = 0; i < numX; i++) {
            for (int j = 0; j < numY; j++) {
               for (int k = 0; k < numZ; k++) {
                  MemoryUtil.memPutInt(phaseAddress + index * 4L, fluidPhase);
                  long offsetAddress = baseAddress + index * 16L;
                  MemoryUtil.memPutFloat(offsetAddress, x);
                  MemoryUtil.memPutFloat(offsetAddress + 4L, y);
                  MemoryUtil.memPutFloat(offsetAddress + 8L, z);
                  MemoryUtil.memPutFloat(offsetAddress + 12L, invParticleMass);
                  index++;
                  z += physics.fluidParticleSize;
               }

               z = (float)posZ;
               y += physics.fluidParticleSize;
            }

            y = (float)posY;
            x += physics.fluidParticleSize;
         }

         PxParticleBufferDesc bufferDesc = PxParticleBufferDesc.createAt(mem, MemoryStack::nmalloc);
         bufferDesc.setMaxParticles(maxParticles);
         bufferDesc.setNumActiveParticles(maxParticles);
         this.particlePositions = new Vector_PxVec4(maxParticles);
         this.cudaPositions = new float[maxParticles * 3];
         this.cudaOldPositions = new float[maxParticles * 3];
         bufferDesc.setPositions(positionInvMass);
         bufferDesc.setPhases(NativeArrayHelpers.voidToU32Ptr(phase));
         this.particleBuffer = PxCudaTopLevelFunctions.CreateAndPopulateParticleBuffer(bufferDesc, cudaMgr);
         particleSystem.addParticleBuffer(this.particleBuffer);
         PxCudaTopLevelFunctions.freePinnedHostBufferPxVec4(cudaMgr, positionInvMass);
         PxCudaTopLevelFunctions.freePinnedHostBufferPxU32(cudaMgr, NativeArrayHelpers.voidToU32Ptr(phase));
      } catch (Throwable var41) {
         if (mem != null) {
            try {
               mem.close();
            } catch (Throwable var40) {
               var41.addSuppressed(var40);
            }
         }

         throw var41;
      }

      if (mem != null) {
         mem.close();
      }
   }

   public void initSphereParticles(PhysicsWorld physics, double posX, double posY, double posZ, double radius) {
      Vector3d offset = physics.getOffset();
      physics.adjustOffset(posX, posY, posZ);
      posX -= offset.x;
      posY -= offset.y;
      posZ -= offset.z;
      MemoryStack mem = MemoryStack.stackPush();

      try {
         double radiusSquared = radius * radius;
         int maxParticles = 0;

         for (double x = -radius; x <= radius; x += physics.fluidParticleSize) {
            for (double y = -radius; y <= radius; y += physics.fluidParticleSize) {
               for (double z = -radius; z <= radius; z += physics.fluidParticleSize) {
                  if (Vector3d.lengthSquared(x, y, z) <= radiusSquared) {
                     maxParticles++;
                  }
               }
            }
         }

         PxCudaContextManager cudaMgr = StarterClient.cudaManager;
         float particleMass = 4187.7397F * physics.fluidParticleSize * physics.fluidParticleSize * physics.fluidParticleSize;
         PxParticleSystem particleSystem = physics.getFluidSystem();
         NativeObject phase = PxCudaTopLevelFunctions.allocPinnedHostBufferPxU32(cudaMgr, maxParticles);
         PxVec4 positionInvMass = PxCudaTopLevelFunctions.allocPinnedHostBufferPxVec4(cudaMgr, maxParticles);
         float invParticleMass = 1.0F / particleMass;
         int index = 0;
         long baseAddress = PxVec4.arrayGet(positionInvMass.getAddress(), 0).getAddress();
         long phaseAddress = phase.getAddress();
         int fluidPhase = physics.getFluidPhase();

         for (double x = -radius; x <= radius; x += physics.fluidParticleSize) {
            for (double y = -radius; y <= radius; y += physics.fluidParticleSize) {
               for (double zx = -radius; zx <= radius; zx += physics.fluidParticleSize) {
                  if (Vector3d.lengthSquared(x, y, zx) <= radiusSquared) {
                     float px = (float)(posX + x);
                     float py = (float)(posY + y);
                     float pz = (float)(posZ + zx);
                     MemoryUtil.memPutInt(phaseAddress + index * 4L, fluidPhase);
                     long offsetAddress = baseAddress + index * 16L;
                     MemoryUtil.memPutFloat(offsetAddress, px);
                     MemoryUtil.memPutFloat(offsetAddress + 4L, py);
                     MemoryUtil.memPutFloat(offsetAddress + 8L, pz);
                     MemoryUtil.memPutFloat(offsetAddress + 12L, invParticleMass);
                     index++;
                  }
               }
            }
         }

         PxParticleBufferDesc bufferDesc = PxParticleBufferDesc.createAt(mem, MemoryStack::nmalloc);
         bufferDesc.setMaxParticles(maxParticles);
         bufferDesc.setNumActiveParticles(maxParticles);
         this.particlePositions = new Vector_PxVec4(maxParticles);
         this.cudaPositions = new float[maxParticles * 3];
         this.cudaOldPositions = new float[maxParticles * 3];
         bufferDesc.setPositions(positionInvMass);
         bufferDesc.setPhases(NativeArrayHelpers.voidToU32Ptr(phase));
         this.particleBuffer = PxCudaTopLevelFunctions.CreateAndPopulateParticleBuffer(bufferDesc, cudaMgr);
         particleSystem.addParticleBuffer(this.particleBuffer);
         PxCudaTopLevelFunctions.freePinnedHostBufferPxVec4(cudaMgr, positionInvMass);
         PxCudaTopLevelFunctions.freePinnedHostBufferPxU32(cudaMgr, NativeArrayHelpers.voidToU32Ptr(phase));
      } catch (Throwable var39) {
         if (mem != null) {
            try {
               mem.close();
            } catch (Throwable var38) {
               var39.addSuppressed(var38);
            }
         }

         throw var39;
      }

      if (mem != null) {
         mem.close();
      }
   }

   @Override
   public void add(PhysicsWorld world) {
      this.world = world;
      this.controller.init(world, this);
   }

   @Override
   public void spawnParticle(double radius, double x, double y, double z) {
   }

   @Override
   public void remove(PhysicsWorld world) {
      if (this.min != null) {
         this.unloadBounds(world, this.min, this.max);
      }

      if (this.particleBuffer != null) {
         world.getFluidSystem().removeParticleBuffer(this.particleBuffer);
      }
   }

   @Override
   public void destroy() {
      if (this.particleBuffer != null) {
         this.particlePositions.destroy();
         this.particleBuffer.release();
         this.particleBuffer = null;
      }
   }
}
