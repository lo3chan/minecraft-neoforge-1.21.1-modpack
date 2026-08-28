/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.DoorBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.DoorHingeSide
 *  net.minecraft.world.level.block.state.properties.DoubleBlockHalf
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.joml.Vector3d
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.diebuddies.physics.vines;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.diebuddies.minecraft.ChunkHelper;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.ragdoll.DoorRagdoll;
import net.diebuddies.physics.ragdoll.DynamicRagdoll;
import net.diebuddies.physics.vines.Adjustable;
import net.diebuddies.physics.vines.DynamicSetting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class DoorSetting
extends DynamicSetting {
    @Adjustable(id="Hitbox scale", min=0.1, max=10.0, step=0.01, translationId="physicsmod.prop.door.hitboxscale")
    public Vector3f hitboxScale;
    @Adjustable(id="Stiffness", min=0.1, max=5000.0, step=0.1, translationId="physicsmod.prop.door.stiffness")
    public float stiffness;
    @Adjustable(id="Damping", min=0.1, max=100.0, step=0.1, translationId="physicsmod.prop.door.damping")
    public float damping;
    @Adjustable(id="Collision", min=0.1, max=100.0, step=0.1, translationId="physicsmod.prop.door.collision")
    public boolean collision;

    public DoorSetting(Vector3f hitboxScale, float stiffness, float damping, boolean linkedPhysics, boolean collision) {
        this.collision = collision;
        this.hitboxScale = hitboxScale;
        this.stiffness = stiffness;
        this.damping = damping;
        this.linkedPhysics = linkedPhysics;
    }

    public DoorSetting() {
        this.collision = true;
        this.hitboxScale = new Vector3f(0.85f);
        this.stiffness = 1580.0f;
        this.damping = 8.0f;
        this.linkedPhysics = true;
    }

    @Override
    public DynamicRagdoll createRagdoll(PhysicsMod mod, BlockState current, BlockPos pos, Long2ObjectMap<BlockState> availableBlocks) {
        if (current.getValue((Property)DoorBlock.HALF) == DoubleBlockHalf.UPPER) {
            availableBlocks.remove(ChunkHelper.calcRelativeIndex(pos.getX(), pos.getY(), pos.getZ()));
            return null;
        }
        DoorRagdoll ragdoll = new DoorRagdoll();
        ragdoll.hitboxScale.set((Vector3fc)this.hitboxScale);
        ragdoll.collision = this.collision;
        ragdoll.hookedEntity = this.createPart(mod, ragdoll, current, pos.getY(), pos.getX(), pos.getY(), pos.getZ());
        this.createPart(mod, ragdoll, (BlockState)current.setValue((Property)DoorBlock.HALF, (Comparable)DoubleBlockHalf.UPPER), pos.getY(), pos.getX(), pos.getY() + 1, pos.getZ());
        ragdoll.addConnection(1, 0, true);
        ragdoll.stiffness = this.stiffness;
        ragdoll.damping = this.damping;
        ragdoll.linkedPhysics = this.linkedPhysics;
        Vector3d hookPos = new Vector3d();
        hookPos.y = 0.5;
        Direction direction = (Direction)current.getValue((Property)DoorBlock.FACING);
        boolean open = (Boolean)current.getValue((Property)DoorBlock.OPEN) == false;
        boolean hinge = current.getValue((Property)DoorBlock.HINGE) == DoorHingeSide.RIGHT;
        Direction result = null;
        switch (direction) {
            default: {
                result = open ? Direction.EAST : (hinge ? Direction.NORTH : Direction.SOUTH);
                break;
            }
            case SOUTH: {
                result = open ? Direction.SOUTH : (hinge ? Direction.EAST : Direction.WEST);
                break;
            }
            case WEST: {
                result = open ? Direction.WEST : (hinge ? Direction.SOUTH : Direction.NORTH);
                break;
            }
            case NORTH: {
                result = open ? Direction.NORTH : (hinge ? Direction.WEST : Direction.EAST);
            }
        }
        switch (result) {
            default: {
                hookPos.z = 0.90625;
                hookPos.x = hinge ? 0.90625 : 0.09375;
                break;
            }
            case SOUTH: {
                hookPos.z = 0.09375;
                hookPos.x = hinge ? 0.09375 : 0.90625;
                break;
            }
            case WEST: {
                hookPos.x = 0.90625;
                hookPos.z = hinge ? 0.09375 : 0.90625;
                break;
            }
            case EAST: {
                hookPos.x = 0.09375;
                double d = hookPos.z = hinge ? 0.90625 : 0.09375;
            }
        }
        if (!open) {
            switch (result) {
                default: {
                    hookPos.x = 1.0 - hookPos.x;
                    break;
                }
                case SOUTH: {
                    hookPos.x = 1.0 - hookPos.x;
                    break;
                }
                case WEST: {
                    hookPos.z = 1.0 - hookPos.z;
                    break;
                }
                case EAST: {
                    hookPos.z = 1.0 - hookPos.z;
                }
            }
        }
        Vector3f offset = ragdoll.hookedEntity.models.get((int)0).mesh.offset;
        hookPos.x -= (double)offset.x % 1.0;
        hookPos.y -= (double)offset.y % 1.0;
        hookPos.z -= (double)offset.z % 1.0;
        ragdoll.hook = hookPos;
        availableBlocks.remove(ChunkHelper.calcRelativeIndex(pos.getX(), pos.getY(), pos.getZ()));
        return ragdoll;
    }

    @Override
    public boolean isValid(BlockState state) {
        return state.hasProperty((Property)DoorBlock.FACING) && state.hasProperty((Property)DoorBlock.OPEN) && state.hasProperty((Property)DoorBlock.HINGE) && state.hasProperty((Property)DoorBlock.HALF);
    }

    @Override
    public Block defaultBlock() {
        return Blocks.OAK_DOOR;
    }
}

