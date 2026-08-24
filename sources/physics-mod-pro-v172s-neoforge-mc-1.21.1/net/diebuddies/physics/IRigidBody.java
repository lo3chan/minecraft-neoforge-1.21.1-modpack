package net.diebuddies.physics;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.math.Vector3i;
import net.diebuddies.minecraft.weather.WeatherEffects;
import net.diebuddies.physics.ocean.OceanWorld;
import net.diebuddies.physics.smoke.ParticleInfo;
import net.diebuddies.physics.wind.WeatherDomain;
import net.diebuddies.util.DoublyLinkedList;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.EmptyFluid;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.physics.PxActorFlagEnum;
import physx.physics.PxArticulationLink;
import physx.physics.PxForceModeEnum;
import physx.physics.PxRigidActor;
import physx.physics.PxRigidBody;
import physx.physics.PxRigidBodyFlagEnum;
import physx.physics.PxRigidDynamic;
import physx.physics.PxShape;

public abstract class IRigidBody implements DoublyLinkedList.NodeStorage<IRigidBody> {
   private static int counter;
   private int hashCode;
   private PhysicsWorld physics;
   protected PxShape shape;
   protected PxRigidActor rigidBody;
   protected PhysicsEntity entity;
   protected Object userData;
   private float mass;
   private MutableBlockPos blockPos;
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
   private MutableBlockPos tmpPos;
   private DoublyLinkedList.Node<IRigidBody> node;

   public IRigidBody() {
      this.hashCode = counter++;
      this.blockPos = new MutableBlockPos();
      this.tmpPos = new MutableBlockPos();
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
            this.entity.getOldTransformation().set(this.entity.getTransformation());
            if (this.entity.getRotation() != null) {
               this.entity.getOldRotation().set(this.entity.getRotation());
            }

            double yPos = this.changedTranslation.y + physics.getOffset().y;
            if (yPos < physics.getWorld().getMinBuildHeight() - 10) {
               this.entity.startDespawnAnimation(physics.getWorld());
            }

            this.entity.getTransformation().translation(this.changedTranslation.x, this.changedTranslation.y, this.changedTranslation.z);
         } else if (!this.smoke) {
            this.entity.getOldTransformation().set(this.entity.getTransformation());
            if (this.entity.getRotation() != null) {
               this.entity.getOldRotation().set(this.entity.getRotation());
            }

            double yPos = this.changedTranslation.y + physics.getOffset().y;
            if (yPos < physics.getWorld().getMinBuildHeight() - 10) {
               this.entity.startDespawnAnimation(physics.getWorld());
            }

            this.entity
               .getTransformation()
               .translationRotate(
                  this.changedTranslation.x,
                  this.changedTranslation.y,
                  this.changedTranslation.z,
                  this.changedRotation.x,
                  this.changedRotation.y,
                  this.changedRotation.z,
                  this.changedRotation.w
               );
            if (this.entity.getRotation() != null) {
               this.entity.getRotation().set(this.changedRotation);
            }
         }
      }

      this.changedTransformation = false;
   }

   public void setPhysicsWorld(PhysicsWorld physics) {
      this.physics = physics;
   }

   public void updatePhysics(PhysicsWorld physics, double diff, boolean blocksChanged) {
      this.physics = physics;
      if (ConfigClient.windPhysics && this.getRigidBody() instanceof PxRigidBody rigidBody && !this.isKinematicOrFrozen()) {
         PxTransform transform = rigidBody.getGlobalPose();
         float posX = MemoryUtil.memGetFloat(transform.getAddress() + 16L);
         float posY = MemoryUtil.memGetFloat(transform.getAddress() + 20L);
         float posZ = MemoryUtil.memGetFloat(transform.getAddress() + 24L);
         Vector3d offset = physics.getOffset();
         WeatherDomain weatherDomain = physics.getWeatherDomain();
         int rx = Mth.floor(posX + offset.x);
         int ry = Mth.floor(posY + offset.y);
         int rz = Mth.floor(posZ + offset.z);
         float forceStrength = weatherDomain.getWindStrength(rx, ry, rz);
         if (forceStrength > 0.001F) {
            Vector3f windDirection = weatherDomain.getWindDirection(rx, ry, rz);
            MemoryStack mem = MemoryStack.stackPush();

            try {
               float strengthMultiplier = 0.35F;
               forceStrength *= strengthMultiplier;
               PxVec3 counterForce = PxVec3.createAt(
                  mem, MemoryStack::nmalloc, windDirection.x * forceStrength, windDirection.y * forceStrength * 0.2F, windDirection.z * forceStrength
               );
               rigidBody.addForce(counterForce, PxForceModeEnum.eVELOCITY_CHANGE, true);
            } catch (Throwable var29) {
               if (mem != null) {
                  try {
                     mem.close();
                  } catch (Throwable var28) {
                     var29.addSuppressed(var28);
                  }
               }

               throw var29;
            }

            if (mem != null) {
               mem.close();
            }
         }
      }

      if (this.liquid) {
         PxRigidActor rigidBodyx = this.getRigidBody();
         PxTransform transform = rigidBodyx.getGlobalPose();
         float posX = MemoryUtil.memGetFloat(transform.getAddress() + 16L);
         float posY = MemoryUtil.memGetFloat(transform.getAddress() + 20L);
         float posZ = MemoryUtil.memGetFloat(transform.getAddress() + 24L);
         this.loadChunkPhysics(posX, posY, posZ);
         this.changedTransformation = true;
         this.changedTranslation.set(posX, posY, posZ);
      } else if (this.smoke) {
         ParticleInfo info = (ParticleInfo)this.getUserData();
         this.loadChunkPhysics((float)info.pos.x, (float)info.pos.y, (float)info.pos.z);
         this.changedTransformation = true;
      } else {
         this.isSleeping = false;
         if (this.rigidBody instanceof PxRigidDynamic dynamicBody) {
            this.isSleeping = dynamicBody.isSleeping();
         }

         Vector3d waveForce = null;
         if (!this.wasSleeping || !this.isSleeping) {
            this.changedTransformation = true;
            this.entity.getOldTransformation().set(this.entity.getTransformation());
            if (this.entity.getRotation() != null) {
               this.entity.getOldRotation().set(this.entity.getRotation());
            }

            PxTransform transform = this.rigidBody.getGlobalPose();
            float rotX = MemoryUtil.memGetFloat(transform.getAddress());
            float rotY = MemoryUtil.memGetFloat(transform.getAddress() + 4L);
            float rotZ = MemoryUtil.memGetFloat(transform.getAddress() + 8L);
            float rotW = MemoryUtil.memGetFloat(transform.getAddress() + 12L);
            float posX = MemoryUtil.memGetFloat(transform.getAddress() + 16L);
            float posY = MemoryUtil.memGetFloat(transform.getAddress() + 20L);
            float posZ = MemoryUtil.memGetFloat(transform.getAddress() + 24L);
            if (ConfigClient.areOceanPhysicsEnabled() && !this.isKinematicOrFrozen()) {
               OceanWorld oceanWorld = physics.getOceanWorld();
               Vector3d offset = physics.getOffset();
               double worldX = posX + offset.x;
               double worldY = posY + offset.y;
               double worldZ = posZ + offset.z;
               waveForce = oceanWorld.calculateWaveForce(worldX, worldY, worldZ);
            }

            this.changedTranslation.set(posX, posY, posZ);
            this.changedRotation.set(rotX, rotY, rotZ, rotW).normalize();
            this.loadChunkPhysics(posX, posY, posZ);
            if (this.blockState == null || !this.tmpPos.equals(this.blockPos) || blocksChanged) {
               MutableBlockPos tmp = this.blockPos;
               this.blockPos = this.tmpPos;
               this.tmpPos = tmp;
               this.blockState = physics.getWorld().getBlockState(this.blockPos);
               if (!(this.blockState.getFluidState().getType() instanceof EmptyFluid)) {
                  this.fluidHeight = this.blockState.getFluidState().getOwnHeight();
                  if (this.blockState
                     .getFluidState()
                     .getType()
                     .isSame(physics.getWorld().getFluidState(this.tmpPos.set(this.blockPos).move(0, 1, 0)).getType())) {
                     this.fluidHeight = 1.0F;
                  }

                  Vec3 fv = this.blockState.getFluidState().getFlow(physics.getWorld(), this.blockPos);
                  this.fluidVelocity.set(fv.x(), fv.y(), fv.z());
                  boolean bubble = false;
                  if (this.blockState.getBlock() == Blocks.BUBBLE_COLUMN) {
                     bubble = true;
                     boolean drag = (Boolean)this.blockState.getValue(BubbleColumnBlock.DRAG_DOWN);
                     if (drag) {
                        this.fluidVelocity.y = -2.0;
                     } else {
                        this.fluidVelocity.y = 8.0;
                     }
                  }

                  this.fluidVelocity.y = bubble ? this.fluidVelocity.y : Math.max(this.fluidVelocity.y, 0.0);
               } else {
                  this.fluidHeight = -1.0F;
                  this.fluidVelocity.set(0.0);
               }
            }
         }

         this.wasSleeping = this.isSleeping;
         float height = this.getFluidHeight();
         boolean oceanWaveForce = waveForce != null && waveForce.y >= 0.0;
         boolean waterBlockForce = height >= 0.0F && (this.getEntity().getTransformation().m31() + physics.getOffset().y) % 1.0 < height;
         if ((oceanWaveForce || waterBlockForce && (waveForce == null || waveForce.y >= 0.0)) && this.getRigidBody() instanceof PxRigidBody rigidBodyx) {
            if (!this.inWater) {
               this.inWater = true;
               this.gravityBefore = this.gravity;
               this.setGravity(false);
               if (ConfigClient.areOceanPhysicsEnabled() && !this.isKinematicOrFrozen() && (ConfigClient.oceanRipples || ConfigClient.oceanParticles)) {
                  float vy = rigidBodyx.getLinearVelocity().getY();
                  if (Math.abs(vy) > 4.52) {
                     Vector3d offset = physics.getOffset();
                     double worldX = this.entity.getTransformation().m30() + offset.x;
                     double worldY = this.entity.getTransformation().m31() + offset.y;
                     double worldZ = this.entity.getTransformation().m32() + offset.z;
                     double objectSize = this.entity.getBoundingSphereRadius();
                     if (ConfigClient.oceanRipples) {
                        double speed = net.diebuddies.math.Math.remapClamp(objectSize, 0.1, 2.0, 0.0325, 0.0625);
                        int amount = (int)net.diebuddies.math.Math.remapClamp(objectSize, 0.1, 2.0, 140.0, 240.0);
                        int lifetime = (int)net.diebuddies.math.Math.remapClamp(objectSize, 0.1, 2.0, 60.0, 80.0);
                        float scale = (float)net.diebuddies.math.Math.remapClamp(objectSize, 0.1, 2.0, 0.15, 0.65);
                        OceanWorld oceanWorld = physics.getOceanWorld();
                        oceanWorld.spawnRipple(amount, lifetime, scale, worldX, worldY, worldZ, speed);
                     }

                     if (objectSize > 0.4) {
                        int splashamount = (int)net.diebuddies.math.Math.remapClamp(objectSize, 0.4, 2.0, 30.0, 75.0);
                        double intensity = net.diebuddies.math.Math.remapClamp(objectSize, 0.4, 2.0, 0.25, 0.7);
                        float volume = (float)intensity * ConfigClient.oceanSplashVolume;
                        float pitch = net.diebuddies.math.Math.random() * 0.4F + 0.7F;
                        Level level = physics.getLevel();
                        level.playLocalSound(worldX, worldY, worldZ, WeatherEffects.SPLASH_SOUND_EVENT, SoundSource.AMBIENT, volume, pitch, true);
                        if (ConfigClient.oceanParticles) {
                           OceanWorld.createWaterSplash(level, worldX, worldY, worldZ, 0.0, 0.0, 0.0, 0.25, intensity, splashamount);
                        }
                     }
                  }
               }
            }

            float mass = this.getMass();
            float flowStrength = 4.6F;
            this.setAngularDamping(2.355F);
            this.setLinearDamping(2.355F);
            MemoryStack mem = MemoryStack.stackPush();

            try {
               Vector3d direction = this.getFluidVelocity();
               Vector3f buoyancy = physics.getDynamicsWorld().getBuoyancy();
               if (rigidBodyx instanceof PxArticulationLink) {
                  PxVec3 v = rigidBodyx.getLinearVelocity();
                  PxVec3 a = rigidBodyx.getAngularVelocity();
                  float damping = 0.885F;
                  float aDamping = 0.885F;
                  float vx = MemoryUtil.memGetFloat(v.getAddress());
                  float vy = MemoryUtil.memGetFloat(v.getAddress() + 4L);
                  float vz = MemoryUtil.memGetFloat(v.getAddress() + 8L);
                  float ax = MemoryUtil.memGetFloat(a.getAddress());
                  float ay = MemoryUtil.memGetFloat(a.getAddress() + 4L);
                  float az = MemoryUtil.memGetFloat(a.getAddress() + 8L);
                  PxVec3 flowForce = PxVec3.createAt(mem, MemoryStack::nmalloc, -vx * (1.0F - damping), -vy * (1.0F - damping), -vz * (1.0F - damping));
                  rigidBodyx.addForce(flowForce, PxForceModeEnum.eVELOCITY_CHANGE);
                  PxVec3 angularForce = PxVec3.createAt(mem, MemoryStack::nmalloc, -ax * (1.0F - aDamping), -ay * (1.0F - aDamping), -az * (1.0F - aDamping));
                  rigidBodyx.addTorque(angularForce, PxForceModeEnum.eVELOCITY_CHANGE);
               }

               float forceX = buoyancy.x + (float)direction.x * flowStrength;
               float forceY = buoyancy.y + (float)direction.y * flowStrength;
               float forceZ = buoyancy.z + (float)direction.z * flowStrength;
               if (waveForce != null) {
                  forceX = (float)(forceX + (buoyancy.x * waveForce.y + -waveForce.x * 3.0));
                  forceY = (float)(forceY + buoyancy.y * waveForce.y);
                  forceZ = (float)(forceZ + (buoyancy.z * waveForce.y + -waveForce.z * 3.0));
               }

               PxVec3 counterForce = PxVec3.createAt(mem, MemoryStack::nmalloc, forceX * mass, forceY * mass, forceZ * mass);
               rigidBodyx.addForce(counterForce, PxForceModeEnum.eFORCE);
            } catch (Throwable var30) {
               if (mem != null) {
                  try {
                     mem.close();
                  } catch (Throwable var27) {
                     var30.addSuppressed(var27);
                  }
               }

               throw var30;
            }

            if (mem != null) {
               mem.close();
            }
         } else {
            if (this.inWater) {
               this.setGravity(this.gravityBefore);
               this.inWater = false;
            }

            this.setAngularDamping(0.0F);
            this.setLinearDamping(0.0F);
         }
      }
   }

   private void loadChunkPhysics(float x, float y, float z) {
      Vector3d offset = this.physics.getOffset();
      this.tmpPos.set(x + offset.x, y + offset.y, z + offset.z);
      if ((this.blockState == null || !this.tmpPos.equals(this.blockPos)) && !this.isKinematicOrFrozen()) {
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
      return !this.kinematic && !this.frozen ? !this.wasSleeping || !this.isSleeping : false;
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

            this.entity.getOldTransformation().set(this.entity.getTransformation());
            if (this.entity.getRotation() != null) {
               this.entity.getOldRotation().set(this.entity.getRotation());
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
      if (this.getRigidBody() instanceof PxRigidDynamic rigidBody) {
         PxVec3 v = rigidBody.getLinearVelocity();
         v.setX((net.diebuddies.math.Math.random() - 0.5F) * strength);
         v.setY((net.diebuddies.math.Math.random() - 0.5F) * strength);
         v.setZ((net.diebuddies.math.Math.random() - 0.5F) * strength);
         rigidBody.setLinearVelocity(v);
      }
   }

   public void applyRandomSpawnForces() {
      this.applyRandomSpawnForces(9.0F);
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

   public MutableBlockPos getBlockPos() {
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

   @Override
   public int hashCode() {
      return this.hashCode;
   }
}
