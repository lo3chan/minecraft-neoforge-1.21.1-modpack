package net.diebuddies.physics.verlet.constraints;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Map;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.verlet.VerletHelper;
import net.diebuddies.physics.verlet.VerletPoint;
import net.diebuddies.physics.verlet.VerletSimulation;
import net.diebuddies.physics.verlet.VerletSimulationData;
import net.diebuddies.physics.vines.VineHelper;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4fStack;
import org.joml.Vector3i;

public class WorldConstraint implements VerletConstraint {
   public static final double CONTACT_FRICTION = 0.6;
   private Entity entity;
   private Level level;
   private Map<Vector3i, List<AABB>> bodies;
   private Vector3i tmpInt = new Vector3i();
   private VerletHelper helper = new VerletHelper();
   private float preferUpMovement;

   public WorldConstraint(Level level, float preferUpMovement) {
      this.preferUpMovement = preferUpMovement;
      this.level = level;
   }

   public WorldConstraint(Entity entity, float preferUpMovement) {
      this.preferUpMovement = preferUpMovement;
      this.entity = entity;
   }

   public WorldConstraint(Entity entity) {
      this(entity, 0.7F);
   }

   @Override
   public void updateBefore(double delta, VerletSimulation simulation) {
   }

   @Override
   public void subStep(double percent, VerletSimulation simulation) {
   }

   @Override
   public void updateAfter(double delta, VerletSimulation simulation) {
      this.checkVerletCollisions(simulation);
   }

   @Override
   public boolean initAsyncData(PhysicsWorld world, VerletSimulation simulation) {
      VerletSimulationData data = simulation.getData();
      if (data.points.size() == 0) {
         return false;
      } else {
         if (this.entity != null) {
            this.level = this.entity.getCommandSenderWorld();
         }

         PhysicsWorld physics = PhysicsMod.getInstance(this.level).getPhysicsWorld();
         VerletPoint start = data.points.get(0);
         MutableBlockPos min = new MutableBlockPos(start.position.x + data.offset.x, start.position.y + data.offset.y, start.position.z + data.offset.z);
         MutableBlockPos max = new MutableBlockPos(start.position.x + data.offset.x, start.position.y + data.offset.y, start.position.z + data.offset.z);

         for (int i = 0; i < data.points.size(); i++) {
            VerletPoint point = data.points.get(i);
            int x = Mth.floor(point.position.x + data.offset.x);
            int y = Mth.floor(point.position.y + data.offset.y);
            int z = Mth.floor(point.position.z + data.offset.z);
            if (x < min.getX()) {
               min.setX(x);
            } else if (x > max.getX()) {
               max.setX(x);
            }

            if (y < min.getY()) {
               min.setY(y);
            } else if (y > max.getY()) {
               max.setY(y);
            }

            if (z < min.getZ()) {
               min.setZ(z);
            } else if (z > max.getZ()) {
               max.setZ(z);
            }
         }

         this.bodies = new Object2ObjectOpenHashMap();
         MutableBlockPos currentPos = new MutableBlockPos(0, 0, 0);
         if (max.getX() - min.getX() > 10) {
            return false;
         } else if (max.getY() - min.getY() > 10) {
            return false;
         } else if (max.getZ() - min.getZ() > 10) {
            return false;
         } else {
            for (int xx = min.getX() - 1; xx <= max.getX() + 1; xx++) {
               for (int yx = min.getY() - 1; yx <= max.getY() + 1; yx++) {
                  for (int zx = min.getZ() - 1; zx <= max.getZ() + 1; zx++) {
                     currentPos.set(xx, yx, zx);
                     BlockState state = physics.getWorld().getBlockState(currentPos);
                     if (state.getBlock() != Blocks.AIR) {
                        VoxelShape voxelShape = state.getCollisionShape(physics.getWorld(), currentPos);
                        if (!voxelShape.isEmpty() && VineHelper.getSetting(state) == null) {
                           for (AABB aabb : voxelShape.toAabbs()) {
                              this.addToSuroundings(
                                 new AABB(
                                    aabb.minX + xx - data.offset.x,
                                    aabb.minY + yx - data.offset.y,
                                    aabb.minZ + zx - data.offset.z,
                                    aabb.maxX + xx - data.offset.x,
                                    aabb.maxY + yx - data.offset.y,
                                    aabb.maxZ + zx - data.offset.z
                                 ),
                                 xx,
                                 yx,
                                 zx,
                                 this.bodies
                              );
                           }
                        }
                     }
                  }
               }
            }

            return false;
         }
      }
   }

   private void addToSuroundings(AABB box, int x, int y, int z, Map<Vector3i, List<AABB>> bodies) {
      for (int xi = -1; xi <= 1; xi++) {
         for (int yi = -1; yi <= 1; yi++) {
            for (int zi = -1; zi <= 1; zi++) {
               List<AABB> boxes = bodies.get(this.tmpInt.set(x + xi, y + yi, z + zi));
               if (boxes == null) {
                  boxes = new ObjectArrayList();
                  bodies.put(new Vector3i(this.tmpInt), boxes);
               }

               boxes.add(box);
            }
         }
      }
   }

   private void checkVerletCollisions(VerletSimulation simulation) {
      VerletSimulationData data = simulation.getData();
      double enlarge = 0.05;

      for (VerletPoint point : data.points) {
         if (!point.locked) {
            int x = Mth.floor(point.position.x + data.offset.x);
            int y = Mth.floor(point.position.y + data.offset.y);
            int z = Mth.floor(point.position.z + data.offset.z);
            List<AABB> boxes = this.bodies.get(this.tmpInt.set(x, y, z));
            if (boxes != null) {
               for (int i = 0; i < boxes.size(); i++) {
                  AABB box = boxes.get(i);
                  if (this.helper
                     .movePointOutOfBox(
                        point.position,
                        this.preferUpMovement,
                        (float)(box.minX - enlarge),
                        (float)(box.minY - enlarge),
                        (float)(box.minZ - enlarge),
                        (float)(box.maxX + enlarge),
                        (float)(box.maxY + enlarge),
                        (float)(box.maxZ + enlarge)
                     )) {
                     point.friction = 0.6;
                     break;
                  }
               }
            }
         }
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
