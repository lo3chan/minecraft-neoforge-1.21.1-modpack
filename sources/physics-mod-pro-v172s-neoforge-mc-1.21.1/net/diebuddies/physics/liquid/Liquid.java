package net.diebuddies.physics.liquid;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.dualcontouring.Chunk;
import net.diebuddies.dualcontouring.OctreeNode;
import net.diebuddies.dualcontouring.Vertex;
import net.diebuddies.dualcontouring.Voxel;
import net.diebuddies.opengl.Data;
import net.diebuddies.opengl.Mesh;
import net.diebuddies.opengl.Usage;
import net.diebuddies.opengl.VAO;
import net.diebuddies.physics.IRigidBody;
import net.diebuddies.physics.PhysicsEntity;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.StarterClient;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4d;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3i;
import physx.physics.PxRigidDynamic;

public class Liquid {
   public static LiquidContouringThread[] threads;
   public int density;
   public int textureID;
   public Matrix4d transformation = new Matrix4d();
   public Liquid.LiquidInfo info;
   public VAO vao;
   public double range = 1.25;
   public Vector3d origin;
   private Vector3d tmpOrigin = new Vector3d();
   private Vector3d tmpPos = new Vector3d();
   public Vector2f textureScale = new Vector2f(1.0F);
   public int gridSize;
   public float damping = 0.9F;
   public int color = -1;
   public boolean sourceAlive;
   public BlockPos blockPos;
   public LiquidController controller;
   public PhysicsWorld world;
   public short materialID = -1;
   public short renderType = 1;
   public long meshIndex;
   public List<IRigidBody> particles = new ObjectArrayList();
   private static final ConcurrentLinkedQueue<Mesh> meshPool = new ConcurrentLinkedQueue<>();
   private ConcurrentLinkedQueue<Liquid.LiquidInfo> infoQueue = new ConcurrentLinkedQueue<>();

   public Liquid(LiquidController controller) {
      initThreads();
      this.controller = controller;
   }

   public static void initThreads() {
      if (threads == null) {
         threads = new LiquidContouringThread[ConfigClient.liquidThreads];

         for (int i = 0; i < threads.length; i++) {
            threads[i] = new LiquidContouringThread();
            threads[i].setDaemon(true);
            threads[i].start();
         }
      }
   }

   public void blockUpdate(PhysicsWorld physicsWorld, BlockPos pos, BlockState state) {
      if (pos.equals(this.blockPos) && !state.isAir()) {
         this.sourceAlive = false;
      }
   }

   public boolean update(PhysicsWorld physicsWorld, double diff) {
      this.removeDead();
      this.removeFarAway();
      this.controller.update(this, diff);
      if (this.particles.isEmpty()) {
         return true;
      } else {
         this.createMesh();
         return false;
      }
   }

   private void removeFarAway() {
      this.tmpOrigin.set(this.origin).sub(this.world.getOffset());
      double despawnRangeSquared = ConfigClient.liquidSourceDistance * ConfigClient.liquidSourceDistance;
      double destroyRangeSquared = (ConfigClient.liquidSourceDistance + 5.0) * (ConfigClient.liquidSourceDistance + 5.0);

      for (int i = 0; i < this.particles.size(); i++) {
         IRigidBody particle = this.particles.get(i);
         particle.getEntity().getTransformation().getTranslation(this.tmpPos);
         double distSquared = this.tmpOrigin.distanceSquared(this.tmpPos);
         if (distSquared > destroyRangeSquared) {
            particle = this.particles.remove(i--);
            this.world.removeBody(particle);
            if (particle.getLastChunk() != null && !particle.isKinematicOrFrozen()) {
               this.world.removeLoadedChunkEntity(particle.getLastChunk());
            }

            this.world.getDynamicsWorld().removeActor(particle.getRigidBody());
            particle.destroy();
         } else if (distSquared > despawnRangeSquared) {
            particle.getEntity().startDespawnAnimation(this.world.getWorld());
         }
      }
   }

   private void createMesh() {
      this.meshIndex++;
      final long currentMeshIndex = this.meshIndex;
      LiquidContouringThread thread = threads[(int)(currentMeshIndex % threads.length)];
      Liquid.LiquidInfo newestInfo = null;
      Liquid.LiquidInfo current = null;

      while ((current = this.infoQueue.poll()) != null) {
         if (newestInfo == null) {
            newestInfo = current;
         } else if (newestInfo.meshIndex < current.meshIndex) {
            if (newestInfo.mesh.size(Data.INDEX) > 0) {
               meshPool.add(newestInfo.mesh);
            }

            newestInfo = current;
         }
      }

      if (newestInfo != this.info && newestInfo != null) {
         if (this.vao != null) {
            this.vao.destroy();
         }

         VAO newVao = null;
         this.info = newestInfo;
         if (this.info.mesh.size(Data.INDEX) > 0) {
            newVao = this.info.mesh.constructVAO(Usage.STATIC);
            meshPool.add(this.info.mesh);
         }

         this.transformation.set(this.info.transformation);
         this.vao = newVao;
      }

      int pSize = this.particles.size();
      final Vector3d offset = new Vector3d(this.world.getOffset());
      final List<Vector3d> positions = new ObjectArrayList(pSize);
      final FloatList scales = new FloatArrayList(pSize);
      final IntList ambients = new IntArrayList(pSize);
      MutableBlockPos blockPos = new MutableBlockPos();

      for (int i = 0; i < pSize; i++) {
         PhysicsEntity particle = this.particles.get(i).getEntity();
         Vector3d position = particle.getTransformation().getTranslation(new Vector3d());
         positions.add(position);
         scales.add(particle.getDespawnScale(this.world.getWorld()));
         ambients.add(particle.getLight(this.world.getWorld(), blockPos.set(position.x + offset.x, position.y + offset.y, position.z + offset.z)));
      }

      Event liquidContouring = new Event() {
         @Override
         public void run() {
            Vector3d min = new Vector3d(1.7976931348623157E308, 1.7976931348623157E308, 1.7976931348623157E308);
            Vector3d max = new Vector3d(-1.7976931348623157E308, -1.7976931348623157E308, -1.7976931348623157E308);
            Vector3d tmpPos = new Vector3d();

            for (int i = 0; i < positions.size(); i++) {
               Vector3d position = positions.get(i);
               min.min(position);
               max.max(position);
            }

            int iRange = (int)Math.round(Math.ceil(Liquid.this.range));
            int minX = Math.round((float)Math.floor(min.x * Liquid.this.gridSize)) - iRange;
            int minY = Math.round((float)Math.floor(min.y * Liquid.this.gridSize)) - iRange;
            int minZ = Math.round((float)Math.floor(min.z * Liquid.this.gridSize)) - iRange;
            int maxX = Math.round((float)Math.ceil(max.x * Liquid.this.gridSize)) + iRange;
            int maxY = Math.round((float)Math.ceil(max.y * Liquid.this.gridSize)) + iRange;
            int maxZ = Math.round((float)Math.ceil(max.z * Liquid.this.gridSize)) + iRange;
            int width = maxX - minX + 1;
            int height = maxY - minY + 1;
            int depth = maxZ - minZ + 1;
            Set<Vector3i> tmpCells = this.thread.tmpCells;
            Set<Vector3i> affectedCells = this.thread.affectedCells;
            if (width > 0 && height > 0 && depth > 0) {
               Chunk chunk = this.thread.chunk.reset(width, height, depth, (byte)-20, 0, Liquid.this.color);

               for (int i = 0; i < positions.size(); i++) {
                  tmpPos.set((Vector3dc)positions.get(i));
                  tmpPos.mul(Liquid.this.gridSize);
                  float scale = scales.getFloat(i);
                  int ambient = ambients.getInt(i);
                  short strength = (short)(120.0F * scale);
                  int x = (int)tmpPos.x;
                  int y = (int)tmpPos.y;
                  int z = (int)tmpPos.z;
                  double rangeSquared = Liquid.this.range * Liquid.this.range;

                  for (int xo = -iRange; xo <= iRange; xo++) {
                     for (int yo = -iRange; yo <= iRange; yo++) {
                        for (int zo = -iRange; zo <= iRange; zo++) {
                           int ix = x + xo;
                           int iy = y + yo;
                           int iz = z + zo;
                           int ixm = ix - minX;
                           int iym = iy - minY;
                           int izm = iz - minZ;
                           if (ixm >= 0 && iym >= 0 && izm >= 0 && ixm < width && iym < height && izm < depth) {
                              double distanceSquared = tmpPos.distanceSquared(ix, iy, iz);
                              Voxel voxel = chunk.get(ixm, iym, izm);
                              voxel.ambient = Math.max(voxel.ambient, ambient);
                              if (distanceSquared < rangeSquared) {
                                 double perc = 1.0 - Math.min(Math.sqrt(distanceSquared) / Liquid.this.range, 1.0);
                                 voxel.density = (byte)Math.min(voxel.density + perc * strength, 126.0);
                                 tmpCells.add(this.thread.vectorPool.get(ixm, iym, izm));
                              }
                           }
                        }
                     }
                  }
               }

               for (Vector3i tmpCell : tmpCells) {
                  affectedCells.add(tmpCell);
                  affectedCells.add(this.thread.vectorPool.get(tmpCell.x - 1, tmpCell.y, tmpCell.z));
                  affectedCells.add(this.thread.vectorPool.get(tmpCell.x, tmpCell.y - 1, tmpCell.z));
                  affectedCells.add(this.thread.vectorPool.get(tmpCell.x, tmpCell.y, tmpCell.z - 1));
                  affectedCells.add(this.thread.vectorPool.get(tmpCell.x - 1, tmpCell.y - 1, tmpCell.z));
                  affectedCells.add(this.thread.vectorPool.get(tmpCell.x - 1, tmpCell.y, tmpCell.z - 1));
                  affectedCells.add(this.thread.vectorPool.get(tmpCell.x, tmpCell.y - 1, tmpCell.z - 1));
                  affectedCells.add(this.thread.vectorPool.get(tmpCell.x - 1, tmpCell.y - 1, tmpCell.z - 1));
               }

               OctreeNode root = new OctreeNode();
               List<Vertex> vertexBuffer = this.thread.vertexBuffer;
               IntArrayList indexBuffer = this.thread.indexBuffer;
               root.min = new Vector3i();
               this.thread.dualContouringLiquid.constructOctreeLinear(root, chunk, affectedCells);
               this.thread.dualContouringLiquid.generateMesh(root, vertexBuffer, indexBuffer);
               int indexSize = indexBuffer.size();
               Liquid.LiquidInfo info = Liquid.this.new LiquidInfo();
               info.meshIndex = currentMeshIndex;
               info.transformation = new Matrix4d()
                  .setTranslation(
                     offset.x + (double)minX / Liquid.this.gridSize,
                     offset.y + (double)minY / Liquid.this.gridSize,
                     offset.z + (double)minZ / Liquid.this.gridSize
                  );
               if (indexSize > 0) {
                  Mesh mesh = Liquid.meshPool.poll();
                  if (!StarterClient.iris && !StarterClient.optifabric) {
                     info.mesh = this.thread
                        .dualContouringLiquid
                        .buildMeshFlatClassic(
                           vertexBuffer,
                           indexBuffer,
                           mesh,
                           new Vector3d((double)minX / Liquid.this.gridSize, (double)minY / Liquid.this.gridSize, (double)minZ / Liquid.this.gridSize),
                           1.0 / Liquid.this.gridSize,
                           Liquid.this.textureScale,
                           Liquid.this.color
                        );
                  } else {
                     info.mesh = this.thread
                        .dualContouringLiquid
                        .buildMeshFlat(
                           vertexBuffer,
                           indexBuffer,
                           mesh,
                           new Vector3d((double)minX / Liquid.this.gridSize, (double)minY / Liquid.this.gridSize, (double)minZ / Liquid.this.gridSize),
                           1.0 / Liquid.this.gridSize,
                           Liquid.this.textureScale,
                           Liquid.this.color
                        );
                  }
               } else {
                  info.mesh = new Mesh();
               }

               Liquid.this.infoQueue.add(info);
               this.thread.vertexBuffer.clear();
               this.thread.indexBuffer.clear();
            }

            this.thread.tmpCells.clear();
            this.thread.affectedCells.clear();
            this.thread.vectorPool.reset();
         }
      };
      liquidContouring.id = this.hashCode();
      thread.queueEvent(liquidContouring);
   }

   private void removeDead() {
      for (int i = 0; i < this.particles.size(); i++) {
         if (this.particles.get(i).isDestroyed()) {
            this.particles.remove(i--);
         }
      }
   }

   public void add(PhysicsWorld world) {
      this.world = world;
      this.controller.init(world, this);
   }

   public void spawnParticle(double radius, double x, double y, double z) {
      PhysicsEntity entity = new PhysicsEntity(PhysicsEntity.Type.LIQUID, null);
      entity.getTransformation().translation(x, y, z);
      entity.getOldTransformation().set(entity.getTransformation());
      entity.models = null;
      IRigidBody body = this.world.addPhysicsSphere(entity, (float)radius);
      body.liquid = true;
      ((PxRigidDynamic)body.getRigidBody()).setMaxAngularVelocity((float)Math.toRadians(360.0));
      ((PxRigidDynamic)body.getRigidBody()).setLinearDamping(this.damping);
      ((PxRigidDynamic)body.getRigidBody()).setAngularDamping(this.damping);
      ((PxRigidDynamic)body.getRigidBody()).setMaxDepenetrationVelocity(1.0F);
      this.particles.add(body);
   }

   public void remove(PhysicsWorld world) {
      this.removeDead();

      for (int i = 0; i < this.particles.size(); i++) {
         IRigidBody particle = this.particles.get(i);
         world.removeBody(particle);
         if (particle.getLastChunk() != null && !particle.isKinematicOrFrozen()) {
            world.removeLoadedChunkEntity(particle.getLastChunk());
         }

         world.getDynamicsWorld().removeActor(particle.getRigidBody());
      }
   }

   public void destroy() {
      this.removeDead();

      for (int i = 0; i < this.particles.size(); i++) {
         this.particles.get(i).destroy();
      }

      this.particles.clear();
      if (this.vao != null) {
         this.vao.destroy();
      }

      this.vao = null;
   }

   class LiquidInfo {
      volatile long meshIndex;
      volatile Mesh mesh;
      volatile Matrix4d transformation;
   }
}
