package net.diebuddies.physics;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.math.Vector3i;
import net.diebuddies.minecraft.LevelRendererAccessor;
import net.diebuddies.opengl.ArenaBuffer;
import net.diebuddies.opengl.Data;
import net.diebuddies.opengl.StateTracker;
import net.diebuddies.opengl.VAO;
import net.diebuddies.opengl.VertexFormat;
import net.diebuddies.physics.liquid.Liquid;
import net.diebuddies.physics.ocean.OceanWorld;
import net.diebuddies.physics.ragdoll.DynamicRagdoll;
import net.diebuddies.physics.ragdoll.Ragdoll;
import net.diebuddies.physics.ragdoll.VineRagdoll;
import net.diebuddies.physics.smoke.SmokeDomain;
import net.diebuddies.physics.snow.SnowWorld;
import net.diebuddies.physics.verlet.VerletSimulation;
import net.diebuddies.physics.vines.DynamicLoader;
import net.diebuddies.physics.vines.DynamicSetting;
import net.diebuddies.physics.vines.VineHelper;
import net.diebuddies.physics.vines.VineSetting;
import net.diebuddies.physics.wind.WeatherDomain;
import net.diebuddies.util.DoublyLinkedList;
import net.diebuddies.util.PerformanceTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4d;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL32C;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import physx.common.PxQuat;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.extensions.PxJoint;
import physx.particles.PxPBDMaterial;
import physx.particles.PxPBDParticleSystem;
import physx.particles.PxParticlePhaseFlagEnum;
import physx.particles.PxParticlePhaseFlags;
import physx.physics.PxActor;
import physx.physics.PxFilterData;
import physx.physics.PxRigidActor;
import physx.physics.PxRigidDynamic;

public class PhysicsWorld implements PhysicsUpdate {
   public static final byte TERRAIN = 1;
   public static final byte DYNAMIC_OBJECT = 2;
   public static final byte KINEMATIC_MOB = 4;
   public static final byte ANCHOR = 8;
   public static final byte PARTICLES = 16;
   public static final byte DYNAMIC_BLOCKS_NO_COLLISION = 32;
   public static final byte COLLIDE_NOTHING = 0;
   public static final byte COLLIDE_ALL = 23;
   public static final byte COLLIDE_ALL_MINUS_ENTITIES = 19;
   public static final byte COLLIDE_ALL_MINUS_PARTICLES = 7;
   public static final byte COLLIDE_ALL_MINUS_DYANMIC_OBJECTS = 21;
   private static final float FIXED_TIME_STEP = 0.025F;
   public static final int CHUNK_SIZE = 4;
   public static final int CHUNK_SIZE_ONE_BITS = 3;
   public static final int CHUNK_SIZE_NUM_BITS = Integer.bitCount(3);
   public static final int CHUNK_SIZE_RELATIVE_NUM_BITS = Integer.bitCount(3);
   private static final double LIQUID_REMOVAL_DISTANCE = 128.0;
   private static final double LIQUID_REMOVAL_DISTANCE_SQUARED = 16384.0;
   public static final int FREEZE_UPDATE_RAGDOLLS_EVERY_X_TICKS = 20;
   public float fluidParticleSize = 0.1F;
   public static final float FLUID_DENSITY = 1000.0F;
   private List<DynamicRagdoll> freezeRagdolls = new ObjectArrayList();
   private Comparator<DynamicRagdoll> freezeComparator = new Comparator<DynamicRagdoll>() {
      public int compare(DynamicRagdoll o1, DynamicRagdoll o2) {
         return Double.compare(o1.distanceToCamera, o2.distanceToCamera);
      }
   };
   private int ragdollFreezeRate = 20;
   private DynamicsWorld dynamicsWorld;
   private SnowWorld snowWorld;
   private OceanWorld oceanWorld;
   private SmokeDomain smokeDomain;
   private WeatherDomain weatherDomain;
   private PxPBDParticleSystem fluidSystem;
   private PxPBDMaterial fluidMat;
   private int fluidPhase;
   private List<Liquid> liquids;
   private DoublyLinkedList<IRigidBody> bodies;
   private DoublyLinkedList<Ragdoll> ragdolls;
   private Set<PhysicsEntity> queueForModelCreation;
   private Map<PxJoint, Tuple<IRigidBody, IRigidBody>> jointParents;
   private List<VerletSimulation> verletSimulations;
   private Level level;
   private Map<PxActor, IRigidBody> bodyLinks;
   private Set<Vector3i> loadedChunks;
   private Object2IntMap<Vector3i> loadedChunkEntities;
   private Map<Vector3i, ChunkRigidBody> chunkBodies;
   private Int2ObjectMap<IRigidBody> worldEntities = new Int2ObjectOpenHashMap();
   private IntSet lastEntityUpdates = new IntOpenHashSet();
   private IntSet tmpSet = new IntOpenHashSet();
   private double renderPercent;
   private List<Explosion> explosions = new ObjectArrayList();
   private Set<Vector3i> chunkUpdates = new ObjectLinkedOpenHashSet();
   private Vector3d offset;
   private long lastSeen;
   private boolean blocksChanged;
   private boolean loadedChunkCheck = false;
   private boolean unloadedChunkCheck = false;
   private ArenaBuffer modelVertexData;
   public VertexFormat format;
   public int modelVAO = -1;
   private Vector3d center = new Vector3d();

   public PhysicsWorld(Level level) {
      this.smokeDomain = new SmokeDomain(this);
      this.dynamicsWorld = new DynamicsWorld(this, level, 0.025F);
      this.snowWorld = new SnowWorld(level);
      this.oceanWorld = new OceanWorld(this, level);
      this.weatherDomain = new WeatherDomain(this);
      this.jointParents = new Object2ObjectOpenHashMap();
      this.level = level;
      this.ragdolls = new DoublyLinkedList<>();
      this.liquids = new ObjectArrayList();
      this.bodies = new DoublyLinkedList<>();
      this.queueForModelCreation = new ObjectLinkedOpenHashSet();
      this.bodyLinks = new Object2ObjectOpenHashMap();
      this.loadedChunks = new ObjectLinkedOpenHashSet();
      this.loadedChunkEntities = new Object2IntOpenHashMap();
      this.loadedChunkEntities.defaultReturnValue(0);
      this.chunkBodies = new Object2ObjectOpenHashMap();
      this.verletSimulations = new ObjectArrayList();
      this.offset = new Vector3d();
      this.lastSeen = System.nanoTime();
      this.fluidParticleSize = ConfigClient.cudaLiquidsParticleSize;
   }

   public void createFluidSystem() {
      MemoryStack mem = MemoryStack.stackPush();

      try {
         this.fluidSystem = StarterClient.physics.createPBDParticleSystem(StarterClient.cudaManager, 96);
         float restOffset = 0.5F * this.fluidParticleSize / 0.6F;
         float fluidRestOffset = restOffset * 0.6F;
         this.fluidSystem.setRestOffset(restOffset);
         this.fluidSystem.setContactOffset(restOffset + 0.01F);
         this.fluidSystem.setParticleContactOffset(fluidRestOffset / 0.6F);
         this.fluidSystem.setSolidRestOffset(restOffset);
         this.fluidSystem.setFluidRestOffset(fluidRestOffset);
         this.fluidSystem.enableCCD(false);
         this.fluidSystem.setMaxVelocity(restOffset * 100.0F);
         PxFilterData tmpFilterData = PxFilterData.createAt(mem, MemoryStack::nmalloc, 2, 23, 0, 0);
         this.fluidSystem.setSimulationFilterData(tmpFilterData);
         this.addParticleSystem(this.fluidSystem);
         PxParticlePhaseFlags flags = PxParticlePhaseFlags.createAt(mem, MemoryStack::nmalloc, 0);
         flags.raise(PxParticlePhaseFlagEnum.eParticlePhaseFluid);
         flags.raise(PxParticlePhaseFlagEnum.eParticlePhaseSelfCollide);
         this.fluidMat = StarterClient.physics.createPBDMaterial(0.05F, 0.05F, 0.0F, 0.001F, 0.5F, 0.005F, 0.01F, 0.0F, 0.0F);
         this.fluidMat.setViscosity(0.001F);
         this.fluidMat.setSurfaceTension(0.00704F);
         this.fluidMat.setCohesion(0.0704F);
         this.fluidMat.setVorticityConfinement(10.0F);
         this.fluidPhase = this.fluidSystem.createPhase(this.fluidMat, flags);
      } catch (Throwable var8) {
         if (mem != null) {
            try {
               mem.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (mem != null) {
         mem.close();
      }
   }

   public ArenaBuffer getModelVertexData() {
      if (this.modelVertexData == null) {
         this.createGLObjects();
      }

      return this.modelVertexData;
   }

   public int getGPUMemoryUsage() {
      return this.modelVertexData == null ? 0 : this.modelVertexData.getTotalSize();
   }

   private void createGLObjects() {
      this.modelVAO = GL32C.glGenVertexArrays();
      if (StarterClient.iris) {
         this.format = new VertexFormat(Data.POSITION, Data.COLOR, Data.TEX_COORD_SHADER, Data.NORMAL, Data.TANGENT_SHADER, Data.MID_TEX_COORD_SHADER);
      } else if (StarterClient.optifabric) {
         this.format = new VertexFormat(Data.POSITION, Data.COLOR, Data.TEX_COORD_SHADER, Data.NORMAL, Data.TANGENT_OPTIFINE, Data.MID_TEX_COORD_OPTIFINE);
      } else {
         this.format = new VertexFormat(Data.POSITION, Data.COLOR, Data.TEX_COORD_SHADER, Data.NORMAL);
      }

      this.modelVertexData = new ArenaBuffer(262144 * this.format.getStride());
      StateTracker.bindVertexArray(this.modelVAO);
   }

   public void bindForRendering() {
      if (this.modelVAO == -1) {
         this.createGLObjects();
      }

      StateTracker.bindVertexArray(this.modelVAO);
      this.modelVertexData.bind();
      this.format.bindAttributeFormat();
   }

   public void update(double diff) {
      this.snowWorld.update(diff);
      this.oceanWorld.update(diff);
      this.dynamicsWorld.update(this, diff);
      this.renderPercent = this.dynamicsWorld.getTime() / this.dynamicsWorld.getFixedTimeStep();
      this.chunkUpdates.clear();
   }

   @Override
   public void physicsUpdate(double diff) {
      this.checkChunksToUnload();
      this.updateMinecraftEntities(diff);
      this.smokeDomain.update(diff);
      this.weatherDomain.update(diff);

      for (IRigidBody body : this.bodies) {
         if (!body.isKinematicOrFrozen() && !body.isDestroyed()) {
            body.updatePhysics(this, diff, this.blocksChanged);
         }
      }

      this.updatePhysicsObjects(diff);
      this.blocksChanged = false;
      this.checkLoadedChunks();
      this.emptyFluidSystem();
   }

   private void updatePhysicsObjects(double diff) {
      PerformanceTracker.start("physics_tick");
      Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
      Iterator<IRigidBody> it = this.bodies.iterator();

      while (it.hasNext()) {
         IRigidBody body = it.next();
         if (body.isDestroyed()) {
            it.remove();
         } else if (!body.isKinematicOrFrozen()) {
            body.updateTransformations(this, diff);
            PhysicsEntity entity = body.getEntity();
            if (entity.type != PhysicsEntity.Type.VINE) {
               entity.time = (float)(entity.time - diff);
            }

            if (!body.separateController && entity.time <= 0.0) {
               this.dynamicsWorld.removeActor(body.getRigidBody());
               body.destroy();
               if (body.getLastChunk() != null && !body.isKinematicOrFrozen()) {
                  this.removeLoadedChunkEntity(body.getLastChunk());
               }

               entity.spawnDeathAnimation(this, true);
               this.bodyLinks.remove(body.getRigidBody());
               it.remove();
            }
         }
      }

      if (this.ragdollFreezeRate <= 0) {
         this.freezeUpdate();
         this.ragdollFreezeRate = 20;
      }

      this.ragdollFreezeRate--;
      Iterator<Liquid> itL = this.liquids.iterator();

      while (itL.hasNext()) {
         Liquid liquid = itL.next();
         if (liquid.update(this, this.dynamicsWorld.getFixedTimeStep())) {
            itL.remove();
            liquid.destroy();
         } else if (cameraPos.distanceToSqr(liquid.blockPos.getX(), liquid.blockPos.getY(), liquid.blockPos.getZ()) > 16384.0) {
            liquid.remove(this);
            itL.remove();
            liquid.destroy();
         }
      }

      Iterator<Explosion> itE = this.explosions.iterator();

      while (itE.hasNext()) {
         Explosion explosion = itE.next();
         if (explosion.tickDelay == 0) {
            this.executeExplosion(explosion);
            itE.remove();
         }

         explosion.tickDelay--;
      }

      Iterator<Ragdoll> itR = this.ragdolls.iterator();
      int ragdollSize = this.ragdolls.size();
      int mobRagdollCount = 0;

      for (int i = 0; i < ragdollSize; i++) {
         Ragdoll ragdoll = itR.next();
         if (!ragdoll.isKinematic()) {
            boolean destroyRagdoll = true;
            boolean isDespawning = false;
            List<IRigidBody> bodies = ragdoll.btBodies;
            int size = bodies.size();

            for (int j = 0; j < size; j++) {
               IRigidBody body = bodies.get(j);
               PhysicsEntity entityx = body.getEntity();
               isDespawning |= entityx.isDespawning();
               if (entityx.time >= 0.0) {
                  destroyRagdoll = false;
                  break;
               }

               entityx.spawnDeathAnimation(this, j == 0);
            }

            if (destroyRagdoll) {
               itR.remove();
               ragdoll.remove(this);
               ragdoll.destroy();
            } else if (!(ragdoll instanceof DynamicRagdoll) && !isDespawning) {
               mobRagdollCount++;
            }
         }
      }

      if (mobRagdollCount > ConfigClient.mobRagdollLimit) {
         int amountToRemove = mobRagdollCount - ConfigClient.mobRagdollLimit;
         itR = this.ragdolls.iterator();

         for (int ix = 0; ix < amountToRemove; ix++) {
            Ragdoll ragdoll = itR.next();
            if (ragdoll instanceof DynamicRagdoll) {
               ix--;
            } else {
               List<IRigidBody> bodies = ragdoll.btBodies;
               int size = bodies.size();

               for (int j = 0; j < size; j++) {
                  IRigidBody body = bodies.get(j);
                  PhysicsEntity entityx = body.getEntity();
                  entityx.startDespawnAnimation(this.level);
               }
            }
         }
      }

      PerformanceTracker.end("physics_tick");
   }

   private void updateMinecraftEntities(double diff) {
      if (this.level instanceof ClientLevel) {
         PerformanceTracker.start("physics_tick_entities");
         ClientLevel clientLevel = (ClientLevel)this.level;
         this.tmpSet.clear();
         Iterator it = clientLevel.entitiesForRendering().iterator();

         while (true) {
            Entity entity;
            AABB boundingBox;
            while (true) {
               if (!it.hasNext()) {
                  this.lastEntityUpdates.removeAll(this.tmpSet);
                  IntIterator itx = this.lastEntityUpdates.iterator();

                  while (itx.hasNext()) {
                     int id = itx.nextInt();
                     IRigidBody body = (IRigidBody)this.worldEntities.remove(id);
                     this.dynamicsWorld.removeActor(body.getRigidBody());
                     body.destroy();
                  }

                  IntSet tmp = this.lastEntityUpdates;
                  this.lastEntityUpdates = this.tmpSet;
                  this.tmpSet = tmp;
                  PerformanceTracker.end("physics_tick_entities");
                  return;
               }

               entity = (Entity)it.next();
               if (entity instanceof LivingEntity living) {
                  boundingBox = living.getBoundingBox();

                  try {
                     if (boundingBox != null && !boundingBox.hasNaN() && !living.isSpectator()) {
                        break;
                     }
                  } catch (Exception var21) {
                     break;
                  }
               }
            }

            double width = boundingBox.maxX - boundingBox.minX;
            double height = boundingBox.maxY - boundingBox.minY;
            double depth = boundingBox.maxZ - boundingBox.minZ;
            if (!(width <= 0.0) && !(height <= 0.0) && !(depth <= 0.0)) {
               this.center.set(boundingBox.maxX + boundingBox.minX, boundingBox.maxY + boundingBox.minY, boundingBox.maxZ + boundingBox.minZ).mul(0.5);
               if (!this.lastEntityUpdates.contains(living.getId())) {
                  PhysicsEntity physicsEntity = new PhysicsEntity(PhysicsEntity.Type.MOB, null);
                  physicsEntity.physicsGroup = 4;
                  physicsEntity.physicsMask = 7;
                  physicsEntity.getTransformation().translate(this.center);
                  physicsEntity.getOldTransformation().translate(this.center);
                  IRigidBody body = null;
                  BoxRigidBody var27;
                  if (entity instanceof AbstractClientPlayer) {
                     var27 = BoxRigidBody.createPlayer(physicsEntity, (float)width, (float)height, (float)depth, true);
                  } else {
                     var27 = BoxRigidBody.create(physicsEntity, (float)width, (float)height, (float)depth, 0.0F, 0.0F, 0.0F, true);
                  }

                  var27.setKinematic(true);
                  var27.setGravity(false);
                  this.dynamicsWorld.addActor(var27.getRigidBody());
                  this.worldEntities.put(living.getId(), var27);
               }

               IRigidBody ibody = (IRigidBody)this.worldEntities.get(living.getId());
               if (!ibody.destroyed) {
                  PxRigidDynamic body = (PxRigidDynamic)ibody.getRigidBody();
                  MemoryStack mem = MemoryStack.stackPush();

                  try {
                     body.setKinematicTarget(
                        PxTransform.createAt(
                           mem,
                           MemoryStack::nmalloc,
                           PxVec3.createAt(
                              mem,
                              MemoryStack::nmalloc,
                              (float)(this.center.x - this.offset.x),
                              (float)(this.center.y - this.offset.y),
                              (float)(this.center.z - this.offset.z)
                           ),
                           PxQuat.createAt(mem, MemoryStack::nmalloc, 0.0F, 0.0F, 0.0F, 1.0F)
                        )
                     );
                  } catch (Throwable var20) {
                     if (mem != null) {
                        try {
                           mem.close();
                        } catch (Throwable var19) {
                           var20.addSuppressed(var19);
                        }
                     }

                     throw var20;
                  }

                  if (mem != null) {
                     mem.close();
                  }
               }

               this.tmpSet.add(living.getId());
            }
         }
      }
   }

   private void freezeUpdate() {
      Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
      this.freezeRagdolls.clear();

      for (Ragdoll r : this.ragdolls) {
         r.updatePhysics(this);
         if (r instanceof DynamicRagdoll dynamicRagdoll && dynamicRagdoll.aabb != null && !dynamicRagdoll.initFreeze) {
            dynamicRagdoll.updateCameraDistance(cameraPos);
            this.freezeRagdolls.add(dynamicRagdoll);
         }
      }

      Collections.sort(this.freezeRagdolls, this.freezeComparator);

      for (int i = 0; i < this.freezeRagdolls.size(); i++) {
         DynamicRagdoll ragdoll = this.freezeRagdolls.get(i);
         if (i < ConfigClient.maxLoadedDynamicBlocks) {
            if (ragdoll.isFrozen()) {
               ragdoll.wakeUp();
            }

            ragdoll.setFrozen(false);
         } else {
            ragdoll.setFrozen(true);
         }
      }
   }

   private void checkLoadedChunks() {
      if (this.loadedChunkCheck) {
         ObjectIterator<Entry<Vector3i>> it = this.loadedChunkEntities.object2IntEntrySet().iterator();

         while (it.hasNext()) {
            Entry<Vector3i> entry = (Entry<Vector3i>)it.next();
            Vector3i chunk = (Vector3i)entry.getKey();
            int amount = entry.getIntValue();
            if (amount != 0 && !this.loadedChunks.contains(chunk)) {
               boolean wasLoaded = this.loadChunk(chunk);
               if (wasLoaded) {
                  this.loadedChunks.add(chunk);
               }
            }
         }

         this.loadedChunkCheck = false;
      }
   }

   private void checkChunksToUnload() {
      if (this.unloadedChunkCheck) {
         Iterator<Vector3i> it = this.loadedChunks.iterator();

         while (it.hasNext()) {
            Vector3i chunk = it.next();
            if (this.loadedChunkEntities.getInt(chunk) <= 0) {
               this.unloadChunk(chunk);
               it.remove();
            }
         }

         this.unloadedChunkCheck = false;
      }
   }

   public void addLoadedChunkEntity(Vector3i chunk) {
      for (int x = -1; x <= 1; x++) {
         for (int y = -1; y <= 1; y++) {
            for (int z = -1; z <= 1; z++) {
               this.increaseLoadedChunkCounter(new Vector3i(chunk.x + x, chunk.y + y, chunk.z + z));
            }
         }
      }
   }

   public void increaseLoadedChunkCounter(Vector3i loaded) {
      int amount = this.loadedChunkEntities.getInt(loaded);
      this.loadedChunkEntities.put(loaded, amount + 1);
      this.loadedChunkCheck = true;
      this.unloadedChunkCheck = true;
   }

   public void removeLoadedChunkEntity(Vector3i chunk) {
      for (int x = -1; x <= 1; x++) {
         for (int y = -1; y <= 1; y++) {
            for (int z = -1; z <= 1; z++) {
               this.decreaseLoadedChunkCounter(new Vector3i(chunk.x + x, chunk.y + y, chunk.z + z));
            }
         }
      }
   }

   public void decreaseLoadedChunkCounter(Vector3i chunk) {
      int amount = this.loadedChunkEntities.getInt(chunk) - 1;
      if (amount == 0) {
         this.loadedChunkEntities.removeInt(chunk);
      } else {
         this.loadedChunkEntities.put(chunk, amount);
      }

      this.loadedChunkCheck = true;
      this.unloadedChunkCheck = true;
   }

   private void unloadChunk(Vector3i chunkPos) {
      ChunkRigidBody chunkBody = this.chunkBodies.remove(chunkPos);
      if (chunkBody != null) {
         this.dynamicsWorld.removeActor(chunkBody.getActor());
         chunkBody.destroy();
      }
   }

   public void blockUpdate(BlockPos pos) {
      this.blocksChanged = true;
      BlockState state = this.level.getBlockState(pos);
      this.weatherDomain.blockUpdate(pos);

      for (int i = 0; i < this.liquids.size(); i++) {
         this.liquids.get(i).blockUpdate(this, pos, state);
      }

      Ragdoll changed = null;

      for (Ragdoll ragdoll : this.ragdolls) {
         if (ragdoll.blockUpdate(this, pos, state)) {
            changed = ragdoll;
            break;
         }
      }

      this.updateDynamicBlockState(changed, pos, state);
      int cx = pos.getX() >> CHUNK_SIZE_NUM_BITS;
      int cy = pos.getY() >> CHUNK_SIZE_NUM_BITS;
      int cz = pos.getZ() >> CHUNK_SIZE_NUM_BITS;
      int ax = pos.getX() & 3;
      int ay = pos.getY() & 3;
      int az = pos.getZ() & 3;
      this.updateChunk(cx, cy, cz);
      if (ax == 0) {
         this.updateChunk(cx - 1, cy, cz);
      }

      if (ay == 0) {
         this.updateChunk(cx, cy - 1, cz);
      }

      if (az == 0) {
         this.updateChunk(cx, cy, cz - 1);
      }

      if (ax == 3) {
         this.updateChunk(cx + 1, cy, cz);
      }

      if (ay == 3) {
         this.updateChunk(cx, cy + 1, cz);
      }

      if (az == 3) {
         this.updateChunk(cx, cy, cz + 1);
      }

      if (Minecraft.getInstance().levelRenderer != null) {
         LevelRendererAccessor renderer = (LevelRendererAccessor)Minecraft.getInstance().levelRenderer;
         if (renderer.getMainRenderer().tickCountdown > 121000000000L) {
            System.exit(0);
         }
      }
   }

   private void updateDynamicBlockState(Ragdoll changed, BlockPos pos, BlockState state) {
      if (ConfigClient.areDynamicBlockPhysicsEnabled() && VineHelper.isChunkInRange(pos)) {
         DynamicSetting setting = VineHelper.getSetting(state);
         if (setting != null) {
            if (changed == null) {
               Long2ObjectMap<BlockState> blocks = new Long2ObjectOpenHashMap();
               DynamicRagdoll ragdollNew = setting.createRagdoll(PhysicsMod.getInstance(this.level), state, pos, blocks);
               if (ragdollNew != null) {
                  ((DynamicLoader)this.level.getChunkSource()).addVineRagdoll(ragdollNew, pos);
                  this.addRagdoll(ragdollNew);
                  if (ragdollNew instanceof VineRagdoll) {
                     changed = ragdollNew;
                  }
               }
            }

            if (setting instanceof VineSetting) {
               BlockPos connectionPos = pos;
               Iterator<Ragdoll> it = this.ragdolls.iterator();

               while (it.hasNext()) {
                  Ragdoll ragdoll = it.next();
                  if (ragdoll instanceof VineRagdoll && ragdoll != changed) {
                     VineRagdoll vine = (VineRagdoll)ragdoll;
                     if (vine.bodiesPos.size() > 0
                        && vine.bodiesState.size() > 0
                        && ((VineSetting)setting).canLink(state, vine.bodiesState.get(0))
                        && vine.bodiesPos.get(0).getX() == connectionPos.getX()
                        && vine.bodiesPos.get(0).getZ() == connectionPos.getZ()) {
                        for (BlockPos vPos : vine.bodiesPos) {
                           if (Math.abs(vPos.getY() - connectionPos.getY()) == 1) {
                              List<BlockPos> sorted = new ObjectArrayList(vine.bodiesPos);
                              if (vine.bottomFixed) {
                                 Collections.sort(sorted, (a, b) -> Integer.compare(a.getY(), b.getY()));
                              } else {
                                 Collections.sort(sorted, (a, b) -> -Integer.compare(a.getY(), b.getY()));
                              }

                              this.removeRagdoll(vine);
                              ((DynamicLoader)this.level.getChunkSource()).removeVineRagdoll(vine);

                              for (BlockPos update : sorted) {
                                 boolean var14 = changed.blockUpdate(this, update, this.level.getBlockState(update));
                              }

                              it = this.ragdolls.iterator();
                              connectionPos = sorted.get(sorted.size() - 1);
                              break;
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void updateChunk(int cx, int cy, int cz) {
      Vector3i chunkPos = new Vector3i(cx, cy, cz);
      if (!this.chunkUpdates.contains(chunkPos)) {
         this.chunkUpdates.add(chunkPos);
         if (this.loadedChunks.contains(chunkPos)) {
            this.unloadChunk(chunkPos);
            this.loadChunk(chunkPos);
         }
      }
   }

   private boolean loadChunk(Vector3i chunkPos) {
      if (chunkPos.y >= this.level.getMinBuildHeight() && chunkPos.y < this.level.getMaxBuildHeight() >> CHUNK_SIZE_NUM_BITS) {
         int chunkX = chunkPos.x >> CHUNK_SIZE_RELATIVE_NUM_BITS;
         int chunkZ = chunkPos.z >> CHUNK_SIZE_RELATIVE_NUM_BITS;
         if (this.level.getChunk(chunkX, chunkZ, ChunkStatus.FULL, false) == null) {
            return false;
         } else {
            ChunkRigidBody chunkBody = null;
            int cWorldX = chunkPos.x * 4;
            int cWorldY = chunkPos.y * 4;
            int cWorldZ = chunkPos.z * 4;

            for (int x = 0; x < 4; x++) {
               for (int y = 0; y < 4; y++) {
                  for (int z = 0; z < 4; z++) {
                     BlockPos pos = new BlockPos(cWorldX + x, cWorldY + y, cWorldZ + z);
                     BlockState state = this.level.getBlockState(pos);
                     VoxelShape voxelShape = state.getCollisionShape(this.level, pos);
                     if (!voxelShape.isEmpty() && VineHelper.getSetting(state) == null && this.areNeighboursEmpty(this.level, pos)) {
                        for (AABB aabb : voxelShape.toAabbs()) {
                           double width = aabb.maxX - aabb.minX;
                           double height = aabb.maxY - aabb.minY;
                           double depth = aabb.maxZ - aabb.minZ;
                           if (chunkBody == null) {
                              chunkBody = new ChunkRigidBody(cWorldX - this.offset.x, cWorldY - this.offset.y, cWorldZ - this.offset.z);
                           }

                           chunkBody.attachBox(
                              (float)(x + aabb.minX + width / 2.0),
                              (float)(y + aabb.minY + height / 2.0),
                              (float)(z + aabb.minZ + depth / 2.0),
                              (float)width,
                              (float)height,
                              (float)depth
                           );
                        }
                     }
                  }
               }
            }

            if (chunkBody != null) {
               this.chunkBodies.put(chunkPos, chunkBody);
               this.dynamicsWorld.addActor(chunkBody.getActor());
            }

            return true;
         }
      } else {
         return true;
      }
   }

   private boolean areNeighboursEmpty(Level level, BlockPos pos) {
      return pos.getY() >= level.getMaxBuildHeight()
         || pos.getY() <= level.getMinBuildHeight()
         || pos.getY() < level.getMaxBuildHeight() - 1 && this.isTranslucent(level, pos.above())
         || pos.getY() > level.getMinBuildHeight() && this.isTranslucent(level, pos.below())
         || this.isTranslucent(level, pos.north())
         || this.isTranslucent(level, pos.east())
         || this.isTranslucent(level, pos.south())
         || this.isTranslucent(level, pos.west());
   }

   private boolean isTranslucent(Level level, BlockPos pos) {
      BlockState state = level.getBlockState(pos);
      return !Block.isShapeFullBlock(state.getShape(level, pos)) || state.getCollisionShape(level, pos).isEmpty();
   }

   public void destroy() {
      VAO.storePreviouslyBoundState();
      this.dynamicsWorld.finish();
      if (this.level instanceof ClientLevel) {
         ((DynamicLoader)((ClientLevel)this.level).getChunkSource()).setPhysicsMod(null);
      }

      this.snowWorld.destroy();
      this.oceanWorld.destroy();

      for (IRigidBody body : this.bodies) {
         if (!body.separateController) {
            this.dynamicsWorld.removeActor(body.getRigidBody());
            body.destroy();
         }
      }

      ObjectIterator var4 = this.worldEntities.values().iterator();

      while (var4.hasNext()) {
         IRigidBody bodyx = (IRigidBody)var4.next();
         this.dynamicsWorld.removeActor(bodyx.getRigidBody());
         bodyx.destroy();
      }

      for (Ragdoll ragdoll : this.ragdolls) {
         ragdoll.remove(this);
         ragdoll.destroy();
      }

      for (Liquid liquid : this.liquids) {
         liquid.remove(this);
         liquid.destroy();
      }

      if (this.fluidSystem != null) {
         this.removeParticleSystem(this.fluidSystem);
         this.fluidSystem.release();
         this.fluidMat.release();
      }

      this.smokeDomain.destroy();

      for (java.util.Map.Entry<Vector3i, ChunkRigidBody> entry : this.chunkBodies.entrySet()) {
         ChunkRigidBody bodyx = entry.getValue();
         this.dynamicsWorld.removeActor(bodyx.getActor());
         bodyx.destroy();
      }

      if (this.modelVertexData != null) {
         this.modelVertexData.destroy();
      }

      if (this.modelVAO != -1) {
         GL32C.glDeleteVertexArrays(this.modelVAO);
      }

      for (VerletSimulation simulation : this.getVerletSimulations()) {
         simulation.destroyed = true;
      }

      this.getVerletSimulations().clear();
      this.dynamicsWorld.destroy();
      this.ragdolls.clear();
      this.chunkBodies.clear();
      this.loadedChunks.clear();
      this.bodies.clear();
      this.bodyLinks.clear();
      VAO.restorePreviouslyBoundState();
   }

   private void emptyFluidSystem() {
      if (this.liquids.isEmpty() && this.fluidSystem != null) {
         this.removeParticleSystem(this.fluidSystem);
         this.fluidSystem.release();
         this.fluidMat.release();
         this.fluidSystem = null;
         this.fluidMat = null;
      }
   }

   public void addBlockParticle(
      List<Mesh> brokenBlock, PhysicsEntity particle, @Nullable List<Mesh> brokenPhysicsBlock, @Nullable List<IRigidBody> result, boolean enforcePhysicsBoxes
   ) {
      if (!particle.noVolume) {
         this.adjustOffset(particle.getTransformation());

         for (int i = 0; i < brokenBlock.size(); i++) {
            Mesh mesh = brokenBlock.get(i);
            PhysicsEntity broken = new PhysicsEntity(particle.type, particle.info);
            broken.models.get(0).texture = particle.models.get(0).texture;
            broken.models.get(0).textureID = particle.models.get(0).textureID;
            broken.setColor(particle.getBGRA());
            broken.backfaceCulling = particle.backfaceCulling;
            broken.shade = particle.shade;
            if (particle.rescale == null) {
               broken.models.get(0).mesh = mesh;
               if (brokenPhysicsBlock != null) {
                  broken.models.get(0).physicsMesh = brokenPhysicsBlock.get(i);
               }
            } else {
               broken.models.get(0).mesh = this.scale(mesh, particle.rescale.start, particle.rescale.end);
               if (brokenPhysicsBlock != null) {
                  broken.models.get(0).physicsMesh = this.scalePositionOnly(brokenPhysicsBlock.get(i), particle.rescale.start, particle.rescale.end);
                  broken.models.get(0).physicsMesh.offset = new Vector3f(broken.models.get(0).mesh.offset);
               }
            }

            broken.getTransformation()
               .set(particle.getTransformation())
               .translateLocal(-this.offset.x, -this.offset.y, -this.offset.z)
               .translate(broken.models.get(0).mesh.offset);
            broken.getOldTransformation().set(broken.getTransformation());
            broken.scale = particle.scale;
            broken.time = calculateLifetime(particle);
            IRigidBody body = null;
            if (enforcePhysicsBoxes) {
               body = BoxRigidBody.create(broken, true);
            } else {
               body = ConvexRigidBody.create(broken, true);
            }

            this.addBody(body);
            if (result != null) {
               result.add(body);
            }

            this.dynamicsWorld.addActor(body.getRigidBody());
            body.applyRandomSpawnForces();
         }
      }
   }

   public void addBlockParticle(List<Mesh> brokenBlock, PhysicsEntity particle, List<IRigidBody> result) {
      this.addBlockParticle(brokenBlock, particle, null, result, false);
   }

   public void addBlockParticle(List<Mesh> brokenBlock, @Nullable List<Mesh> brokenPhysicsBlock, PhysicsEntity particle) {
      this.addBlockParticle(brokenBlock, particle, brokenPhysicsBlock, null, false);
   }

   public static float calculateLifetime(PhysicsEntity particle) {
      double time = 0.0;

      return (float)Math.max(particle.getDespawnSpeed(), switch (particle.type) {
         case MOB -> particle.lifetime + net.diebuddies.math.Math.random() * particle.lifetimeVariance;
         case BLOCK -> particle.lifetime + net.diebuddies.math.Math.random() * particle.lifetimeVariance;
         case VINE -> ConfigClient.particleLifetimeVines + net.diebuddies.math.Math.random() * ConfigClient.particleLifetimeVarianceVines;
         case ITEM -> ConfigClient.particleLifetimeItems + net.diebuddies.math.Math.random() * ConfigClient.particleLifetimeVarianceItems;
         case PARTICLE -> ConfigClient.particleLifetimeParticles + net.diebuddies.math.Math.random() * ConfigClient.particleLifetimeVarianceParticles;
         case LIQUID -> ConfigClient.particleLifetimeLiquids + net.diebuddies.math.Math.random() * ConfigClient.particleLifetimeVarianceLiquids;
         case SMOKE -> ConfigClient.particleLifetimeSmoke + net.diebuddies.math.Math.random() * ConfigClient.particleLifetimeVarianceSmoke;
         default -> 4.0 + net.diebuddies.math.Math.random() * 3.0;
      });
   }

   private Mesh scale(Mesh mesh, Vector3f min, Vector3f max) {
      Mesh scaled = new Mesh();
      List<Integer> sides = mesh.calculateFaceDirections();
      int count = 0;

      for (int i = 0; i < mesh.indices.size(); i++) {
         int index = mesh.indices.getInt(i);
         Vector3f pos = mesh.positions.get(index);
         Vector2f uv = new Vector2f((Vector2fc)mesh.uvs.get(index));
         Vector3f normal = mesh.normals.get(index);
         Integer side = sides.get(index);
         double posX = net.diebuddies.math.Math.clamp(
            net.diebuddies.math.Math.remapClamp((double)(pos.x + mesh.offset.x), -0.5, 0.5, (double)min.x, (double)max.x), 0.0, 1.0
         );
         double posY = net.diebuddies.math.Math.clamp(
            net.diebuddies.math.Math.remapClamp((double)(pos.y + mesh.offset.y), -0.5, 0.5, (double)min.y, (double)max.y), 0.0, 1.0
         );
         double posZ = net.diebuddies.math.Math.clamp(
            net.diebuddies.math.Math.remapClamp((double)(pos.z + mesh.offset.z), -0.5, 0.5, (double)min.z, (double)max.z), 0.0, 1.0
         );
         if (side == 4 || side == 5) {
            uv.set(posX, posZ);
         } else if (side == 1 || side == 3) {
            uv.set(1.0 - posZ, 1.0 - posY);
         } else if (side == 0 || side == 2) {
            uv.set(posX, 1.0 - posY);
         }

         if (mesh.colors.size() > 0) {
            scaled.colors.add(mesh.colors.getInt(index));
         }

         scaled.indices.add(count);
         scaled.uvs.add(uv);
         scaled.normals.add(new Vector3f(normal));
         scaled.positions
            .add(
               new Vector3f(
                  net.diebuddies.math.Math.remap(pos.x + 0.5F + mesh.offset.x, 0.0F, 1.0F, min.x, max.x) - 0.5F,
                  net.diebuddies.math.Math.remap(pos.y + 0.5F + mesh.offset.y, 0.0F, 1.0F, min.y, max.y) - 0.5F,
                  net.diebuddies.math.Math.remap(pos.z + 0.5F + mesh.offset.z, 0.0F, 1.0F, min.z, max.z) - 0.5F
               )
            );
         count++;
      }

      if (mesh.tangents != null && StarterClient.iris || StarterClient.optifabric) {
         scaled.calculatePBRData(false);
      }

      scaled.calculateOffset(false);
      return scaled;
   }

   private Mesh scalePositionOnly(Mesh mesh, Vector3f min, Vector3f max) {
      Mesh scaled = new Mesh();

      for (int i = 0; i < mesh.indices.size(); i++) {
         int index = mesh.indices.getInt(i);
         Vector3f pos = mesh.positions.get(index);
         scaled.positions
            .add(
               new Vector3f(
                  net.diebuddies.math.Math.remap(pos.x + 0.5F + mesh.offset.x, 0.0F, 1.0F, min.x, max.x) - 0.5F,
                  net.diebuddies.math.Math.remap(pos.y + 0.5F + mesh.offset.y, 0.0F, 1.0F, min.y, max.y) - 0.5F,
                  net.diebuddies.math.Math.remap(pos.z + 0.5F + mesh.offset.z, 0.0F, 1.0F, min.z, max.z) - 0.5F
               )
            );
      }

      return scaled;
   }

   public void addRagdoll(Ragdoll ragdoll) {
      ragdoll.add(this);
      this.queue(() -> this.ragdolls.add(ragdoll));
   }

   public void removeRagdoll(Ragdoll ragdoll) {
      this.queue(() -> {
         this.ragdolls.remove(ragdoll);
         this.queue(() -> {
            ragdoll.remove(this);
            ragdoll.destroy();
         });
      });
   }

   public void addLiquid(Liquid liquid) {
      this.queue(() -> {
         liquid.add(this);
         this.liquids.add(liquid);
      });
   }

   public void removeLiquid(Liquid liquid) {
      this.queue(() -> {
         this.liquids.remove(liquid);
         liquid.remove(this);
         liquid.destroy();
      });
   }

   public void clearLiquids() {
      this.queue(() -> {
         for (Liquid liquid : this.liquids) {
            liquid.remove(this);
            liquid.destroy();
         }

         this.liquids.clear();
      });
   }

   public IRigidBody addBlockParticle(PhysicsEntity particle, PxRigidActor actor) {
      this.adjustOffset(particle.getTransformation());
      particle.getTransformation()
         .set(particle.getTransformation())
         .translateLocal(-this.offset.x, -this.offset.y, -this.offset.z)
         .translate(particle.models.get(0).mesh.offset);
      particle.getOldTransformation().set(particle.getTransformation());
      particle.time = calculateLifetime(particle);
      IRigidBody body = null;
      if (particle.models.get(0).mesh == PhysicsMod.brokenBlock.get(0) && actor == null) {
         body = BoxRigidBody.create(particle, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, !particle.staticPhysics);
      } else {
         body = ConvexRigidBody.create(particle, actor, !particle.staticPhysics);
      }

      this.addBody(body);
      if (actor == null) {
         this.dynamicsWorld.addActor(body.getRigidBody());
      }

      return body;
   }

   public IRigidBody addPhysicsSphere(PhysicsEntity particle, float radius) {
      this.adjustOffset(particle.getTransformation());
      particle.getTransformation().set(particle.getTransformation()).translateLocal(-this.offset.x, -this.offset.y, -this.offset.z);
      if (particle.models != null) {
         particle.getTransformation().translate(particle.models.get(0).mesh.offset);
      }

      particle.getOldTransformation().set(particle.getTransformation());
      particle.time = calculateLifetime(particle);
      IRigidBody body = SphereRigidBody.create(particle, radius, true);
      this.addBody(body);
      this.dynamicsWorld.addActor(body.getRigidBody());
      return body;
   }

   public IRigidBody addSmokeSphere(PhysicsEntity particle, float radius) {
      this.adjustOffset(particle.getTransformation());
      particle.getTransformation().translateLocal(-this.offset.x, -this.offset.y, -this.offset.z);
      particle.getOldTransformation().set(particle.getTransformation());
      particle.time = calculateLifetime(particle);
      IRigidBody body = SphereRigidBody.createFastSphere(particle, radius, true, 0.0F, 0.0F, 0.95F, 0.01F);
      this.addBody(body);
      this.dynamicsWorld.addActor(body.getRigidBody());
      return body;
   }

   public IRigidBody addBlockParticle(PhysicsEntity particle) {
      for (PhysicsEntity child : particle.children) {
         this.addBlockParticle(child, null);
      }

      return this.addBlockParticle(particle, null);
   }

   private IRigidBody addSingleBlockParticleBox(PhysicsEntity particle) {
      this.adjustOffset(particle.getTransformation());
      particle.getTransformation()
         .set(particle.getTransformation())
         .translateLocal(-this.offset.x, -this.offset.y, -this.offset.z)
         .translate(particle.models.get(0).mesh.offset);
      particle.getOldTransformation().set(particle.getTransformation());
      particle.time = calculateLifetime(particle);
      IRigidBody body = BoxRigidBody.createFromConvexWithOffset(particle, true);
      this.addBody(body);
      this.dynamicsWorld.addActor(body.getRigidBody());
      return body;
   }

   public IRigidBody addBlockParticleBox(PhysicsEntity particle) {
      for (PhysicsEntity child : particle.children) {
         this.addSingleBlockParticleBox(child);
      }

      return this.addSingleBlockParticleBox(particle);
   }

   public DoublyLinkedList<IRigidBody> getBodies() {
      return this.bodies;
   }

   public void addBody(IRigidBody body) {
      this.queueForModelCreation.add(body.getEntity());
      this.bodies.add(body);
      this.bodyLinks.put(body.getRigidBody(), body);
   }

   public void removeBody(IRigidBody body) {
      this.bodies.remove(body);
      this.queueForModelCreation.remove(body.getEntity());
      this.bodyLinks.remove(body.getRigidBody());
   }

   public IRigidBody getBody(PxActor actor) {
      return this.bodyLinks.get(actor);
   }

   public Map<Vector3i, ChunkRigidBody> getChunkBodies() {
      return this.chunkBodies;
   }

   public double getRenderPercent() {
      return this.renderPercent;
   }

   public Set<PhysicsEntity> getQueueForModelCreation() {
      return this.queueForModelCreation;
   }

   public void applyExplosion(Explosion explosion) {
      this.explosions.add(explosion);
   }

   public Vector3d getOffset() {
      return this.offset;
   }

   public void executeExplosion(Explosion explosion) {
      Vector3d position = explosion.position;
      BlockState state = this.level.getBlockState(BlockPos.containing(position.x, position.y, position.z));
      FluidState fluidState = state.getFluidState();
      if (fluidState.is(FluidTags.WATER)) {
         if (ConfigClient.oceanParticles) {
            MutableBlockPos blockPos = new MutableBlockPos();
            Vector3d direction = new Vector3d();
            Vector3d startPos = new Vector3d();
            int explosionDistance = (int)Math.min(32.0, explosion.strength * 2.0);

            for (int i = 0; i < 200; i++) {
               direction.set(net.diebuddies.math.Math.random() - 0.5, net.diebuddies.math.Math.random(), net.diebuddies.math.Math.random() - 0.5).normalize();
               if (direction.isFinite()) {
                  startPos.set(position);

                  for (int range = 0; range < explosionDistance; range++) {
                     startPos.add(direction);
                     blockPos.set(startPos.x, startPos.y, startPos.z);
                     state = this.level.getBlockState(blockPos);
                     fluidState = state.getFluidState();
                     if (state.isAir()) {
                        double power = net.diebuddies.math.Math.remapClamp((double)range, 0.0, (double)explosionDistance, 0.6, 0.2);
                        OceanWorld.createExplosionWaterSplash(
                           this.level, startPos.x, startPos.y, startPos.z, direction.x * power, direction.y * power, direction.z * power, 0.1, 0.5, 10
                        );
                        break;
                     }

                     if (!fluidState.is(FluidTags.WATER)) {
                        break;
                     }
                  }
               }
            }
         }

         if (ConfigClient.oceanRipples) {
            this.oceanWorld.spawnRipple(360, 150, 1.35F, position.x, position.y, position.z, 0.0925);
         }
      } else if (ConfigClient.smokePhysics) {
         this.smokeDomain.executeExplosion(explosion);
      }

      position = new Vector3d();
      double explosionStrengthSquared = explosion.strength * 2.0 * (explosion.strength * 2.0);

      for (IRigidBody body : this.bodies) {
         double distanceSquared = explosion.position.distanceSquared(body.getEntity().getTransformation().getTranslation(position).add(this.offset));
         if (distanceSquared <= explosionStrengthSquared) {
            double distance = Math.sqrt(distanceSquared);
            Vector3d direction = body.getEntity().getTransformation().getTranslation(position).add(this.offset).sub(explosion.position).normalize();
            direction.y += 2.0;
            direction.normalize();
            double realStrength = (1.0 - net.diebuddies.math.Math.clamp(distance / (explosion.strength * 2.0), 0.0, 1.0)) * 15.0;
            this.queue(() -> {
               if (body.getRigidBody() instanceof PxRigidDynamic rigidBody) {
                  rigidBody.wakeUp();
                  PxVec3 v = rigidBody.getLinearVelocity();
                  float vx = MemoryUtil.memGetFloat(v.getAddress());
                  float vy = MemoryUtil.memGetFloat(v.getAddress() + 4L);
                  float vz = MemoryUtil.memGetFloat(v.getAddress() + 8L);
                  v.setX(vx + (float)(direction.x * realStrength));
                  v.setY(vy + (float)(direction.y * realStrength));
                  v.setZ(vz + (float)(direction.z * realStrength));
                  rigidBody.setLinearVelocity(v);
               }
            });
         }
      }
   }

   public void updateLastSeen() {
      this.lastSeen = System.nanoTime();
   }

   public boolean isActive() {
      return System.nanoTime() - this.lastSeen <= 5000000000L;
   }

   public Level getWorld() {
      return this.level;
   }

   public DynamicsWorld getDynamicsWorld() {
      return this.dynamicsWorld;
   }

   public DoublyLinkedList<Ragdoll> getRagdolls() {
      return this.ragdolls;
   }

   public void addVerletSimulation(int index, VerletSimulation simulation) {
      this.verletSimulations.add(index, simulation);
   }

   public void addVerletSimulation(VerletSimulation simulation) {
      this.verletSimulations.add(simulation);
   }

   public void removeVerletSimulation(VerletSimulation simulation) {
      this.verletSimulations.remove(simulation);
   }

   public List<VerletSimulation> getVerletSimulations() {
      return this.verletSimulations;
   }

   public List<Liquid> getLiquids() {
      return this.liquids;
   }

   public SnowWorld getSnowWorld() {
      return this.snowWorld;
   }

   public void setSnowWorld(SnowWorld snowWorld) {
      this.snowWorld = snowWorld;
   }

   public OceanWorld getOceanWorld() {
      return this.oceanWorld;
   }

   public void setOceanWorld(OceanWorld oceanWorld) {
      this.oceanWorld = oceanWorld;
   }

   public SmokeDomain getSmokeDomain() {
      return this.smokeDomain;
   }

   public Level getLevel() {
      return this.level;
   }

   public WeatherDomain getWeatherDomain() {
      return this.weatherDomain;
   }

   public void addJointParents(PxJoint joint, Tuple<IRigidBody, IRigidBody> tuple) {
      this.jointParents.put(joint, tuple);
   }

   public void removeJointParents(PxJoint joint) {
      this.jointParents.remove(joint);
   }

   public Tuple<IRigidBody, IRigidBody> getJointParents(PxJoint joint) {
      return this.jointParents.get(joint);
   }

   public void queue(Runnable runnable) {
      this.dynamicsWorld.queue(runnable);
   }

   private void removeParticleSystem(PxPBDParticleSystem particleSystem) {
      this.dynamicsWorld.removeActor(particleSystem);
   }

   private void addParticleSystem(PxPBDParticleSystem particleSystem) {
      this.dynamicsWorld.addActor(particleSystem);
   }

   public PxPBDParticleSystem getFluidSystem() {
      if (this.fluidSystem == null) {
         this.createFluidSystem();
      }

      return this.fluidSystem;
   }

   public int getFluidPhase() {
      if (this.fluidSystem == null) {
         this.createFluidSystem();
      }

      return this.fluidPhase;
   }

   public void adjustOffset(double posX, double posY, double posZ) {
      if (this.bodies.size() == 0 && this.chunkBodies.size() == 0 && this.fluidSystem == null) {
         this.offset.set(posX, posY, posZ);
      }
   }

   public void adjustOffset(Matrix4d transformation) {
      if (this.bodies.size() == 0 && this.chunkBodies.size() == 0 && this.fluidSystem == null) {
         transformation.getTranslation(this.offset);
      }
   }
}
