/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.joml.Matrix4fStack
 *  org.joml.Vector3i
 *  org.joml.Vector3ic
 */
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
import net.diebuddies.physics.verlet.constraints.VerletConstraint;
import net.diebuddies.physics.vines.VineHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4fStack;
import org.joml.Vector3i;
import org.joml.Vector3ic;

public class WorldConstraint
implements VerletConstraint {
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
        this(entity, 0.7f);
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
        }
        if (this.entity != null) {
            this.level = this.entity.getCommandSenderWorld();
        }
        PhysicsWorld physics = PhysicsMod.getInstance(this.level).getPhysicsWorld();
        VerletPoint start = data.points.get(0);
        BlockPos.MutableBlockPos min = new BlockPos.MutableBlockPos(start.position.x + data.offset.x, start.position.y + data.offset.y, start.position.z + data.offset.z);
        BlockPos.MutableBlockPos max = new BlockPos.MutableBlockPos(start.position.x + data.offset.x, start.position.y + data.offset.y, start.position.z + data.offset.z);
        for (int i = 0; i < data.points.size(); ++i) {
            VerletPoint point = data.points.get(i);
            int x = Mth.floor((double)(point.position.x + data.offset.x));
            int y = Mth.floor((double)(point.position.y + data.offset.y));
            int z = Mth.floor((double)(point.position.z + data.offset.z));
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
                continue;
            }
            if (z <= max.getZ()) continue;
            max.setZ(z);
        }
        this.bodies = new Object2ObjectOpenHashMap();
        BlockPos.MutableBlockPos currentPos = new BlockPos.MutableBlockPos(0, 0, 0);
        if (max.getX() - min.getX() > 10) {
            return false;
        }
        if (max.getY() - min.getY() > 10) {
            return false;
        }
        if (max.getZ() - min.getZ() > 10) {
            return false;
        }
        for (int x = min.getX() - 1; x <= max.getX() + 1; ++x) {
            for (int y = min.getY() - 1; y <= max.getY() + 1; ++y) {
                for (int z = min.getZ() - 1; z <= max.getZ() + 1; ++z) {
                    VoxelShape voxelShape;
                    currentPos.set(x, y, z);
                    BlockState state = physics.getWorld().getBlockState((BlockPos)currentPos);
                    if (state.getBlock() == Blocks.AIR || (voxelShape = state.getCollisionShape((BlockGetter)physics.getWorld(), (BlockPos)currentPos)).isEmpty() || VineHelper.getSetting(state) != null) continue;
                    for (AABB aabb : voxelShape.toAabbs()) {
                        this.addToSuroundings(new AABB(aabb.minX + (double)x - data.offset.x, aabb.minY + (double)y - data.offset.y, aabb.minZ + (double)z - data.offset.z, aabb.maxX + (double)x - data.offset.x, aabb.maxY + (double)y - data.offset.y, aabb.maxZ + (double)z - data.offset.z), x, y, z, this.bodies);
                    }
                }
            }
        }
        return false;
    }

    private void addToSuroundings(AABB box, int x, int y, int z, Map<Vector3i, List<AABB>> bodies) {
        for (int xi = -1; xi <= 1; ++xi) {
            for (int yi = -1; yi <= 1; ++yi) {
                for (int zi = -1; zi <= 1; ++zi) {
                    ObjectArrayList boxes = bodies.get(this.tmpInt.set(x + xi, y + yi, z + zi));
                    if (boxes == null) {
                        boxes = new ObjectArrayList();
                        bodies.put(new Vector3i((Vector3ic)this.tmpInt), (List<AABB>)boxes);
                    }
                    boxes.add((AABB)box);
                }
            }
        }
    }

    private void checkVerletCollisions(VerletSimulation simulation) {
        VerletSimulationData data = simulation.getData();
        double enlarge = 0.05;
        block0: for (VerletPoint point : data.points) {
            int z;
            int y;
            int x;
            List<AABB> boxes;
            if (point.locked || (boxes = this.bodies.get(this.tmpInt.set(x = Mth.floor((double)(point.position.x + data.offset.x)), y = Mth.floor((double)(point.position.y + data.offset.y)), z = Mth.floor((double)(point.position.z + data.offset.z))))) == null) continue;
            for (int i = 0; i < boxes.size(); ++i) {
                AABB box = boxes.get(i);
                if (!this.helper.movePointOutOfBox(point.position, this.preferUpMovement, (double)((float)(box.minX - enlarge)), (double)((float)(box.minY - enlarge)), (double)((float)(box.minZ - enlarge)), (double)((float)(box.maxX + enlarge)), (double)((float)(box.maxY + enlarge)), (double)((float)(box.maxZ + enlarge)))) continue;
                point.friction = 0.6;
                continue block0;
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

